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

/**
 * Value object for menu status.
 */
final class MenuStatus {

  private final String image;
  private final String caption;


  public MenuStatus(final String image, final String caption) {
    this.image = image;
    this.caption = caption;
  }


  public String getImage() {
    return image;
  }


  public String getCaption() {
    return caption;
  }


  @SuppressWarnings("RedundantIfStatement")
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    final MenuStatus that = (MenuStatus)obj;

    if (!caption.equals(that.caption)) return false;
    if (!image.equals(that.image)) return false;

    return true;
  }


  public int hashCode() {
    int result = image.hashCode();
    result = 29 * result + caption.hashCode();
    return result;
  }


  public String toString() {
    return "MenuStatus{" +
      "image='" + image + '\'' +
      ", caption='" + caption + '\'' +
      '}';
  }
}
