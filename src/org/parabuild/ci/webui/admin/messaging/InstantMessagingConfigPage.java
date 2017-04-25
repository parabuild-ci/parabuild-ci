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
package org.parabuild.ci.webui.admin.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.webui.admin.system.AbstractSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.ConversationalTierlet;

/**
 * This page is responsible for configurarin IM
 */
public final class InstantMessagingConfigPage extends AbstractSystemConfigurationPage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(InstantMessagingConfigPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Instant Messaging Configration";

  /**
   * Constructor
   */
  public InstantMessagingConfigPage() {
    super(Pages.ADMIN_INSTANT_MESSAGING_CONFIG, new JabberNotificationConfigPanel());
    setTitle(makeTitle(PAGE_TITLE_DEFAULT));
  }
}


