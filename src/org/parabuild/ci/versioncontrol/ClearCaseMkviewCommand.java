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
package org.parabuild.ci.versioncontrol;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;

/**
 * Executes ClearCase mkvew command. Minimally this should call
 * ClearCaseRmtagCommand. Ideally shown use lsview using the
 * given tag or somehow else vaildate that the view exists before
 * deleting. Otherwise storage will remain on the server.
 * <p/>
 * This command always deletes local copy before executing
 * cleartoo mkview.
 *
 * @see ClearCaseRmtagCommand
 */
final class ClearCaseMkviewCommand extends ClearCaseCommand {

  private static final Log log = LogFactory.getLog(ClearCaseMkviewCommand.class);

  private final String viewTag;
  private final byte textModeCode;
  private final byte storageLocationMode;
  private final String storageLocation;


  /**
   * Constructor
   *
   * @param agent
   * @param exePath
   * @param textModeCode
   * @param viewTag
   * @throws IOException
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_AUTO
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_MSDOS
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_NOT_SET
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_UNIX
   */
  public ClearCaseMkviewCommand(final Agent agent, final String exePath, final byte textModeCode,
                                final byte storageLocationMode, final String storageLocation, final String viewTag,
                                final String ignoreLines)
          throws IOException, AgentFailureException {
    //
    super(agent, exePath);
    super.setCurrentDirectory(agent.getCheckoutDirHome()); // mkview doesn't have current dir
    super.setStderrLineProcessor(new MkViewStderrLineProcessor(ignoreLines));

    // set
    this.viewTag = ArgumentValidator.validateArgumentNotBlank(viewTag, "view tag");
    this.textModeCode = textModeCode;
    this.storageLocation = storageLocation;
    this.storageLocationMode = storageLocationMode;
    if (log.isDebugEnabled()) log.debug("viewTag: " + viewTag);
  }


  /**
   * Constructor
   *
   * @param agent
   * @param exePath
   * @param textMode this one is used because we have a siuation
   *                 when a text mode drop down stores string values instead of
   *                 codes. We should account for this.
   * @param viewTag
   * @throws IOException
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_AUTO
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_MSDOS
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_NOT_SET
   * @see SourceControlSetting#CLEARCASE_TEXT_MODE_UNIX
   */
  public ClearCaseMkviewCommand(final Agent agent, final String exePath,
                                final String textMode, final byte storageLocationMode, final String storageLocation, final String viewTag, final String ignoreLines) throws IOException, AgentFailureException {
    this(agent, exePath, textModeToCode(agent, textMode), storageLocationMode, storageLocation, viewTag, ignoreLines);
  }


  /**
   * Translates string text mode to byte code.
   *
   * @param textMode
   * @return byte code of the text mode.
   */
  private static byte textModeToCode(final Agent agent, final String textMode) throws IOException, AgentFailureException {
    if (StringUtils.isValidInteger(textMode)) {
      return Byte.parseByte(textMode);
    } else {
      // translate
      final ClearCaseTextModeCodeTranslator translator = new ClearCaseTextModeCodeTranslator(agent);
      return translator.translateTextModeName(textMode);
    }
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();
    // has to delete - otherwise mkview complains:
    // "cleartool: Error: Unable to create directory "D:\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o": File exists."
    // also, see bug #740
    final boolean checkoutDirDeleted = agent.deleteCheckoutDir();
    if (log.isDebugEnabled()) log.debug("checkoutDirDeleted: " + checkoutDirDeleted);
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    try {
      // prepare storege location argument
      final boolean isWindows = agent.isWindows();
      final ClearCaseStorageLocationArgumentFactory storageLocationArgumentFactory = new ClearCaseStorageLocationArgumentFactory(agent.getActiveBuildID(), isWindows);
      final String storageLocationArgument = storageLocationArgumentFactory.makeStorageLocationArgument(storageLocationMode, storageLocation);

      // text mode
      final ClearCaseTextModeCodeTranslator tr = new ClearCaseTextModeCodeTranslator(agent);
      final String textMode = tr.translateTextModeCode(textModeCode);
      final String textModeToAppend = !StringUtils.isBlank(textMode) ? " -tmode " + textMode : "";
      final String checkoutDirName = agent.getCheckoutDirName();

      // compse command line
      final StringBuilder sb = new StringBuilder(100);
      sb.append(" mkview ");
      sb.append(textModeToAppend);
      sb.append(storageLocationArgument);
      sb.append(" -tag ").append(viewTag);
      sb.append(" -snapshot ");
      sb.append(isWindows ? StringUtils.putIntoDoubleQuotes(checkoutDirName) : checkoutDirName); // pathame for view
      return sb.toString();
    } catch (final BuildException e) {
      throw IoUtils.createIOException(e);
    }
  }


  /**
   * Mkview stderr line processor.
   */
  private static final class MkViewStderrLineProcessor extends AbstractClearCaseStderrProcessor {

    public MkViewStderrLineProcessor(final String ignoreLines) {
      super(ignoreLines);
    }


    protected int doProcessLine(final int index, final String line) {
      // ignore that "strange" lines - See bug #740.
      if (line.startsWith("cleartool: Warning: Unable to register new snapshot view: not a ClearCase object")
              || line.startsWith("snapshot view may not be recognized by some commands")) {
        log.warn("ignored cleartool line: " + line);
        return RESULT_IGNORE;
      } else {
        return RESULT_ADD_TO_ERRORS;
      }
    }
  }
}
