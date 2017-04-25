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

/**
 */
public final class WebUIConstants {

  public static final byte MODE_EDIT = 1;
  public static final byte MODE_VIEW = 2;

  /**
   * This mode is used to tell the panel that it is used as
   * a part of other panel that will use editable parts of
   * it to enter (overwrite) thei own values.
   */
  public static final byte MODE_INHERITED = 3;

  public static final String COOKIE_DISPLAY_GROUP_ID = "parabuild_display_gid";
  public static final String COOKIE_LAST_LOGGED_NAME = "parabuild_last_logged_name";
  public static final String COOKIE_MERGE_DISPLAY_GROUP_ID = "parabuild_merge_display_gid";
  public static final String COOKIE_REMEMBER_ME = "parabuild_rememeber_me";
  public static final String COOKIE_STATUS_VIEW = "parabuild.status.view";

  public static final int TWO_WEEKS_IN_SECONDS = 14 * 60 * 60 * 24 * 7;
  public static final int YEAR_IN_SECONDS = 365 * 60 * 60 * 24 * 7;

  public static final String IMAGE_3232_BULLET_BALL_GLASS_GREEN_GIF = "/parabuild/images/3232/bullet_ball_glass_green.gif";
  public static final String IMAGE_3232_BULLET_BALL_GLASS_BLUE_GIF = "/parabuild/images/3232/bullet_ball_glass_blue.gif";
  public static final String IMAGE_3232_BULLET_BALL_GLASS_RED_GIF = "/parabuild/images/3232/bullet_ball_glass_red.gif";
  public static final String IMAGE_3232_BULLET_BALL_GLASS_GRAY_GIF = "/parabuild/images/3232/bullet_ball_glass_gray.gif";
  public static final String IMAGE_GREEN_THROBBER_GIF = "/parabuild/images/throbber-green.gif";
  public static final String IMAGE_RED_THROBBER_GIF = "/parabuild/images/throbber-red.gif";
  public static final String IMAGE_BLACK_THROBBER_GIF = "/parabuild/images/throbber-black.gif";

  public static final String SESSION_ATTR_SELECTED_DISPLAY_GROUP_ID = "selected.display.group.id";
  public static final String SESSION_ATTR_DETAILED_BUILD_ID = "session.detailed.build.id";
  public static final String SESSION_ATTR_STATUS_VIEW = "session.status.view";


  private WebUIConstants() {
  }
}
