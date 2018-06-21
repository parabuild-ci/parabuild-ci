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
package org.parabuild.ci.webui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.search.HitsTraverserCallback;
import org.parabuild.ci.search.LuceneDocumentFactory;
import org.parabuild.ci.search.SearchHitsTraverser;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Writes to web's PrintWriter
 */
public final class WritingHitsTraverserCallback implements HitsTraverserCallback, Serializable {

  private static final long serialVersionUID = -4874877212465157160L; // NOPMD
  private static final Log log = LogFactory.getLog(WritingHitsTraverserCallback.class);

  public static final String STR_SEPARATOR = "&nbsp;&nbsp;&gt;&nbsp;&nbsp;";

  private transient PrintWriter pw = null;


  /**
   * Constructor.
   *
   * @param pw
   */
  public WritingHitsTraverserCallback(final PrintWriter pw) {
    this.pw = pw;
  }


  /**
   * Callback method.
   *
   * @param document
   *
   * @see SearchHitsTraverser
   */
  public void foundStepLog(final Document document) {
    //validate
    if (pw == null) {
      log.error("Writer was null when traversing step log");
      return;
    }
    // compose a link caption
    final String logID = document.get(LuceneDocumentFactory.FIELD_SEQUENCE_LOG_ID);
    if (StringUtils.isValidInteger(logID)) {
      // get params
      final String buildName = document.get(LuceneDocumentFactory.FIELD_BUILD_NAME);
      final String fileName = document.get(LuceneDocumentFactory.FIELD_SEQUENCE_LOG_FILE_NAME_IN_DIR);
      final String logDescr = document.get(LuceneDocumentFactory.FIELD_SEQUENCE_LOG_DESCR);
      final String logPath = document.get(LuceneDocumentFactory.FIELD_SEQUENCE_LOG_CONFIG_PATH);
      final String pathType = document.get(LuceneDocumentFactory.FIELD_SEQUENCE_LOG_PATH_TYPE);
      final String runNumber = document.get(LuceneDocumentFactory.FIELD_BUILD_RUN_NUMBER);
      final String seqName = document.get(LuceneDocumentFactory.FIELD_SEQUENCE_NAME);

      // make caption
      final StringBuffer caption = new StringBuffer(30);

      // build name, if any
      if (!StringUtils.isBlank(buildName)) {
        caption.append(buildName);
        // run number, if any
        if (!StringUtils.isBlank(runNumber)) {
          caption.append(" #").append(runNumber).append(STR_SEPARATOR);
        }
      }

      // sequence name if any
      if (!StringUtils.isBlank(seqName)) {
        caption.append(seqName).append(STR_SEPARATOR);
      }

      // log file or dir path, if any
      final boolean logPathPresent = !StringUtils.isBlank(logPath);
      if (logPathPresent) {
        caption.append(logPath);
      }

      // log descr., if any
      if (!StringUtils.isBlank(logDescr)) {
        final String lBracket = logPathPresent ? "(" : "";
        final String rBracket = logPathPresent ? ")" : "";
        caption.append(' ');
        caption.append(lBracket);
        caption.append(logDescr);
        caption.append(rBracket);
      }

      final String targetElement = "target=\"_blank\"";

      // dir/or fil log?
      final StringBuffer fileNameParam = new StringBuffer(100);
      if (StringUtils.isValidInteger(pathType)) {
        final int intPathType = Integer.parseInt(pathType);
        if (intPathType == StepLog.PATH_TYPE_TEXT_DIR) {
          if (!StringUtils.isBlank(fileName)) {
            String encodedFileName = null;
            try {
              // UTF-8
              encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
              try {
                // didn't work, platform encoding
                encodedFileName = URLEncoder.encode(fileName, System.getProperty("file.encoding"));
              } catch (final UnsupportedEncodingException e1) {
                // didn't work, leave as is
                encodedFileName = fileName;
              }
            }
            fileNameParam.append('&').append(Pages.PARAM_FILE_NAME).append('=').append(encodedFileName);
            caption.append(STR_SEPARATOR).append(fileName); // REVIEWME: we don't encode file name for the anchor caption - take care of it when there is time.
          }
        }
      }


      if (caption.length() == 0) return; // didn't manage to compose caption
      pw.println("<br/>");
      pw.println(new StringBuffer(200).append("<a ").append(targetElement).append(" style=\"font-family: sans-serif; font-size: 12; font-weight: normal; color: #000080;\" href=\"/parabuild/build/log.htm?logid=").append(logID).append(fileNameParam.toString()).append("\">").append(caption).append("</a>").toString());
    }
  }


  public void foundChangeList(final Document document) {
    //validate
    if (pw == null) {
      log.error("Writer was null when traversing step log");
      return;
    }

    // compose a link caption
    final String buildRunID = document.get(LuceneDocumentFactory.FIELD_BUILD_RUN_ID);
    if (StringUtils.isValidInteger(buildRunID)) {
      // get params
      final String buildName = document.get(LuceneDocumentFactory.FIELD_BUILD_NAME);
      final String runNumber = document.get(LuceneDocumentFactory.FIELD_BUILD_RUN_NUMBER);
      final String changeListNumber = document.get(LuceneDocumentFactory.FIELD_CHANGELIST_NUMBER);
      final String user = document.get(LuceneDocumentFactory.FIELD_CHANGELIST_USER);

      // make caption
      final StringBuffer caption = new StringBuffer(30);

      // build name, if any
      if (!StringUtils.isBlank(buildName)) {
        caption.append(buildName);
        // run number, if any
        if (!StringUtils.isBlank(runNumber)) {
          caption.append(" #").append(runNumber).append(STR_SEPARATOR);
        }
      }

      // change list number and user
      if (!StringUtils.isBlank(changeListNumber)) {
        caption.append("change list #").append(changeListNumber);
        if (!StringUtils.isBlank(user)) {
          caption.append(" by ").append(user);
        }
      }

      if (caption.length() == 0) return; // didn't manage to compose caption

      final String targetElement = "target=\"_blank\"";
      pw.println("<br/>");
      pw.println("<a " + targetElement +
        " style=\"font-family: sans-serif; font-size: 14; font-weight: normal; color: #000080;\"" +
        " href=\"/parabuild/build/changes.htm?buildrunid=" + buildRunID + "\">" + caption + "</a>");
    }
  }


  public void foundResult(final Document document) {
    //validate
    if (pw == null) {
      log.error("Writer was null when traversing step log");
      return;
    }

    // compose a link caption
    final String buildID = document.get(LuceneDocumentFactory.FIELD_BUILD_ID);
    if (!StringUtils.isValidInteger(buildID)) return;

    final String stepResultID = document.get(LuceneDocumentFactory.FIELD_RESULT_STEP_RESULT_ID);
    if (!StringUtils.isValidInteger(stepResultID)) return;

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final StepResult stepResult = cm.getStepResult(Integer.parseInt(stepResultID));
    if (stepResult == null) return;

    // get params
    final String buildName = document.get(LuceneDocumentFactory.FIELD_BUILD_NAME);
    final String runNumber = document.get(LuceneDocumentFactory.FIELD_BUILD_RUN_NUMBER);
    final String fileName = document.get(LuceneDocumentFactory.FIELD_RESULT_FILE_NAME);

    // make caption
    final StringBuffer caption = new StringBuffer(30);

    // build name, if any
    if (!StringUtils.isBlank(buildName)) {
      caption.append(buildName);
      // run number, if any
      if (!StringUtils.isBlank(runNumber)) {
        caption.append(" #").append(runNumber).append(STR_SEPARATOR);
      }
    }

    // change list number and user
    if (!StringUtils.isBlank(fileName)) {
      caption.append("result file: ").append(fileName);
    }

    if (caption.length() == 0) return; // didn't manage to compose caption

    final String url = WebuiUtils.makeResultURLPathInfo(Integer.parseInt(buildID), stepResult.getID(), fileName);
    final String targetElement = "target=\"_blank\"";
    pw.println("<br/>");
    pw.println("<a " + targetElement +
      " style=\"font-family: sans-serif; font-size: 14; font-weight: normal; color: #000080;\"" +
      " href=\"" + url + "\">" + caption + "</a>");
  }
}