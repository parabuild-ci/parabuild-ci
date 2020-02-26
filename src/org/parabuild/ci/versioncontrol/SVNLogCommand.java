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
package org.parabuild.ci.versioncontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * Subversion update command. Typical command would look like
 * <p/>
 * <code>
 * svn --non-interactive --username <user name> --password <password> --stop-on-copy --verbose log <URL>/<depot path>
 * </code>
 */
final class SVNLogCommand extends SVNCommand {

  private static final Log log = LogFactory.getLog(SVNLogCommand.class);

  private final RepositoryPath depotPath;
  private final String changeListNumberFrom;
  private final String changeListNumberTo;
  private final boolean useLimitOption;
  private final int maxChangeLists;
  private boolean useXMLFormat = true;


  /**
   * Constructor.
   *
   * @param agent              to run command at.
   * @param exePath            path to svn executable.
   * @param url                SVN depot URL
   * @param depotPath          relative path at SVN depot.
   * @param changeListNumberTo
   * @param useLimitOption
   * @param maxChangeLists
   * @throws IOException
   */
  SVNLogCommand(final Agent agent, final String exePath, final String url, final RepositoryPath depotPath,
                final String changeListNumberFrom, final String changeListNumberTo, final boolean useLimitOption,
                final int maxChangeLists) throws IOException, AgentFailureException {

    super(agent, exePath, url);

    // set
    this.depotPath = (RepositoryPath) ArgumentValidator.validateArgumentNotNull(depotPath, "depot path");
    this.changeListNumberFrom = ArgumentValidator.validateArgumentNotBlank(changeListNumberFrom, "change list number");
    this.changeListNumberTo = changeListNumberTo;
    this.useLimitOption = useLimitOption;
    this.maxChangeLists = maxChangeLists;
  }


  public void setUseXMLFormat(final boolean useXMLFormat) {
    this.useXMLFormat = useXMLFormat;
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected final String getExeArguments() {
    if (log.isDebugEnabled()) {
      log.debug("useLimitOption: " + useLimitOption);
    }
    if (log.isDebugEnabled()) {
      log.debug("maxChangeLists: " + maxChangeLists);
    }
    final boolean applyLimitOption = useLimitOption && maxChangeLists != Integer.MAX_VALUE;
    if (log.isDebugEnabled()) {
      log.debug("applyLimitOption: " + applyLimitOption);
    }
    final StringBuilder result = new StringBuilder(200);
    if (useXMLFormat) {
      result.append("--xml").append(' ');
    }
    result.append(applyLimitOption ? " --limit " + maxChangeLists : "");
    result.append(" --stop-on-copy --verbose log ").append(makeRevisionRange());
    result.append(' ').append(StringUtils.putIntoDoubleQuotes(getUrl() + '/' + depotPath.getPath()));
    return result.toString();
  }


  /**
   * Helper method to compse revision range.
   */
  private String makeRevisionRange() {
    final String revisionRange;
    if ("HEAD".equals(changeListNumberFrom)) {
      revisionRange = ""; // no range, all changes.
    } else if (StringUtils.isValidInteger(changeListNumberFrom)) {
      revisionRange = "-r" + changeListNumberTo + ':' + changeListNumberFrom;
    } else {
      throw new IllegalStateException("Unexpected change list number format: " + changeListNumberFrom);
    }
    return revisionRange;
  }


  public String toString() {
    return "SVNLogCommand{" +
            "changeListNumberFrom='" + changeListNumberFrom + '\'' +
            ", changeListNumberTo='" + changeListNumberTo + '\'' +
            ", depotPath=" + depotPath +
            ", maxChangeLists=" + maxChangeLists +
            ", useLimitOption=" + useLimitOption +
            ", useXMLFormat=" + useXMLFormat +
            "} " + super.toString();
  }
}


