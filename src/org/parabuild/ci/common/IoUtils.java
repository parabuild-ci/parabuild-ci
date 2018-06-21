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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class IoUtils {

  private static final Log log = LogFactory.getLog(IoUtils.class);


  private static final Map PROHIBITED_PATHS_MAP = new HashMap(21);


  static {
    final String[] PROHIBITED_PATHS = {"/bin", "/boot", "/command", "/dev", "/etc", "/home", "/initrd", "/lib", "/lost+found", "/mnt", "/opt", "/proc", "/root", "/sbin", "/service", "/srv", "/tmp", "/usr", "/var", "/"};
    for (int i = 0; i < PROHIBITED_PATHS.length; i++) {
      PROHIBITED_PATHS_MAP.put(PROHIBITED_PATHS[i], Boolean.TRUE);
    }
  }


  /**
   * Utility class
   */
  private IoUtils() {
  }


  public static boolean isProhibitedPath(final File file) throws IOException {
    if (file == null) return false;
    final String canonicalPath = file.getCanonicalPath();
    return PROHIBITED_PATHS_MAP.get(canonicalPath) != null
            || (canonicalPath.length() == 2 || canonicalPath.length() == 3) && canonicalPath.charAt(1) == ':'
            ;
  }


  /**
   * Closes input stream regardles of thrown exception
   */
  public static void closeHard(final InputStream is) {
    if (is != null) {
      try {
        is.close();
      } catch (final IOException ignore) {
        logCloseWarning(ignore);
      }
    }
  }


  /**
   * Closes input stream regardles of thrown exception
   */
  public static void closeHard(final Reader reader) {
    if (reader != null) {
      try {
        reader.close();
      } catch (final IOException ignore) {
        logCloseWarning(ignore);
      }
    }
  }


  /**
   * Closes input stream reader regardles of thrown exception
   */
  public static void closeHard(final InputStreamReader isr) {
    if (isr != null) {
      try {
        isr.close();
      } catch (final IOException ignore) {
        logCloseWarning(ignore);
      }
    }
  }


  /**
   * A utility method to read small size files to a string
   */
  public static String fileToString(final File file) {
    String result = "";
    try {
      result = inputStreamToString(new FileInputStream(file));
    } catch (final IOException e) {
      log.error("Error while transforming file \"" + file + "\" to string", e);
    }
    return result;
  }


  /**
   * A utility method to read small size files to a string.
   * <p/>
   * Closes input stream upon completion.
   */
  public static String inputStreamToString(final InputStream is) throws IOException {
    BufferedReader br = null;
    final StringBuffer sb = new StringBuffer(100);
    try {
      br = new BufferedReader(new InputStreamReader(is));
      String ln = br.readLine();
      while (ln != null) {
        sb.append(ln).append('\n');
        ln = br.readLine();
      }
    } finally {
      closeHard(br);
    }
    return sb.toString();
  }


  /**
   * Closes Writer regardles of thrown exception
   */
  public static void closeHard(final Writer w) {
    if (w != null) {
      try {
        w.flush();
        w.close();
      } catch (final IOException ignore) {
        logCloseWarning(ignore);
      }
    }
  }


  /**
   * Closes output stream regardles of thrown exception
   */
  public static void closeHard(final OutputStream os) {
    if (os != null) {
      try {
        os.flush();
        os.close();
      } catch (final IOException ignore) {
        logCloseWarning(ignore);
      }
    }
  }


  /**
   * Closes Connection regardles of thrown exception
   */
  public static void closeHard(final Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (final SQLException ignore) {
        ignoreExpectedException(ignore);
      }
    }
  }


  /**
   * Closes Statement regardles of thrown exception
   */
  public static void closeHard(final Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (final SQLException ignore) {
        ignoreExpectedException(ignore);
      }
    }
  }


  /**
   * Closes ResultSet regardles of thrown exception
   */
  public static void closeHard(final ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (final SQLException ignore) {
        ignoreExpectedException(ignore);
      }
    }
  }


  /**
   * Commits hard
   */
  public static void commitHard(final Connection conn) {
    if (conn != null) {
      try {
        conn.commit();
      } catch (final SQLException ignore) {
        ignoreExpectedException(ignore);
      }
    }
  }


  public static void copyInputToOuputStream(final InputStream in, final OutputStream out) throws IOException {
    int bytesRead;
    final byte[] b = new byte[1024];
    while ((bytesRead = in.read(b)) != -1) {
      out.write(b, 0, bytesRead);
    }
  }


  /**
   */
  public static String stackTraceToString(final Throwable thr) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    thr.printStackTrace(new PrintStream(baos));
    return baos.toString();
  }


  /**
   * Deletes given list of files
   */
  public static boolean deleteFilesHard(final List fileList) {
    boolean result = true;
    for (final Iterator i = fileList.iterator(); i.hasNext(); ) {
      result = deleteFileHard((File) i.next()) && result;
    }
    return result;
  }


  /**
   * Deletes given list of files
   */
  public static boolean deleteFilesHard(final File[] fileList) {
    boolean result = true;
    for (int i = 0; i < fileList.length; i++) {
      result = deleteFileHard(fileList[i]) && result;
    }
    return result;
  }


  /**
   * Deletes given file or directory
   */
  public static boolean deleteFileHard(final File file) {
    if (file == null || !file.exists()) return true;
    final File fileToDelete = toUNC(file);
    if (fileToDelete.isDirectory()) {
//      if (log.isDebugEnabled()) log.debug("directory to delete: " + fileToDelete);
      final String[] files = fileToDelete.list();
//      if (log.isDebugEnabled()) log.debug("files: " + files);
      if (files != null) {
        for (int ii = 0; ii < files.length; ii++) {
          deleteFileHard(new File(fileToDelete, files[ii]));
        }
      }
    }

    final boolean deleted = fileToDelete.delete();
    if (!deleted)
      log.debug("result of delete for \"" + fileToDelete + '\"' + deleted);
    if (fileToDelete.exists())
      log.warn("Could not delete file \"" + fileToDelete + '\"', new Throwable("Stack trace at call"));
    return deleted;
  }


  private static void logCloseWarning(final IOException ignore) {
    log.warn("Exception while closing of IO object hard", ignore);
  }


  /**
   * Deletes content of a directory
   */
  public static void emptyDir(final File dir) {
    // REVIEWME: Should we check if the dir is writable and throw an exception?
    if (!dir.exists()) return;
    if (!dir.canWrite())
      log.warn("Trying to empty read-only or inaccessible directory " + dir);
    final File[] files = toUNC(dir).listFiles();
    for (int i = 0; i < files.length; i++) {
      deleteFileHard(files[i]);
    }
  }


  private static File toUNC(final File file) {
    final String absolutePath = file.getAbsolutePath();
    if (absolutePath.length() > 6
            && absolutePath.charAt(1) == ':'
            && !absolutePath.startsWith("\\\\")) {

      return new File("\\\\.\\" + absolutePath);
    } else {
      return file;
    }
  }


  /**
   * Copies file from source location to destination.
   * <p/>
   * Currently copies only files, not directories.
   *
   * @param source      File to copy from
   * @param destination to copy to
   */
  public static void copyFile(final File source, final File destination) throws IOException {
    // check
    if (source.isDirectory())
      throw new IOException("Copying from a directory (\"" + source.getAbsolutePath() + "\") is not supported");
    if (destination.isDirectory())
      throw new IOException("Copying to a directory (\"" + source.getAbsolutePath() + "\") is not supported");
    // copy
    InputStream is = null;
    OutputStream os = null;
    try {
      if (!(destination.length() == 0))
        throw new IOException("Cannot copy to file \"" + destination.getCanonicalPath() + "\" - file in not empty");
      is = new FileInputStream(source);
      os = new FileOutputStream(destination);
      copyInputToOuputStream(is, os);
    } finally {
      closeHard(is);
      closeHard(os);
      destination.setLastModified(source.lastModified());
    }
  }


  /**
   * Checks if given path to check is under given expected parent
   * path.
   *
   * @return true if yes
   */
  public static boolean isFileUnder(final String pathToCheck, final String expectedParent) {
    final String absExpected = expectedParent.replace('\\', '/');
    final String absPathToCheck = pathToCheck.replace('\\', '/');
    return absPathToCheck.startsWith(absExpected);
  }


  /**
   * Obtains canonical path from given path. If there is an
   * IOException while processing, will throw IllegalStateException.
   *
   * @throws IllegalStateException
   */
  public static String getCanonicalPathHard(final String path) {
    return getCanonicalPathHard(new File(ArgumentValidator.validateArgumentNotBlank(path, "canonical path")));
  }


  /**
   * Obtains canonical path from given path. If there is an
   * IOException while processing, will throw IllegalStateException.
   *
   * @throws IllegalStateException
   */
  public static String getCanonicalPathHard(final File file) {
    try {
      return file.getCanonicalPath();
    } catch (final IOException e) {
      log.fatal("Can not get canonical path for " + file.toString(), e);
      throw makeIllegalStateException(e);
    }
  }


  /**
   * Obtains canonical File from given path. If there is an
   * IOException while processing, will throw IllegalStateException.
   *
   * @throws IllegalStateException
   */
  public static File getCanonicalFileHard(final File file) {
    try {
      return file.getCanonicalFile();
    } catch (final IOException e) {
      log.fatal("Can not get canonical file for " + file.toString(), e);
      throw makeIllegalStateException(e);
    }
  }


  /**
   * This method is used solely for purposes of explicit ignoring
   * expected exceptions to avoid uneeded complains of source
   * code validation tools like PMD ir Findbugs.
   */
  public static void ignoreExpectedException(final Exception e) {
  }


  /**
   * Line skipper
   */
  public static String readUntil(final BufferedReader reader, final String beginsWith1, final String beginsWith2) throws IOException {
    String line = reader.readLine();
    while (!(line == null || line.startsWith(beginsWith1) || line.startsWith(beginsWith2))) {
      line = reader.readLine();
    }
    return line;
  }


  /**
   * This method will consume lines from the reader up to the
   * line that begins with the String specified but not past a
   * line that begins with the notPast String. If the line that
   * begins with the beginsWith String is found then it will be
   * returned. Otherwise null is returned.
   *
   * @param reader     Reader to read lines from.
   * @param beginsWith String to match to the beginning of a
   *                   line.
   * @param notPast    String which indicates that lines should stop
   *                   being consumed, even if the begins with match has not been
   *                   found. Pass null to this method to ignore this string.
   * @return String that begin as indicated, or null if none
   * matched to the end of the reader or the notPast line
   * was found.
   * @throws IOException
   */
  public static String readToNotPast(final BufferedReader reader, final String beginsWith, final String notPast) throws IOException {
    return readToNotPast(reader, beginsWith, notPast, false);
  }


  public static String readToNotPast(final BufferedReader reader, final String beginsWith, final String notPast, final boolean trimLine) throws IOException {
    final boolean checkingNotPast = notPast != null;

    String nextLine = readAndTrim(reader, trimLine);
    while (nextLine != null && !nextLine.startsWith(beginsWith)) {
      if (checkingNotPast && nextLine.startsWith(notPast)) {
        return null;
      }
      nextLine = readAndTrim(reader, trimLine);
    }
    return nextLine;
  }


  public static String readAndTrim(final BufferedReader reader, final boolean trim) throws IOException {
    final String result = reader.readLine();
    if (trim) {
      if (result == null) {
        return result;
      } else {
        return result.trim();
      }
    } else {
      return result;
    }
  }


  public static String skipEmptyLines(final BufferedReader reader) throws IOException {
    String line = reader.readLine();
    while (line != null && line.length() == 0) {
      line = reader.readLine();
    }
    return line;
  }


  /**
   * Helper method
   */
  public static void createDirs(final File dir) throws IOException {
    if (!dir.exists()) {
      if (!dir.mkdirs())
        throw new IOException("Error creating a directory. The user may not have enough rights or the path is invalid: \"" + dir.toString() + '\"');
    }
  }


  /**
   * Creates IOException with initialized cause.
   *
   * @param descr exception description.
   * @param cause cause.
   * @return Created IOException with initialized cause.
   */
  public static IOException createIOException(final String descr, final Throwable cause) {
    final IOException ex = new IOException(descr);
    ex.initCause(cause);
    return ex;
  }


  /**
   * Creates IOException with initialized cause. Uses cause's
   * description as new exception's decription.
   *
   * @param cause cause.
   * @return Created IOException with initialized cause.
   */
  public static IOException createIOException(final Exception cause) {
    if (cause instanceof IOException) return (IOException) cause;
    return createIOException(StringUtils.toString(cause), cause);
  }


  /**
   * Copies a directory.
   */
  public static void copyDirectory(final File srcDir, final File dstDir) throws IOException {
    if (srcDir.isDirectory()) {
      if ("CVS".equals(srcDir.getName())) {
        return;
      }

      if (dstDir.exists()) {
        if (dstDir.isFile())
          throw new IOException("Can not copy a directory to a file");
      } else {
        dstDir.mkdir();
      }

      final String[] children = srcDir.list();
      for (int i = 0; i < children.length; i++) {
        copyDirectory(new File(srcDir, children[i]),
                new File(dstDir, children[i]));
      }
    } else {
      copyFile(srcDir, dstDir);
    }
  }


  /**
   * Zips a single file.
   *
   * @param file
   * @param zippedFile
   * @throws IOException
   */
  public static void zipFile(final File file, final File zippedFile) throws IOException {
    ZipOutputStream zos = null;
    try {
      zos = new ZipOutputStream(new FileOutputStream(zippedFile));
      zipFile(file.getParent(), file, zos);
    } finally {
      closeHard(zos);
    }
  }


  public static void zipDir(final File dir, final File zippedFile) throws IOException {
    if (!dir.isDirectory())
      throw new IOException("Not a directory: " + dir.getPath());
    if (!dir.exists())
      throw new IOException("Does not exist: " + dir.getPath());
    ZipOutputStream zos = null;
    try {
      final String baseDir = dir.getAbsolutePath();
      zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zippedFile), 10000));
      traverseDir(new File(baseDir), new DirectoryZipperCallback(baseDir, zos));
    } finally {
      closeHard(zos);
    }
  }


  /**
   * Helper method.
   *
   * @see #zipFile(File, File)
   */
  private static void zipFile(final String baseDir, final File f, final ZipOutputStream zos) throws IOException {
    FileInputStream fis = null;
    try {
      // validate
      final String path = f.getPath();
      if (!f.isFile())
        throw new IOException("Not a file: " + path);
      if (!f.exists())
        throw new IOException("Does not exist: " + path);
      if (!path.startsWith(baseDir))
        throw new IOException("Not under base dir: " + path);
      // create a FileInputStream on top of f
      fis = new FileInputStream(f);
      // create a new zip entry
      final ZipEntry anEntry = new ZipEntry(path.substring(baseDir.length() + 1).replace('\\', '/'));
      anEntry.setTime(f.lastModified());
      // place the zip entry in the ZipOutputStream object
      zos.putNextEntry(anEntry);
      copyInputToOuputStream(fis, zos);
      // now write the content of the file to the ZipOutputStream
      fis.close();
    } finally {
      closeHard(fis);
    }
  }


  public static void closeHard(final ZipFile zipFile) {
    if (zipFile == null) return;
    try {
      zipFile.close();
    } catch (final IOException ignore) {
      logCloseWarning(ignore);
    }
  }


  public static void closeHard(final Socket socket) {
    if (socket == null) return;
    try {
      socket.close();
    } catch (final IOException e) {
      ignoreExpectedException(e);
    }
  }


  /**
   * Helper method.
   *
   * @param stringBuffer
   * @return InputStream that can be used to read from the given
   * StringBuffer
   */
  public static InputStream stringBufferToInputStream(final StringBuffer stringBuffer) {
    return new ByteArrayInputStream(stringBuffer.toString().getBytes());
  }


  /**
   * Helper method.
   *
   * @param string
   * @return InputStream that can be used to read from the given
   * StringBuffer
   */
  public static InputStream stringToInputStream(final String string) {
    return new ByteArrayInputStream(string.getBytes());
  }


  public static void moveFile(final File errorFile, final File destination) throws IOException {
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(errorFile));
      bos = new BufferedOutputStream(new FileOutputStream(destination));
      copyInputToOuputStream(bis, bos);
      closeHard(bos);
      closeHard(bis);
      deleteFileHard(errorFile.getCanonicalFile());
    } finally {
      closeHard(bos);
      closeHard(bis);
    }
  }


  public static SQLException makeSQLException(final Exception e) {
    final SQLException se = new SQLException(StringUtils.toString(e));
    se.initCause(e);
    return se;
  }


  public static String getLocalHostName() throws UnknownHostException {
    final String result = InetAddress.getLocalHost().toString();
    // parse
    final int slashIndex = result.indexOf('/');
    if (slashIndex >= 0) return result.substring(0, slashIndex);
    return result;
  }


  public static String getLocalHostNameHard() {
    try {
      return getLocalHostName();
    } catch (final UnknownHostException e) {
      log.warn("Exception while getting local host: " + StringUtils.toString(e), e);
      return "localhost";
    }
  }


  public static void writeStringToFile(final File file, final String s) throws IOException {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      for (int i = 0; i < s.length(); i++) {
        fos.write(s.charAt(i));
      }
    } finally {
      closeHard(fos);
    }
  }


  /**
   * Returns a text resource from the class path as a string.
   *
   * @param resourceName the resource name to be found in the class path.
   * @return a text resource from the class path as a string
   */
  public static String getResourceAsString(final String resourceName) throws IOException {

    final InputStream is = IoUtils.class.getClassLoader().getResourceAsStream(resourceName);
    if (is == null) {
      throw new IOException("Resource '" + resourceName + "' not found");
    }

    return inputStreamToString(is);
  }


  /**
   * Tests if a specified file has given extension.
   */
  public static final class ExtensionFileFilter implements FilenameFilter {

    private String ext = null;


    /**
     * Constructor
     */
    public ExtensionFileFilter(final String ext) {
      this.ext = ext;
    }


    /**
     * Tests if a specified file has given extension.
     */
    public boolean accept(final File dir, final String name) {
      if (StringUtils.isBlank(ext)) return true;
      return name.endsWith(ext);
    }


    public String toString() {
      return "ExtensionFileFilter{" +
              "ext='" + ext + '\'' +
              '}';
    }
  }


  /**
   * Traverses a directory.
   *
   * @param dir      to traverse.
   * @param callback object.
   * @return int count of traversed dirs.
   * @see DirectoryTraverserCallback
   */
  public static int traverseDir(final File dir, final DirectoryTraverserCallback callback) throws IOException {
    if (!dir.isDirectory()) throw new IOException("Not a directory: " + dir.toString());
    return traversePath(dir, callback);
  }


  /**
   * Traverses a path
   *
   * @param dir      to traverse.
   * @param callback object.
   * @return int count of traversed dirs.
   * @see DirectoryTraverserCallback
   */
  public static int traversePath(final File dir, final DirectoryTraverserCallback callback) throws IOException {
    final int[] counter = new int[]{0};
    traversePath(dir, callback, counter, true);
    return counter[0];
  }


  /**
   * Helper recursive file lister. Does not return dir itself.
   *
   * @param path
   * @throws IOException
   */
  private static boolean traversePath(final File path, final DirectoryTraverserCallback callback, final int[] counter, final boolean firstTime) throws IOException {
    if (path == null || !path.exists()) return false;
    boolean keepGoing = true;
    if (!firstTime) {
      counter[0]++;
      keepGoing = callback.callback(path);
    }
    if (keepGoing && path.isDirectory()) {
      final File[] files = path.listFiles();
      if (files == null)
        throw new IOException("Files cannot be listed for the path \"" + path.toString() + "\". The path may be not a directory or an I/O error has occured.");
      for (int ii = 0; keepGoing && ii < files.length; ii++) {
        keepGoing = traversePath(files[ii], callback, counter, false);
      }
    }
    return keepGoing;
  }


  public static boolean isFileUnder(final File child, final File parent) throws IOException {
    return child.getCanonicalPath().startsWith(parent.getCanonicalPath());
  }


  public static IllegalStateException makeIllegalStateException(final Exception e) {
    final IllegalStateException ise = new IllegalStateException(StringUtils.toString(e));
    ise.initCause(e);
    return ise;
  }


  /**
   * Creates a temp file that will be deleted at JVM exit.
   *
   * @param prefix file prefix
   * @param suffix files suffix (extension)
   * @return File created files
   * @throws IOException
   */
  public static File createTempFile(final String prefix, final String suffix) throws IOException {
    return File.createTempFile(prefix, suffix);
  }


  /**
   * Creates a temp file that will be deleted at JVM exit.
   *
   * @param prefix file prefix
   * @param suffix files suffix (extension)
   * @return File created files
   * @throws IOException
   */
  public static File createTempFile(final String prefix, final String suffix, final File parentDir) throws IOException {
    return File.createTempFile(prefix, suffix, parentDir);
  }


  /**
   * @see IoUtils#zipDir(File, File)
   */
  private static final class DirectoryZipperCallback implements DirectoryTraverserCallback {

    private final String baseDir;
    private final ZipOutputStream zos;


    DirectoryZipperCallback(final String baseDir, final ZipOutputStream zos) {
      this.baseDir = baseDir;
      this.zos = zos;
    }


    public boolean callback(final File file) throws IOException {
      if (file.isFile()) zipFile(baseDir, file, zos);
      return true;
    }


    public String toString() {
      return "DirectoryZipperCallback{" +
              "baseDir='" + baseDir + '\'' +
              ", zos=" + zos +
              '}';
    }
  }
}
