package org.jetbrains.java.decompiler.util.future;

public class JString {
  public static String repeat(String string, int count) {
    if (count < 0) {
      throw new IllegalArgumentException("count is negative: " + count);
    }
    if (count == 1) {
      return string;
    }
    final int len = string.length();
    if (len == 0 || count == 0) {
      return "";
    }

    StringBuilder builder = new StringBuilder(len * count);
    for (int i = 0; i < count; i++) {
      builder.append(string);
    }
    return builder.toString();
  }
}
