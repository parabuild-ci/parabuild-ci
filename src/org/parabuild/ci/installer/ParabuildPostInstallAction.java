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
package org.parabuild.ci.installer;

import com.install4j.api.actions.AbstractInstallAction;
import com.install4j.api.context.InstallerContext;
import com.install4j.api.context.UserCanceledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * This action runs after general file level installation of
 * Parabuild is completed.
 */
public final class ParabuildPostInstallAction extends AbstractInstallAction {

  private static final Log log = LogFactory.getLog(AbstractUnixDaemonCreator.class);
  private static final String LIB_COMMON_ENDORSED = "lib" + File.separatorChar + "common" + File.separatorChar + "endorsed";
  private static final String XERCES_IMPL_JAR = LIB_COMMON_ENDORSED + File.separatorChar + "xercesImpl.jar";
  private static final String XML_PARSER_APIS_JAR = LIB_COMMON_ENDORSED + File.separatorChar + "xmlParserAPIs.jar";


  public boolean install(final InstallerContext ctx) throws UserCanceledException {
    try {
      uninstallEndorsedJars(ctx);
      createUnixDaemon(ctx);
    } catch (final UserCanceledException e) {
      ctx.getProgressInterface().showFailure(StringUtils.toString(e));
      log.warn("Exaception while performing unattened action", e);
      throw e;
    }
    return true;
  }


  /**
   * Creates files necessary to make a daemon.
   */
  public void createUnixDaemon(final InstallerContext installerContext) throws UserCanceledException {
    try {
      final UnixDaemonCreator creator = UnixDaemonCreatorFactory.getCreator();
      creator.createDaemon(installerContext);
    } catch (final IOException e) {
      final UserCanceledException uce = new UserCanceledException(StringUtils.toString(e));
      uce.initCause(e);
      throw uce;
    }
  }


  /**
   * This action uninstalls Tomcat's endorsed jars that conflict with JRE 1.5.
   */
  private void uninstallEndorsedJars(final InstallerContext installerContext) {
    deleteIfExists(new File(installerContext.getInstallationDirectory(), XERCES_IMPL_JAR));
    deleteIfExists(new File(installerContext.getInstallationDirectory(), XML_PARSER_APIS_JAR));
  }


  private static void deleteIfExists(final File file) {
    if (file.exists()) {
      file.delete();
    }
  }
}
