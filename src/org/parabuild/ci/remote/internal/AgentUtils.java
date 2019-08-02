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
package org.parabuild.ci.remote.internal;

import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.HessianProtocolException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Collection of internal Agent utilities
 */
public final class AgentUtils {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(AgentUtils.class); //NOPMD


  private AgentUtils() {
  }


  public static IOException hessianRuntimeExceptionToIOException(final HessianRuntimeException hre, final String host) {

    final Throwable cause = hre.getRootCause() != null ? hre.getRootCause() : hre;
    if (IOException.class.isAssignableFrom(cause.getClass())) {
      if (cause instanceof HessianProtocolException) {
        if (StringUtils.isBlank(StringUtils.toString(cause))) {
          final String possibleErrorMessage = StringUtils.toString(hre);
          return new IOException("Parabuild agent host " + host + " is not available or is not accessible" + StringUtils.isBlank(possibleErrorMessage) + ": " + possibleErrorMessage, cause);
        } else {
          return new IOException("Parabuild agent host " + host + " is not available: " + StringUtils.toString(cause), cause);
        }
      } else if (cause instanceof ConnectException) {
        return new IOException("Can not connect to remote agent host at " + host + ". Remote agent is not up or is not accessible: " + StringUtils.toString(cause));
      } else {
        return (IOException)cause;
      }
    } else {
      return new IOException("Unexpected I/O error: " + StringUtils.toString(cause), cause);
    }
  }
}
