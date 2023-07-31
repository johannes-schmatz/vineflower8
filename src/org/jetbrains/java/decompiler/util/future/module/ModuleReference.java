package org.jetbrains.java.decompiler.util.future.module;

import java.lang.invoke.MethodHandle;

public class ModuleReference {
  private final Class<?> moduleReferenceClass = ImplHelper.getClass("java.lang.module.ModuleReference");
  private final MethodHandle descriptor = ImplHelper.getHandle(
    moduleReferenceClass,
    "descriptor",
    "()Ljava/lang/module/ModuleDescriptor;"
  );
  private final MethodHandle open = ImplHelper.getHandle(
    moduleReferenceClass,
    "open",
    "()Ljava/lang/module/ModuleReader;"
  );
  private final Object moduleReference;

  public ModuleReference(Object moduleReference) {
    this.moduleReference = moduleReference;
  }

  public ModuleDescriptor descriptor() {
    try {
      Object moduleDescriptor = descriptor.invoke(moduleReference);
      return new ModuleDescriptor(moduleDescriptor);
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }

  public ModuleReader open() {
    try {
      Object moduleReader = open.invoke(moduleReference);
      return new ModuleReader(moduleReader);
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }
}
