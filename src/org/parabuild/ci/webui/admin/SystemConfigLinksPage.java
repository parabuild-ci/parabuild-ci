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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is repsonsible for parabuild configuration options.
 */
public final class SystemConfigLinksPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -7355330766753424778L; // NOPMD

  protected Result executeSystemConfigurationPage(final Parameters params) {
    return Result.Done();
  }
}
