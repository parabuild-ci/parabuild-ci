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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.parabuild.ci.common.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton to manage logging level (debug/release)
 */
public final class Log4jConfigurator {

  private static final Log log = LogFactory.getLog(Log4jConfigurator.class);

  public static final String DEBUG_LOG4_PROPERTIES = "/debug.log4j2.xml";
  public static final String RELEASE_LOG4_PROPERTIES = "/log4j2.xml";

  private static Log4jConfigurator instance = new Log4jConfigurator();


  public static Log4jConfigurator getInstance() {
    return instance;
  }


  /**
   * Initializes logging level.
   *
   * @param debug
   * @throws IOException
   */
  public synchronized void initialize(final boolean debug) throws IOException {
    InputStream is = null;
    try {
      if (debug) {
        if (log.isDebugEnabled()) log.debug("LOADING DEBUG PROPERTIES ");
        is = getClass().getResourceAsStream(DEBUG_LOG4_PROPERTIES);
      } else {
        if (log.isDebugEnabled()) log.debug("LOADING STANDARD PROPERTIES ");
        is = getClass().getResourceAsStream(RELEASE_LOG4_PROPERTIES);
      }
      LogManager.resetConfiguration();
      if (is != null) {
        final Properties properties = new Properties();
        properties.load(is);
        final PropertyConfigurator propertyConfigurator = new PropertyConfigurator();
        propertyConfigurator.doConfigure(properties, LogManager.getLoggerRepository());
      }
    } finally {
      IoUtils.closeHard(is);
    }
  }

}
