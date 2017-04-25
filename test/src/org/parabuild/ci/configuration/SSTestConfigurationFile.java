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
package org.parabuild.ci.configuration;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.*;

/**
 * Tests ConfigurationFile
 */
public final class SSTestConfigurationFile extends ServersideTestCase {

  public static final String STR_TRUE = "true";

  private ErrorManager errorManager = null;
  private boolean savedNotificationEnablement;
  private ConfigurationFile configFile = null;


  public SSTestConfigurationFile(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_setProperty() throws Exception {
    configFile.setProperty(ConfigurationFile.PROP_PROD_INFO_DISABLED, STR_TRUE);
    assertTrue(ConfigurationFile.LOCAL_CONFIG_FILE.exists());

    final String value = configFile.getProperty(ConfigurationFile.PROP_PROD_INFO_DISABLED);
    assertEquals(STR_TRUE, value);
  }


  public void test_propertyDefinedAndEquals() {
    // first set it so we use existing
    configFile.setProperty(ConfigurationFile.PROP_PROD_INFO_DISABLED, STR_TRUE);
    assertTrue(configFile.propertyDefinedAndEquals(ConfigurationFile.PROP_PROD_INFO_DISABLED, STR_TRUE));
    assertTrue(!configFile.propertyDefinedAndEquals(ConfigurationFile.PROP_PROD_INFO_DISABLED, "blah"));

    // now clear it up
    configFile.setProperty(ConfigurationFile.PROP_PROD_INFO_DISABLED, null);
    assertTrue(!configFile.propertyDefinedAndEquals(ConfigurationFile.PROP_PROD_INFO_DISABLED, STR_TRUE));
    assertTrue(!configFile.propertyDefinedAndEquals(ConfigurationFile.PROP_PROD_INFO_DISABLED, "blah"));
  }


  public void test_systemPropertyHasPriority() {
    final String testProperty = "parabuild.test.system.property." + System.currentTimeMillis();
    final String filePropValue = "fileValue";
    final String systemPropValue = "systemValue";

    // set in file and verify is set
    configFile.setProperty(testProperty, filePropValue);
    assertEquals(filePropValue, configFile.getProperty(testProperty));

    // set in system and verify is set
    System.setProperty(testProperty, systemPropValue);
    assertEquals(systemPropValue, configFile.getProperty(testProperty));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestConfigurationFile.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.configFile = ConfigurationFile.getInstance();
    errorManager = ErrorManagerFactory.getErrorManager();
    savedNotificationEnablement = errorManager.isNotificationEnabled();
    errorManager.enableNotification(false);
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    errorManager.enableNotification(savedNotificationEnablement);
  }
}
