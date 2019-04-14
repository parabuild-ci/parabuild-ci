/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Common string utilities
 */
public final class StringUtils implements CommonConstants {


  private static final char[] HEX_DIGITS = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  private static final char CHAR_SINGLE_QUOTE = '\'';
  private static final char CHAR_DOUBLE_QUOTE = '\"';


  private static final int BASELENGTH = 255;
  private static final int LOOKUPLENGTH = 64;
  private static final int TWENTYFOURBITGROUP = 24;
  private static final int EIGHTBIT = 8;
  private static final int SIXTEENBIT = 16;
  private static final int FOURBYTE = 4;
  private static final int SIGN = -128;
  private static final byte PAD = (byte)'=';
  private static final byte [] base64Alphabet = new byte[BASELENGTH];
  private static final byte [] lookUpBase64Alphabet = new byte[LOOKUPLENGTH];

  private static final byte[] EMPY_BYTE_ARRAY = new byte[0];

  public static final String REGEX_STRICT_NAME = "[a-zA-Z][-a-zA-Z_0-9]*";


  static {
    for (int i = 0; i < BASELENGTH; i++) {
      base64Alphabet[i] = -1;
    }
    for (int i = 'Z'; i >= 'A'; i--) {
      base64Alphabet[i] = (byte)(i - 'A');
    }
    for (int i = 'z'; i >= 'a'; i--) {
      base64Alphabet[i] = (byte)(i - 'a' + 26);
    }
    for (int i = '9'; i >= '0'; i--) {
      base64Alphabet[i] = (byte)(i - '0' + 52);
    }

    base64Alphabet['+'] = 62;
    base64Alphabet['/'] = 63;

    for (int i = 0; i <= 25; i++) {
      lookUpBase64Alphabet[i] = (byte)('A' + i);
    }

    for (int i = 26, j = 0; i <= 51; i++, j++) {
      lookUpBase64Alphabet[i] = (byte)('a' + j);
    }

    for (int i = 52, j = 0; i <= 61; i++, j++) {
      lookUpBase64Alphabet[i] = (byte)('0' + j);
    }

    lookUpBase64Alphabet[62] = (byte)'+';
    lookUpBase64Alphabet[63] = (byte)'/';
  }


  /**
   * Returns true if string is null
   */
  public static boolean isNull(final String string) {
    return string == null;
  }


  /**
   * Returns true if string is blank
   *
   * @throws NullPointerException if string is null
   */
  public static boolean isBlank(final String string) {
    if (isNull(string)) return true;
    final int length = string.length();
    if (length == 0) return true;
    for (int i = 0; i < length; i++) {
      if (string.charAt(i) > ' ') return false;
    }
    return true;
  }


  /**
   * Makes word duration.
   */
  public static StringBuffer durationToString(final long seconds, final boolean fullWords) {
    final StringBuffer result = new StringBuffer(30);
    long secondsLeft = seconds;
    final long durationDays = secondsLeft / 86400L;
    if (durationDays > 0) {
      result.append(Long.toString(durationDays)).append(fullWords ? " days " : "d ");
      secondsLeft %= 86400L;
    }

    final long durationHours = secondsLeft / 3600L;
    if (durationHours > 0) {
      result.append(durationHours < 10 ? "0" : "").append(Long.toString(durationHours)).append(fullWords ? " hours " : "h ");
      secondsLeft %= 3600L;
    }
    if (durationDays > 0) return result;

    final long durationMinutes = secondsLeft / 60L;
    if (durationMinutes > 0) {
      result.append(durationMinutes < 10 ? "0" : "").append(Long.toString(durationMinutes)).append(fullWords ? " minutes " : "m ");
      secondsLeft %= 60L;
    }
    if (durationHours > 0) return result;

    if (secondsLeft < 10) result.append('0');
    return result.append(Long.toString(secondsLeft)).append(fullWords ? " seconds " : "s ");
  }


  /**
   * Cleans up exception from the exception name
   */
  public static String toString(final Throwable e) {
    final String message = e.toString();
    final int i = message.indexOf("ion: ");
    String result = null;
    if (i >= 0) {
      result = message.substring(i + 5).trim();
    } else {
      result = message.trim();
    }
    if (isBlank(result)) {
      result = e.getMessage();
    }
    return result;
  }


  /**
   * Returns true if parameter is a valid string represenation of
   * integer value
   */
  public static boolean isValidInteger(final String s) {
    if (isBlank(s)) return false;
    try {
      Integer.parseInt(s);
      return true;
    } catch (final Exception e) {
      return false;
    }
  }


  /**
   * Validates if the name is a strict name
   */
  public static boolean isValidStrictName(final String name) {
    return Pattern.compile(REGEX_STRICT_NAME).matcher(name).matches();
  }


  /**
   * Extracts a stacktrace from Throwable to a String
   */
  public static String stackTraceToString(final Throwable th) {
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream(1000);
      final PrintStream ps = new PrintStream(baos);
      th.printStackTrace(ps);
      return baos.toString();
    } finally {
      if (baos != null) {
        try {
          baos.flush();
          baos.close();
        } catch (final IOException ignore) {
          ignoreException(ignore);
        }
      }
    }
  }


  /**
   * Empty exception ingnorer
   */
  private static void ignoreException(final Exception ignore) {
  }


  /**
   * Formats date in accordance with given format
   *
   * @param date Date to format
   * @param format String format used to format Date
   *
   * @return String with formatted date
   */
  public static String formatDate(final Date date, final String format) {
    return new SimpleDateFormat(format, Locale.US).format(date);
  }


  /**
   * Gets file name out of file path
   */
  public static String extractNameFromFilePath(final String filePath) {
    final int lastSlash = filePath.lastIndexOf('/');
    return filePath.substring(lastSlash + 1);
  }


  /**
   * Gets file name out of file path
   */
  public static String extractPathFromFilePath(final String filePath) {
    final int lastSlash = filePath.lastIndexOf('/');
    if (lastSlash == -1) {
      return "";
    } else {
      return filePath.substring(0, lastSlash);
    }
  }


  /**
   * Converts array of int reperesenting an int-encoded string to
   * a String
   */
  public static String intArrayToString(final int[] array) {
    final StringBuilder result = new StringBuilder(array.length);
    for (int i = 0; i < array.length; i++) {
      result.append((char)array[i]);
    }
    return result.toString();
  }


  /**
   * Returns true if first character of the string is a letter
   */
  public static boolean isFirstLetter(final String s) {
    if (isBlank(s)) return false;
    return Pattern.compile("[a-zA-Z]").matcher(s.substring(0, 1)).matches();
  }


  /**
   * @return true if system property is set and equals given
   *         value.
   */
  public static boolean systemPropertyEquals(final String name, final String value) {
    final String property = System.getProperty(name);
    return property != null && property.equalsIgnoreCase(value);
  }


  /**
   * @return true this string pattern is empty
   */
  public static boolean patternIsEmpty(final String pattern) {
    if (isBlank(pattern)) return true;
    return !new StringTokenizer(pattern, "\n \r", false).hasMoreTokens();
  }


  /**
   * Breaks a possibly multiline string to a list of lines. Empty
   * lines are excluded from the list.
   */
  public static List multilineStringToList(final String multilineString) {
    final List result = new ArrayList(3);
    if (isBlank(multilineString)) return result;
    for (final StringTokenizer st = new StringTokenizer(multilineString, "\n\r", false); st.hasMoreTokens();) {
      final String s = st.nextToken();
      if (isBlank(s)) continue;
      result.add(s.trim());
    }
    return result;
  }


  /**
   * Converts a list of Strings to a String contaning items of
   * the list as lines separated by "\n".
   */
  public static String linesToString(final List stringList) {
    final StringBuilder result = new StringBuilder(300);
    for (int i = 0, n = stringList.size(); i < n; i++) {
      result.append((String)stringList.get(i)).append('\n');
    }
    return result.toString();
  }


  public static String[] toStringArray(final List stringList) {
    return (String[])stringList.toArray(new String[stringList.size()]);
  }


  /**
   * Returns a byte array from a string of hexadecimal digits.
   */
  public static byte[] decodeFromHex(final String hex) {
    final int len = hex.length();
    final byte[] buf = new byte[(len + 1 >> 1)];

    int i = 0, j = 0;
    if (len % 2 == 1) {
      buf[j++] = (byte)fromDigit(hex.charAt(i++));
    }

    while (i < len) {
      buf[j++] = (byte)(fromDigit(hex.charAt(i++)) << 4 |
        fromDigit(hex.charAt(i++)));
    }
    return buf;
  }


  /**
   * Returns the number from 0 to 15 corresponding to the hex
   * digit <i>ch</i>.
   */
  private static int fromDigit(final char ch) {
    if (ch >= '0' && ch <= '9') {
      return ch - '0';
    }
    if (ch >= 'A' && ch <= 'F') {
      return ch - 'A' + 10;
    }
    if (ch >= 'a' && ch <= 'f') {
      return ch - 'a' + 10;
    }

    throw new IllegalArgumentException("invalid hex digit '" + ch + '\'');
  }


  /**
   * Returns a string of hexadecimal digits from a byte array.
   * Each byte is converted to 2 hex symbols.
   */
  public static String encodeToHex(final byte[] ba) { // NOPMD - "A user given array is stored directly"  - we do NOT store anything.
    final int length = ba.length;
    final char[] buf = new char[(length << 1)];
    for (int i = 0, j = 0; i < length;) {
      final int k = ba[i++];
      buf[j++] = HEX_DIGITS[k >>> 4 & 0x0F];
      buf[j++] = HEX_DIGITS[k & 0x0F];
    }
    return String.valueOf(buf);
  }


  /**
   * Digests password with MD5 and encodes it as a hex String.
   *
   * @param password to digest.
   *
   * @return hex encoded password digest.
   */
  public static String digest(final String password) throws NoSuchAlgorithmException {
    final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    messageDigest.reset();
    messageDigest.update(password.trim().toLowerCase().getBytes());
    return encodeToHex(messageDigest.digest());
  }


  public static long daysToMillis(final int days) {
    return (long)days * 24L * 60L * 60L * 1000L;
  }


  /**
   * Truncates string to maxLen.
   *
   * @param in imput string
   * @param maxLen max length
   */
  public static String truncate(final String in, final int maxLen) {
    if (in == null || in.length() <= maxLen) {
      return in;
    } else {
      return in.substring(0, maxLen);
    }
  }


  public static void appendWithNewLineIfNotNull(final StringBuffer sb, final String label, final String value) {
    if (!isBlank(value)) {
      sb.append(label).append(' ').append(value);
      sb.append(STR_CR);
    }
  }


  /**
   * Puts string into double quotes. Other leading and trailing
   * quotes are removed first.
   *
   * @param stringToProcess String to quote, trimmed before
   * quoting.
   *
   * @return double-quoted string.
   */
  public static String putIntoDoubleQuotes(final String stringToProcess) {

    final char[] dd = stringToProcess.trim().toCharArray();
    final int len = dd.length;
    int st = 0;
    for (; st < len; st++) {
      final char c = dd[st];
      if (c != CHAR_DOUBLE_QUOTE && c != CHAR_SINGLE_QUOTE) break;
    }

    int en = len;
    for (; en > st; en--) {
      final char c = dd[en - 1];
      if (c != CHAR_DOUBLE_QUOTE && c != CHAR_SINGLE_QUOTE) break;
    }

    //if (log.isDebugEnabled()) log.debug("st: " + st);
    //if (log.isDebugEnabled()) log.debug("en: " + en);
    //if (log.isDebugEnabled()) log.debug("stringToProcess: " + "\"" + stringToProcess + "\"");
    return '\"' + stringToProcess.substring(st, en) + '\"';
  }


  public static String removeDoubleQuotes(final String stringToProcess) {
    if (stringToProcess.startsWith("\"") && stringToProcess.endsWith("\"")) {
      return stringToProcess.substring(1, stringToProcess.length() - 1);
    } else {
      return stringToProcess;
    }
  }


  /**
   * Fixes CRLF according to local system.
   *
   * @param stringToFix
   *
   * @return String with fixed CRLF according to local system.
   */
  public static String fixCRLF(final String stringToFix) {
    StringWriter sw = null;
    PrintWriter pw = null;
    BufferedReader br = null;
    try {
      sw = new StringWriter(500);
      pw = new PrintWriter(sw);
      br = new BufferedReader(new StringReader(stringToFix));
      String line = br.readLine();
      while (line != null) {
        pw.println(line);
        line = br.readLine();
      }
    } catch (final Exception e) {
      ignoreException(e);
    } finally {
      IoUtils.closeHard(pw);
      IoUtils.closeHard(br);
    }
    return sw.toString();
  }


  public static List split(final String value, final int maxLength) {
    final List parts = new ArrayList(23);
    final int originalLength = value.length();
    if (originalLength > maxLength) {
      // split it if necessary
      final int splitCount = originalLength / maxLength;
      for (int splitIndex = 0; splitIndex < splitCount; splitIndex++) {
        parts.add(value.substring(splitIndex * maxLength, (splitIndex + 1) * maxLength));
      }
      // process last piece if any
      final int leftOverLength = originalLength - splitCount * maxLength;
      if (leftOverLength > 0) {
        parts.add(value.substring(originalLength - leftOverLength, originalLength));
      }
    } else {
      parts.add(value);
    }
    return parts;
  }


  public static String formatWithTrailingZeroes(final int value, final int zeroes) {
    final String stringValue = Integer.toString(value);
    final StringBuilder result = new StringBuilder(5);
    final int numberOfZeroesToAdd = zeroes - stringValue.length();
    for (int i = 0; i < numberOfZeroesToAdd; i++) {
      result.append('0');
    }
    result.append(stringValue);
    return result.toString();
  }


  /**
   * Makes pattern array of compiled regex patterns.
   */
  public static Pattern[] makeRegexPatternsFromMultilineString(final String customPatterns) {
    final Set result = new HashSet(5);
    for (final StringTokenizer st = new StringTokenizer(customPatterns, "\n", false); st.hasMoreTokens();) {
      final String pattern = st.nextToken().trim();
      if (isRegex(pattern)) result.add(Pattern.compile(pattern));
    }
    return (Pattern[])result.toArray(new Pattern[result.size()]);
  }


  /**
   * @param pattern
   *
   * @return true if pattern starts with '^' and ands wiht '$' 
   */
  public static boolean isRegex(final String pattern) {
    return !isBlank(pattern) && pattern.charAt(0) == '^' && pattern.endsWith("$");
  }


  public static boolean isBase64(final String isValidString) {
    return isArrayByteBase64(isValidString.getBytes());
  }


  public static boolean isBase64(final byte octect) {
    //shall we ignore white space? JEFF??
    return octect == PAD || base64Alphabet[octect] != -1;
  }


  public static boolean isArrayByteBase64(final byte[] arrayOctect) {
    final int length = arrayOctect.length;
    if (length == 0) {
      // shouldn't a 0 length array be valid base64 data?
      // return false;
      return true;
    }
    for (int i = 0; i < length; i++) {
      if (!isBase64(arrayOctect[i]))
        return false;
    }
    return true;
  }


  /**
   * Encodes hex octects into Base64.
   *
   * @param binaryData Array containing binary data to encode.
   * @return Base64-encoded data.
   */
  public static byte[] encode(final byte[] binaryData) {
    final int lengthDataBits = binaryData.length * EIGHTBIT;
    final int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
    final int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
    byte[] encodedData = null;


    if (fewerThan24bits == 0) {
      // 16 or 8 bit
      encodedData = new byte[ (numberTriplets << 2) ];
    } else {
      //data not divisible by 24 bit
      encodedData = new byte[ (numberTriplets + 1 << 2) ];
    }

    byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

    int encodedIndex = 0;
    int dataIndex = 0;
    int i = 0;
    //log.debug("number of triplets = " + numberTriplets);
    for (i = 0; i < numberTriplets; i++) {
      dataIndex = i * 3;
      b1 = binaryData[dataIndex];
      b2 = binaryData[dataIndex + 1];
      b3 = binaryData[dataIndex + 2];

      //log.debug("b1= " + b1 +", b2= " + b2 + ", b3= " + b3);

      l = (byte)(b2 & 0x0f);
      k = (byte)(b1 & 0x03);

      encodedIndex = i << 2;
      final byte val1 = (b1 & SIGN) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xc0);
      final byte val2 = (b2 & SIGN) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xf0);
      final byte val3 = (b3 & SIGN) == 0 ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xfc);

      encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
      //log.debug( "val2 = " + val2 );
      //log.debug( "k4   = " + (k<<4) );
      //log.debug(  "vak  = " + (val2 | (k<<4)) );
      encodedData[encodedIndex + 1] =
        lookUpBase64Alphabet[val2 | k << 4];
      encodedData[encodedIndex + 2] =
        lookUpBase64Alphabet[l << 2 | val3];
      encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3f];
    }

    // form integral number of 6-bit groups
    final int dataIndex1 = i * 3;
    final int encodedIndex1 = i << 2;
    if (fewerThan24bits == EIGHTBIT) {
      final byte b11 = binaryData[dataIndex1];
      final byte k1 = (byte)(b11 & 0x03);
      //log.debug("b1=" + b1);
      //log.debug("b1<<2 = " + (b1>>2) );
      final byte val1 = (b11 & SIGN) == 0 ? (byte)(b11 >> 2) : (byte)(b11 >> 2 ^ 0xc0);
      encodedData[encodedIndex1] = lookUpBase64Alphabet[val1];
      encodedData[encodedIndex1 + 1] = lookUpBase64Alphabet[k1 << 4];
      encodedData[encodedIndex1 + 2] = PAD;
      encodedData[encodedIndex1 + 3] = PAD;
    } else if (fewerThan24bits == SIXTEENBIT) {

      final byte b11 = binaryData[dataIndex1];
      final byte b21 = binaryData[dataIndex1 + 1];
      final byte l1 = (byte)(b21 & 0x0f);
      final byte k1 = (byte)(b11 & 0x03);

      final byte val1 = (b11 & SIGN) == 0 ? (byte)(b11 >> 2) : (byte)(b11 >> 2 ^ 0xc0);
      final byte val2 = (b21 & SIGN) == 0 ? (byte)(b21 >> 4) : (byte)(b21 >> 4 ^ 0xf0);

      encodedData[encodedIndex1] = lookUpBase64Alphabet[val1];
      encodedData[encodedIndex1 + 1] =
        lookUpBase64Alphabet[val2 | k1 << 4];
      encodedData[encodedIndex1 + 2] = lookUpBase64Alphabet[l1 << 2];
      encodedData[encodedIndex1 + 3] = PAD;
    }

    return encodedData;
  }


  /**
   * Decodes Base64 data into octects
   *
   * @param base64Data Byte array containing Base64 data
   * @return Array containing decoded data.
   */
  public static byte[] decode(final byte[] base64Data) {
    // handle the edge case, so we don't have to worry about it later
    if (base64Data.length == 0) { return EMPY_BYTE_ARRAY; } // NOPMD

    final int numberQuadruple = base64Data.length / FOURBYTE;
    byte[] decodedData = null;
    byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

    // Throw away anything not in base64Data

    int encodedIndex = 0;
    int dataIndex = 0;
    {
      // this sizes the output array properly - rlw
      int lastData = base64Data.length;
      // ignore the '=' padding
      while (base64Data[lastData - 1] == PAD) {
        if (--lastData == 0) {
          return EMPY_BYTE_ARRAY; // NOPMD
        }
      }
      decodedData = new byte[ lastData - numberQuadruple ];
    }

    for (int i = 0; i < numberQuadruple; i++) {
      dataIndex = i << 2;
      marker0 = base64Data[dataIndex + 2];
      marker1 = base64Data[dataIndex + 3];

      b1 = base64Alphabet[base64Data[dataIndex]];
      b2 = base64Alphabet[base64Data[dataIndex + 1]];

      if (marker0 != PAD && marker1 != PAD) {
        //No PAD e.g 3cQl
        b3 = base64Alphabet[marker0];
        b4 = base64Alphabet[marker1];

        decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
        decodedData[encodedIndex + 1] =
          (byte)((b2 & 0xf) << 4 | b3 >> 2 & 0xf);
        decodedData[encodedIndex + 2] = (byte)(b3 << 6 | b4);
      } else if (marker0 == PAD) {
        //Two PAD e.g. 3c[Pad][Pad]
        decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
      } else if (marker1 == PAD) {
        //One PAD e.g. 3cQ[Pad]
        b3 = base64Alphabet[marker0];

        decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
        decodedData[encodedIndex + 1] =
          (byte)((b2 & 0xf) << 4 | b3 >> 2 & 0xf);
      }
      encodedIndex += 3;
    }
    return decodedData;
  }

  public static String diffToString(final int diff) {
    return '(' + (diff >= 0 ? "+" : "-") + Integer.toString(diff) + ')';
  }
}
