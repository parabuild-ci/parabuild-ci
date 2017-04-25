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
package org.parabuild.ci.remote;

import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.HessianProtocolException;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Collection of utilities to handler remote communications.
 */
public final class RemoteUtils {

  private RemoteUtils() {
  }


  public static IOException hessianRuntimeExeptionToIOException(final HessianRuntimeException hre, final String host) throws AgentFailureException {

    final Throwable cause = hre.getRootCause() != null ? hre.getRootCause() : hre;
    if (IOException.class.isAssignableFrom(cause.getClass())) {
      if (cause instanceof HessianProtocolException) {
        if (StringUtils.isBlank(StringUtils.toString(cause))) {
          throw new AgentFailureException("Parabuild host " + host + " is not available or password used to access the host is invalid", cause);
        } else {
          throw new AgentFailureException("Parabuild host " + host + " is not available: " + StringUtils.toString(cause), cause);
        }
      } else if (cause instanceof ConnectException) {
        throw new AgentFailureException("Can not connect to the remote host at " + host + ". The remote host is down or is not accessible: " + StringUtils.toString(cause));
      } else {
        return (IOException) cause;
      }
    } else {
      final IOException e = new IOException("Unexpected I/O error: " + StringUtils.toString(cause));
      e.initCause(cause);
      return e;
    }
  }


  /**
   * Makes sure that host:port combination has both parts. If hostPortToMormilize
   * is emptry it is set to localhost. If port is not provided it is set to 8080.
   *
   * @param hostPortToNormalize
   * @return normilized host:port combination.
   */
  public static String normalizeHostPort(final String hostPortToNormalize) {
    final String normalizedHost = StringUtils.isBlank(hostPortToNormalize) ? "localhost" : hostPortToNormalize;
    String result = null;
    if (normalizedHost.indexOf(':') > -1) {
      result = normalizedHost;
    } else {
      result = normalizedHost + ":8080";
    }
    return result;
  }
}
