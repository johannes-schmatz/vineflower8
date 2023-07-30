package org.jetbrains.java.decompiler.util.future;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JInputStream {
  private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
  private static final int DEFAULT_BUFFER_SIZE = 16384;
  public static byte[] readAllBytes(InputStream is) throws IOException {
    return readNBytes(is, Integer.MAX_VALUE);
  }
  public static byte[] readNBytes(InputStream is, int len) throws IOException {
    if (len < 0) {
      throw new IllegalArgumentException("len < 0");
    }

    List<byte[]> bufs = null;
    byte[] result = null;
    int total = 0;
    int remaining = len;
    int n;
    do {
      byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
      int nread = 0;

      // read to EOF which may read more or less than buffer size
      while ((n = is.read(buf, nread,
        Math.min(buf.length - nread, remaining))) > 0) {
        nread += n;
        remaining -= n;
      }

      if (nread > 0) {
        if (MAX_BUFFER_SIZE - total < nread) {
          throw new OutOfMemoryError("Required array size too large");
        }
        if (nread < buf.length) {
          buf = Arrays.copyOfRange(buf, 0, nread);
        }
        total += nread;
        if (result == null) {
          result = buf;
        } else {
          if (bufs == null) {
            bufs = new ArrayList<>();
            bufs.add(result);
          }
          bufs.add(buf);
        }
      }
      // if the last call to read returned -1 or the number of bytes
      // requested have been read then break
    } while (n >= 0 && remaining > 0);

    if (bufs == null) {
      if (result == null) {
        return new byte[0];
      }
      return result.length == total ?
        result : Arrays.copyOf(result, total);
    }

    result = new byte[total];
    int offset = 0;
    remaining = total;
    for (byte[] b : bufs) {
      int count = Math.min(b.length, remaining);
      System.arraycopy(b, 0, result, offset, count);
      offset += count;
      remaining -= count;
    }

    return result;
  }

  public static long transferTo(InputStream is, OutputStream out) throws IOException {
    Objects.requireNonNull(out, "out");
    long transferred = 0;
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int read;
    while ((read = is.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
      out.write(buffer, 0, read);
      if (transferred < Long.MAX_VALUE) {
        try {
          transferred = Math.addExact(transferred, read);
        } catch (ArithmeticException ignore) {
          transferred = Long.MAX_VALUE;
        }
      }
    }
    return transferred;
  }
}
