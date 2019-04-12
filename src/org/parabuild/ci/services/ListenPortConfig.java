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
package org.parabuild.ci.services;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

/**
 * This class is responsible for obtaining a listen port from the servlet container. Currently only Tomcat is supported.
 */
final class ListenPortConfig {

  /**
   * Retrieves HTTP listen port used by the app. It assumes there
   * is only one HTTP connector.
   */
  public int getListenPort() throws ConfigurationException {

    try {

      // Get connector
      final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
      final ObjectName objectName = new ObjectName("Parabuild:type=Connector,*");
      final Set mbeans = mbeanServer.queryMBeans(objectName, null);

      // REVIEWME: simeshev@parabuildci.org - 2017-09-18 =- this assumes that there is only 1 connector.
      // If the user adds more connectors, this can be a problem.
      final ObjectInstance mbean = (ObjectInstance) mbeans.iterator().next();

      // Get and return the listener port
      return (Integer) mbeanServer.getAttribute(mbean.getObjectName(), "port");
    } catch (final RuntimeException e) {

      throw e;
    } catch (final Exception e) {

      throw new ConfigurationException("Error while getting Parabuild listener port: " + e.toString(), e);
    }
  }
}
