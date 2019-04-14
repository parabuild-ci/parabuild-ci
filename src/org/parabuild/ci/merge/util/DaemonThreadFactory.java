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
package org.parabuild.ci.merge.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import org.parabuild.ci.common.ThreadUtils;


public final class DaemonThreadFactory implements ThreadFactory {

  private static final Log log = LogFactory.getLog(DaemonThreadFactory.class);

  private final String daemonName;


  public DaemonThreadFactory(final String name) {
    this.daemonName = name;
  }


  public Thread newThread(final Runnable runnable) {
    if (log.isDebugEnabled()) log.debug("creating new thread for runnable: " + runnable);
    return ThreadUtils.makeDaemonThread(runnable, daemonName);
  }


  public String toString() {
    return "DaemonThreadFactory{" +
      "daemonName='" + daemonName + '\'' +
      '}';
  }
}