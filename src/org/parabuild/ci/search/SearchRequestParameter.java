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
package org.parabuild.ci.search;

import com.dautelle.util.Enum;

import java.util.Collection;


/**
 * Defines search request parameters.
 */
public final class SearchRequestParameter extends Enum {

  private static final long serialVersionUID = 8599669894057947730L; // NOPMD

  /**
   * Limit results to a given build ID.
   */
  public static final SearchRequestParameter BUILD_ID = new SearchRequestParameter();

  public static final Collection VALUES = getInstances(SearchRequestParameter.class);
}
