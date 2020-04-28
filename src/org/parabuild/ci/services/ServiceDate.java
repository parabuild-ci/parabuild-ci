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

import java.util.Date;

/**
 * Purpose of this class to take out import of Date class from ServiceLicenseInformation
 *
 */
final class ServiceDate extends Date {

  private static final long serialVersionUID = -5054915987091849836L;


  /**
   */
  public ServiceDate() {
  }


  /**
   */
  public ServiceDate(final Date date) {
    super(date.getTime());
  }


  /**
   */
  public ServiceDate(final long date) {
    super(date);
  }


  /**
   * Return a copy of this object.
   */
  public Object clone() { // NOPMD
    return super.clone();
  }
}
