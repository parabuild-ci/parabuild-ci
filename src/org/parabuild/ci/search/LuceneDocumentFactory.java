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
package org.parabuild.ci.search;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.apache.lucene.document.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.SearchPage;
import org.parabuild.ci.webui.LogPage;

/**
 * Utility class.
 */
public final class LuceneDocumentFactory {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(LuceneDocumentFactory.class); // NOPMD


  // Document types
  public static final String TYPE_SEQUENCE_LOG = "tsl";
  public static final String TYPE_CHANGE_LIST = "tchl";
  public static final String TYPE_BUILD_RESULT = "tbrr";

  public static final String FIELD_DOCUMENT_TYPE = "fndt";
  public static final String FIELD_CONTENT = "fncn";
  public static final String FIELD_BUILD_ID = "fnbi";
  public static final String FIELD_BUILD_STARTED = "fnbs";
  public static final String FIELD_BUILD_FINISHED = "fnbf";
  public static final String FIELD_BUILD_NAME = "fnbn";
  public static final String FIELD_BUILD_RUN_NUMBER = "fnbrn";
  public static final String FIELD_BUILD_RUN_ID = "fnbri";
  public static final String FIELD_STEP_STARTED = "fnss";
  public static final String FIELD_STEP_FINISHED = "fnsf";

  // change list-specific fields
  public static final String FIELD_CHANGELIST_ID = "fchli";
  public static final String FIELD_CHANGELIST_BRANCH = "fchlb";
  public static final String FIELD_CHANGELIST_NUMBER = "fchln";
  public static final String FIELD_CHANGELIST_USER = "fchlu";

  // sequence log-specific fields
  public static final String FIELD_SEQUENCE_LOG_ID = "fnsli";
  public static final String FIELD_SEQUENCE_LOG_TYPE = "fnslt";
  public static final String FIELD_SEQUENCE_LOG_PATH_TYPE = "fnslpt";

  /**
   * Log description as it was defined by log configuration. This
   * parameter can be used to show log description in search
   * results.
   */
  public static final String FIELD_SEQUENCE_LOG_DESCR = "fnsld";

  /**
   * Log path as it was defined by log configuration. This
   * parameter can be used to show log path in search results.
   */
  public static final String FIELD_SEQUENCE_LOG_CONFIG_PATH = "fnslcp";

  /**
   * This parameter is present in a hit for logs presenting a lis
   * of files in a directory. It allows to get a name of a log
   * file in the archive.
   * <p/>
   * SearchPage utilizes it to compose a file ID parameter for
   * LogPage so that a user can be directed right to the content
   * of a particular log in a directory.
   *
   * @see LogPage
   * @see SearchPage
   */
  public static final String FIELD_SEQUENCE_LOG_FILE_NAME_IN_DIR = "fnslfnid";

  /**
   * Sequence name.
   */
  public static final String FIELD_SEQUENCE_NAME = "fnsn";


  /**
   * Result file name field.
   */
  public static final String FIELD_RESULT_FILE_NAME = "fnrfn";


  /**
   * Result step run id field.
   */
  public static final String FIELD_RESULT_STEP_RESULT_ID = "fnsri";


  /**
   * Private constructor to protect factory from creation.
   */
  private LuceneDocumentFactory() {
  }


  /**
   * Creates indexable Lucene document from a StepLog. Uses given
   * file instead one defined by stepLog.
   *
   * @param stepLog
   *
   * @return indexable Lucene document
   *
   * @see StepLog
   */
  public static Document makeDocument(final BuildRun buildRun, final StepRun stepRun,
                                      final StepLog stepLog, final File stepLogFile)
    throws FileNotFoundException {

    final Document result = new Document();
    result.add(makeDocumentTypeField(TYPE_SEQUENCE_LOG));

    // add header fields
    addBuildRunFieldsToDocument(buildRun, result);

    result.add(new Field(FIELD_SEQUENCE_LOG_ID, stepLog.getIDAsString(), true, false, false));
    result.add(new Field(FIELD_SEQUENCE_LOG_TYPE, Byte.toString(stepLog.getType()), true, false, false));
    result.add(new Field(FIELD_SEQUENCE_LOG_PATH_TYPE, Byte.toString(stepLog.getPathType()), true, false, false));
    result.add(new Field(FIELD_SEQUENCE_LOG_CONFIG_PATH, stepLog.getPath(), true, false, false));
    result.add(new Field(FIELD_SEQUENCE_NAME, stepRun.getName(), true, false, false));
    result.add(new Field(FIELD_SEQUENCE_LOG_DESCR, stepLog.getDescription(), true, false, false));

    // set searchable content
    final Reader br = new BufferedReader(new FileReader(stepLogFile));
    result.add(Field.Text(FIELD_CONTENT, br));
    result.add(Field.UnStored(FIELD_CONTENT, stepLog.getDescription()));
    result.add(Field.UnStored(FIELD_CONTENT, buildRun.getBuildName()));
    return result;
  }


  /**
   * Creates indexable Lucene document from a StepLog. Uses given
   * file instead one defined by stepLog.
   * <p/>
   * Also appends a path to log file inside archive dir to a
   * document.
   */
  public static Document makeDocument(final BuildRun buildRun, final StepRun stepRun, final StepLog stepLog, final File stepLogFile, final String fileNameInArchiveDir) throws FileNotFoundException {
    final Document result = makeDocument(buildRun, stepRun, stepLog, stepLogFile);
    result.add(new Field(FIELD_SEQUENCE_LOG_FILE_NAME_IN_DIR, fileNameInArchiveDir, true, false, false));
    return result;
  }


  /**
   * Creates an indexable lucene document from change list.
   */
  public static Document makeDocument(final BuildRun buildRun, final ChangeList changeList, final List changes) {
    final Document result = new Document();
    result.add(makeDocumentTypeField(TYPE_CHANGE_LIST));

    // add header fields
    addBuildRunFieldsToDocument(buildRun, result);

    // index fields
    result.add(new Field(FIELD_CHANGELIST_ID, changeList.getChangeListIDAsString(), true, false, false));
    result.add(new Field(FIELD_CHANGELIST_USER, changeList.getUser(), true, true, false));
    result.add(new Field(FIELD_CHANGELIST_NUMBER, changeList.getNumber(), true, true, false));
    if (!StringUtils.isBlank(changeList.getBranch())) {
      result.add(new Field(FIELD_CHANGELIST_BRANCH, changeList.getBranch(), true, true, false));
      result.add(Field.UnStored(FIELD_CONTENT, changeList.getBranch()));
    }


    // add content
    result.add(Field.UnStored(FIELD_CONTENT, changeList.getNumber()));
    result.add(Field.UnStored(FIELD_CONTENT, changeList.getUser()));
    result.add(Field.UnStored(FIELD_CONTENT, changeList.getDescription()));
    for (final Iterator i = changes.iterator(); i.hasNext();) {
      final Change change = (Change)i.next();
      result.add(Field.UnStored(FIELD_CONTENT, change.getChangeTypeAsString()));
      result.add(Field.UnStored(FIELD_CONTENT, change.getFilePath()));
      result.add(Field.UnStored(FIELD_CONTENT, change.getRevision()));
    }

    // return
    return result;
  }


  public static Document makeDocument(final BuildRun buildRun, final StepResult stepResult, final String fileName) {
    final Document result = new Document();
    result.add(makeDocumentTypeField(TYPE_BUILD_RESULT));

    // add header fields
    addBuildRunFieldsToDocument(buildRun, result);

    // add content
    result.add(new Field(FIELD_RESULT_STEP_RESULT_ID, Integer.toString(stepResult.getID()), true, true, false));
    result.add(new Field(FIELD_RESULT_FILE_NAME, fileName, true, true, false));
    result.add(Field.UnStored(FIELD_CONTENT, fileName));

    // return
    return result;
  }


  /**
   * Adds build run fields to the given document.
   */
  private static void addBuildRunFieldsToDocument(final BuildRun buildRun, final Document document) {
    for (final Iterator i = makeBuildRunFields(buildRun).iterator(); i.hasNext();) {
      document.add((Field)i.next());
    }
  }


  private static List makeBuildRunFields(final BuildRun buildRun) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRunConfig buildRunConfig = cm.getBuildRunConfig(buildRun);
    final List result = new ArrayList(3);
    // index fields
    result.add(new Field(FIELD_BUILD_ID, Integer.toString(buildRunConfig.getActiveBuildID()), true, true, false));
    result.add(new Field(FIELD_BUILD_RUN_NUMBER, buildRun.getBuildRunNumberAsString(), true, true, false));
    result.add(new Field(FIELD_BUILD_RUN_ID, buildRun.getBuildRunIDAsString(), true, true, false));
    result.add(new Field(FIELD_BUILD_NAME, buildRun.getBuildName(), true, false, false));
    result.add(Field.Keyword(FIELD_BUILD_STARTED, buildRun.getStartedAt()));
    // NOTE: vimeshev - 10/30/2004 - logs are coming prom steps when a
    // build is not finished yet.
    if (buildRun.getFinishedAt() != null) {
      result.add(Field.Keyword(FIELD_BUILD_FINISHED, buildRun.getFinishedAt()));
    }

    // content fields
    result.add(new Field(FIELD_CONTENT, buildRun.getBuildName(), false, true, false));
    result.add(new Field(FIELD_CONTENT, buildRun.getBuildRunNumberAsString(), false, true, false));
    return result;
  }


  /**
   * Create doc type field.
   *
   * @param type
   *
   * @see #TYPE_SEQUENCE_LOG
   */
  public static Field makeDocumentTypeField(final String type) {
    return new Field(FIELD_DOCUMENT_TYPE, // name
      type, // value
      true, // store
      true, // index
      false  // token
    );
  }
}
