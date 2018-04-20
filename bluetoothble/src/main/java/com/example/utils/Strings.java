//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class Strings {
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  private static final int PAD_LIMIT = 8192;

  private Strings() {
  }

  public static <T> String joinAnd(String delimiter, String lastDelimiter, Collection<T> objs) {
    if (objs != null && !objs.isEmpty()) {
      Iterator<T> iter = objs.iterator();
      StringBuilder buffer = new StringBuilder(toString(iter.next()));
      int i = 1;

      while (iter.hasNext()) {
        T obj = iter.next();
        if (notEmpty(obj)) {
          ++i;
          buffer.append(i == objs.size() ? lastDelimiter : delimiter).append(toString(obj));
        }
      }

      return buffer.toString();
    } else {
      return "";
    }
  }

  public static <T> String joinAnd(String delimiter, String lastDelimiter, T... objs) {
    return joinAnd(delimiter, lastDelimiter, (Collection) Arrays.asList(objs));
  }

  public static <T> String join(String delimiter, Collection<T> objs) {
    if (objs != null && !objs.isEmpty()) {
      Iterator<T> iter = objs.iterator();
      StringBuilder buffer = new StringBuilder(toString(iter.next()));

      while (iter.hasNext()) {
        T obj = iter.next();
        if (notEmpty(obj)) {
          buffer.append(delimiter).append(toString(obj));
        }
      }

      return buffer.toString();
    } else {
      return "";
    }
  }

  public static <T> String join(String delimiter, T... objects) {
    return join(delimiter, (Collection) Arrays.asList(objects));
  }

  public static String toString(InputStream input) {
    StringWriter sw = new StringWriter();
    copy(new InputStreamReader(input, Charset.forName("UTF-8")), sw);
    return sw.toString();
  }

  public static String toString(Reader input) {
    StringWriter sw = new StringWriter();
    copy(input, sw);
    return sw.toString();
  }

  public static int copy(Reader input, Writer output) {
    long count = copyLarge(input, output);
    return count > 2147483647L ? -1 : (int) count;
  }

  public static long copyLarge(Reader input, Writer output) {
    try {
      char[] buffer = new char[4096];

      long count;
      int n;
      for (count = 0L; -1 != (n = input.read(buffer)); count += (long) n) {
        output.write(buffer, 0, n);
      }

      return count;
    } catch (IOException var6) {
      throw new RuntimeException(var6);
    }
  }

  public static String toString(Object o) {
    return toString(o, "");
  }

  public static String toString(Object o, String def) {
    return o == null ? def : (o instanceof InputStream ? toString((InputStream) o)
        : (o instanceof Reader ? toString((Reader) o)
            : (o instanceof Object[] ? join(", ", (Object[]) ((Object[]) o))
                : (o instanceof Collection ? join(", ", (Collection) o) : o.toString()))));
  }

  public static boolean isEmpty(Object o) {
    return toString(o).trim().length() == 0;
  }

  public static boolean notEmpty(Object o) {
    return toString(o).trim().length() != 0;
  }

  public static String md5(String s) {
    try {
      byte[] hash = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"));
      StringBuilder hashString = new StringBuilder();
      byte[] var3 = hash;
      int var4 = hash.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        byte aHash = var3[var5];
        String hex = Integer.toHexString(aHash);
        if (hex.length() == 1) {
          hashString.append('0');
          hashString.append(hex.charAt(hex.length() - 1));
        } else {
          hashString.append(hex.substring(hex.length() - 2));
        }
      }

      return hashString.toString();
    } catch (Exception var8) {
      throw new RuntimeException(var8);
    }
  }

  public static String capitalize(String s) {
    String c = toString((Object) s);
    return c.length() >= 2 ? c.substring(0, 1).toUpperCase() + c.substring(1)
        : (c.length() >= 1 ? c.toUpperCase() : c);
  }

  public static boolean equals(Object a, Object b) {
    return toString(a).equals(toString(b));
  }

  public static boolean equalsIgnoreCase(Object a, Object b) {
    return toString(a).toLowerCase().equals(toString(b).toLowerCase());
  }

  public static String[] chunk(String str, int chunkSize) {
    if (!isEmpty(str) && chunkSize != 0) {
      int len = str.length();
      int arrayLen = (len - 1) / chunkSize + 1;
      String[] array = new String[arrayLen];

      for (int i = 0; i < arrayLen; ++i) {
        array[i] = str.substring(i * chunkSize,
            i * chunkSize + chunkSize < len ? i * chunkSize + chunkSize : len);
      }

      return array;
    } else {
      return new String[0];
    }
  }

  public static String namedFormat(String str, Map<String, String> substitutions) {
    Entry entry;
    for (Iterator var2 = substitutions.entrySet().iterator(); var2.hasNext();
        str = str.replace('$' + (String) entry.getKey(), (CharSequence) entry.getValue())) {
      entry = (Entry) var2.next();
    }

    return str;
  }

  public static String namedFormat(String str, Object... nameValuePairs) {
    if (nameValuePairs.length % 2 != 0) {
      throw new InvalidParameterException("You must include one value for each parameter");
    } else {
      HashMap<String, String> map = new HashMap(nameValuePairs.length / 2);

      for (int i = 0; i < nameValuePairs.length; i += 2) {
        map.put(toString(nameValuePairs[i]), toString(nameValuePairs[i + 1]));
      }

      return namedFormat(str, (Map) map);
    }
  }

  public static String rightPad(String str, int size) {
    return rightPad(str, size, ' ');
  }

  public static String rightPad(String str, int size, char padChar) {
    if (str == null) {
      return null;
    } else {
      int pads = size - str.length();
      return pads <= 0 ? str : (pads > 8192 ? rightPad(str, size, String.valueOf(padChar))
          : str.concat(padding(pads, padChar)));
    }
  }

  public static String rightPad(String str, int size, String padStr) {
    if (str == null) {
      return null;
    } else {
      if (isEmpty(padStr)) {
        padStr = " ";
      }

      int padLen = padStr.length();
      int strLen = str.length();
      int pads = size - strLen;
      if (pads <= 0) {
        return str;
      } else if (padLen == 1 && pads <= 8192) {
        return rightPad(str, size, padStr.charAt(0));
      } else if (pads == padLen) {
        return str.concat(padStr);
      } else if (pads < padLen) {
        return str.concat(padStr.substring(0, pads));
      } else {
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();

        for (int i = 0; i < pads; ++i) {
          padding[i] = padChars[i % padLen];
        }

        return str.concat(new String(padding));
      }
    }
  }

  public static String leftPad(String str, int size) {
    return leftPad(str, size, ' ');
  }

  public static String leftPad(String str, int size, char padChar) {
    if (str == null) {
      return null;
    } else {
      int pads = size - str.length();
      return pads <= 0 ? str : (pads > 8192 ? leftPad(str, size, String.valueOf(padChar))
          : padding(pads, padChar).concat(str));
    }
  }

  public static String leftPad(String str, int size, String padStr) {
    if (str == null) {
      return null;
    } else {
      if (isEmpty(padStr)) {
        padStr = " ";
      }

      int padLen = padStr.length();
      int strLen = str.length();
      int pads = size - strLen;
      if (pads <= 0) {
        return str;
      } else if (padLen == 1 && pads <= 8192) {
        return leftPad(str, size, padStr.charAt(0));
      } else if (pads == padLen) {
        return padStr.concat(str);
      } else if (pads < padLen) {
        return padStr.substring(0, pads).concat(str);
      } else {
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();

        for (int i = 0; i < pads; ++i) {
          padding[i] = padChars[i % padLen];
        }

        return (new String(padding)).concat(str);
      }
    }
  }

  private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
    if (repeat < 0) {
      throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
    } else {
      char[] buf = new char[repeat];

      for (int i = 0; i < buf.length; ++i) {
        buf[i] = padChar;
      }

      return new String(buf);
    }
  }
}
