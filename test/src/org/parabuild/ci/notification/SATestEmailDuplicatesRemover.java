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

import java.util.*;
import javax.mail.internet.*;
import junit.framework.*;

/**
 * Tests EmailDuplicatesRemover
 */
public class SATestEmailDuplicatesRemover extends TestCase {

  private EmailDuplicatesRemover duplicatesRemover = null;


  public SATestEmailDuplicatesRemover(final String s) {
    super(s);
  }


  public void test_removeDuplicates() throws Exception {
    final List addresses = new ArrayList(5);
    addresses.add(new InternetAddress("test@test", "test test"));
    addresses.add(new InternetAddress("test@test", "test_other_name test_other_name"));
    addresses.add(new InternetAddress("test1@test", "test_other_name test_other_name"));
    duplicatesRemover.removeDuplicates(addresses);
    assertEquals(2, addresses.size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestEmailDuplicatesRemover.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    duplicatesRemover = new EmailDuplicatesRemover();
  }
}
