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

import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * A panel responsible for displaying generic test results.
 */
final class GenericTestLogPanel extends MessagePanel {

  private static final long serialVersionUID = -4213722365233896310L;


  public GenericTestLogPanel(final ArchiveManager archiveManager, final StepLog stepLog) {

    InputStream archivedLogInputStream = null;
    try {

      showContentBorder(false);
      hideTitle();

      // Load log (in form of a property file in case of a generic test) from the archive
      archivedLogInputStream = archiveManager.getArchivedLogInputStream(stepLog);
      final Properties properties = new Properties();
      properties.load(archivedLogInputStream);
      IoUtils.closeHard(archivedLogInputStream);


      // Iterate property file and display them on the log page
      final GridIterator gridIterator = new GridIterator(this, 2);
      for (final Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {

        final Map.Entry entry = (Map.Entry) iterator.next();
        final String propertyName = (String) entry.getKey();
        final String propertyValue = (String) entry.getKey();
        gridIterator.add(new CommonFieldLabel(propertyName + ':'));
        gridIterator.add(new CommonLabel(propertyValue));
      }
    } catch (final Exception e) {

      // Display error instead of the log
      showErrorMessage("Unexpected error while retrieving a log: " + e.toString());

      // Report error to the system error log.
      final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
      errorManager.reportSystemError(new org.parabuild.ci.error.Error(e.toString(), e));
    } finally {
      IoUtils.closeHard(archivedLogInputStream);
    }
  }
}
