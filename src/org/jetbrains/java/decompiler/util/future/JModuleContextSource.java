package org.jetbrains.java.decompiler.util.future;

import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.struct.StructContext;
import org.jetbrains.java.decompiler.util.ModuleBasedContextSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JModuleContextSource {
  private static MethodHandle getStaticHandle(String className, String name, String descriptor) {
    try {
      Class<?> clazz = Class.forName(className);
      MethodType type = MethodType.fromMethodDescriptorString(descriptor, clazz.getClassLoader());
      return MethodHandles.publicLookup().findStatic(clazz, name, type);
    } catch (ClassNotFoundException t) {
      return null;
    } catch (Throwable t) {
      t.printStackTrace();
      throw sneak(t);
    }
  }

  private static MethodHandle getHandle(String className, String name, String descriptor) {
    try {
      Class<?> clazz = Class.forName(className);
      MethodType type = MethodType.fromMethodDescriptorString(descriptor, clazz.getClassLoader());
      return MethodHandles.publicLookup().findVirtual(clazz, name, type);
    } catch (ClassNotFoundException t) {
      return null;
    } catch (Throwable t) {
      t.printStackTrace();
      throw sneak(t);
    }
  }

  private static RuntimeException sneak(Throwable t) {
    throw new RuntimeException("Java version: " + System.getProperty("java.version"), t);
  }

  private static final MethodHandle moduleFinder_ofSystem = getStaticHandle("java.lang.module.ModuleFinder", "ofSystem", "()Ljava/lang/module/ModuleFinder;");
  private static final MethodHandle moduleFinder_findAll = getHandle("java.lang.module.ModuleFinder", "findAll", "()Ljava/util/Set;");
  private static final MethodHandle moduleReference_descriptor = getHandle("java.lang.module.ModuleReference", "descriptor", "()Ljava/lang/module/ModuleDescriptor;");
  private static final MethodHandle moduleReference_open = getHandle("java.lang.module.ModuleReference", "open", "()Ljava/lang/module/ModuleReader;");
  private static final MethodHandle moduleDescriptor_toNameAndVersion = getHandle("java.lang.module.ModuleDescriptor", "toNameAndVersion", "()Ljava/lang/String;");
  private static final MethodHandle moduleReader_list = getHandle("java.lang.module.ModuleReader", "list", "()Ljava/util/stream/Stream;");
  private static final MethodHandle moduleReader_open = getHandle("java.lang.module.ModuleReader", "open", "(Ljava/lang/String;)Ljava/util/Optional;");
  private static final MethodHandle moduleReader_close = getHandle("java.lang.module.ModuleReader", "close", "()V");

  // In Java 9+: java.lang.module.ModuleFinder.ofSystem().findAll().forEach(action)
  private static void ModuleFinder_ofSystem_findAll_forEach(Consumer<Object> action) {
    try {
      Object moduleFinder = moduleFinder_ofSystem.invoke();
      Object set = moduleFinder_findAll.invoke(moduleFinder);

      @SuppressWarnings("unchecked")
      Set<Object> set1 = (Set<Object>) set;

      // box it in
      for (Object moduleReference: set1) {
        action.accept(moduleReference);
      }
    } catch (Throwable t) {
      throw sneak(t);
    }
  }

  private static String ModuleReference_descriptor_toNameAndVersion(Object moduleReference) {
    try {
      Object descriptor = moduleReference_descriptor.invoke(moduleReference);
      Object nameAndVersion = moduleDescriptor_toNameAndVersion.invoke(descriptor);

      @SuppressWarnings("unchecked")
      String nameAndVersion1 = (String) nameAndVersion;

      return nameAndVersion1;
    } catch (Throwable t) {
      throw sneak(t);
    }
  }
  private static Object ModuleReference_open(Object moduleReference) throws IOException {
    try {
      return moduleReference_open.invoke(moduleReference);
    } catch (IOException e) {
      throw e;
    } catch (Throwable t) {
      throw sneak(t);
    }
  }

  private static Stream<String> ModuleReader_list(Object moduleReader) throws IOException {
    try {
      Object stream = moduleReader_list.invoke(moduleReader);

      @SuppressWarnings("unchecked")
      Stream<String> stream1 = (Stream<String>) stream;

      return stream1;
    } catch (IOException e) {
      throw e;
    } catch (Throwable t) {
      throw sneak(t);
    }
  }
  private static Optional<InputStream> ModuleReader_open(Object moduleReader, String resource) throws IOException {
    try {
      Object optional = moduleReader_open.invoke(moduleReader, resource);

      @SuppressWarnings("unchecked")
      Optional<InputStream> optional1 = (Optional<InputStream>) optional;

      return optional1;
    } catch (IOException e) {
      throw e;
    } catch (Throwable t) {
      throw sneak(t);
    }
  }
  private static void ModuleReader_close(Object moduleReader) {
    try {
      moduleReader_close.invoke(moduleReader);
    } catch (Throwable t) {
      throw sneak(t);
    }
  }

  public static void addAllModulePathIfExists(StructContext ctx) {
    // this is true for running on Java 9+
    if (moduleFinder_ofSystem != null) {
      addAllSystemModules(ctx);
    }
    // on Java 8: no need to add anything.
  }

  private static void addAllSystemModules(StructContext ctx) {
    ModuleFinder_ofSystem_findAll_forEach(module -> {
      String nameAndVersion = ModuleReference_descriptor_toNameAndVersion(module);
      try {
        Object reader = ModuleReference_open(module);
        ctx.addSpace(new ModuleContextSource(reader, nameAndVersion), false);
      } catch (IOException e) {
        DecompilerContext.getLogger().writeMessage("Error loading module " + nameAndVersion, e);
      }
    });
  }


  private static class ModuleContextSource extends ModuleBasedContextSource implements AutoCloseable {
    private final Object reader;

    public ModuleContextSource(final Object reader, final String nameAndVersion) {
      super(nameAndVersion);
      this.reader = reader;
    }

    @Override
    public Stream<String> entryNames() throws IOException {
      return ModuleReader_list(reader);
    }

    @Override
    public InputStream getInputStream(String resource) throws IOException {
      return ModuleReader_open(reader, resource).orElse(null);
    }

    @Override
    public void close() throws Exception {
      ModuleReader_close(reader);
    }
  }
}
