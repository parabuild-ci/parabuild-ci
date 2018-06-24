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

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;

import java.io.File;

/**
 * Responsible for configuring cache.
 */
public final class SupportService implements Service {

  private static final Log log = LogFactory.getLog(SupportService.class);
  private byte serviceStatus = SERVICE_STATUS_NOT_STARTED;
  private PooledExecutor executor = null;


  public void startupService() {
    initSystemProperties();
    initErrorDir();
    initCache();
    initTaskManager();
    serviceStatus = SERVICE_STATUS_STARTED;
  }


  /**
   * Initializes task manager.
   */
  private void initTaskManager() {
    executor = new PooledExecutor(new LinkedQueue(), 10);
    executor.setThreadFactory(new ThreadFactory() {
      public Thread newThread(final Runnable runnable) {
        return ThreadUtils.makeDaemonThread(runnable, "ParabuildTaskRunnerThread");
      }
    });
    executor.setKeepAliveTime(-1); // live forever
  }


  /**
   * Asynchronously executes task
   *
   * @param task to execute.
   */
  public void executeTask(final Runnable task) {
    try {
      executor.execute(task);
    } catch (final InterruptedException e) {
      IoUtils.ignoreExpectedException(e);
      Thread.currentThread().interrupt();
    }
  }


  private static void initSystemProperties() {
    // NOTE: vimeshev - 08/24/2005 - see #718 - there was some
    // reference that "it may help to do it this way" in addition
    // to "-Djava.awt.headless=true" from command line. 
    System.setProperty("java.awt.headless", "true");
  }


  /**
   * Initialises ehcache manager.
   */
  private static void initCache() {
    try {
      CacheManager.create(IoUtils.stringToInputStream(IoUtils.getResourceAsString("ehcache.xml")));

    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      initCacheHard();
      reportStartupError("Error starting up cache:", e);
    }
  }


  /**
   * Attempts to init cache to default values ignoring
   * exceptions.
   */
  private static void initCacheHard() {
    try {
      CacheManager.create();
    } catch (final Exception e) {
      log.error("Error creating cache", e);
    }
  }


  /**
   * Initializes system error directory
   */
  private static void initErrorDir() {
    try {
      final File f = ConfigurationManager.getSystemNewErrorsDirectory();
      if (!f.exists()) {
        f.mkdirs();
      }
    } catch (final Exception e) {
      reportStartupError("Error creating error dir:", e);
    }
  }


  public void shutdownService() {
    executor.shutdownNow();
    RuntimeUtils.shutdown();
    serviceStatus = SERVICE_STATUS_NOT_STARTED;
  }


  public ServiceName serviceName() {
    return ServiceName.SUPPORT_SERVICE;
  }


  public byte getServiceStatus() {
    return serviceStatus;
  }


  /**
   * Helper method to report errors.
   *
   */
  private static void reportStartupError(final String descr, final Exception e) {
    final Error error = new Error(descr + ' ' + StringUtils.toString(e));
    error.setSendEmail(false);
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
