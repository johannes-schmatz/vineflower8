package org.jetbrains.java.decompiler.util.future.module;

import java.lang.invoke.MethodHandle;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleFinder {
  private static final Class<?> moduleFinderClass = ImplHelper.getClass("java.lang.module.ModuleFinder");
  private static final MethodHandle ofSystem = ImplHelper.getStaticHandle(
    moduleFinderClass,
    "ofSystem",
    "()Ljava/lang/module/ModuleFinder;"
  );
  private static final MethodHandle findAll = ImplHelper.getHandle(
    moduleFinderClass,
    "findAll",
    "()Ljava/util/Set;"
  );

  public static boolean exists() {
    return moduleFinderClass != null;
  }

  public static ModuleFinder ofSystem() {
    try {
      Object moduleFinder = ofSystem.invoke();
      return new ModuleFinder(moduleFinder);
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }

  private final Object moduleFinder;
  public ModuleFinder(Object moduleFinder) {
    this.moduleFinder = moduleFinder;
  }

  public Set<ModuleReference> findAll() {
    try {
      Object set = findAll.invoke(moduleFinder);

      @SuppressWarnings("unchecked")
      Set<Object> set1 = (Set<Object>) set;

      // box it in
      Set<ModuleReference> set2 = new LinkedHashSet<>(set1.size());
      for (Object moduleReference: set1) {
        set2.add(new ModuleReference(moduleReference));
      }
      return set2;
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }
}
