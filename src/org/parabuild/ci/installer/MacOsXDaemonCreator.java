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

import com.install4j.api.context.FileOptions;
import com.install4j.api.context.InstallerContext;
import com.install4j.api.context.OverwriteMode;
import com.install4j.api.context.UserCanceledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;

/**
 * This class creates Linux daemon files.
 */
public final class MacOsXDaemonCreator implements UnixDaemonCreator {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(MacOsXDaemonCreator.class); // NOPMD

  public static final String STARTUP_DIR = "/Library/StartupItems/Parabuild";
  public static final String STARTUP_PARABUILD = STARTUP_DIR + '/' + "Parabuild";
  public static final String STARTUP_PARAMETERS = STARTUP_DIR + '/' + "StartupParameters.plist";
  public static final String STARTUP_ENGLISH_LPROJ_DIR = STARTUP_DIR + '/' + "Resources/English.lproj";
  public static final String STARTUP_LOCALIZABLE_STRINGS = STARTUP_ENGLISH_LPROJ_DIR + '/' + "Localizable.strings";
  private static final TemplateParameter[] EMPTY_TEMPLATE_PARAMETER_ARRAY = new TemplateParameter[0];


  private InstallerContext ictx = null;
  private DirectoryOwnerChanger ownerChanger = null;
  private InstallerUserCreator userCreator = null;


  public MacOsXDaemonCreator(final InstallerUserCreator userCreator, final DirectoryOwnerChanger ownerChanger) {
    this.ownerChanger = ownerChanger;
    this.userCreator = userCreator;
  }


  public void createDaemon(final InstallerContext installerContext) throws IOException, UserCanceledException {
    this.ictx = installerContext;
    createUser();
    createGroup();
    chownDirs();
    installDaemon();
  }


  /**
   * Changes ownership of install dirs.
   */
  private void chownDirs() {
    ownerChanger.changeOwner(ictx.getInstallationDirectory(), InstallerConstants.PARABUILD_USER);
  }


  public final void createGroup() throws IOException {
    userCreator.createGroup(InstallerConstants.PARABUILD_GROUP);
  }


  public final void createUser() throws IOException {
    userCreator.createUser(InstallerConstants.PARABUILD_USER);
  }


  public void installDaemon() throws UserCanceledException, IOException {
    // startup script
    final TemplateParameter[] params = {new TemplateParameter(0, ictx.getInstallationDirectory().toString())};
    final String macOSXLibraryStartupItemsParabuildParabuild = IoUtils.getResourceAsString("macosx/Parabuild");
    installFile(macOSXLibraryStartupItemsParabuildParabuild, new File(STARTUP_PARABUILD), params);

    // localasible strings
    final String macOSXLibraryStartupItemsParabuildResourcesEnglishLprojLocalizableStrings = IoUtils.getResourceAsString("macosx/Resources/English.lproj/Localizable.strings");
    installFile(macOSXLibraryStartupItemsParabuildResourcesEnglishLprojLocalizableStrings, new File(STARTUP_LOCALIZABLE_STRINGS), EMPTY_TEMPLATE_PARAMETER_ARRAY);

    // startup param list
    final String macOSXLibraryStartupItemsParabuildStartupParametersPlist = IoUtils.getResourceAsString("macosx/StartupParameters.plist");
    installFile(macOSXLibraryStartupItemsParabuildStartupParametersPlist, new File(STARTUP_PARAMETERS), EMPTY_TEMPLATE_PARAMETER_ARRAY);
  }


  private void installFile(final String content, final File destinationFile, final TemplateParameter[] templateParams) throws IOException, UserCanceledException {

    // get size
    int size = 0;
    for (int i = 0; i < templateParams.length; i++) {
      final TemplateParameter templateParam = templateParams[i];
      size = Math.max(size, templateParam.index() + 1);
    }

    // create param list
    final Object[] paramList = new Object[size];
    for (int i = 0; i < templateParams.length; i++) {
      final TemplateParameter templateParam = templateParams[i];
      paramList[templateParam.index()] = templateParam.setTo();
    }


    // set params in the template
    final MessageFormat messageFormat = new MessageFormat(content);
    final String formattedContent = messageFormat.format(paramList);
//    if (log.isDebugEnabled()) log.debug("formattedContent: " + formattedContent);
    // write content to temp file
    StringReader sr = null;
    BufferedReader br = null;
    BufferedWriter bw = null;
    File tempFile = null;
    try {
      tempFile = IoUtils.createTempFile(".installer", "tmp");
      bw = new BufferedWriter(new FileWriter(tempFile));
      sr = new StringReader(formattedContent);
      br = new BufferedReader(sr);
      for (String line = br.readLine(); line != null;) {
        bw.write(line);
        bw.newLine();
        line = br.readLine();
      }
    } finally {
      IoUtils.closeHard(br);
      IoUtils.closeHard(bw);
      IoUtils.closeHard(sr);
    }

    // install file
    ictx.installFile(tempFile, destinationFile, new FileOptions("755", OverwriteMode.ALWAYS, false));

    // delete temp file
    tempFile.delete();
  }


  /**
   * Holds parameter information.
   */
  private static final class TemplateParameter {

    private int idx = 0;
    private String to = null;


    /**
     * Constructor.
     *
     * @param index param index
     * @param setTo to waht string to replace
     */
    TemplateParameter(final int index, final String setTo) {
      this.idx = index;
      this.to = setTo;
    }


    public int index() {
      return idx;
    }


    public String setTo() {
      return to;
    }


    public String toString() {
      return "TemplateParameter{" +
              "idx=" + idx +
              ", to='" + to + '\'' +
              '}';
    }
  }


  public String toString() {
    return "MacOsXDaemonCreator{" +
            "ictx=" + ictx +
            ", ownerChanger=" + ownerChanger +
            ", userCreator=" + userCreator +
            '}';
  }
}
