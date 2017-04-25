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
import org.parabuild.ci.common.StringUtils;

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
    } catch (UserCanceledException e) {
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
    } catch (IOException e) {
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


  private void deleteIfExists(final File file) {
    if (file.exists()) {
      file.delete();
    }
  }
}

/**
 To start matlabserver automatically at system boot, create the following links and file while logged in as root (superuser).
 ln -s $MATLAB/webserver/webboot /etc/webboot$WEBSERVER_MARKER
 ln -s $MATLAB/webserver/webdown /etc/webdown$WEBSERVER_MARKER



 $WEBSERVER_MARKER is a marker string that uniquely identifies this release of the MATLAB Web Server. It is defined in the matlabserver.conf file. (See matlabserver.conf.) The default is _TMW$RELEASE, where $RELEASE is a string like 'R12', also set in matlabserver.conf.


 Note    Add the -c configuration file option to webboot and webdown if the matlabserver.conf file is not in <matlab>/webserver or in the directory where the script is located. For example: webboot -c $CONFIGURATION_FILE $CONFIGURATION_FILE is the path to the file matlabserver.conf.


 In the directory $MATLAB/webserver are two initialization scripts:
 rc.web.sol2 (Solaris)
 rc.web.glnx86 (Linux)

 Solaris users should copy the script as shown below.

 cp $MATLAB/webserver/rc.web.sol2 /etc/init.d/webserver

 Linux users should copy the appropriate script as shown below.

 cp $MATLAB/webserver/rc.web.glnx86 /etc/init.d/webserver (Debian)
 cp $MATLAB/webserver/rc.web.glnx86 /etc/rc.d/init.d/webserver
 (Red Hat)



 Open the copied file in a text editor and follow the directions for modifying the file. Save and close the file when you are done.
 Solaris users should create a link in the rc directory associated with run level 3.
 cd /etc/rc3.d; ln -s ../init.d/webserver S20webserver



 Linux users should look in /etc/inittab for the default run level. Create a link in the rc directory associated with that run level. For example, if it is 5

 cd /etc/rc5.d; ln -s ../init.d/webserver S95weberver (Debian)
 cd /etc/rc.d/rc5.d; ln -s init.d/webserver S95webserver (Red Hat)



 You can test the changes you have made without rebooting your system. To start the MATLAB Web Server on Solaris, enter
 cd /etc/init.d
 ./webserver start



 On Linux, enter

 cd /etc/init.d (Debian)
 cd /etc/rc.d/init.d (Red Hat)
 ./webserver start



 To check that the MATLAB Web Server is operational on any system, enter
 cd $MATLAB/webserver
 webstat -c $CONFIGURATION_FILE



 $CONFIGURATION_FILE is the path to the file matlabserver.conf.
 */