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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.OrderedList;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This class is a generic error page
 */
public final class ErrorPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 4416156450516265120L; // NOPMD
  private static final Log log = LogFactory.getLog(ErrorPage.class); // NOPMD
  private static final String TEXT_PAGE_NOT_FOUND = "Requested page can not be found.";
  private static final String TEXT_STATUS_PAGES = "Build statuses page";

  private final Panel cp = new Panel();


  public ErrorPage() {
    super.markTopMenuItemSelected(MENU_SELECTION_ERRORS);
    setTitle(makeTitle("Error"));
    baseContentPanel().getUserPanel().add(cp);
  }


  public Result executePage(final Parameters parameters) {

    final String viewtierStatusCode = parameters.getParameterValue(Parameters.PARAM_ERROR_STATUS_CODE);
// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//    if (log.isDebugEnabled()) log.debug("viewtierStatusCode: " + viewtierStatusCode);
//    //if (log.isDebugEnabled()) log.debug("parameters.getParameterNames(): " + parameters.getParameterNames());
//    if (log.isDebugEnabled()) log.debug("======= keys =====");
//    Map m = parameters.getParametersMap();
//    for (final Iterator iter = m.keySet().iterator(); iter.hasNext();) {
//      Object key = iter.next();
//      if (log.isDebugEnabled()) log.debug("key: " + key.toString());
//    }
//
//    if (log.isDebugEnabled()) log.debug("======= values =====");
//    for (final Iterator iter = m.values().iterator(); iter.hasNext();) {
//      Object value = iter.next();
//      if (log.isDebugEnabled()) log.debug("value: " + value.toString());
//    }
// ////////////////////////////////////////////////////////////////////////////////////////

    if (StringUtils.isBlank(viewtierStatusCode)) {
      //
      // non-viewtier web UI status.
      cp.add(new CommonLabel("Requested service currently is not available."));
//      reportError(viewtierStatusCode, parameters);
    } else {
      //
      // viewtier web ui status
      if ("404".equals(viewtierStatusCode)) {
        setTitle(makeTitle(TEXT_PAGE_NOT_FOUND));
        cp.add(new BoldCommonLabel(TEXT_PAGE_NOT_FOUND));
      } else {
        // set content
        setTitle(makeTitle("Requested service is not available."));
        if (isValidAdminUser()) {
          cp.add(new CommonLabel("Requested service currently is not available. Please check administrative list of errors below."));
          cp.add(new CommonLink("Error list", Pages.ADMIN_ERROR_LIST));
        } else {
          cp.add(new CommonLabel("Requested service currently is not available. Build administrator has been notified about the problem. Please try later or contact build administrator."));
        }
        reportError(viewtierStatusCode, parameters);
      }
    }

    // add list of available pages
    final OrderedList olOfferedPages = new OrderedList();
    cp.add(new CommonLabel("Where would you like to go from here?"));
    cp.add(olOfferedPages);
    if (isValidAdminUser()) {
      olOfferedPages.add(new CommonLink(TEXT_STATUS_PAGES, Pages.ADMIN_BUILDS));
      olOfferedPages.add(new CommonLink("Error list", Pages.ADMIN_ERROR_LIST));
    } else {
      olOfferedPages.add(new CommonLink(TEXT_STATUS_PAGES, Pages.PUBLIC_BUILDS));
    }

    return Result.Done();
  }


  /**
   * Reports error to admin list
   */
  private void reportError(final String viewtierStatusCode, final Parameters parameters) {
    final Error error = new Error();
    error.setSubsystemName(Error.ERROR_SUSBSYSTEM_WEBUI);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    error.setOutputToLog(true);
    final String viewtierException = parameters.getParameterValue("viewtier-error-exception-string");
    final String viewtierRequest = parameters.getParameterValue("viewtier-error-request-url");
    final String viewtierStacktrace = parameters.getParameterValue("viewtier-error-exception-stacktrace");
    if (!StringUtils.isBlank(viewtierException)) {
      error.setDescription(viewtierException);
    }
    if (!StringUtils.isBlank(viewtierStatusCode)) {
      error.setHTTPStatus(viewtierStatusCode);
    }
    if (!StringUtils.isBlank(viewtierRequest)) {
      error.setHTTPRequestURL(viewtierRequest);
    }
    if (!StringUtils.isBlank(viewtierStacktrace)) {
      error.setDetails(viewtierStacktrace);
    }
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "ErrorPage{" +
            "cp=" + cp +
            "} " + super.toString();
  }
}
