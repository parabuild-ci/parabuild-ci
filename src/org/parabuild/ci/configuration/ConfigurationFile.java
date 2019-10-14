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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * This singleton class is responsible for storing and retrieving
 * properties from a local config file.
 */
public final class ConfigurationFile {

  private static final Log LOG = LogFactory.getLog(ConfigurationFile.class); // NOPMD

  /**
   * If this property is set, Parabuild will run in remote
   * agent mode.
   * <p/>
   * This property can be set from the command line.
   */
  public static final File LOCAL_CONFIG_FILE = IoUtils.getCanonicalFileHard(new File(ConfigurationConstants.CATALINA_BASE, "system" + ConfigurationConstants.FS + "parabuild.conf"));

  private static final String PROP_BUILD_MANAGER_ADDRESS = "parabuild.build.manager.ipaddress";
  private static final String PROP_NONLOCAL_ADDRESS_ENABLED = "parabuild.nonlocal.address.enabled";
  private static final String PROP_AGENT_DEBUG_OUTPUT_ENABLED = "parabuild.agent.debug.output.enabled";
  public static final String PROP_PROD_INFO_DISABLED = "parabuild.pid.disabled";
  public static final String PROP_RUNNER_ID = "parabuild.runner.id";
  public static final String PROP_ALLOW_ADMINISTRATOR_USER = "parabuild.allow.administrator.user";


  private ConfigurationFile() {
  }


  public static ConfigurationFile getInstance() {
    return new ConfigurationFile();
  }


  /**
   * Returns a property from local configuration file which is
   * stored at system/parabuild.cfg.
   * <p/>
   * This file is used for rare cases when a configuration
   * information should be stored and editted manually. Rest of
   * the cases should be handled by configuration in the
   * database.
   * <p/>
   * Property in this file can start with "parabuild." only.
   *
   * @return null if property is not set
   */
  public String getProperty(final String name) {
    // validate
    ArgumentValidator.validateArgumentNotBlank(name, "system property");
    if (!name.startsWith("parabuild.")) return null;

    // first check if the systemProperty is set through the command line
    final String propName = name.trim().toLowerCase();
    final String systemProperty = System.getProperty(propName);
    if (!StringUtils.isBlank(systemProperty)) return systemProperty;

    // process
    String result = null;
    InputStream is = null;
    try {
      if (LOCAL_CONFIG_FILE.exists()) {
        final Properties properties = new Properties();
        properties.load(is = new FileInputStream(LOCAL_CONFIG_FILE));
        result = properties.getProperty(propName);
      }
    } catch (final IOException e) {
// REVIEWME: simeshev@parabuilci.org -> break realm
//      org.parabuild.ci.error.Error err = new org.parabuild.ci.error.Error("There was an error reading Parabuild configuration file.");
//      err.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
//      err.setDetails(e);
//      err.setSendEmail(false);
//      ErrorManagerFactory.getErrorManager().reportSystemError(err);
      LOG.error("Error while getting property", e);
    } finally {
      IoUtils.closeHard(is);
    }
    return result;
  }


  /**
   * Sets property in a local configuration file.
   *
   * @param propName to set
   * @param propValue to set. If null, the property named
   * propName will be removed.
   */
  public void setProperty(final String propName, final String propValue) {
    // validate
    ArgumentValidator.validateArgumentNotBlank(propName, "property name");
    if (!propName.startsWith("parabuild.")) throw new IllegalArgumentException("Illegal property name");
    // process
    InputStream is = null;
    OutputStream os = null;
    try {
      // load
      final Properties properties = new Properties();
      if (LOCAL_CONFIG_FILE.exists()) {
        properties.load(is = new FileInputStream(LOCAL_CONFIG_FILE));
      }
      IoUtils.closeHard(is);

      if (StringUtils.isBlank(propValue)) {
        properties.remove(propName);
      } else {
        properties.setProperty(propName, propValue);
      }
      os = new FileOutputStream(LOCAL_CONFIG_FILE);
      properties.store(os, "Parabuild configuration file");
    } catch (final IOException e) {
// REVIEWME: simeshev@parabuilci.org -> break realm
//      org.parabuild.ci.error.Error err = new org.parabuild.ci.error.Error("There was an error reading Parabuild configuration file.");
//      err.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
//      err.setDetails(e);
//      err.setSendEmail(false);
//      ErrorManagerFactory.getErrorManager().reportSystemError(err);
      LOG.error("Error while setting property", e);
    } finally {
      IoUtils.closeHard(is);
      IoUtils.closeHard(os);
    }
  }


  /**
   * @return Build mamanger address or null if property is not
   *         set.
   */
  public String getBuildManagerAddress() {
    final String propertyValue = getProperty(PROP_BUILD_MANAGER_ADDRESS);
    if (StringUtils.isBlank(propertyValue)) return propertyValue;
    // REVIEWME: This handling of the erroneuous dot at the
    // end will work only if there is one dot. We might try
    // to make it more stable by using regex if needed.
    if (propertyValue.endsWith(".")) return propertyValue;
    return propertyValue;
  }


  /**
   * @return Build mamanger address or null if property is not
   *         set.
   */
  public boolean isNonLocalAddressEnabled() {
    return propertyDefinedAndEqualsIgnoreCase(PROP_NONLOCAL_ADDRESS_ENABLED, "true");
  }


  /**
   * @return true if debug output on an agent is enabled.
   */
  public boolean isAgentDebugOutputEnabled() {
    return propertyDefinedAndEqualsIgnoreCase(PROP_AGENT_DEBUG_OUTPUT_ENABLED, "true");
  }


  /**
   * @return true if Administrator user is allowed under Windows
   */
  public boolean isAdministratorUserAllowed() {
    return propertyDefinedAndEqualsIgnoreCase(PROP_ALLOW_ADMINISTRATOR_USER, "true");
  }


  /**
   * Purpose of this method is to return true if a diven property
   * does stored in local parabuild.cfg and equas a given value.
   *
   * @param prop to look up
   * @param value to compare to if the property is defined
   *
   * @return true if prop exists and equal given value
   */
  public boolean propertyDefinedAndEquals(final String prop, final String value) {
    final String foundProp = getProperty(prop);
    return !StringUtils.isBlank(foundProp) && foundProp.equals(value);
  }


  /**
   * Purpose of this method is to return true if a diven property
   * does stored in local parabuild.cfg and equas a given value.
   *
   * @param prop to look up
   * @param value to compare to if the property is defined
   *
   * @return true if prop exists and equal given value
   */
  public boolean propertyDefinedAndEqualsIgnoreCase(final String prop, final String value) {
    final String foundProp = getProperty(prop);
    return !StringUtils.isBlank(foundProp) && foundProp.equalsIgnoreCase(value);
  }
}
