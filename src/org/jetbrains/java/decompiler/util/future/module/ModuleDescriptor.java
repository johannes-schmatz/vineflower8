package org.jetbrains.java.decompiler.util.future.module;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Set;

public class ModuleDescriptor {
  private static final Class<?> moduleDescriptorClass = ImplHelper.getClass("java.lang.module.ModuleDescriptor");
  private static final MethodHandle toNameAndVersion = ImplHelper.getHandle(
    moduleDescriptorClass,
    "toNameAndVersion",
    "()Ljava/lang/String;"
  );
  private static final MethodHandle newModule = ImplHelper.getStaticHandle(
    moduleDescriptorClass,
    "newModule",
    "(Ljava/lang/String;Ljava/util/Set;)Ljava/lang/module/ModuleDescriptor$Builder;"
  );
  private final Object moduleDescriptor;

  public ModuleDescriptor(Object moduleDescriptor) {
    this.moduleDescriptor = moduleDescriptor;
  }

  public String toNameAndVersion() {
    try {
      Object string = toNameAndVersion.invoke(moduleDescriptor);

      @SuppressWarnings("unchecked")
      String string1 = (String) string;

      return string1;
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }


  public static Builder newModule(String moduleName, Set<Modifier> mods) {
    if (true) throw new RuntimeException("NOPE, not needed anymore");
    try {
      Set<?> mods1 = ImplHelper.toImpl(mods);
      Object builder = newModule.invoke(moduleName, mods1);
      return new Builder(builder);
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }

  public static class Builder {
    private static final Class<?> builderClass = ImplHelper.getClass("java.lang.module.ModuleDescriptor$Builder");
    private static final MethodHandle version = ImplHelper.getHandle(
      builderClass,
      "version",
      "(Ljava/lang/String;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle requires = ImplHelper.getHandle(
      builderClass,
      "requires",
      "(Ljava/util/Set;Ljava/lang/String;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle requires1 = ImplHelper.getHandle(
      builderClass,
      "requires",
      "(Ljava/util/Set;Ljava/lang/String;Ljava/lang/module/ModuleDescriptor$Version;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle exports = ImplHelper.getHandle(
      builderClass,
      "exports",
      "(Ljava/util/Set;Ljava/lang/String;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle exports1 = ImplHelper.getHandle(
      builderClass,
      "exports",
      "(Ljava/util/Set;Ljava/lang/String;Ljava/util/Set;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle uses = ImplHelper.getHandle(
      builderClass,
      "uses",
      "(Ljava/lang/String;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle opens = ImplHelper.getHandle(
      builderClass,
      "opens",
      "(Ljava/util/Set;Ljava/lang/String;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle opens1 = ImplHelper.getHandle(
      builderClass,
      "opens",
      "(Ljava/util/Set;Ljava/lang/String;Ljava/util/Set;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle provides = ImplHelper.getHandle(
      builderClass,
      "provides",
      "(Ljava/lang/String;Ljava/util/List;)Ljava/lang/module/ModuleDescriptor$Builder;"
    );
    private static final MethodHandle build = ImplHelper.getHandle(
      builderClass,
      "build",
      "()Ljava/lang/module/ModuleDescriptor;"
    );

    private final Object builder;
    public Builder(Object builder) {
      this.builder = builder;
    }

    public void version(String string) {
      try {
        version.invoke(builder, string);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void requires(Set<Requires.Modifier> mods, String string) {
      try {
        Set<?> mods1 = ImplHelper.toImpl(mods);
        requires.invoke(builder, mods1, string);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void requires(Set<Requires.Modifier> mods, String string, Version version) {
      try {
        Set<?> mods1 = ImplHelper.toImpl(mods);
        Object version1 = version.version;
        requires1.invoke(builder, mods1, string, version1);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void exports(Set<Exports.Modifier> mods, String string) {
      try {
        Set<?> mods1 = ImplHelper.toImpl(mods);
        exports.invoke(builder, mods1, string);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void exports(Set<Exports.Modifier> mods, String string, Set<String> set) {
      try {
        Set<?> mods1 = ImplHelper.toImpl(mods);
        exports1.invoke(builder, mods1, string, set);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void uses(String string) {
      try {
        uses.invoke(builder, string);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void opens(Set<Opens.Modifier> mods, String string) {
      try {
        Set<?> mods1 = ImplHelper.toImpl(mods);
        opens.invoke(builder, mods1, string);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void opens(Set<Opens.Modifier> mods, String string, Set<String> strings) {
      try {
        Set<?> mods1 = ImplHelper.toImpl(mods);
        opens1.invoke(builder, mods1, string, strings);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public void provides(String string, List<String> strings) {
      try {
        provides.invoke(builder, string, strings);
      } catch (Throwable t) {
        ImplHelper.sneak(t);
      }
    }
    public ModuleDescriptor build() {
      try {
        Object moduleDescriptor = build.invoke(builder);
        return new ModuleDescriptor(moduleDescriptor);
      } catch (Throwable t) {
        throw ImplHelper.sneak(t);
      }
    }
  }

  public enum Modifier implements ImplHelper.EnumHelper {
    OPEN("OPEN"), SYNTHETIC("SYNTHETIC"), MANDATED("MANDATED");

    public final Object modifier;
    Modifier(String fieldName) {
      this.modifier = ImplHelper.getEnumVariant("java.lang.module.ModuleDescriptor$Modifier", fieldName);
    }

    @Override
    public Object getInstance() {
      return modifier;
    }
  }

  public static class Requires {
    public enum Modifier implements ImplHelper.EnumHelper {
      TRANSITIVE("TRANSITIVE"), STATIC("STATIC"), SYNTHETIC("SYNTHETIC"), MANDATED("MANDATED");

      public final Object modifier;
      Modifier(String fieldName) {
        this.modifier = ImplHelper.getEnumVariant("java.lang.module.ModuleDescriptor$Requires$Modifier", fieldName);
      }

      @Override
      public Object getInstance() {
        return modifier;
      }
    }
  }

  public static class Version {
    private static final Class<?> versionClass = ImplHelper.getClass("java.lang.module.ModuleDescriptor$Version");
    private static final MethodHandle parse = ImplHelper.getStaticHandle(
      versionClass,
      "parse",
      "(Ljava/lang/String;)Ljava/lang/module/ModuleDescriptor$Version;"
    );

    public final Object version;
    public Version(Object version) {
      this.version = version;
    }

    public static Version parse(String versionString) {
      try {
        Object version = parse.invoke(versionString);

        return new Version(version);
      } catch (Throwable t) {
        throw ImplHelper.sneak(t);
      }
    }
  }

  public static class Exports {
    public enum Modifier implements ImplHelper.EnumHelper {
      SYNTHETIC("SYNTHETIC"), MANDATED("MANDATED");

      public final Object modifier;
      Modifier(String fieldName) {
        this.modifier = ImplHelper.getEnumVariant("java.lang.module.ModuleDescriptor$Exports$Modifier", fieldName);
      }

      @Override
      public Object getInstance() {
        return modifier;
      }
    }
  }

  public static class Opens {
    public enum Modifier implements ImplHelper.EnumHelper {
      SYNTHETIC("SYNTHETIC"), MANDATED("MANDATED");

      public final Object modifier;
      Modifier(String fieldName) {
        this.modifier = ImplHelper.getEnumVariant("java.lang.module.ModuleDescriptor$Opens$Modifier", fieldName);
      }

      @Override
      public Object getInstance() {
        return modifier;
      }
    }
  }
}
