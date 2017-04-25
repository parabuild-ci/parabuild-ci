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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;

/**
 * SaveErrorProcessor
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 16, 2009 9:10:15 PM
 */
public final class SaveErrorProcessor {

  public boolean process(final MessagePanel messagePanel, final Exception e) {
    // show error
    final String description = "Error while saving information: " + StringUtils.toString(e);
    messagePanel.showErrorMessage(description + ". Please cancel editing and try again.");
    // record error
    final Error error = new Error(description);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
    return false;
  }
}
