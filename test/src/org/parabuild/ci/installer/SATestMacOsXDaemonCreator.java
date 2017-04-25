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

import com.install4j.api.actions.Action;
import com.install4j.api.beans.Bean;
import com.install4j.api.beans.ExternalFile;
import com.install4j.api.beans.ScriptProperty;
import com.install4j.api.context.FileInfo;
import com.install4j.api.context.FileOptions;
import com.install4j.api.context.FileSetSetup;
import com.install4j.api.context.InstallationComponentSetup;
import com.install4j.api.context.InstallerContext;
import com.install4j.api.context.LauncherSetup;
import com.install4j.api.context.ProgressInterface;
import com.install4j.api.context.UserCanceledException;
import com.install4j.api.context.WizardContext;
import com.install4j.api.events.InstallerEventListener;
import com.install4j.api.screens.Screen;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * Tests MacOsXDaemonCreator
 * @noinspection ZeroLengthArrayAllocation
 */
public final class SATestMacOsXDaemonCreator extends TestCase {

  private static final Log log = LogFactory.getLog(SATestMacOsXDaemonCreator.class);


  public void test_createDaemon() throws UserCanceledException, IOException {
    // create mock ups
    final MockInstallerUserCreator mockUserCreator = new MockInstallerUserCreator();
    final DirectoryOwnerChanger mockOwnerChanger = new MockDirectoryOwnerChanger();
    final MockInstallerContext mockInstallerContext = new MockInstallerContext();

    // execute createDaemon
    final MacOsXDaemonCreator macOsXDaemonCreator = new MacOsXDaemonCreator(mockUserCreator, mockOwnerChanger);
    macOsXDaemonCreator.createDaemon(mockInstallerContext);

    // assert files
    final Map installedFileMap = mockInstallerContext.getInstalledFileMap();
    assertNotNull(installedFileMap.get(MacOsXDaemonCreator.STARTUP_PARABUILD));
    assertNotNull(installedFileMap.get(MacOsXDaemonCreator.STARTUP_PARAMETERS));
    assertNotNull(installedFileMap.get(MacOsXDaemonCreator.STARTUP_LOCALIZABLE_STRINGS));

    // assers users
    assertTrue(mockUserCreator.isCreateUserCalled());
    assertEquals(mockUserCreator.createUserNameCalled, InstallerConstants.PARABUILD_USER);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestMacOsXDaemonCreator.class);
  }


  public SATestMacOsXDaemonCreator(final String s) {
    super(s);
  }


  /**
   * Mock InstallerUserCreator
   */
  private static class MockInstallerUserCreator implements InstallerUserCreator {

    private boolean createUserCalled = false;
    private String createUserNameCalled = null;


    public void createGroup(final String group) throws IOException {
    }


    public void createUser(final String user) throws IOException {
      createUserCalled = true;
      createUserNameCalled = user;
    }


    public boolean isCreateUserCalled() {
      return createUserCalled;
    }


    public String getCreateUserNameCalled() {
      return createUserNameCalled;
    }
  }

  /**
   * Mock InstallerUserCreator
   */
  private static class MockDirectoryOwnerChanger implements DirectoryOwnerChanger {

    public void changeOwner(final File installationDir, final String user) {

    }
  }

  /**
   * Mock InstallerContext
   */
  private static class MockInstallerContext implements InstallerContext {

    private final Map installedFileMap = new HashMap(11);


    public Map getInstalledFileMap() {
      return Collections.unmodifiableMap(installedFileMap);
    }


    public boolean isCreateMenu() {
      return false;
    }


    public String getProgramGroup() {
      return null;
    }


    public boolean isCreateMenuAllUsers() {
      return false;
    }


    public Collection getServiceSetups() {
      return null;
    }


    public Collection getInstallationComponents() {
      return null;
    }


    public InstallationComponentSetup getInstallationComponentById(String id) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Collection getLaunchers() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public LauncherSetup getLauncherById(String id) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isCancelling() {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void handleCriticalException(Throwable e) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public File getDestinationFile(File archiveFile) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public File getDestinationFile(String archivePath) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public FileInfo getDestinationFileInfo(String archivePath) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object getVariable(String variableName) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean getBooleanVariable(String variableName) {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setVariable(String variableName, Object value) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public Set getVariableNames() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String getCompilerVariable(String variableName) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object runScript(ScriptProperty scriptProperty, Bean bean, Object[] parameters) throws Exception {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public ProgressInterface getProgressInterface() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void goForward(int numberOfScreens, boolean checkCondition, boolean executeActions) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void goBack(int numberOfScreens) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void goBackInHistory(int numberOfScreens) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void goBackInHistory(Screen targetScreen) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public File getExternalFile(ExternalFile externalFile, boolean installedLocation) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isErrorOccured() {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setErrorOccured(boolean errorOccured) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void registerResponseFileVariable(String variableName) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void registerHiddenVariable(String variableName) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void triggerReboot(boolean askUser) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public Collection getCustomTasks() {
      return null;
    }


    public boolean isCreateDesktopIcon() {
      return false;
    }


    public boolean isCreateQuickLaunchIcon() {
      return false;
    }


    public String getMediaName() {
      return null;
    }


    public boolean installFile(final File file, final File file1) throws UserCanceledException {
      return false;
    }


    public boolean installFile(final File file, final File file1, final FileOptions fileOptions) throws UserCanceledException {
      final String fileName = file1.toString().replace('\\', '/');
      installedFileMap.put(fileName, fileName);
      return false;
    }


    public boolean installFile(final File file, final File file1, final FileOptions fileOptions, final ProgressInterface progressInterface, final int i, final int i1) throws UserCanceledException {
      return false;
    }


    public void registerUninstallFile(final File file) {

    }


    public void setInstallationDirectory(File installationDirectory) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public File getInstallerFile() {
      return null;
    }


    public void abort() {

    }


    public String getLanguageId() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String getMessage(String key) throws MissingResourceException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public File getInstallationDirectory() {
      return TestHelper.getTestTempDir();
    }


    public boolean isAdminUser() {
      return false;
    }


    public String getUserVariableValue(final String s) {
      return null;
    }


    public void rebootAfterFinish() {

    }


    public boolean isUnattended() {
      return false;
    }


    public boolean isConsole() {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String getApplicationId() {
      return null;
    }


    public String getAddOnApplicationId() {
      return null;
    }


    public void addInstallerEventListener(InstallerEventListener listener) {

    }


    public void removeInstallerEventListener(InstallerEventListener listener) {

    }


    public void gotoScreen(Screen screen) {

    }


    public Screen getScreenById(String id) {
      return null;
    }


    public Screen[] getScreens() {
      return new Screen[0];
    }


    public Screen[] getScreens(Class screenClass) {
      return new Screen[0];
    }


    public Screen getFirstScreen(Class screenClass) {
      return null;
    }


    public Action getActionById(String id) {
      return null;
    }


    public Action[] getActions(Screen screen) {
      return new Action[0];
    }


    public Action[] getActions(Class actionClass, Screen screen) {
      return new Action[0];
    }


    public Action getFirstAction(Class actionClass, Screen screen) {
      return null;
    }


    public WizardContext getWizardContext() {
      return null;
    }


    public Collection getFileSets() {
      return null;
    }


    public FileSetSetup getFileSetById(String id) {
      return null;  
    }
  }
}
