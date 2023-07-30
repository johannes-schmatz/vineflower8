package org.jetbrains.java.decompiler.util.future;

import java.util.Collections;
import java.util.Map;

public class JMap {
  public static <K, V> Map<K, V> of() {
    return Collections.emptyMap();
  }

  public static <K, V> Map<K, V> of(K key, V value) {
    return Collections.singletonMap(key, value);
  }
}
