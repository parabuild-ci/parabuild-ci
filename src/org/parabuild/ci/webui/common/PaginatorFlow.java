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
package org.parabuild.ci.webui.common;

import java.util.*;

import org.parabuild.ci.common.StringUtils;
import viewtier.ui.Flow;
import viewtier.ui.Parameters;

/**
 */
public final class PaginatorFlow extends Flow {

  private static final long serialVersionUID = 5709845224353856193L;
  private final int pageCount;
  private int selectedPage;


  public PaginatorFlow(final String page, final Properties linkParameters, final int recordCount, final Parameters parameters, final int pageLength) {
    final int fullPageCount = recordCount / pageLength;
    pageCount = fullPageCount + (fullPageCount * pageLength < recordCount ? 1 : 0);
    selectedPage = parameters.isParameterPresent(Pages.PARAM_PAGE_NUM) && StringUtils.isValidInteger(parameters.getParameterValue(Pages.PARAM_PAGE_NUM)) ? Integer.parseInt(parameters.getParameterValue(Pages.PARAM_PAGE_NUM)) : 1;
    if (selectedPage < 1) {
      selectedPage = 1;
    } else if (selectedPage > pageCount) {
      selectedPage = pageCount;
    }
    if (pageCount > 1) {
      add(new BoldCommonLabel("Pages: "));
      for (int i = 1; i <= pageCount; i++) {
        if (i == selectedPage) {
          add(new CommonLabel(Integer.toString(i)));
        } else {
          final Properties pageParameters = new Properties();
          pageParameters.putAll(linkParameters);
          pageParameters.setProperty(Pages.PARAM_PAGE_NUM, Integer.toString(i));
          add(new CommonBoldLink(Integer.toString(i), page, pageParameters));
        }
        if (i != pageCount) {
          add(new MenuDividerLabel(false));
        }
      }
    }
  }


  public PaginatorFlow(final String page, final String paramName, final int paramValue, final int recordCount, final Parameters parameters, final int pageLength) {
    this(page, makeLinkParameters(paramName, paramValue), recordCount, parameters, pageLength);
  }


  public int getPageCount() {
    return pageCount;
  }


  public int getSelectedPage() {
    return selectedPage;
  }


  private static Properties makeLinkParameters(final String paramName, final int paramValue) {
    final Properties p = new Properties();
    p.setProperty(paramName, Integer.toString(paramValue));
    return p;
  }
}
