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
package org.parabuild.ci.webui;

import java.util.*;

/**
 * Value object to hold a URL and parameters. This allows
 * consumers of this class to use their own captions and
 * colors when creating links.
 */
public class LinkURL {

  private String url = null;
  private Properties parameters = null;


  public LinkURL(final String url, final Properties parameters) {
    this.url = url;
    this.parameters = parameters;
  }


  public LinkURL(final String url, final String parameterName, final String parameterValue) {
    this.url = url;
    this.parameters = new Properties();
    this.parameters.setProperty(parameterName, parameterValue);
  }


  public LinkURL(final String url, final String parameterName, final int parameterValue) {
    this(url, parameterName, Integer.toString(parameterValue));
  }


  public String getUrl() {
    return url;
  }


  public Properties getParameters() {
    return parameters;
  }
}
