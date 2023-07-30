package org.jetbrains.java.decompiler.util.future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JList {
  public static <T> List<T> of() {
    return Collections.emptyList();
  }

  public static <T> List<T> of(T element) {
    return Collections.singletonList(element);
  }

  public static <T> List<T> of(T first, T... elements) {
    List<T> list = new ArrayList<>(1 + elements.length);
    list.add(first);
    Collections.addAll(list, elements);
    return list;
  }

  public static <T> List<T> copyOf(Collection<T> collection) {
    return Collections.unmodifiableList(new ArrayList<>(collection));
  }
}
