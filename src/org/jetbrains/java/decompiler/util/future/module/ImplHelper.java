package org.jetbrains.java.decompiler.util.future.module;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

class ImplHelper {
  public static Class<?> getClass(String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException t) {
      return null;
    }
  }

  public static MethodHandle getStaticHandle(Class<?> clazz, String name, String descriptor) {
    if (clazz == null) return null;

    try {
      MethodType type = MethodType.fromMethodDescriptorString(descriptor, clazz.getClassLoader());
      return MethodHandles.publicLookup().findStatic(clazz, name, type);
    } catch (Throwable t) {
      t.printStackTrace();
      throw sneak(t);
    }
  }

  public static MethodHandle getHandle(Class<?> clazz, String name, String descriptor) {
    if (clazz == null) return null;

    try {
      MethodType type = MethodType.fromMethodDescriptorString(descriptor, clazz.getClassLoader());
      return MethodHandles.publicLookup().findVirtual(clazz, name, type);
    } catch (Throwable t) {
      t.printStackTrace();
      throw sneak(t);
    }
  }

  public static Object getEnumVariant(String className, String name) {
    Class<?> clazz = getClass(className);
    if (clazz == null) return null;

    try {
      Field field = clazz.getDeclaredField(name);
      Object instance = field.get(null);

      return instance;
    } catch (Throwable t) {
      throw sneak(t);
    }
  }

  @FunctionalInterface
  public interface EnumHelper {
    Object getInstance();
  }

  public static <E> Set<E> toImpl(Set<? extends EnumHelper> set) {
    Set<E> out = new LinkedHashSet<>();
    for (EnumHelper i : set) {
      @SuppressWarnings("unchecked")
      E e = (E) i.getInstance();
      out.add(e);
    }
    return out;
  }

  public static RuntimeException sneak(Throwable t) {
    throw new RuntimeException("Java version: " + System.getProperty("java.version"), t);
  }
}
