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
package org.parabuild.ci.notification;

import java.io.*;
import java.util.*;
import javax.mail.*;

import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.StepRun;

/**
 * Composite notification manager is a composition of multiple
 * notification managers presented to a caller as one.
 */
public final class CompositeNotificationManager implements NotificationManager, Serializable {

  private static final long serialVersionUID = 7143257488995569178L; // NOPMD
  private final List managers = new ArrayList(11);


  public void add(final NotificationManager manager) {
    managers.add(manager);
  }


  /**
   * Sends out notification that build sequence started. This
   * method should not throw exceptions as this is the last point
   * point where notification can be delivered. If implementing
   * method encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param buildRun to report
   * @param buildSequence to report
   *
   * @see ErrorManager
   */
  public void notifyBuildStepStarted(final BuildRun buildRun, final BuildSequence buildSequence) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.notifyBuildStepStarted(buildRun, buildSequence);
    }
  }


  /**
   * Sends out notification about build sequence results. This
   * method should not throw exceptions as this is the last point
   * point where notification can be delivered. If implementing
   * method encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param stepRun to report
   *
   * @see ErrorManager
   */
  public void notifyBuildStepFinished(final StepRun stepRun) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.notifyBuildStepFinished(stepRun);
    }
  }


  /**
   * Sets version control user e-mail map to be respected when
   * generating "To:" list. If a user defined in the source
   * control map, it will be used first.
   */
  public void setVCSUserMap(final Map sourceControlUsersMap) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.setVCSUserMap(sourceControlUsersMap);
    }
  }


  /**
   * Sends out notification that build sequence hung. This method
   * should not throw exceptions as this is the last point point
   * where notification can be delivered. If implementing method
   * encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param buildRun to report
   * @param buildSequence to report
   *
   * @see ErrorManager
   */
  public void notifyBuildStepHung(final BuildRun buildRun, final BuildSequence buildSequence) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.notifyBuildStepHung(buildRun, buildSequence);
    }
  }


  /**
   * Sends user password in accordance with user email in the
   * DB.
   */
  public void sendUserPassword(final String userName, final String newPassword) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.sendUserPassword(userName, newPassword);
    }
  }


  /**
   * Sends out notification that build run failed with system
   * error. This method should not throw exceptions as this is
   * the last point point where notification can be delivered. If
   * implementing method encounters exceptional conditions, it
   * should use ErrorManager to report errors.
   *
   * @param buildRun that has failed
   * @param e Exception at failure
   *
   * @see ErrorManager
   */
  public void notifyBuildStepFailed(final BuildRun buildRun, final Exception e) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.notifyBuildStepFailed(buildRun, e);
    }
  }


  public void sendTestEmailMessage(final List properties) throws MessagingException {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.sendTestEmailMessage(properties);
    }
  }


  public void notifyChangeListsWaitingForMerge(final int activeMergeID) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.notifyChangeListsWaitingForMerge(activeMergeID);
    }
  }


  public void enableNotification(final boolean enable) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.enableNotification(enable);
    }
  }


  public void notifyMergeFailedBecauseOfConflicts(final BranchMergeConfiguration mergeConfiguration, final ChangeList changeList, final List conflicts) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      ((NotificationManager)i.next()).notifyMergeFailedBecauseOfConflicts(mergeConfiguration, changeList, conflicts);
    }
  }


  /**
   * Sends notification about error to build administrator
   *
   * @see
   */
  public void notifyBuildAdministrator(final Error error) {
    for (final Iterator i = managers.iterator(); i.hasNext();) {
      final NotificationManager manager = (NotificationManager)i.next();
      manager.notifyBuildAdministrator(error);
    }
  }


  public String toString() {
    return "CompositeNotificationManager{" +
      "managers=" + managers +
      '}';
  }
}
