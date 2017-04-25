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

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 */
public final class VersionControlTestUtil {

  /** @noinspection UnusedDeclaration*/
  private static final Log LOG = LogFactory.getLog(SATestSVNTextChangeLogParser.class); // NOPMD


  private VersionControlTestUtil() {
  }


  /**
   * Helper assert method.
   *
   * @param result                   change lists
   * @param requiredChangeListNumber to be found
   * @param requiredUserName         to be present
   * @param dateFormatter            the date formatter
   * @param requiredChangeListDate   to be equal
   * @param requiredChangeListSize   to be equal
   * @throws ParseException if there was a parsing error
   */
  public static void assertChangeListExistsAndValid(final List result, final String requiredChangeListNumber,
                                                    final String requiredUserName, final SimpleDateFormat dateFormatter,
                                                    final String requiredChangeListDate, final int requiredChangeListSize) throws ParseException {
    boolean found = false;
    for (Iterator i = result.iterator(); i.hasNext();) {
      final ChangeList changeList = (ChangeList) i.next();
      //noinspection ControlFlowStatementWithoutBraces
      if ((changeList.getNumber() == null && requiredChangeListNumber == null) || changeList.getNumber().equals(requiredChangeListNumber)) {
        found = true;
        Assert.assertTrue(changeList.getDescription().trim().length() > 0);
        Assert.assertEquals(requiredUserName, changeList.getUser());
        Assert.assertEquals(dateFormatter.parse(requiredChangeListDate), changeList.getCreatedAt());
        final Set changes = changeList.getChanges();
        Assert.assertEquals(requiredChangeListSize, changes.size());
        for (Iterator j = changes.iterator(); j.hasNext();) {
          final Change change = (Change) j.next();
          Assert.assertTrue(!change.getFilePath().startsWith(" "));
        }
      }
    }
    Assert.assertTrue("Change list should be present", found);
  }
}
