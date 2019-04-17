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

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.process.Tailer;
import org.parabuild.ci.process.ZeroLengthTailer;
import org.parabuild.ci.remote.internal.InputStreamToFileCopier;
import org.parabuild.ci.remote.internal.InputStreamToFileCopierAndMerger;
import org.parabuild.ci.remote.internal.SyncronizedPrintWriter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to retrieve system environment
 */
public final class RuntimeUtils {

  private static final Log log = LogFactory.getLog(RuntimeUtils.class);

  // win types
  public static final byte SYSTEM_TYPE_WINNT = 0;
  public static final byte SYSTEM_TYPE_WIN95 = 1;

  // unix types
  public static final byte SYSTEM_TYPE_CYGWIN = 2;
  public static final byte SYSTEM_TYPE_UNIX = 3;
  public static final byte SYSTEM_TYPE_SUNOS = 4;
  public static final byte SYSTEM_TYPE_MACOSX = 5;
  public static final byte SYSTEM_TYPE_HPUX = 6;
  public static final byte SYSTEM_TYPE_LINUX = 7;
  public static final byte SYSTEM_TYPE_DEBIAN = 8;

  private static final Map startupEnv = new HashMap(89);
  private static final List macAddressList = new ArrayList(5);
  private static byte systemType = SYSTEM_TYPE_WINNT;

  private static final int POOL_KEEP_ALIVE = -1;
  private static final int POOL_MAX_SIZE = Integer.MAX_VALUE;
  private static final int POOL_INITIAL_THREAD_COUNT = 30;
  private static final PooledExecutor streamCopierPool = ThreadUtils.makeThreadPool(POOL_KEEP_ALIVE, POOL_MAX_SIZE, POOL_INITIAL_THREAD_COUNT, "StreamCopierThread");
  private static final String EXE_SUFFIX = ".exe";


  // init
  static {
    init();
  }


  private RuntimeUtils() {
  }


  private static void init() {
    try {
      // Process process = null;
      String executeResult = null;

      // preinit system type
      final Properties p = System.getProperties();
      final String os = p.getProperty("os.name").toLowerCase();
      if (os.contains("indows")) {
        if (os.contains("nt") || os.contains("200") || os.contains("windows xp") || os.contains("windows vista")) {
          systemType = SYSTEM_TYPE_WINNT;
          executeResult = execute("cmd.exe /C set");
        } else {
          systemType = SYSTEM_TYPE_WIN95;
          executeResult = execute("command.com /C set");
        }
      } else if (os.contains("sunos") || os.contains("solaris")) {
        systemType = SYSTEM_TYPE_SUNOS;
        executeResult = execute("env");
      } else if (os.contains("linux")) {
        systemType = SYSTEM_TYPE_LINUX;
        executeResult = execute("env");
      } else if (os.contains("hp") && os.contains("ux")) {
        systemType = SYSTEM_TYPE_HPUX;
        executeResult = execute("env");
      } else if (!StringUtils.isBlank(p.getProperty("mrj.version"))) {
        systemType = SYSTEM_TYPE_MACOSX;
        executeResult = execute("env");
      } else {
        systemType = SYSTEM_TYPE_UNIX;
        executeResult = execute("env");
      }

      // if (log.isDebugEnabled()) log.debug("executeResult.length(): " + executeResult.length());
      startupEnv.putAll(new EnvironmentParser().parseEnvironment(executeResult));

      if (systemType == SYSTEM_TYPE_LINUX && new File("/etc/debian_version").exists()) {
        // adjust initial system type for debian
        systemType = SYSTEM_TYPE_DEBIAN;
      } else if ("true".equals(System.getProperty("parabuild.detect.cygwin", "false"))) {
        if (startupEnv.get("TERM") != null) { // NOPMD
          // adjust initial system type for cygwin
          String uname = null;
          // REVIEWME: consider a better way than catching exception (see #760)
          try {
            uname = execute("uname");
          } catch (final Exception e) {
            IoUtils.ignoreExpectedException(e);
          }
          if (!StringUtils.isBlank(uname) && uname.startsWith("CYGWIN")) {
            systemType = SYSTEM_TYPE_CYGWIN;
          }
        }
      }

      // adjust classpath
      if (systemType() == SYSTEM_TYPE_CYGWIN) {
        final String classPathName = "CLASSPATH";
        final String classPathValue = (String) startupEnv.get(classPathName);
        if (classPathValue != null) startupEnv.put(classPathName, cygwinUnixPathToWindows(classPathValue));
      }

      // init MAC address list
      if (isWindows()) {
        BufferedReader in = null;
        try {
          final Pattern pattern = Pattern.compile(".*Physical Address.*: (.*)");
          final String ipconfigOutput = execute("ipconfig.exe /all");
          in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ipconfigOutput.getBytes())));
          String line = in.readLine();
          while (line != null) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
              macAddressList.add(matcher.group(1));
            }
            line = in.readLine();
          }
        } catch (final Exception e) {
          IoUtils.ignoreExpectedException(e);
        } finally {
          IoUtils.closeHard(in);
        }
      }
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw IoUtils.makeIllegalStateException(e);
    }
  }


  /**
   * Returns value of system environment variable
   */
  public static String getEnvVariable(final String variableName) {
    return (String) startupEnv.get(variableName);
  }


  /**
   * Returns an unmodifyable list with MAC addresses of this system.
   *
   * @return an unmodifyable list with MAC addresses of this system.
   */
  public static List getMacAddressList() {
    return Collections.unmodifiableList(macAddressList);
  }


  /**
   * Returns system type
   */
  public static byte systemType() {
    return systemType;
  }


  /**
   * Converts Windows path to Cygwin path. This method mas be
   * executed in Cygwin environment.
   */
  public static String cygwinWindowsPathToUnix(final String windowsPath) throws IOException {
    try {
      if (systemType() != SYSTEM_TYPE_CYGWIN)
        throw new IllegalStateException("Attempted to access cygwin while not in Cygwin environment");
      String result = execute("cygpath --unix --path '" + windowsPath + '\'');
      while (result.endsWith(".") || result.endsWith("\n")) {
        result = result.substring(0, result.length() - 1);
      }
      return result;
    } catch (final CommandStoppedException e) {
      throw IoUtils.createIOException("Command was stopped", e);
    }
  }


  /**
   * Converts Cygwin path to Windows path. This method mast be
   * executed in Cygwin environment.
   */
  public static String cygwinUnixPathToWindows(final String unixPath) throws IOException {
    try {
      if (systemType() != SYSTEM_TYPE_CYGWIN)
        throw new IllegalStateException("Attempted to access cygwin while not in Cygwin environment");
      String result = execute("cygpath --windows --path '" + unixPath + '\'');
      while (result.endsWith(".") || result.endsWith("\n")) {
        result = result.substring(0, result.length() - 1);
      }
      return result;
    } catch (final CommandStoppedException e) {
      throw IoUtils.createIOException("Command was stopped", e);
    }
  }


  /**
   * Executes command. If InputStream is not null; will first
   * write to process OutputStream.
   */
  public static int execute(final File directoryToExecuteIn, final String cmdLine, final Map environment, final OutputStream stdout, final OutputStream stderr) throws IOException, CommandStoppedException {
    OutputStream os = null;
    CloseAwareInputStream is = null;
    CloseAwareInputStream es = null;
//    Thread stdoutThread = null;
//    Thread stderrThread = null;
    InputStreamToFileCopier stdoutCopier = null;
    InputStreamToFileCopier stderrCopier = null;
    try {
      final String[] envp = getEnvironmentParametersFromMap(environment);
      final String[] command = new CommandLineParser().parse(cmdLine);
      final Process process = Runtime.getRuntime().exec(command, envp, directoryToExecuteIn);
      os = process.getOutputStream();
      is = new CloseAwareInputStream(process.getInputStream());
      es = new CloseAwareInputStream(process.getErrorStream());
      stdoutCopier = new InputStreamToFileCopier(is, stdout);
      stderrCopier = new InputStreamToFileCopier(es, stderr);
      streamCopierPool.execute(stderrCopier);
      streamCopierPool.execute(stdoutCopier);
//      stdoutThread = ThreadUtils.makeDaemonThread(stdoutCopier, "StdoutCopier");
//      stderrThread = ThreadUtils.makeDaemonThread(stderrCopier, "StderrCopier");
//      stdoutThread.start();
//      stderrThread.start();
      waitForProcess(process);
      final int exitValue = process.exitValue();
      waitForCopier(stderrCopier);
      waitForCopier(stdoutCopier);
      return exitValue;
    } catch (final InterruptedException e) {
      throw new CommandStoppedException(e);
    } finally {
      waitForCopier(stderrCopier);
      waitForCopier(stdoutCopier);
      IoUtils.closeHard(os);
      IoUtils.closeHard(is);
      IoUtils.closeHard(es);
    }
  }


  /**
   * Executes command
   */
  public static int execute(final File directoryToExecuteIn, final String cmdLine, final Map environment, final Tailer tailer, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException {
    validateCurrentDir(directoryToExecuteIn);
    IoUtils.deleteFileHard(stdoutFile);
    IoUtils.deleteFileHard(stderrFile);
    IoUtils.deleteFileHard(mergedFile);
    OutputStream os = null;
    CloseAwareInputStream is = null;
    CloseAwareInputStream es = null;
    SyncronizedPrintWriter mergedWriter = null;
    InputStreamToFileCopier stdoutCopier = null;
    InputStreamToFileCopier stderrCopier = null;
    final String[] envp = getEnvironmentParametersFromMap(environment);
    final String[] command = new CommandLineParser().parse(cmdLine);
    try {
      final Process process = Runtime.getRuntime().exec(command, envp, directoryToExecuteIn);
      os = process.getOutputStream();
      is = new CloseAwareInputStream(process.getInputStream());
      es = new CloseAwareInputStream(process.getErrorStream());
      if (mergedFile == null) {
        stdoutCopier = new InputStreamToFileCopier(is, stdoutFile);
        stderrCopier = new InputStreamToFileCopier(es, stderrFile);
      } else {
        mergedWriter = new SyncronizedPrintWriter(new BufferedOutputStream(new FileOutputStream(mergedFile)), tailer);
        stdoutCopier = new InputStreamToFileCopierAndMerger(is, stdoutFile, mergedWriter);
        stderrCopier = new InputStreamToFileCopierAndMerger(es, stderrFile, mergedWriter);
      }
      streamCopierPool.execute(stderrCopier);
      streamCopierPool.execute(stdoutCopier);
      waitForProcess(process);
      final int exitValue = process.exitValue();
      waitForCopier(stderrCopier);
      waitForCopier(stdoutCopier);
      return exitValue;
    } catch (final InterruptedException e) {
      throw new CommandStoppedException(e);
    } finally {
      waitForCopier(stderrCopier);
      waitForCopier(stdoutCopier);
      IoUtils.closeHard(mergedWriter);
      IoUtils.closeHard(os);
      IoUtils.closeHard(is);
      IoUtils.closeHard(es);
    }
  }


  public static int execute(final File directoryToExecuteIn, final String cmdLine, final Map environment, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException {
    return execute(directoryToExecuteIn, cmdLine, environment, new ZeroLengthTailer(), stdoutFile, stderrFile, mergedFile);
  }


  private static void validateCurrentDir(final File directoryToExecuteIn) throws IOException {
    if (directoryToExecuteIn != null) {
      if (!directoryToExecuteIn.exists())
        throw new IOException("Directory does not exist: \"" + directoryToExecuteIn.getCanonicalPath() + '\"');
      if (!directoryToExecuteIn.isDirectory())
        throw new IOException("Path is not a directory: \"" + directoryToExecuteIn.getCanonicalPath() + '\"');
    }
  }


  /**
   * Executes cmdLine
   */
  public static String execute(final String cmdLine) throws IOException, CommandStoppedException {
    ByteArrayOutputStream os = null;
    try {
      os = new ByteArrayOutputStream(1000);
      execute(null, cmdLine, null, os, new NullOutputStream());
      return os.toString();
    } finally {
      IoUtils.closeHard(os);
    }
//
//    OutputStream os = null;
//    InputStreamReader isr = null;
//    try {
//      StringBuffer result = new StringBuffer(100);
//      Process process = Runtime.getRuntime().exec(cmdLine);
//      os = process.getOutputStream();
//      isr = new InputStreamReader(process.getInputStream());
//      int bytesRead;
//      char[] b = new char[100];
//      while ((bytesRead = isr.read(b)) != -1) {
//        result.append(b, 0, bytesRead);
//      }
//      waitForProcess(process);
//      process.exitValue();
//      return result.toString();
//    } finally {
//      IoUtils.closeHard(isr);
//      IoUtils.closeHard(os);
//    }
  }


  /**
   * @return true if system considered unix in terms of shell.
   */
  public static boolean isUnix() {
    return systemType() == SYSTEM_TYPE_CYGWIN || systemType() == SYSTEM_TYPE_UNIX || systemType() == SYSTEM_TYPE_SUNOS || systemType() == SYSTEM_TYPE_HPUX || systemType() == SYSTEM_TYPE_LINUX || systemType() == SYSTEM_TYPE_DEBIAN || systemType() == SYSTEM_TYPE_MACOSX;
  }


  /**
   * @return true if system considered windows in terms of
   * shell.
   */
  public static boolean isWindows() {
    return systemType() == SYSTEM_TYPE_WIN95 || systemType() == SYSTEM_TYPE_WINNT;
  }


  /**
   * Accepts exact path to a given command. A command may be in
   * uneven quotes (both single and double).
   */
  public static boolean commandIsAvailable(final String command) {
    try {
      final String quotedCommand = StringUtils.putIntoDoubleQuotes(command);
      final String[] parsed = new CommandLineParser().parse(quotedCommand);
      if (parsed.length != 1) return false; // not a single command
      final File commmandFile = new File(parsed[0]);
      // first try to find given executable file in path if command is not in path
      if (!commmandFile.isAbsolute()) {
        final String pathVariable = getEnvVariable(isWindows() ? "Path" : "PATH");
        final StringTokenizer stPath = new StringTokenizer(pathVariable, File.pathSeparator, false);
        while (stPath.hasMoreTokens()) {
          String commandToCheck = null;
          if (isWindows() && !command.endsWith(EXE_SUFFIX)) {
            commandToCheck = command + EXE_SUFFIX;
          } else {
            commandToCheck = command;
          }
          final String path = stPath.nextToken();
          final File fullPath = new File(path, commandToCheck);
          if (fullPath.exists() && fullPath.isFile()) return true;
        }
      }
      if (!commmandFile.exists()) return false;
      return commmandFile.isFile();
    } catch (final CommandLineParserException e) {
      return false;
    }
  }


  /**
   * Helper method to wait for process to exit
   *
   * @param process
   */
  private static void waitForProcess(final Process process) throws CommandStoppedException {
    try {
      process.waitFor();
    } catch (final InterruptedException e) {
      if (log.isDebugEnabled()) log.debug("will destroy process", e);
      process.destroy();
      throw new CommandStoppedException();
    }
  }


  public static Map getStartupEnv() {
    return Collections.unmodifiableMap(startupEnv);
  }


  /**
   * This helper method transforms Map with string name-value
   * pair to a String array suitable for passing to execute
   * method
   *
   * @param environment
   */
  public static String[] getEnvironmentParametersFromMap(final Map environment) {

    if (environment == null || environment.isEmpty()) { // do not overwrite env
      return null;
    }

    final Map executeMap = new TreeMap(startupEnv); // copy in startup env

    for (final Iterator i = environment.entrySet().iterator(); i.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) i.next();
      if (entry.getValue() != null) {
        executeMap.put(entry.getKey(), entry.getValue());
      } else {
        // null value signals that a variable should be removed
        executeMap.remove(entry.getKey());
      }
    }

    // create string array
    int index = 0;
    final String[] result = new String[executeMap.size()];
    for (final Iterator iter = executeMap.entrySet().iterator(); iter.hasNext(); ) {
      final Map.Entry e = (Map.Entry) iter.next();
      result[index++] = e.getKey() + "=" + e.getValue();
    }
    return result;
  }


  /**
   * Tries to delete a file using OS's "remove" command.
   *
   * @param pathToDelete
   * @return false if a path was still there after deletion.
   */
  public static boolean deleteHard(final File pathToDelete) {
    if (log.isDebugEnabled()) log.debug("deleting hard way: " + pathToDelete);
    final boolean deletable = pathToDelete != null && pathToDelete.exists();
    if (log.isDebugEnabled()) log.debug("deletable: " + deletable);
    if (!deletable) return true;

    if (isUnix()) {

      deleteHardOnUnix(pathToDelete);
    } else if (isWindows()) {

      deleteHardOnWindows(pathToDelete);
    }

    final boolean existsAfterDelete = pathToDelete.exists();
    if (log.isDebugEnabled()) log.debug("existsAfterDelete: " + existsAfterDelete);
    return !existsAfterDelete;
  }


  /**
   * Shuts down internally started threads. This method should be called once right before the server shutdown.
   */
  public static void shutdown() {

    streamCopierPool.shutdownNow();
  }


  private static void deleteHardOnUnix(final File pathToDelete) {

    File rm = null;
    if (systemType() == SYSTEM_TYPE_LINUX || systemType() == SYSTEM_TYPE_DEBIAN || systemType() == SYSTEM_TYPE_MACOSX) {
      rm = new File("/bin/rm");
    } else if (systemType() == SYSTEM_TYPE_SUNOS) {
      rm = new File("/usr/bin/rm");
    }
    final boolean canUseHardDelete = rm != null && rm.exists();
    if (log.isDebugEnabled()) log.debug("canUseHardDelete: " + canUseHardDelete);
    if (canUseHardDelete) {

      try {

        // validate path is not "root-like"
        if (IoUtils.isProhibitedPath(pathToDelete)) {

          throw new IOException("Cannot delete prohibited path: " + pathToDelete);
        }

        final File currentDir = pathToDelete.getParentFile();
        if (log.isDebugEnabled()) log.debug("currentDir: " + currentDir);

        // make rm command
        final StringBuilder cmd = new StringBuilder(200);
        cmd.append(rm.getCanonicalPath());
        if (pathToDelete.isDirectory()) {

          cmd.append(" -rf ");
        } else {

          cmd.append(" -f ");
        }
        cmd.append(StringUtils.putIntoDoubleQuotes(pathToDelete.getCanonicalPath()));
//        if (log.isDebugEnabled()) log.debug("cmd: " + cmd.toString());
        // execute
        final int rc = execute(currentDir, cmd.toString(), null, new NullOutputStream(), new NullOutputStream());
        if (log.isDebugEnabled()) log.debug("rc: " + rc);
      } catch (final CommandStoppedException e) {

        IoUtils.ignoreExpectedException(e);
      } catch (final IOException e) {

        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  private static void deleteHardOnWindows(final File pathToDelete) {

    try {

      // Validate path is not "root-like"
      if (IoUtils.isProhibitedPath(pathToDelete)) {
        throw new IOException("Cannot delete prohibited path: " + pathToDelete);
      }

      final File currentDir = pathToDelete.getParentFile();

      // Make rd command
      final StringBuilder cmd = new StringBuilder(200);
      cmd.append("cmd.exe /c rd /s /q ");
      cmd.append(StringUtils.putIntoDoubleQuotes(pathToDelete.getCanonicalPath()));

      // Execute
      final int rc = execute(currentDir, cmd.toString(), null, new NullOutputStream(), new NullOutputStream());
      if (log.isDebugEnabled()) log.debug("rc: " + rc);

    } catch (final CommandStoppedException e) {

      IoUtils.ignoreExpectedException(e);
    } catch (final IOException e) {

      IoUtils.ignoreExpectedException(e);
    }
  }


  private static void waitForCopier(final InputStreamToFileCopier copier) {
    try {
      if (copier == null) return;
      copier.getFinishLatch().acquire();
    } catch (final InterruptedException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }
}
