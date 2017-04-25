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
package org.parabuild.ci.tray;

import java.io.IOException;
import java.util.List;

import com.caucho.hessian.client.HessianRuntimeException;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.RemoteUtils;

/**
 * The main goal of this class is to wrap calls the remote
 * methods that trow HessianRuntimeException. This implements
 * Decorator pattern.
 */
final class BuildStatusServiceHessianExceptionWrapper implements BuildStatusService {

  private final BuildStatusService delegate;
  private final String host;


  /**
   * Constructor.
   *
   * @param delegate to wrap.
   */
  public BuildStatusServiceHessianExceptionWrapper(final BuildStatusService delegate, final String host) {
    this.host = host;
    this.delegate = delegate;
  }


  /**
   * @return List of {@link BuildStatus} objects.
   */
  public List getBuildStatusList() throws IOException, AgentFailureException {
    try {
      return delegate.getBuildStatusList();
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, host);
    }
  }
}
