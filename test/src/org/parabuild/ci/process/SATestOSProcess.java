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
package org.parabuild.ci.process;

import junit.framework.*;

/**
 * Tests ProcessManager functionality
 */
public class SATestOSProcess extends TestCase {

  private static final String PRC_NAME = "A";
  private static final String PRC_PATH = "B";
  private static final String PRC_CMD  = "C";
  private static final String PRC_USER = "D";
  
  private OSProcess process;


  public SATestOSProcess(final String s) {
    super(s);
  }


  /**
   * Test process fields fill
   */
  public void test_value() throws Exception {
    assertTrue(process.getUser().equals(PRC_USER));
    assertTrue(process.getName().equals(PRC_NAME));
    assertTrue(process.getPath().equals(PRC_PATH));
    assertTrue(process.getCommandLine().equals(PRC_CMD));
    assertTrue(process.getPID()  == 1);
    assertTrue(process.getPPID() == 2);
  }


  /**
   * Tests processes equality and process modification
   */
  public void test_equals() throws Exception {
    OSProcess copy = new OSProcess(process);
    assertTrue(process.equals(copy));
    copy = new OSProcess(process);
    copy.setPID(copy.getPID() + 1);
    assertTrue(!process.equals(copy));    
    copy = new OSProcess(process);
    copy.setPPID(copy.getPPID() + 1);
    assertTrue(!process.equals(copy));    
    copy = new OSProcess(process);
    copy.setUser('X' + copy.getUser());
    assertTrue(!process.equals(copy));    
    copy = new OSProcess(process);
    copy.setPath('X' + copy.getPath());
    assertTrue(!process.equals(copy));    
    copy = new OSProcess(process);
    copy.setName('X' + copy.getName());
    assertTrue(!process.equals(copy));        
    copy = new OSProcess(process);
    copy.setCommandLine('X' + copy.getCommandLine());
    assertTrue(!process.equals(copy));    
  }

  protected void setUp() throws Exception {
    super.setUp();
    process = new OSProcess(1, 
                           2,
                           PRC_NAME,
                           PRC_PATH,
                           PRC_CMD,
                           PRC_USER);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestOSProcess.class);
  }
}
