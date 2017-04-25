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
 * Contains image names used in the tray application.
 */
final class TrayImageResourceCollection {

  private static final String IMAGES_RESOURCE_HOME = "/resources/images/";
  public static final String IMAGE_ACCESS_FORBIDDEN = IMAGES_RESOURCE_HOME + "bullet_ball_glass_yellow.gif";
  public static final String IMAGE_FAILED = IMAGES_RESOURCE_HOME + "bullet_ball_glass_red.gif";
  public static final String IMAGE_NOT_RUN_YET = IMAGES_RESOURCE_HOME + "bullet_ball_glass_blue.gif";
  public static final String IMAGE_SUCCESSFUL = IMAGES_RESOURCE_HOME + "bullet_ball_glass_green.gif";
  public static final String IMAGE_RUNNING_WAS_SUCCESSFUL = IMAGES_RESOURCE_HOME + "bullet_triangle_green.gif";
  public static final String IMAGE_RUNNING_FAILED = IMAGES_RESOURCE_HOME + "bullet_triangle_red.gif";
  public static final String IMAGE_RUNNING_NOT_RUN_YET = IMAGES_RESOURCE_HOME + "bullet_triangle_blue.gif";
  public static final String IMAGE_INACTIVE = IMAGES_RESOURCE_HOME + "bullet_ball_glass_gray.gif";


  private TrayImageResourceCollection() {
  }
}
