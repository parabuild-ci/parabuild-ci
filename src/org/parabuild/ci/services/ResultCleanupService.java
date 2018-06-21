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
package org.parabuild.ci.services;

import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Service that periodically cleans up results and logs.
 */
public final class ResultCleanupService implements Service {

  private final Timer timer = new Timer(true);

  private volatile byte status = SERVICE_STATUS_NOT_STARTED;


  public void startupService() {


    status = SERVICE_STATUS_STARTED;

    final TimerTask timerTask = new TimerTask() {
      public void run() {

        final List buildsConfigs = ConfigurationManager.getInstance().getAllBuildConfigurations();
        for (final Iterator i = buildsConfigs.iterator(); i.hasNext(); ) {

          try {
            final BuildConfig config = (BuildConfig) i.next();
            final ArchiveManager archiveManager = ArchiveManagerFactory.getArchiveManager(config.getActiveBuildID());
            archiveManager.packExpiredBuildLogs();
            archiveManager.deleteExpiredBuildResults();
          } catch (final Exception e) {
            final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
            errorManager.reportSystemError(new org.parabuild.ci.error.Error("Error while cleaning up build archive: " + StringUtils.toString(e), e));
          }
        }
      }
    };

    final Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 2);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    timer.scheduleAtFixedRate(timerTask, calendar.getTime(), 24L * 60L * 60L * 1000L);
  }


  public void shutdownService() {
    timer.cancel();
    status = SERVICE_STATUS_NOT_STARTED;
  }


  public ServiceName serviceName() {
    return ServiceName.RESULT_CLEANUP_SERVICE;
  }


  public byte getServiceStatus() {
    return status;
  }
}
