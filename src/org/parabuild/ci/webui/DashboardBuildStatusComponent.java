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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.BuildStatusURLGenerator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DashboardBuildStatusComponent extends CustomComponent {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(DashboardBuildStatusComponent.class); // NOPMD

  private static final String PARABUILD_THROBBER_GREEN = "/parabuild/images/throbber-dash-green.gif";
  private static final String PARABUILD_THROBBER_RED = "/parabuild/images/throbber-dash-red.gif";
  private static final String PARABUILD_THROBBER_GRAY = "/parabuild/images/throbber-dash-gray.gif";

  private BuildState buildState = null;
  private boolean setShowDetails = false;
  private SimpleDateFormat dateFormat = null;
  private static final long serialVersionUID = -6652384422627467378L;


  /**
   * Sets state.
   *
   * @param buildState {@link BuildState}
   */
  public void setBuildState(final BuildState buildState) {
    this.buildState = buildState;
  }


  /**
   * Enables displaying change list and date.
   *
   * @param setShowDetails
   */
  public void setShowDetails(final boolean setShowDetails) {
    this.setShowDetails = setShowDetails;
  }


  /**
   * Sets date format.
   *
   * @param dateFormat
   */
  public void setDateFormat(final SimpleDateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }


  public void render(final RenderContext renderContext) {
    if (WebuiUtils.isBuildRunNotNullAndComplete(buildState.getLastCompleteBuildRun())) {
      if (buildState.getLastCompleteBuildRun().successful()) {
        renderSuccessful(renderContext, buildState.isBusy());
      } else {
        renderFailed(renderContext, buildState.isBusy());
      }
    } else {
      renderNotRunYet(renderContext, buildState.isBusy());
    }
  }


  private void renderSuccessful(final RenderContext renderContext, final boolean busy) {
    final PrintWriter writer = renderContext.getWriter();

    // get URL
    final String url = new BuildStatusURLGenerator().makeBuildStatusURL("", buildState.getActiveBuildID());
    writer.print("<a title=\"" + getTitle() + "\" class=\"dash_status_anchor\" href=\"" + url + "\">");
    writer.print("<div class=\"dash_box_green\">\n" +
            "   <div class=\"dash_top_green\"><div></div></div>\n" +
            "      <div class=\"dash_content_green\">\n");
    writeContent(busy, writer, PARABUILD_THROBBER_GREEN, "&nbsp;");
    writer.print("      </div>\n" +
            // print build name
            "      <div class=\"dash_content_bottom\">" + getDetailsHTML() + "</div>\n" +

            "   <div class=\"dash_bottom_green\"><div></div></div>\n" +
            "</div>");
    writer.print("</a>");
  }


  private void renderFailed(final RenderContext renderContext, final boolean busy) {
    final PrintWriter writer = renderContext.getWriter();

    // get URL
    final String url;
    final BuildStatusURLGenerator buildStatusURLGenerator = new BuildStatusURLGenerator();
    if (busy) {
      url = buildStatusURLGenerator.makeBuildStatusURL("", buildState.getLastCompleteBuildRun());
    } else {
      // REVIEWME: simeshev@parabuilci.org -> make it return path to the broen build error log
      url = buildStatusURLGenerator.makeBuildStatusURL("", buildState.getLastCompleteBuildRun());
    }

    writer.print("<a title=\"" + getTitle() + "\" class=\"dash_status_anchor\" href=\"" + url + "\">");
    writer.print("<div class=\"dash_box_red\">\n" +
            "   <div class=\"dash_top_red\"><div></div></div>\n" +
            "      <div class=\"dash_content_red\">\n");
    final StringBuffer brokenDuration = makeBrokenDuration();
    writeContent(busy, writer, PARABUILD_THROBBER_RED, brokenDuration == null ? "&nbsp;" : brokenDuration.toString());
    writer.print("      </div>\n" +
            // print build name
            "      <div class=\"dash_content_bottom\">" + getDetailsHTML() + "</div>\n" +

            "   <div class=\"dash_bottom_red\"><div></div></div>\n" +
            "</div>");
    writer.print("</a>");
  }


  private void renderNotRunYet(final RenderContext renderContext, final boolean busy) {
    final PrintWriter writer = renderContext.getWriter();

    // get URL
    final String url = new BuildStatusURLGenerator().makeBuildStatusURL("", buildState.getActiveBuildID());
    final String buildName = WebuiUtils.getBuildName(buildState);
    writer.print("<a title=\"" + buildName + "\" class=\"dash_status_anchor\" href=\"" + url + "\">");
    writer.print("<div class=\"dash_box_gray\">\n" +
            "   <div class=\"dash_top_gray\"><div></div></div>\n" +
            "      <div class=\"dash_content_gray\">\n");
    writeContent(busy, writer, PARABUILD_THROBBER_GRAY, "&nbsp;");
    writer.print("      </div>\n" +
            // print build name
            "      <div class=\"dash_content_bottom\">" + buildName + "</div>\n" +

            "   <div class=\"dash_bottom_gray\"><div></div></div>\n" +
            "</div>");
    writer.print("</a>");
  }


  private StringBuffer makeBrokenDuration() {
    final Date finishedAt = buildState.getLastCompleteBuildRun().getFinishedAt();
    if (finishedAt != null) {
      final long lastBuildRunAgo = (new Date().getTime() - buildState.getLastCompleteBuildRun().getFinishedAt().getTime()) / 1000L;
      if (lastBuildRunAgo > 0L) {
        return StringUtils.durationToString(lastBuildRunAgo, false);
      }
    }
    return null;
  }


  private void writeContent(final boolean busy, final PrintWriter writer, final String throbberImageSource, final String idleText) {
    if (busy) {
      writer.print("<img border=\"0\" src=\"" + throbberImageSource + "\"/>");
    } else {
      writer.print(idleText);
    }
  }


  private String getDetailsHTML() {
    final String buildName = WebuiUtils.getBuildName(buildState);
    if (setShowDetails) {
      return new StringBuffer(30)
              .append(buildName).append("<br>").append(getBuildNumber()).append("&nbsp;@&nbsp;")
              .append(getChangeListNumber()).toString();
    } else {
      return buildName;
    }
  }


  private String getTitle() {
    final String date = getDate();
    return new StringBuffer(40)
            .append(WebuiUtils.getBuildName(buildState)).append(" #").append(getBuildNumber())
            .append('@').append(getChangeListNumber()).append(StringUtils.isBlank(date) ? "" : " on " + date).toString();
  }


  private String getDate() {
    String buildDate = "";
    if (!buildState.isBusy() && buildState.getLastCompleteBuildRun() != null) {
      final Date finishedAt = buildState.getLastCompleteBuildRun().getFinishedAt();
      if (finishedAt != null) {
        buildDate = dateFormat.format(finishedAt);
      }
    }
    return buildDate;
  }


  private String getChangeListNumber() {
    String changeListNumber = buildState.getCurrentlyRunningChangeListNumber();
    if (StringUtils.isBlank(changeListNumber)) {
      final BuildRun lastRun = buildState.getLastCompleteBuildRun();
      if (lastRun != null) {
        changeListNumber = lastRun.getChangeListNumber();
      }
    }
    return changeListNumber;
  }


  private String getBuildNumber() {
    String buildNumber = buildState.getCurrentlyRunningBuildNumber() <= 0 ? "" : Integer.toString(buildState.getCurrentlyRunningBuildNumber());
    if (StringUtils.isBlank(buildNumber)) {
      final BuildRun lastRun = buildState.getLastCompleteBuildRun();
      if (lastRun != null) {
        buildNumber = lastRun.getBuildRunNumberAsString();
      }
    }
    return buildNumber;
  }


  public String toString() {
    return "DashboardBuildStatusComponent{" +
            "buildState=" + buildState +
            ", setShowDetails=" + setShowDetails +
            ", dateFormat=" + dateFormat +
            "} " + super.toString();
  }
}
