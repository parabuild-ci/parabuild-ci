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

/**
 * Implementation of {@link ChangeURL} interface.
 */
final class ChangeURLImpl implements ChangeURL {

  private String url = null;

  private String caption = null;


  /**
   * Constructor
   *
   * @param url URL
   * @param caption caption
   */
  public ChangeURLImpl(final String url, final String caption) {
    this.url = url;
    this.caption = caption;
  }


  /**
   * @return URL part of ChangeURL
   */
  public String getURL() {
    return url;
  }


  /**
   * @return URL part of ChangeURL
   */
  public String getCaption() {
    return caption;
  }


  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ChangeURLImpl changeURL = (ChangeURLImpl)o;

    if (caption != null ? !caption.equals(changeURL.caption) : changeURL.caption != null) return false;
    if (url != null ? !url.equals(changeURL.url) : changeURL.url != null) return false;

    return true;
  }


  public int hashCode() {
    int result = url != null ? url.hashCode() : 0;
    result = 29 * result + (caption != null ? caption.hashCode() : 0);
    return result;
  }


  public String toString() {
    return "ChangeURLImpl{" +
      "url='" + url + '\'' +
      ", caption='" + caption + '\'' +
      '}';
  }
}
