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
package org.parabuild.ci.webui.common;

import viewtier.ui.Image;

/**
 * Image class to display horisontal bar to shown quantity of
 * successful/failed builds.
 */
public class StatisticsQuantityBarImage extends Image {

  public static final int MAX_IMAGE_BAR_WIDTH = 300;
  public static final int IMAGE_BAR_HEIGTH = 10;
  // image URLs
  public static final String GREEN_DOT_IMAGE_URL = "/images/green_dot.gif";
  public static final String RED_DOT_IMAGE_URL = "/images/red_dot.gif";
  private static final long serialVersionUID = -580341226584440245L;


  /**
   * Constructor.
   *
   * @param url
   * @param description
   * @param value
   */
  public StatisticsQuantityBarImage(final String url, final String description, final int max, final int value) {
    super(url, description, claculateXSize(max, value), IMAGE_BAR_HEIGTH);
  }


  /**
   * Helper method to claculate image's X-size.
   *
   * @param value
   *
   * @return normalized X-size
   */
  private static int claculateXSize(final int max, final int value) {
    if (max == 0) return 0;
    return (value * MAX_IMAGE_BAR_WIDTH) / max;
  }
}

