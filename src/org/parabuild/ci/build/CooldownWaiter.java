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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.versioncontrol.SourceControl;

/**
 * This class provides an ability to wait until either a maximum
 * of attempts or no changes (code base cooled down).
 */
final class CooldownWaiter {

  private static final Log log = LogFactory.getLog(CooldownWaiter.class);

  private final int activeBuildID;
  private final int startChangeListID;
  private final SourceControl sourceControl;
  private final ConfigurationManager cm;
  private final Object lock;


  CooldownWaiter(final Object lock, final int activeBuildID, final SourceControl sourceControl, final int startChangeListID) {
    this.cm = ConfigurationManager.getInstance();
    this.activeBuildID = activeBuildID;
    this.sourceControl = sourceControl;
    this.startChangeListID = startChangeListID;
    this.lock = lock;
  }


  public int waitUntilCoolsDown() throws CommandStoppedException, BuildException, AgentFailureException {
    // get interval
    final int cooldownIntervalSecs = cm.getScheduleSettingValue(activeBuildID, ScheduleProperty.AUTO_COOLDOWN_INTERVAL, ScheduleProperty.DEFAULT_COOLDOWN_INTERVAL);
    if (log.isDebugEnabled()) {
      log.debug("cooldownIntervalSecs: " + cooldownIntervalSecs);
    }
    if (cooldownIntervalSecs <= 0) {
      return startChangeListID; // don't need to wait
    }

    // get attempts
    final int maxCooldownTries = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.MAX_COOLDOWN_TRIES, SystemProperty.DEFAULT_MAX_COOLDOWN_TRIES);
    if (log.isDebugEnabled()) {
      log.debug("maxCooldownTries: " + maxCooldownTries);
    }
    if (maxCooldownTries <= 0) {
      return startChangeListID;  // don't need to wait
    }

    // wait to cooldown
    final long cooldownIntervalMillis = (long) cooldownIntervalSecs * 1000L;
    int previousChangeListID = startChangeListID;
    int newChangeListID = previousChangeListID;
    for (int attempt = 1; attempt <= maxCooldownTries; attempt++) {
      ThreadUtils.checkIfInterrupted();
      if (log.isDebugEnabled()) {
        log.debug("attempt: " + attempt);
      }
      synchronized (lock) {
        try {
          lock.wait(cooldownIntervalMillis);
        } catch (final InterruptedException e) {
          throw new CommandStoppedException(e);
        }
      }
      ThreadUtils.checkIfInterrupted();
      newChangeListID = sourceControl.getChangesSince(previousChangeListID);
      if (newChangeListID == previousChangeListID) {
        if (log.isDebugEnabled()) {
          log.debug("nothing came after wait");
        }
        break;
      } else {
        previousChangeListID = newChangeListID;
      }
    }
    return newChangeListID;
  }


  public String toString() {
    return "CooldownWaiter{" +
            "activeBuildID=" + activeBuildID +
            ", startChangeListID=" + startChangeListID +
            ", sourceControl=" + sourceControl +
            ", cm=" + cm +
            ", lock=" + lock +
            '}';
  }
}
