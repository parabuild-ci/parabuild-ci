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
package org.parabuild.ci.versioncontrol.bazaar;

import org.apache.log4j.Logger;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.VersionControlRemoteCommand;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BazaarCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jan 24, 2010 2:11:30 PM
 */
abstract class BazaarCommand extends VersionControlRemoteCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(BazaarCommand.class); // NOPMD

  private final String exePath;


  protected BazaarCommand(final Agent agent, final String exePath) throws AgentFailureException, IOException {
    super(agent, true);

    // Set and register remote current dir in the signature
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);

    // Set params
    this.exePath = ArgumentValidator.validateArgumentNotBlank(exePath, "Path to bzr executable").trim();

    //noinspection HardcodedLineSeparator
    setInputStream(new ByteArrayInputStream("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n".getBytes()));
  }


  /**
   * Returns arguments to pass to bzr executable including bzr command and it args.
   *
   * @see #preExecute()
   */
  protected abstract String getExeArguments() throws IOException, AgentFailureException;


  /**
   * Callback method - this method is called right after call to
   * execute.
   * <p/>
   * Analyzes log for known errors
   *
   * @param resultCode - execute command result code. /**
   *                   Analyzes log for known errors
   * @throws java.io.IOException if there are errors
   * @noinspection HardcodedLineSeparator
   */
  protected final void postExecute(final int resultCode) throws IOException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("analyze error log");
    }
    BufferedReader reader = null;
    try {
      if (resultCode != 0 && getStderrFile().exists() && getStderrFile().length() > 0) {
        final StringBuffer message = new StringBuffer(100);
        reader = new BufferedReader(new FileReader(getStderrFile()));
        String line = reader.readLine();
        while (line != null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("line: " + line);
          }
          message.append('\n').append("  ").append(line);
          line = reader.readLine();
        }
        throw new IOException("Errors while executing bzr command: " + message + ", command: " + removePasswordFromDebugString(getCommand()));
      }
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected final void preExecute() throws IOException, AgentFailureException {
    validateRemoteCurrentDir();

    // Compose command
    final StringBuffer remoteCommand = new StringBuffer(100);
    remoteCommand.append(exePath);
    remoteCommand.append(' ');
    remoteCommand.append(getExeArguments());

    if (LOG.isDebugEnabled() && StringUtils.systemPropertyEquals("parabuild.bzrcmdd.enabled", "true")) {
      LOG.debug("command: " + remoteCommand);
    }
    // Execute
    super.setCommand(remoteCommand.toString());
  }


  private void validateRemoteCurrentDir() throws IOException {
    if (StringUtils.isBlank(remoteCurrentDir)) {
      throw new IOException("Error accessing Bazaar: Current directory is undefined.");
    }
  }


  /**
   * Converts a branch location to a relative build dir.
   *
   * @param branchLocation
   * @return relative build dir.
   * @throws java.net.MalformedURLException
   */
  public static String branchLocationToRelativeBuildDir(final String branchLocation) {
    final Pattern pattern = Pattern.compile(".*://[^/]+/(.*)");
    final Matcher matcher = pattern.matcher(branchLocation);
    if (!matcher.matches()) {
      return ".";
    }
    final String path = matcher.group(1);
    if (StringUtils.isBlank(path)) {
      return ".";
    }
    final int i = path.lastIndexOf("/");
    if (i < 0) {
      return path;
    } else {
      return path.substring(i + 1);
    }
  }


  public String toString() {
    return "BazaarCommand{" +
            "exePath='" + exePath + '\'' +
            "} " + super.toString();
  }
}