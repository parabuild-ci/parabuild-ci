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

import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

import java.util.Date;
import java.util.List;

/**
 * PVCSChangeListHandler is used to retrive change lists
 * from PVCS change log.
 */
final class PVCSChangeListHandler implements PVCSVlogHandler {

  private final TimeWindowChangeListAccumulator accumulator;


  public PVCSChangeListHandler(final int maxChangeLists, final int maxChangeListSize) {
    this.accumulator = new TimeWindowChangeListAccumulator(60000L, maxChangeLists, maxChangeListSize);
  }


  /**
   * @return List of {@link ChangeList} objects
   */
  public List getAccumutatedChangeLists() {
    return accumulator.getChangeLists();
  }


  /**
   * This method is called before the handle is called first
   * time.
   */
  public void beforeHandle() {

  }


  /**
   * This method is called when a revsion is found in a
   * change log. It is guaranteed that it is called only
   * once for a single revesion.
   *
   * @param changeDate
   * @param revisionDescription
   * @param owner
   * @param branch
   * @param filePath
   * @param revision
   * @param changeType
   */
  public void handle(final Date changeDate, final StringBuffer revisionDescription,
    final String owner, final String branch, final String filePath,
    final String revision, final byte changeType) {
    // save change
    final Change change = new Change();
    change.setFilePath(filePath);
    change.setRevision(revision);
    change.setChangeType(changeType);
    accumulator.add(changeDate, revisionDescription, owner, branch, change);
  }


  /**
   * This method is called fater the handle is called last
   * time.
   */
  public void afterHandle() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
