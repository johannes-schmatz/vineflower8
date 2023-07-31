package org.jetbrains.java.decompiler.util;

import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.struct.StructContext;
import org.jetbrains.java.decompiler.struct.attr.StructGeneralAttribute;
import org.jetbrains.java.decompiler.struct.attr.StructModuleAttribute;
import org.jetbrains.java.decompiler.util.future.JInputStream;
import org.jetbrains.java.decompiler.util.future.JList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jetbrains.java.decompiler.util.future.JMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JrtFinder {
  private static final boolean jrtProviderExists = doesJrtProviderExist();
  private static boolean doesJrtProviderExist() {
    try {
      URI url = URI.create("jrt:/");
      FileSystem fs = FileSystems.newFileSystem(url, JMap.of());
      fs.close();
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

    public static final String CURRENT = "current";

    // https://openjdk.java.net/jeps/220 for runtime image structure and JRT filesystem

  public static void addRuntime(final StructContext ctx) {
    try {
      if (jrtProviderExists) {

        final URI url = URI.create("jrt:/");
        FileSystem fs = FileSystems.newFileSystem(url, JMap.of());
        ctx.addSpace(new JavaRuntimeContextSource("current", fs), false);
      } else {
        File javaHome = new File(System.getProperty("java.home"));
        // legacy runtime, add all jars from the lib and jre/lib folders
        final List<File> jrt = new ArrayList<>();
        Collections.addAll(jrt, new File(javaHome, "jre/lib").listFiles());
        Collections.addAll(jrt, new File(javaHome, "lib").listFiles());
        for (final File lib : jrt) {
          if (lib.isFile() && lib.getName().endsWith(".jar")) {
            ctx.addSpace(lib, false);
          }
        }
      }
    } catch (final IOException ex) {
      DecompilerContext.getLogger().writeMessage("Failed to open current java runtime for inspection", ex);
    }
  }

  public static void addRuntime(final StructContext ctx, final File javaHome) {
    File jrtFsJar = new File(javaHome, "lib/jrt-fs.jar");
    if (jrtFsJar.isFile()) {
      // Java 9+
      if (jrtProviderExists) {
        try {
          final URI url = URI.create("jrt:/");
          if (javaHome == null) {
            FileSystem fs = FileSystems.newFileSystem(url, JMap.of());
            ctx.addSpace(new JavaRuntimeContextSource("current", fs), false);
          } else {
            FileSystem fs = FileSystems.newFileSystem(url, JMap.of("java.home", javaHome.getAbsolutePath()));
            ctx.addSpace(new JavaRuntimeContextSource(javaHome.getAbsolutePath(), fs), false);
          }
        } catch (final IOException ex) {
          DecompilerContext.getLogger().writeMessage("Failed to open java runtime at " + javaHome, ex);
        }
      } else {
        final URI url = URI.create("jrt:/");
        final Map<String, ?> env = JMap.of("java.home", javaHome.getAbsolutePath());
        Object fileSystem = null;
        try {
          // load the lib/jrt-fs.jar, jdk.internal.jrtfs.JrtFileSystemProvider

          // SECURITY: In theory this _could_ be used to get US to execute code from an attacker
          URLClassLoader classLoader = new URLClassLoader(new URL[]{jrtFsJar.toURI().toURL()});
          Class<?> clazz = classLoader.loadClass("jdk.internal.jrtfs.JrtFileSystemProvider");
          Object fileSystemProvider = clazz.getConstructor().newInstance();
          Method newFileSystem = clazz.getDeclaredMethod("newFileSystem", URI.class, Map.class);

          fileSystem = newFileSystem.invoke(fileSystemProvider, url, env);
        } catch (final MalformedURLException | NoSuchMethodException | InstantiationException | InvocationTargetException | ClassNotFoundException |
          IllegalAccessException ex) {

          DecompilerContext.getLogger().writeMessage("Cannot load the newer runtime using the jrt-fs.jar from disk", ex);
        }

        if (fileSystem instanceof FileSystem) {
          FileSystem fs = (FileSystem) fileSystem;
          ctx.addSpace(new JavaRuntimeContextSource(javaHome.getAbsolutePath(), fs), false);
        } else {
          DecompilerContext.getLogger().writeMessage("Cannot cast to FileSystem", IFernflowerLogger.Severity.ERROR);
        }
      }
      return;
    } else if (javaHome.exists()) {
      // legacy runtime, add all jars from the lib and jre/lib folders
      boolean anyAdded = false;
      final List<File> jrt = new ArrayList<>();
      Collections.addAll(jrt, new File(javaHome, "jre/lib").listFiles());
      Collections.addAll(jrt, new File(javaHome, "lib").listFiles());
      for (final File lib : jrt) {
        if (lib.isFile() && lib.getName().endsWith(".jar")) {
          ctx.addSpace(lib, false);
          anyAdded = true;
        }
      }
      if (anyAdded) return;
    }

    // does not exist
    DecompilerContext.getLogger().writeMessage("Unable to detect a java runtime at " + javaHome, IFernflowerLogger.Severity.ERROR);
  }

  static final class JavaRuntimeModuleContextSource extends ModuleBasedContextSource {
    private Path module;

    JavaRuntimeModuleContextSource(final String nameAndVersion, final Path moduleRoot) {
      super(nameAndVersion);
      this.module = moduleRoot;
    }

    @Override
    public InputStream getInputStream(String resource) throws IOException {
      return Files.newInputStream(this.module.resolve(resource));
    }

    @Override
    protected Stream<String> entryNames() throws IOException {
      try (final Stream<Path> dir = Files.walk(this.module)) {
        return dir.map(it -> this.module.relativize(it).toString()).collect(Collectors.toList()).stream();
      }
    }
  }

  static final class JavaRuntimeContextSource implements IContextSource, AutoCloseable {
    private final String identifier;
    private final FileSystem jrtFileSystem;

    public JavaRuntimeContextSource(final String identifier, final FileSystem jrtFileSystem) {
      this.identifier = identifier;
      this.jrtFileSystem = jrtFileSystem;
    }

    @Override
    public String getName() {
      return "Java runtime " + this.identifier;
    }

    @Override
    public Entries getEntries() {
      // One child source for every module in the runtime
      final List<IContextSource> children = new ArrayList<>();
      try {
      final List<Path> modules = Files.list(this.jrtFileSystem.getPath("modules")).collect(Collectors.toList());
      for (final Path module : modules) {
        String nameAndVersion;
        try (final InputStream is = Files.newInputStream(module.resolve("module-info.class"))) {
          StructClass clazz = StructClass.create(new DataInputFullStream(JInputStream.readAllBytes(is)), false);
          StructModuleAttribute moduleAttr = clazz.getAttribute(StructGeneralAttribute.ATTRIBUTE_MODULE);
          if (moduleAttr == null) continue;

          nameAndVersion = moduleAttr.toNameAndVersion();
        } catch (final IOException ex) {
          continue;
        }
        children.add(new JavaRuntimeModuleContextSource(nameAndVersion, module));
      }

        return new Entries(JList.of(), JList.of(), JList.of(), children);
      } catch (final IOException ex) {
        DecompilerContext.getLogger().writeMessage("Failed to read modules from runtime " + this.identifier, ex);
        return Entries.EMPTY;
      }
    }

    @Override
    public InputStream getInputStream(String resource) throws IOException {
      return null; // all resources are part of a child provider
    }

    @Override
    public void close() throws IOException {
      this.jrtFileSystem.close();
    }
  }
}
