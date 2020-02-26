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
package org.parabuild.ci.webui;

import java.util.*;

import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Displays link to build result details
 */
public final class BuildResultLink extends Flow {

  private static final long serialVersionUID = -8378386076913980253L;
  private String caption = null;
  private ColoredResultLink delegate = null;

  /**
   * Creates the build result link with the given caption.
   *
   * @param caption to use with the link.
   * @param buildRun to display.
   */
  public BuildResultLink(final String caption, final BuildRun buildRun) {
    this.caption = caption;
    this.setBuildRun(buildRun);
  }


  /**
   * Creates the build result link with the given caption.
   *
   * @param caption to use with the link.
   * @param buildRun to display.
   */
  public BuildResultLink(final String caption, final BuildRun buildRun, final String title) {
    this.caption = caption;
    this.setBuildRun(buildRun, title);
  }


  private void setBuildRun(final BuildRun buildRun, final String title) {
    setBuildRun(buildRun);
    if (delegate != null) {
      delegate.setTitle(title);
    }
  }


  /**
   * Creates the build result link with the default caption,
   * which is the result returned by buildRun.buildResultToString().
   *
   * @see BuildRun#buildResultToString()
   */
  public BuildResultLink() {
  }


  /**
   * Creates the build result link with the default caption,
   * which is the result returned by buildRun.buildResultToString().
   *
   * @see BuildRun#buildResultToString()
   */
  public BuildResultLink(final BuildRun buildRun) {
    setBuildRun(buildRun);
  }


  public void setBuildRun(final BuildRun buildRun) {
    // process if build run is not null
    if (buildRun == null) return;
    if (buildRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS) {
      addChangesLink(buildRun); // successful, add changes
    } else {
      // try to get logs
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final StepLog log = cm.getFirstBokenLog(buildRun.getBuildRunID());
      if (log == null) {
        addChangesLink(buildRun); // log not found, add link to changes
      } else {
        addBrokenLogLink(buildRun, log); // log found, add link to log
      }
    }
  }


  private void addChangesLink(final BuildRun buildRun) {
    delegate = new ColoredResultLink(caption, buildRun, Pages.BUILD_CHANGES,
      Pages.PARAM_BUILD_RUN_ID, buildRun.getBuildRunIDAsString());
    add(delegate);
  }


  private void addBrokenLogLink(final BuildRun buildRun, final StepLog log) {
    delegate = new ColoredResultLink(caption, buildRun, Pages.BUILD_LOG,
      Pages.PARAM_LOG_ID, log.getIDAsString());
    add(delegate);
  }


  /**
   * This link component changes color depending on build
   * result.
   */
  private static final class ColoredResultLink extends CommonLink {


    private static final long serialVersionUID = -5964393683958399383L;


    public ColoredResultLink(final String caption, final BuildRun buildRun, final String tierlet, final String paramName, final String paramValue) {
      super(!StringUtils.isBlank(caption) ? caption : new VerbialBuildResult().getVerbialResultString(buildRun), tierlet, makeParameters(buildRun, paramName, paramValue));
      setFont(Pages.FONT_BUILD_RESULT);
      if (buildRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS) {
        setForeground(WebuiUtils.makeResultColor(getTierletContext(), Pages.COLOR_BUILD_SUCCESSFUL, UserProperty.SUCCESSFUL_BUILD_COLOR));
      } else {
        if (buildRun.completed()) {
          setForeground(WebuiUtils.makeResultColor(getTierletContext(), Pages.COLOR_BUILD_FAILED, UserProperty.FAILED_BUILD_COLOR));
        } else {
          setForeground(WebuiUtils.makeResultColor(getTierletContext(), Pages.COLOR_BUILD_SUCCESSFUL, UserProperty.SUCCESSFUL_BUILD_COLOR));
        }
      }
    }


    private static Properties makeParameters(final BuildRun buildRun, final String paramName, final String paramValue) {
      final Properties params = new Properties();
      params.setProperty(Pages.PARAM_BUILD_RUN_ID, buildRun.getBuildRunIDAsString());
      params.setProperty(paramName, paramValue);
      return params;
    }
  }


  public String toString() {
    return "BuildResultLink{" +
      "caption='" + caption + '\'' +
      ", delegate=" + delegate +
      '}';
  }
}
