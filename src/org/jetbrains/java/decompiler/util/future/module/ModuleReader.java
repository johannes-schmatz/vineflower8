package org.jetbrains.java.decompiler.util.future.module;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.util.Optional;
import java.util.stream.Stream;

public class ModuleReader {
  private static final Class<?> moduleReaderClass = ImplHelper.getClass("java.lang.module.ModuleReader");
  private static final MethodHandle list = ImplHelper.getHandle(
    moduleReaderClass,
    "list",
    "()Ljava/util/stream/Stream;"
  );
  private static final MethodHandle open = ImplHelper.getHandle(
    moduleReaderClass,
    "open",
    "(Ljava/lang/String;)Ljava/util/Optional;"
  );
  private static final MethodHandle close = ImplHelper.getHandle(
    moduleReaderClass,
    "close",
    "()V"
  );
  private final Object moduleReader;

  public ModuleReader(Object moduleReader) {
    this.moduleReader = moduleReader;
  }

  public Stream<String> list() {
    try {
      Object stream = list.invoke(moduleReader);

      @SuppressWarnings("unchecked")
      Stream<String> stream1 = (Stream<String>) stream;

      return stream1;
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }

  public Optional<InputStream> open(String resource) {
    try {
      Object optional = open.invoke(moduleReader, resource);

      @SuppressWarnings("unchecked")
      Optional<InputStream> optional1 = (Optional<InputStream>) optional;

      return optional1;
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }

  public void close() {
    try {
      close.invoke(moduleReader);
    } catch (Throwable t) {
      throw ImplHelper.sneak(t);
    }
  }
}
