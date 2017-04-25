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
package org.parabuild.ci.versioncontrol;

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;

/**
 * Partially merges two change lists obtained in certain time.
 * <p/>
 * The main purpose is to handler VCS submit window: In some VCS it's
 * possble that files participated in one submission can be
 * timestamp marked with different stamps.
 * <p/>
 * Also it's possible that a VCS log command can be taken and
 * finish while a submission is still in progress, which results
 * in Parabuild changelists that are incomplete. That can cause a
 * build to break because source line will be synced to time when
 * project source line is incomplete.
 * <p/>
 * The idea is to get changes again after first reuest and check
 * if there are any leftovers.
 */
final class ChangeListWindowMerger {

  private static final Log log = LogFactory.getLog(ChangeListWindowMerger.class);


  /**
   * Expects both changelists coming in reverse date order. Note:
   * it can change order of both collections.
   */
  public void mergeInChangesLeft(final List firstRun, final List secondRun) {

    if (secondRun.isEmpty()) return; // nothing to process

    final Map firstRunMap = new HashMap(firstRun.size());

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^
    // transform first run to map

    for (final Iterator iter = firstRun.iterator(); iter.hasNext();) {
      final ChangeList changeList = (ChangeList)iter.next();
      firstRunMap.put(new FirstRunChangeListKey(changeList), changeList);
    }

    // ^^^^^^^^^^^^^^^^^^
    // iterate second run

    // sort second run so that change lists are sorted by date/time. note
    // that it's natural that in the sorted list different change
    // lists will have same date.
    Collections.sort(secondRun, ChangeList.CHANGE_DATE_COMPARATOR);

    // iterate
    Date previousFoundLeftoverDate = null;
    for (final Iterator iter = secondRun.iterator(); iter.hasNext();) {
      final ChangeList secondRunChangeList = (ChangeList)iter.next();
      // find if it is a left over (i.e. there is such a change list in the first run)
      final ChangeList foundInFirstRun = (ChangeList)firstRunMap.get(new FirstRunChangeListKey(secondRunChangeList));
      if (foundInFirstRun != null) {
        // leftover found - add changes from the second run
        // and move change list date to second run's date that we
        // expect is older.
        foundInFirstRun.getChanges().addAll(secondRunChangeList.getChanges());
        if (foundInFirstRun.getCreatedAt().compareTo(secondRunChangeList.getCreatedAt()) <= 0) {
          foundInFirstRun.setCreatedAt(secondRunChangeList.getCreatedAt());
          previousFoundLeftoverDate = secondRunChangeList.getCreatedAt();
        } else {
          // NOTE: if we got here, it means that second run
          // contained change lists that are younger than those in the
          // first run.
          log.warn("Change list in the second run was unexpectely younger then in the first run. First run stamp: \"" + foundInFirstRun.getCreatedAt() + ", second run stamp: " + secondRunChangeList.getCreatedAt());
        }
      } else {
        // leftover not found - check if the date of the change
        // list not found is the same as the previous found one.
        // in this case we still can continue processing because
        // we don jump over time stamps in "future" change lists.
        if (previousFoundLeftoverDate == null || !secondRunChangeList.getCreatedAt().equals(previousFoundLeftoverDate)) {
          // looks like we reached next date - stop processing change
          // lists from the second run.
          break;
        }
      }
    }
  }


  /**
   * Lookup key.
   *
   * @see ChangeListWindowMerger#mergeInChangesLeft
   */
  private static final class FirstRunChangeListKey {

    // key elements
    private String user = null;
    private String description = null;
    private String branch = null;

    // hash code
    private int hashCode = 0;


    /**
     * Constructor.
     */
    public FirstRunChangeListKey(final ChangeList changeList) {
      description = changeList.getDescription();
      user = changeList.getUser();
      branch = changeList.getBranch();

      // NOTE: this key is immutable, so we can pre-calculate
      // hashcode in the constructor and store it for future use.
      hashCode = calculateHashCode();
    }


    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof FirstRunChangeListKey)) return false;

      final FirstRunChangeListKey key = (FirstRunChangeListKey)o;

      if (!description.equals(key.description)) return false;
      if (!user.equals(key.user)) return false;
      if (branch != null ? !branch.equals(key.branch) : key.branch != null) return false;

      return true;
    }


    /**
     * @see FirstRunChangeListKey#FirstRunChangeListKey
     */
    public int hashCode() {
      // return hashCode pre-calculated it the constructor
      return hashCode;
    }


    private int calculateHashCode() {
      int result = user.hashCode();
      result = 29 * result + description.hashCode();
      result = 29 * result + (branch != null ? branch.hashCode() : 0);
      return result;
    }
  }
}
