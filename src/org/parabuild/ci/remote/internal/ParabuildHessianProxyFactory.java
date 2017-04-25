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

import java.io.*;
import java.net.*;

import com.caucho.hessian.client.*;


/**
 * Exposes some otherwise protected methods of HessianProxyFactory
 */
public final class ParabuildHessianProxyFactory extends HessianProxyFactory {

  /**
   * Creates the URL connection.
   */
  protected URLConnection openConnection(final URL url) throws IOException { // NOPMD
    return super.openConnection(url);
  }
}
