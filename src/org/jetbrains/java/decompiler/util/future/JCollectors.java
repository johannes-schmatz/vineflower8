package org.jetbrains.java.decompiler.util.future;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JCollectors {
  private static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

  public static <T, V>
  Collector<T, ?, List<T>> toUnmodifiableList() {
    return new Collector<T, ArrayList<T>, List<T>>() {
      @Override
      public Supplier<ArrayList<T>> supplier() {
        return ArrayList::new;
      }
      @Override
      public BiConsumer<ArrayList<T>, T> accumulator() {
        return List::add;
      }
      @Override
      public BinaryOperator<ArrayList<T>> combiner() {
        return (left, right) -> { left.addAll(right); return left; };
      }
      @Override
      public Function<ArrayList<T>, List<T>> finisher() {
        return list -> list;
      }
      @Override
      public Set<Characteristics> characteristics() {
        return CH_NOID;
      }
    };
  }
}
