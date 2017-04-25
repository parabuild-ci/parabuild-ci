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

import java.io.*;
import javax.servlet.http.*;

import org.parabuild.ci.build.*;
import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.*;

/**
 * This servlet is reponsible for servicing requests for build
 * status. The URL format is the following:
 * <p/>
 * http://parabuild:8080/parabuild/build/status/buildstatus.js?buildid=buildID&buildname
 *
 * @see #PARAM_BORDER_COLOR
 * @see #PARAM_BUILD_NAME
 * @see #PARAM_BORDER_WIDTH
 *
 */
public final class JavascriptBuildStatusServlet extends HttpServlet {

  public static final String IMAGE_ACCESS_FORBIDDEN = "bullet_ball_glass_yellow.gif";
  public static final String IMAGE_FAILED = "bullet_ball_glass_red.gif";
  public static final String IMAGE_NOT_RUN_YET = "bullet_ball_glass_blue.gif";
  public static final String IMAGE_SUCCESSFUL = "bullet_ball_glass_green.gif";
  public static final String IMAGE_RUNNING_WAS_SUCCESSFUL = "bullet_triangle_green.gif";
  public static final String IMAGE_RUNNING_FAILED = "bullet_triangle_red.gif";
  public static final String IMAGE_RUNNING_NOT_RUN_YET = "bullet_triangle_blue.gif";

  public static final String PARAM_BUILD_NAME = "showbuildname";
  public static final String PARAM_BORDER_COLOR = "bordercolor";
  public static final String PARAM_BORDER_WIDTH = "borderwidth";


  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    // Output: image, text
    // Cases:
    //   #1 - build successful;
    //   #2 - build failed;
    //   #3 - not run yet;
    //   #4 - access forbidden
    String imageName = null;
    String caption = null;
    String href = null;
    String anchorStyle = null;
    try {
      // get build config
      final SecurityManager sm = SecurityManager.getInstance();
      final BuildConfig buildConfig = sm.getFeedBuildConfigurationFromRequest(req);
      final BuildService build = ServiceManager.getInstance().getBuildListService().getBuild(buildConfig.getActiveBuildID());
      final BuildState buildState = build.getBuildState();
      final BuildRun lastCompleteBuildRun = buildState.getLastCompleteBuildRun();

      // make caption and achor style based on status
      if (lastCompleteBuildRun == null) {
        caption = "Not run yet";
        imageName = imageNameSwitch(buildState.isRunning(), IMAGE_RUNNING_NOT_RUN_YET, IMAGE_NOT_RUN_YET);
        anchorStyle = "color: blue";
      } else if (lastCompleteBuildRun.successful()) {
        caption = makeCaptionPrefix(lastCompleteBuildRun, showBuildName(req)) + " was successful";
        imageName = imageNameSwitch(buildState.isRunning(), IMAGE_RUNNING_WAS_SUCCESSFUL, IMAGE_SUCCESSFUL);
        anchorStyle = "color: green";
      } else {
        caption = makeCaptionPrefix(lastCompleteBuildRun, showBuildName(req)) + " failed";
        imageName = imageNameSwitch(buildState.isRunning(), IMAGE_RUNNING_FAILED, IMAGE_FAILED);
        anchorStyle = "color: red";
      }

      // make URL pointer to build
      final BuildStatusURLGenerator statusURLGenerator = new BuildStatusURLGenerator();
      href = statusURLGenerator.makeBuildStatusURL(buildConfig.getActiveBuildID());

    } catch (BadRequestException e) {
      caption = "Build not found";
      imageName = IMAGE_ACCESS_FORBIDDEN;
    } catch (AccessForbiddenException e) {
      caption = "Access forbidden";
      imageName = IMAGE_ACCESS_FORBIDDEN;
    }

    // make image source
    final SystemConfigurationManager manager = SystemConfigurationManagerFactory.getManager();
    final String hostNameAndPort = manager.getBuildManagerProtocolHostAndPort();
    final String imageSource = hostNameAndPort + "/parabuild/images/3232/" + imageName;

    // write script
    resp.setContentType("text/javascript");
    final PrintWriter writer = resp.getWriter();
    writer.print(" document.write(\"<span style='" + makeSpanStyle(req) + "'>\");");
    writer.print(" document.write(\"<img src='" + imageSource + "' style='padding: 2px; vertical-align: middle;'/>\");");
    if (StringUtils.isBlank(href)) {
      writer.print(" document.write(\"" + caption + "\");");
    } else {
      writer.print(" document.write(\"<a href='" + href + "' style='" + anchorStyle + "'>" + caption + "</a>\");");
    }
    writer.print(" document.write(\"</span>\");");
  }


  /**
   * Helper method to switch image names based on the selector.
   *
   * @param selector
   * @param imageNameTrue returned if selector is true
   * @param imageNameFalse returned if selector is false
   */
  private String imageNameSwitch(final boolean selector, final String imageNameTrue, final String imageNameFalse) {
    if (selector) {
      return imageNameTrue;
    } else {
      return imageNameFalse;
    }
  }


  /**
   * Helper method. Returns true if "showbuildname" parameter is
   * present and equals "true".
   *
   * @param req
   *
   * @return true if "showbuildname" parameter is present and
   *         equals "true".
   */
  private boolean showBuildName(final HttpServletRequest req) {
    final String parameter = req.getParameter(PARAM_BUILD_NAME);
    if (StringUtils.isBlank(parameter)) return false;
    return parameter.equalsIgnoreCase("true");
  }


  /**
   * Helper method to create span style.
   */
  private String makeSpanStyle(final HttpServletRequest req) {
    // border color
    String borderColor = req.getParameter(PARAM_BORDER_COLOR);
    if (StringUtils.isBlank(borderColor)) borderColor = "C0C0C0";

    // border width
    String borderWidth = req.getParameter(PARAM_BORDER_WIDTH);
    if (StringUtils.isBlank(borderWidth)) borderWidth = "1px";

    // result
    return "border: "  + borderWidth + " solid " + '#' + borderColor + "; padding: 3px; margin: 2px 0.15em; ";
  }


  /**
   * Helper method to create caption prefix.
   */
  private String makeCaptionPrefix(final BuildRun lastCompleteBuildRun, final boolean showBuildName) {
    if (showBuildName) {
      return lastCompleteBuildRun.getBuildName() + '#' + lastCompleteBuildRun.getBuildRunNumberAsString();
    } else {
      return "Build " + lastCompleteBuildRun.getBuildRunNumberAsString();
    }
  }
}
