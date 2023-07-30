package org.jetbrains.java.decompiler.util.future;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class JSet {
  public static <T> Set<T> copyOf(Collection<T> collection) {
    return Collections.unmodifiableSet(new LinkedHashSet<>(collection));
  }
}
