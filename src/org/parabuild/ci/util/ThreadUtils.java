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
package org.parabuild.ci.util;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;


/**
 */
public final class ThreadUtils {

  private ThreadUtils() {
  }


  public static void sleep(final long timeMillis) {
    try {
      Thread.sleep(timeMillis);
    } catch (final InterruptedException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Helper method to created daemon threads.
   *
   * @param name
   *
   * @return Thread that has daemon set to true.
   */
  public static Thread makeDaemonThread(final Runnable runnable, final String name) {
    final Thread th = new Thread(runnable, name);
    th.setDaemon(true);
    return th;
  }


  /**
   * Checks if the thread was interrupted. If it was, throws
   * CommandStoppedException.
   *
   * @throws CommandStoppedException if
   * the thread was interrupted.
   */
  public static void checkIfInterrupted() throws CommandStoppedException {
    if (Thread.interrupted()) throw new CommandStoppedException();
  }


  public static PooledExecutor makeThreadPool(final int keepAlive, final int maxPoolSize, final int initialThreadCount, final String threadNamePrefix) {
    final PooledExecutor result = new PooledExecutor();
    result.setThreadFactory(new ThreadFactory() {

      private int createdThreadCount = 0;


      /**
       * Creates a new thread.
       *
       * @param runnable
       */
      public Thread newThread(final Runnable runnable) {
        return makeDaemonThread(runnable, threadNamePrefix + '-' + createdThreadCount++);
      }
    });
    result.setKeepAliveTime(keepAlive);
    result.setMaximumPoolSize(maxPoolSize);
    result.setMinimumPoolSize(1);
    result.createThreads(initialThreadCount);
    return result;
  }
}
