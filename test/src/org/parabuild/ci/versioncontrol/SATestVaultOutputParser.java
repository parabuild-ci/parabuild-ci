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
import java.io.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.*;


/**
 * Tests Vault consumprion change log parser
 */
public final class SATestVaultOutputParser extends TestCase {

  //private static final Log log = LogFactory.getLog(SATestVaultOutputParser.class);
  private VaultOutputParser vaultOutputParser;


  public SATestVaultOutputParser(final String s) {
    super(s);
  }


  public void test_deserializeError() throws Exception {
    String message = null;
    Vault parsedValue = null;
    try {
      parsedValue = (Vault)(vaultOutputParser.parse(TestHelper.getTestFile("test_vault_history_err_no_repository.xml")));
    } catch (IOException e) {
      message = StringUtils.toString(e);
    }
    assertEquals("Vault error: Repository test_reposi not found", message);
  }


  public void test_deserializeHistory() throws Exception {
    final Vault parsedValue = (Vault)(vaultOutputParser.parse(TestHelper.getTestFile("test_vault_history.xml")));
    //if (log.isDebugEnabled()) log.debug("parsedValue = " + parsedValue);
    // present?
    assertNotNull("History", parsedValue.getHistory());
    assertNotNull("Result", parsedValue.getResult());
    // valid?
    assertEquals("Number of items", 29, parsedValue.getHistory().getItems().size());
    assertTrue("Result success", parsedValue.getResult().isSuccess());
  }


  public void test_deserializeVersionHistory() throws Exception {
    final Vault parsedValue = (Vault)(vaultOutputParser.parse(TestHelper.getTestFile("test_vault_versionhistory.xml")));
    assertNotNull("History", parsedValue.getHistory());
    assertNotNull("Result", parsedValue.getResult());
    assertEquals("Number of items", 7, parsedValue.getHistory().getItems().size());
    assertTrue("Result success", parsedValue.getResult().isSuccess());
    final Vault.History history = parsedValue.getHistory();
    final List items = history.getItems();
    for (int i = 0; i < items.size(); i++) {
      final Vault.Item item = (Vault.Item)items.get(i);
      assertTrue(item.getTxid() > 0);
      assertTrue(item.getDate() != null);
      assertTrue(item.getVersion() > 0);
    }
  }


  protected void setUp() throws Exception {
    vaultOutputParser = new VaultOutputParser();
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestVaultOutputParser.class,
      new String[]{
//        "test_deserializeExitOnly",
        "test_deserializeError"
      });
  }
}
