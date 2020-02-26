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

import java.util.*;
import javax.servlet.http.*;

/**
 * Servlet utility class.
 */
public final class ServletUtils {

  private static final String[] ADDRESSES_IN_172 = createAddressesIn172();
  private static final String PREFIX_172 = "172.";


  private ServletUtils() {
  }


  public static boolean requestIsLocal(final HttpServletRequest request) {
    return requestIsLocal(request.getRemoteAddr());
  }


  public static boolean requestIsLocal(final String remoteAddr) {
    // prevalidate
    if (StringUtils.isBlank(remoteAddr)) {
      return false; // we can not get remote address
    }

    // Check that this is a call from a LAN range 192.* e.t.c
    return remoteAddr.startsWith("192.168.")
      || remoteAddr.startsWith("10.")
      || remoteAddrIsIn172(remoteAddr)
      || remoteAddr.startsWith("169.254.")
      || remoteAddr.startsWith("127.0.0.1");
  }


  private static boolean remoteAddrIsIn172(final String remoteAddr) {
    if (!remoteAddr.startsWith(PREFIX_172)) return false;
    for (int i = 0; i < ADDRESSES_IN_172.length; i++) {
      if (remoteAddr.startsWith(ADDRESSES_IN_172[i])) return true;
    }
    return false;
  }


  private static String[] createAddressesIn172() {
    final List result = new ArrayList(30);
    for (int i = 16; i <= 32; i++) {
      result.add(PREFIX_172 + i + '.');
    }
    return (String[])result.toArray(new String[0]);
  }
}
