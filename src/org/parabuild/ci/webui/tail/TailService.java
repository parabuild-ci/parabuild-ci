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
package org.parabuild.ci.webui.tail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.TailUpdate;
import org.parabuild.ci.services.TailUpdateImpl;

/**
 * This service is responsible for providing log tailing through DWR
 */
public final class TailService {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(TailService.class); // NOPMD


  /**
   * 
   * @param activeBuildID the ID of the build.
   * @param sinceServerTime long time from that get an update.
   *
   * @return TailUpdate since the given server time stamp
   */
  public TailUpdate getUpdate(final int activeBuildID, final long sinceServerTime) {
    try {
//      if (log.isDebugEnabled()) log.debug("activeBuildID: " + activeBuildID);
//      if (log.isDebugEnabled()) log.debug("sinceServerTime: " + sinceServerTime);
//      if (log.isDebugEnabled()) log.debug("tailUpdate: " + tailUpdate);
      return BuildManager.getInstance().getTailUpdate(activeBuildID, sinceServerTime);
    } catch (final Exception e) {
      final Error error = new Error();
      error.setBuildID(activeBuildID);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setDescription("Error while showing log tail: " + StringUtils.toString(e));
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
      return TailUpdateImpl.EMPTY_UPDATE;
    }
  }
}
