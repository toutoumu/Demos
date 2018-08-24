package com.example.demo.testcase.log;

import android.text.TextUtils;
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

/**
 * @see TextUtils
 */
public final class Strings {
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
  /**
   * <p>The maximum size to which the padding constant(s) can expand.</p>
   */
  private static final int PAD_LIMIT = 8192;

  private Strings() {

  }

  /**
   * Like join, but allows for a distinct final delimiter. For english sentences such as
   * "Alice, Bob and Charlie" use ", " and " and " as the delimiters.
   *
   * @param delimiter usually ", "
   * @param lastDelimiter usually " and "
   * @param objs the objects
   * @param <T> the type
   * @return a string
   */
  public static <T> String joinAnd(final String delimiter, final String lastDelimiter,
      final Collection<T> objs) {
    if (objs == null || objs.isEmpty()) {
      return "";
    }

    final Iterator<T> iter = objs.iterator();
    final StringBuilder buffer = new StringBuilder(Strings.toString(iter.next()));
    int i = 1;
    while (iter.hasNext()) {
      final T obj = iter.next();
      if (notEmpty(obj)) {
        buffer.append(++i == objs.size() ? lastDelimiter : delimiter).append(Strings.toString(obj));
      }
    }
    return buffer.toString();
  }

  public static <T> String joinAnd(final String delimiter, final String lastDelimiter,
      final T... objs) {
    return joinAnd(delimiter, lastDelimiter, Arrays.asList(objs));
  }

  public static <T> String join(final String delimiter, final Collection<T> objs) {
    if (objs == null || objs.isEmpty()) {
      return "";
    }

    final Iterator<T> iter = objs.iterator();
    final StringBuilder buffer = new StringBuilder(Strings.toString(iter.next()));

    while (iter.hasNext()) {
      final T obj = iter.next();
      if (notEmpty(obj)) {
        buffer.append(delimiter).append(Strings.toString(obj));
      }
    }
    return buffer.toString();
  }

  public static <T> String join(final String delimiter, final T... objects) {
    return join(delimiter, Arrays.asList(objects));
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
    return count > Integer.MAX_VALUE ? -1 : (int) count;
  }

  public static long copyLarge(Reader input, Writer output) {
    try {
      char[] buffer = new char[DEFAULT_BUFFER_SIZE];
      long count = 0;
      int n;
      while (-1 != (n = input.read(buffer))) {
        output.write(buffer, 0, n);
        count += n;
      }
      return count;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String toString(final Object o) {
    return toString(o, "");
  }

  public static String toString(final Object o, final String def) {
    return o == null ? def : o instanceof InputStream ? toString((InputStream) o)
        : o instanceof Reader ? toString((Reader) o)
            : o instanceof Object[] ? Strings.join(", ", (Object[]) o)
                : o instanceof Collection ? Strings.join(", ", (Collection<?>) o) : o.toString();
  }

  public static boolean isEmpty(final Object o) {
    return toString(o).trim().length() == 0;
  }

  public static boolean notEmpty(final Object o) {
    return toString(o).trim().length() != 0;
  }

  public static String md5(String s) {
    // http://stackoverflow.com/questions/1057041/difference-between-java-and-php5-md5-hash
    // http://code.google.com/p/roboguice/issues/detail?id=89
    try {

      final byte[] hash = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"));
      final StringBuilder hashString = new StringBuilder();

      for (byte aHash : hash) {
        String hex = Integer.toHexString(aHash);

        if (hex.length() == 1) {
          hashString.append('0');
          hashString.append(hex.charAt(hex.length() - 1));
        } else {
          hashString.append(hex.substring(hex.length() - 2));
        }
      }

      return hashString.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String capitalize(String s) {
    final String c = Strings.toString(s);
    return c.length() >= 2 ? c.substring(0, 1).toUpperCase() + c.substring(1)
        : c.length() >= 1 ? c.toUpperCase() : c;
  }

  public static boolean equals(Object a, Object b) {
    return Strings.toString(a).equals(Strings.toString(b));
  }

  public static boolean equalsIgnoreCase(Object a, Object b) {
    return Strings.toString(a).toLowerCase().equals(Strings.toString(b).toLowerCase());
  }

  public static String[] chunk(String str, int chunkSize) {
    if (isEmpty(str) || chunkSize == 0) {
      return new String[0];
    }

    final int len = str.length();
    final int arrayLen = (len - 1) / chunkSize + 1;
    final String[] array = new String[arrayLen];
    for (int i = 0; i < arrayLen; ++i) {
      array[i] = str.substring(i * chunkSize,
          i * chunkSize + chunkSize < len ? i * chunkSize + chunkSize : len);
    }

    return array;
  }

  public static String namedFormat(String str, Map<String, String> substitutions) {
    for (Map.Entry<String, String> entry : substitutions.entrySet()) {
      str = str.replace('$' + entry.getKey(), entry.getValue());
    }

    return str;
  }

  public static String namedFormat(String str, Object... nameValuePairs) {
    if (nameValuePairs.length % 2 != 0) {
      throw new InvalidParameterException("You must include one value for each parameter");
    }

    final HashMap<String, String> map = new HashMap<String, String>(nameValuePairs.length / 2);
    for (int i = 0; i < nameValuePairs.length; i += 2) {
      map.put(Strings.toString(nameValuePairs[i]), Strings.toString(nameValuePairs[i + 1]));
    }

    return namedFormat(str, map);
  }

  /**
   * <p>Right pad a String with spaces (' ').</p>
   *
   * <p>The String is padded to the size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.rightPad(null, *)   = null
   * StringUtils.rightPad("", 3)     = "   "
   * StringUtils.rightPad("bat", 3)  = "bat"
   * StringUtils.rightPad("bat", 5)  = "bat  "
   * StringUtils.rightPad("bat", 1)  = "bat"
   * StringUtils.rightPad("bat", -1) = "bat"
   * </pre>
   *
   * @param str the String to pad out, may be null
   * @param size the size to pad to
   * @return right padded String or original String if no padding is necessary,
   * <code>null</code> if null String input
   */
  public static String rightPad(String str, int size) {
    return rightPad(str, size, ' ');
  }

  /**
   * <p>Right pad a String with a specified character.</p>
   *
   * <p>The String is padded to the size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.rightPad(null, *, *)     = null
   * StringUtils.rightPad("", 3, 'z')     = "zzz"
   * StringUtils.rightPad("bat", 3, 'z')  = "bat"
   * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
   * StringUtils.rightPad("bat", 1, 'z')  = "bat"
   * StringUtils.rightPad("bat", -1, 'z') = "bat"
   * </pre>
   *
   * @param str the String to pad out, may be null
   * @param size the size to pad to
   * @param padChar the character to pad with
   * @return right padded String or original String if no padding is necessary,
   * <code>null</code> if null String input
   * @since 2.0
   */
  public static String rightPad(String str, int size, char padChar) {
    if (str == null) {
      return null;
    }
    int pads = size - str.length();
    if (pads <= 0) {
      return str; // returns original String when possible
    }
    if (pads > PAD_LIMIT) {
      return rightPad(str, size, String.valueOf(padChar));
    }
    return str.concat(padding(pads, padChar));
  }

  /**
   * <p>Right pad a String with a specified String.</p>
   *
   * <p>The String is padded to the size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.rightPad(null, *, *)      = null
   * StringUtils.rightPad("", 3, "z")      = "zzz"
   * StringUtils.rightPad("bat", 3, "yz")  = "bat"
   * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
   * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
   * StringUtils.rightPad("bat", 1, "yz")  = "bat"
   * StringUtils.rightPad("bat", -1, "yz") = "bat"
   * StringUtils.rightPad("bat", 5, null)  = "bat  "
   * StringUtils.rightPad("bat", 5, "")    = "bat  "
   * </pre>
   *
   * @param str the String to pad out, may be null
   * @param size the size to pad to
   * @param padStr the String to pad with, null or empty treated as single space
   * @return right padded String or original String if no padding is necessary,
   * <code>null</code> if null String input
   */
  public static String rightPad(String str, int size, String padStr) {
    if (str == null) {
      return null;
    }
    if (isEmpty(padStr)) {
      padStr = " ";
    }
    int padLen = padStr.length();
    int strLen = str.length();
    int pads = size - strLen;
    if (pads <= 0) {
      return str; // returns original String when possible
    }
    if (padLen == 1 && pads <= PAD_LIMIT) {
      return rightPad(str, size, padStr.charAt(0));
    }

    if (pads == padLen) {
      return str.concat(padStr);
    } else if (pads < padLen) {
      return str.concat(padStr.substring(0, pads));
    } else {
      char[] padding = new char[pads];
      char[] padChars = padStr.toCharArray();
      for (int i = 0; i < pads; i++) {
        padding[i] = padChars[i % padLen];
      }
      return str.concat(new String(padding));
    }
  }

  /**
   * <p>Left pad a String with spaces (' ').</p>
   *
   * <p>The String is padded to the size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.leftPad(null, *)   = null
   * StringUtils.leftPad("", 3)     = "   "
   * StringUtils.leftPad("bat", 3)  = "bat"
   * StringUtils.leftPad("bat", 5)  = "  bat"
   * StringUtils.leftPad("bat", 1)  = "bat"
   * StringUtils.leftPad("bat", -1) = "bat"
   * </pre>
   *
   * @param str the String to pad out, may be null
   * @param size the size to pad to
   * @return left padded String or original String if no padding is necessary,
   * <code>null</code> if null String input
   */
  public static String leftPad(String str, int size) {
    return leftPad(str, size, ' ');
  }

  /**
   * <p>Left pad a String with a specified character.</p>
   *
   * <p>Pad to a size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.leftPad(null, *, *)     = null
   * StringUtils.leftPad("", 3, 'z')     = "zzz"
   * StringUtils.leftPad("bat", 3, 'z')  = "bat"
   * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
   * StringUtils.leftPad("bat", 1, 'z')  = "bat"
   * StringUtils.leftPad("bat", -1, 'z') = "bat"
   * </pre>
   *
   * @param str the String to pad out, may be null
   * @param size the size to pad to
   * @param padChar the character to pad with
   * @return left padded String or original String if no padding is necessary,
   * <code>null</code> if null String input
   * @since 2.0
   */
  public static String leftPad(String str, int size, char padChar) {
    if (str == null) {
      return null;
    }
    int pads = size - str.length();
    if (pads <= 0) {
      return str; // returns original String when possible
    }
    if (pads > PAD_LIMIT) {
      return leftPad(str, size, String.valueOf(padChar));
    }
    return padding(pads, padChar).concat(str);
  }

  /**
   * <p>Left pad a String with a specified String.</p>
   *
   * <p>Pad to a size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.leftPad(null, *, *)      = null
   * StringUtils.leftPad("", 3, "z")      = "zzz"
   * StringUtils.leftPad("bat", 3, "yz")  = "bat"
   * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
   * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
   * StringUtils.leftPad("bat", 1, "yz")  = "bat"
   * StringUtils.leftPad("bat", -1, "yz") = "bat"
   * StringUtils.leftPad("bat", 5, null)  = "  bat"
   * StringUtils.leftPad("bat", 5, "")    = "  bat"
   * </pre>
   *
   * @param str the String to pad out, may be null
   * @param size the size to pad to
   * @param padStr the String to pad with, null or empty treated as single space
   * @return left padded String or original String if no padding is necessary,
   * <code>null</code> if null String input
   */
  public static String leftPad(String str, int size, String padStr) {
    if (str == null) {
      return null;
    }
    if (isEmpty(padStr)) {
      padStr = " ";
    }
    int padLen = padStr.length();
    int strLen = str.length();
    int pads = size - strLen;
    if (pads <= 0) {
      return str; // returns original String when possible
    }
    if (padLen == 1 && pads <= PAD_LIMIT) {
      return leftPad(str, size, padStr.charAt(0));
    }

    if (pads == padLen) {
      return padStr.concat(str);
    } else if (pads < padLen) {
      return padStr.substring(0, pads).concat(str);
    } else {
      char[] padding = new char[pads];
      char[] padChars = padStr.toCharArray();
      for (int i = 0; i < pads; i++) {
        padding[i] = padChars[i % padLen];
      }
      return new String(padding).concat(str);
    }
  }

  private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
    if (repeat < 0) {
      throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
    }
    final char[] buf = new char[repeat];
    for (int i = 0; i < buf.length; i++) {
      buf[i] = padChar;
    }
    return new String(buf);
  }
}
