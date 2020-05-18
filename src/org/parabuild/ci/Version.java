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
package org.parabuild.ci;

import org.parabuild.ci.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * This class hold product version information
 */
public final class Version implements Serializable {

  public static final String STR_PRODUCT_NAME = "Parabuild";

  private static final long serialVersionUID = -2274590107946127380L; // NOPMD

  private static final String META_INF_PARABUILD_VERSION_PROPERTIES = "META-INF/parabuild-version.properties";

  private static final String PARABUILD_RELEASE_VERSION = "parabuild.release.version";

  private static final String PARABUILD_RELEASE_CHANGE = "parabuild.release.change";

  private static final String PARABUILD_RELEASE_BUILD = "parabuild.release.build";

  private static final String PARABUILD_RELEASE_DATE = "parabuild.release.date";

  private static final String STR_SPACE = " ";

  private static final String SNAPSHOT = "SNAPSHOT";


  /**
   * @return product name
   */
  public static String productName() {

    return STR_PRODUCT_NAME;
  }


  /**
   * @return product version
   */
  public static String productVersion() {

    return readReleaseProperty(PARABUILD_RELEASE_VERSION);
  }


  /**
   * @return release date
   */
  public static String releaseDate() {

    return readReleaseProperty(PARABUILD_RELEASE_DATE);
  }


  /**
   * @return release change
   */
  public static String releaseChange() {

    return readReleaseProperty(PARABUILD_RELEASE_CHANGE);
  }


  /**
   * @return release build number
   */
  public static String releaseBuild() {

    return readReleaseProperty(PARABUILD_RELEASE_BUILD);
  }


  public static String versionToString(final boolean fullVersion) {

    final StringBuilder result = new StringBuilder(30);

    // static part
    result.append(productName()).append(STR_SPACE);
    result.append(productVersion()).append(STR_SPACE);
    if (!fullVersion) {
      return result.toString();
    }

    // dynamic part
    if (!releaseDate().isEmpty()) {
      result.append(releaseDate()).append(STR_SPACE);
    }
    if (!releaseChange().isEmpty() && !SNAPSHOT.equals(releaseBuild())) {
      result.append(releaseChange()).append(STR_SPACE);
    }
    if (!releaseBuild().isEmpty() && !"0".equals(releaseBuild()) && !SNAPSHOT.equals(releaseBuild())) {
      result.append("build ").append(releaseBuild()).append(STR_SPACE);
    }
    if (releaseDate().isEmpty() || releaseBuild().isEmpty()) {
      result.append("Internal release").append(STR_SPACE);
    }
    return result.toString();
  }


  private static String readReleaseProperty(final String propertyName) {

    final Properties parabuildVersionProperties = readParabuildVersionProperties();
    return parabuildVersionProperties.getProperty(propertyName);
  }


  private static Properties readParabuildVersionProperties() {

    InputStream inputStream = null;
    try {
      final String resourceAsString = IoUtils.getResourceAsString(META_INF_PARABUILD_VERSION_PROPERTIES);
      inputStream = IoUtils.stringToInputStream(resourceAsString);
      final Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }
}
