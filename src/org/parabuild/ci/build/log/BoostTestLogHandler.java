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
package org.parabuild.ci.build.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.SimpleFileArchivedLogFinder;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.XMLUtils;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.remote.Agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles directory with UnitTestPp logs
 */
public final class BoostTestLogHandler extends AbstractLogHandler {

  /**
   * This valriable defines
   */
  public static final String ARCHIVE_XML_ROOT = "testlogs";
  private static final Log LOG = LogFactory.getLog(BoostTestLogHandler.class);  // NOPMD


  /**
   * Constructor
   */
  public BoostTestLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                             final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte LOG type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_BOOST_TEST_XML_DIR;
  }


  /**
   * Concrete processing.
   *
   * @throws java.io.IOException
   */
  protected void processLog() throws IOException {
    final List tempFiles = new LinkedList();
    try {

      //
      // Check if it's a directory
      //
      if (LOG.isDebugEnabled()) {
        LOG.debug("Boost Test: check if it's a directory: ");
      }
      if (!super.agent.pathIsDirectory(fullyQualifiedResultPath)) {
        reportLogPathIsNotDirectory();
        return;
      }

      // Get files
      if (LOG.isDebugEnabled()) {
        LOG.debug("Boost Test: get files ");
      }
      final String[] files = agent.listFilesInDirectory(fullyQualifiedResultPath, "XML, xml");
      if (files.length == 0) {
        return;
      }

      //
      // Go over list of files in the Boost Test directory
      //
      if (LOG.isDebugEnabled()) {
        LOG.debug("Boost Test: go over list of files in the JUnit directory ");
      }
      for (int j = 0; j < files.length; j++) {
        final String source = files[j];
        if (agent.pathIsDirectory(source)) {
          continue;
        }
        final File localCopy = IoUtils.createTempFile(".auto", ".xml");
        tempFiles.add(localCopy);
        agent.readFile(source, localCopy);
      }
      if (tempFiles.isEmpty()) {
        return;
      }

      //
      // Merge logs
      //
      if (LOG.isDebugEnabled()) {
        LOG.debug("Boost Test: merge logs ");
      }
      final Document mergedDocument = DocumentHelper.createDocument();
      final Element resultRoot = mergedDocument.addElement(ARCHIVE_XML_ROOT);
      for (final Iterator i = tempFiles.iterator(); i.hasNext();) {
        // get file
        final File file = (File) i.next();
        if (file.isDirectory()) {
          continue;
        }
        // read
        final SAXReader reader = new SAXReader();
        final Document documentToMerge = reader.read(file);
        final Element root = documentToMerge.getRootElement();
        // validate it is Boost Test XML - should have TestLog as root
        if (root.getName().equals("TestLog")) {
          // go over tests and add to result
          resultRoot.add(root.createCopy());
        }
      }

      // Store merged logs in archive file
      // create output file
      if (LOG.isDebugEnabled()) {
        LOG.debug("Boost Test: store merged logs in archive file ");
      }
      final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
      final File archiveFile = archiveManager.fileNameToLogPath(archiveFileName);

      // write
      final OutputFormat format = OutputFormat.createCompactFormat();
      final XMLWriter writer = new XMLWriter(new FileOutputStream(archiveFile), format);
      writer.write(mergedDocument);
      writer.close();

      archiveFile.setLastModified(agent.getFileDescriptor(fullyQualifiedResultPath).lastModified());

      // get and save stats
      if (LOG.isDebugEnabled()) LOG.debug("mergedDocument: " + mergedDocument);
      final int errors = XMLUtils.intValueOf(mergedDocument, "count(/testlogs/TestLog/TestSuite/TestCase/Error)") + XMLUtils.intValueOf(mergedDocument, "count(/testlogs/TestLog/TestSuite/TestCase/Exception)");
      final int successes = XMLUtils.intValueOf(mergedDocument, "count(/testlogs/TestLog/TestSuite/TestCase/Info)");
      final int tests = errors + successes;
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_BOOST_TEST_ERRORS, errors);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_BOOST_TEST_SUCCESSES, successes);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_BOOST_TEST_TESTS, tests);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_ERRORS, errors);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES, successes);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS, tests);
      // save LOG info in the db if necessary
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(stepRunID);
      stepLog.setDescription(logConfig.getDescription());
      stepLog.setPath(resolvedResultPath);
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_BOOST_XML);
      stepLog.setFound((byte) 1);
      cm.save(stepLog);
      // TODELETE: debug
      if (LOG.isDebugEnabled()) {
        LOG.debug("Boost Test: saved stepLog: " + stepLog);
      }
    } catch (Exception e) {
      throw IoUtils.createIOException(StringUtils.toString(e), e);
      // TODELETE: debug
    } catch (OutOfMemoryError e) {
      throw IoUtils.createIOException(StringUtils.toString(e), e);
    } finally {
      IoUtils.deleteFilesHard(tempFiles);
    }
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp
   * @return true if LOG being processed was already archived.
   */
  protected boolean isLogAlreadyArchived(final List archivedLogs, final long builderTimeStamp) throws IOException, AgentFailureException {
    final SimpleFileArchivedLogFinder finder = new SimpleFileArchivedLogFinder(agent, archiveManager);
    finder.setIgnoreTimeStamp(isIgnoreTimeStamp());
    return finder.isAlreadyArchived(archivedLogs, fullyQualifiedResultPath, true, builderTimeStamp);
  }
}
