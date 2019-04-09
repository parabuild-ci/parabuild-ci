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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Analyzes  build log
 */
public final class BuildLogAnalyzer {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildLogAnalyzer.class); // NOPMD

  private static final String BUILD_RESULT_CANNOT_BE_IDENTIFIED = "Build result cannot be identified";

  private int errorWindowSize = ConfigurationConstants.DEFAULT_ERROR_LOG_QUOTE_SIZE;
  private String[][] stringFailurePatterns = null;
  private String[][] stringSuccessPatterns = null;
  private final LinkedList logWindow = new LinkedList();
  private final Pattern[] regexFailurePatterns;
  private final Pattern[] regexSuccessPatterns;


  /**
   * Create log analyzer based on the given build sequence. Build
   * sequence should contain custom-defined failure and success
   * patterns.
   *
   * @param seq
   * @param useSeedPatterns if true will use predefined known to
   *                        be failures patterns.
   */
  public BuildLogAnalyzer(final BuildSequence seq, final boolean useSeedPatterns) {
//    final BuildSequence sequence = seq;

    // failure patterns
    final ArrayList seedFailurePatterns = new ArrayList(20);
    if (useSeedPatterns) {
      // general shell errors
      seedFailurePatterns.add(new String[]{"is not recognized as an internal or external command"});
      seedFailurePatterns.add(new String[]{"bash:", "command not found"});
      seedFailurePatterns.add(new String[]{"sh:", "command not found"});
      seedFailurePatterns.add(new String[]{"ksh:", "command not found"});
      seedFailurePatterns.add(new String[]{"Can't open perl script", "No such file or directory"});

      // general ant errors
      seedFailurePatterns.add(new String[]{"Build failed"});
      seedFailurePatterns.add(new String[]{"BUILD FAILED"});
      seedFailurePatterns.add(new String[]{"Compile failed; see the compiler error output for details"});
      seedFailurePatterns.add(new String[]{"Buildfile:", "does not exist!"});

      // general make errors
      seedFailurePatterns.add(new String[]{"make:", "No targets specified and no makefile found.", "Stop."});
      seedFailurePatterns.add(new String[]{"make:", "No rule to make target &&  Stop."});
    }
    // custom errors
    stringFailurePatterns = makeWorkingStringPatterns(seedFailurePatterns, seq.getFailurePatterns());
    regexFailurePatterns = StringUtils.makeRegexPatternsFromMultilineString(seq.getFailurePatterns());

    // success patterns
    final List seedSuccessPatterns = new ArrayList(5);
    stringSuccessPatterns = makeWorkingStringPatterns(seedSuccessPatterns, seq.getSuccessPatterns());
    regexSuccessPatterns = StringUtils.makeRegexPatternsFromMultilineString(seq.getSuccessPatterns());
  }


  /**
   * Create log analyzer based on the given build sequence. Build
   * sequence should contain custom-defined failure and success
   * patterns only. Predefined patterns are not used.
   *
   * @param seq
   */
  public BuildLogAnalyzer(final BuildSequence seq) {
    this(seq, false);
  }


  /**
   * Sets error quote size
   */
  public void setErrorWindowSize(final int errorWindowSize) {
    this.errorWindowSize = errorWindowSize;
  }


  /**
   * Analizes build log
   *
   * @param buildLog
   * @throws BuildException
   */
  public Result analyze(final File buildLog) throws BuildException {

    BufferedReader reader = null;

    // split failure patterns into plain strings and regex
    try {
      logWindow.clear();
      int halfWindow = errorWindowSize >> 1;
      boolean errorFound = false;
      boolean successFound = false;
      String errorDescription = "";
      String successDescription = "";
      reader = new BufferedReader(new FileReader(buildLog));
      for (String logLine = reader.readLine(); logLine != null;) {
        if (!logLine.isEmpty()) {
          pushLineToLogWindow(logLine);
          if (errorFound) {
            // just slide down half of error window
            if (--halfWindow <= 0) {
              break;
            }
          } else {
            // check
            errorFound = findStringPattern(logLine, this.stringFailurePatterns) || findRegexPattern(logLine, regexFailurePatterns);
            if (errorFound) {
              errorDescription = logLine;
            }
          }
          if (!successFound) {
            // check
            successFound = findStringPattern(logLine, stringSuccessPatterns) || findRegexPattern(logLine, regexSuccessPatterns);
            if (successFound) {
              successDescription = logLine;
            }
          }
        }
        logLine = reader.readLine();
      }

      final byte result;
      final String resultDescription;
      boolean patternFound = false;
      if (errorFound) {
        result = BuildRun.BUILD_RESULT_BROKEN;
        resultDescription = errorDescription;
        patternFound = true;
      } else if (successFound) {
        result = BuildRun.BUILD_RESULT_SUCCESS;
        resultDescription = successDescription;
        patternFound = true;
      } else {
        result = BuildRun.BUILD_RESULT_BROKEN; // nothing was found, which a error itself
        resultDescription = BUILD_RESULT_CANNOT_BE_IDENTIFIED;
      }
      return new Result((List) logWindow.clone(), patternFound, result, resultDescription);

    } catch (final Exception e) {
      throw new BuildException("Error while analizing build results: " + StringUtils.toString(e), e);
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  /**
   * Writes resulting log window to a file
   *
   * @param toWrite File to write to
   * @throws FileNotFoundException
   */
  public void writeLogWindow(final File toWrite) throws FileNotFoundException {
    if (logWindow.isEmpty()) {
      return;
    }
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(new FileOutputStream(toWrite));
      for (final Iterator iter = logWindow.iterator(); iter.hasNext();) {
        final String line = (String) iter.next();
        pw.println(line);
      }
    } finally {
      IoUtils.closeHard(pw);
    }
  }


  /**
   * Finds pattern in line
   *
   * @param logLine
   * @param patternSet
   */
  private boolean findStringPattern(final String logLine, final String[][] patternSet) {
    // count patterns in line
    for (int j = 0; j < patternSet.length; j++) {
      int patternsFound = 0;
      final String[] linePatterns = patternSet[j];
      for (int i = 0; i < linePatterns.length; i++) {
        final String linePattern = linePatterns[i];
        if (logLine.length() < linePattern.length()) {
          break;
        }
        if (logLine.contains(linePattern)) {
          patternsFound++;
        }
      }
      if (patternsFound == linePatterns.length) {
        return true;
      }
    }
    return false;
  }


  /**
   * Adds line to error window
   *
   * @param logLine
   */
  private void pushLineToLogWindow(final String logLine) {
    if (errorWindowSize <= 0) {
      return;
    }
    if (logWindow.size() == errorWindowSize) {
      logWindow.removeFirst();
    }
    logWindow.add(logLine);
  }


  /**
   * Makes pattern array of string arrays from seed pattern set
   * and string break -delimited custom patterns.
   *
   * @param seedPatternSet
   * @param customPatterns
   */
  private static String[][] makeWorkingStringPatterns(final List seedPatternSet, final String customPatterns) {
    for (final StringTokenizer st = new StringTokenizer(customPatterns, "\n", false); st.hasMoreTokens();) {
      final String pattern = st.nextToken().trim();
      if (!pattern.isEmpty()) {
        if (StringUtils.isRegex(pattern)) {
          continue;
        }
        seedPatternSet.add(0, new String[]{pattern});
      }
    }
    return (String[][]) seedPatternSet.toArray(new String[seedPatternSet.size()][]);
  }


  private boolean findRegexPattern(final String logLine, final Pattern[] regexPatterns) {
    for (int i = 0; i < regexPatterns.length; i++) {
      if (regexPatterns[i].matcher(logLine).matches()) {
        return true;
      }
    }
    return false;
  }


  public static final class Result {

    private final List errorWindowLines;
    private final boolean patternFound;
    private final byte result;
    private final String resultDescription;


    public Result(final List logWindowLines, final boolean patternFound, final byte result, final String resultDescription) {
      this.errorWindowLines = logWindowLines;
      this.patternFound = patternFound;
      this.result = result;
      this.resultDescription = resultDescription;
    }


    public List getLogWindowLines() {
      return Collections.unmodifiableList(errorWindowLines);
    }


    public byte getResult() {
      return result;
    }


    public String getResultDescription() {
      return resultDescription;
    }


    /**
     * @return if a neither success nor failure pattern is found
     *         this method will return false, {@link #getResult()} will
     *         return {@link BuildRun#BUILD_RESULT_BROKEN} and {@link
     *         #getResultDescription()} will return string containing {@link
     *         BuildRun#BUILD_RESULT_UNKNOWN}.
     */
    public boolean isPatternFound() {
      return patternFound;
    }


    public String toString() {
      return "Result{" +
              "errorWindowLines=" + errorWindowLines +
              ", patternFound=" + patternFound +
              ", result=" + result +
              ", errorLine='" + resultDescription + '\'' +
              '}';
    }
  }


  public String toString() {
    return "BuildLogAnalyzer{" +
            "errorWindowSize=" + errorWindowSize +
            ", stringFailurePatterns=" + (stringFailurePatterns == null ? null : Arrays.asList(stringFailurePatterns)) +
            ", stringSuccessPatterns=" + (stringSuccessPatterns == null ? null : Arrays.asList(stringSuccessPatterns)) +
            ", logWindow=" + logWindow +
            ", regexFailurePatterns=" + (regexFailurePatterns == null ? null : Arrays.asList(regexFailurePatterns)) +
            ", regexSuccessPatterns=" + (regexSuccessPatterns == null ? null : Arrays.asList(regexSuccessPatterns)) +
            '}';
  }
}
