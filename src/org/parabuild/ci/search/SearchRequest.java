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

import java.util.HashMap;
import java.util.Map;

/**
 * Search request
 */
public final class SearchRequest {

  private final String searchQuery;
  private final Map parameters = new HashMap(5);


  /**
   * Constructor.
   *
   * @param searchQuery the search query to execute.
   */
  public SearchRequest(final String searchQuery) {
    this.searchQuery = searchQuery;
  }


  /**
   * Adds search parameter.
   *
   * @param prm   - search request parameter name.
   * @param value - value of the parameter.
   * @see SearchRequestParameter#BUILD_ID
   */
  public void addParameter(final SearchRequestParameter prm, final String value) {
    parameters.put(prm, value);
  }


  /**
   * @return search request parameter.
   */
  public String getParameter(final SearchRequestParameter prm) {
    return (String) parameters.get(prm);
  }


  /**
   * @return search query
   */
  public String getSearchQuery() {
    return searchQuery;
  }


  public String toString() {
    return "SearchRequest{" +
            "parameters=" + parameters +
            ", searchQuery='" + searchQuery + '\'' +
            '}';
  }
}
