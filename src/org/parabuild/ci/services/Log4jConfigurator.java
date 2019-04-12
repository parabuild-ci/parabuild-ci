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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Singleton to manage logging level (debug/release)
 */
public final class Log4jConfigurator {

  public static final String DEBUG_LOG4_CONFIG = "/debug.log4j2.xml";
  public static final String RELEASE_LOG4_CONFIG = "/log4j2.xml";

  private static final Log4jConfigurator instance = new Log4jConfigurator();


  /**
   * Returns a singleton instance of the configurator.
   *
   * @return a singleton instance of the configurator.
   */
  public static Log4jConfigurator getInstance() {

    return instance;
  }


  /**
   * Initializes logging level.
   *
   * @param debug <code>true</code> if the configurator has to load a debug configuration. <code>false</code>
   *              if the configurator has to load release configuration.
   * @throws IOException if an error occurred.
   */
  public synchronized void initialize(final boolean debug) throws IOException {
    try {

      final URL configURL = getConfigURL(debug);

      if (configURL != null) {

        final LoggerContext context = (LoggerContext) LogManager.getContext(false);

        // this will force a reconfiguration
        context.setConfigLocation(configURL.toURI());
      }
    } catch (final URISyntaxException e) {

      throw new IOException(e);
    }
  }


  /**
   * Calculates the config URL.
   *
   * @param debug <code>true</code> if looking for a debug configuration. <code>false</code>
   *              if looking for a release configuration.
   * @return the config URL.
   * @see #DEBUG_LOG4_CONFIG
   * @see #RELEASE_LOG4_CONFIG
   */
  private URL getConfigURL(final boolean debug) {

    if (debug) {

      return getClass().getResource(DEBUG_LOG4_CONFIG);
    } else {

      return getClass().getResource(RELEASE_LOG4_CONFIG);
    }
  }
}
