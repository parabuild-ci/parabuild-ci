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

/**
 * VM level Parabuild properties.
 */
public final class SystemConstants {

  /**
   * System password used to encript 3rdparty passwords. DO NOT
   * CHANGE!!!
   */
  public static final String SYSTEM_PASSWORD = "Viewtier Parabuilt 2.0";


  /**
   * Constant class constructor.
   */
  private SystemConstants() {
  }


  public static final String SYSTEM_PROPERTY_BUILDER_PASSWORD = "parabuild.builder.password";

  /**
   * When set to "true", e-mail notification manager will not
   * send e-mails.
   * <p/>
   * Default is false.
   * <p/>
   * This property can be set from the command line.
   */
  public static final String SYSTEM_PROPERTY_EMAIL_DISABLED = "parabuild.email.disabled";

  /**
   * When set to "true", error manager will print stack traces of
   * exceptions passed as part of a error message.
   * <p/>
   * Deafault is false.
   * <p/>
   * This property can be set from the command line.
   */
  public static final String SYSTEM_PROPERTY_PRINT_STACKTRACE = "parabuild.print.stacktrace";

  /**
   * If this internal property is set to true, logging will
   * output process management related debugs.
   * <p/>
   * Deafault is false.
   * <p/>
   * This property can be set from the command line.
   */
  public static final String SYSTEM_PROPERTY_PROCESS_DEBUG_ENABLED = "parabuild.prd.enabled";

  /**
   * When set to true, ConfigurationService at it startup should
   * populate missing build configurations by copying "current"
   * configs.
   * <p/>
   * This property is set solely by {@link UpgraderToVersion10}
   * and should not be used otherwise.
   * <p/>
   * Default value is not set.
   * <p/>
   * <b>Important:</b> This is an internal system property and
   * <b>should not be used from the command line</b>.
   */
  public static final String SYSTEM_PROPERTY_POPULATE_BUILD_RUN_CONFIGS = "parabuild.populate.build.run.configs";

  public static final String SYSTEM_PROPERTY_INIT_STATISTICS = "parabuild.init.statistics";

  public static final String SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS = "parabuild.init.advanced.settings";

  public static final String SYSTEM_PROPERTY_RELOAD_PRINCIPAL = "parabuild.reload.principal";

  public static final String SYSTEM_PROPERTY_INIT_RETRY_SETTINGS = "parabuild.init.retry.settings";

  /**
   * When set to "true", the remote agent manager will not check caller IP address.
   * <p/>
   * Default is false.
   * <p/>
   * This property can be set from the command line.
   */
  public static final String SYSTEM_PROPERTY_SOURCE_IP_ADDRESS_CHECK_DISABLED = "parabuild.source.ip.address.check.disabled";
}
