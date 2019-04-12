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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

/**
 * Handles directory with CppUnit logs
 */
public final class CppUnitLogHandler extends AbstractLogHandler {

  public static final String ARCHIVE_XML_ROOT = "ParabuildCppUnitTests";
  private static final Log log = LogFactory.getLog(CppUnitLogHandler.class);


  /**
   * Constructor
   */
  public CppUnitLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                           final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_CPPUNIT_XML_DIR;
  }


  /**
   * Concrete processing.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException {
    final List tempFiles = new LinkedList();
    try {

      // check if it's a directory
      // TODELETE: debug
      if (log.isDebugEnabled()) {
        log.debug("CPPULH: check if it's a directory: ");
      }
      if (!super.agent.pathIsDirectory(fullyQualifiedResultPath)) {
        reportLogPathIsNotDirectory();
        return;
      }

      // get files
      // TODELETE: debug
      if (log.isDebugEnabled()) {
        log.debug("CPPULH: get files ");
      }
      final String[] files = agent.listFilesInDirectory(fullyQualifiedResultPath, "XML, xml");
      if (files.length == 0) {
        return;
      }

      // go over list of files in the CppUnit directory
      // TODELETE: debug
      if (log.isDebugEnabled()) {
        log.debug("CPPULH: go over list of files in the CppUnit directory ");
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

      // merge logs
      // REVIEWME: switch to dom4j/jaxen-dom4j instead of jaxen-dom
      // for better performance:
      //   http://jaxen.sourceforge.net
      //   http://dom4j.org/benchmarks/xpath/index.html
      // TODELETE: debug
      if (log.isDebugEnabled()) {
        log.debug("CPPULH: merge logs ");
      }
      final Document mergedDocument = mergeCppUnitLogs(tempFiles);

      // store merged logs in archive file
      // TODELETE: debug
      if (log.isDebugEnabled()) {
        log.debug("CPPULH: store merged logs in archive file ");
      }
      final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
      final File archiveFile = archiveManager.fileNameToLogPath(archiveFileName);
      XMLUtils.writeDom2File(mergedDocument, archiveFile);
      archiveFile.setLastModified(agent.getFileDescriptor(fullyQualifiedResultPath).lastModified());

      // get and save stats
      // TODELETE: debug
      final int failures = XMLUtils.intValueOf(mergedDocument, "count(ParabuildCppUnitTests/TestRun/FailedTests/FailedTest)");
      final int successes = XMLUtils.intValueOf(mergedDocument, "count(ParabuildCppUnitTests/TestRun/SuccessfulTests/Test)");
      final int tests = successes + failures;
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_CPPUNIT_FAILURES, failures);
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_CPPUNIT_SUCCESSES, successes);
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_CPPUNIT_TESTS, tests);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_FAILURES, failures);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES, successes);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS, tests);
      // save log info in the db if necessary
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(resolvedResultPath);
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_CPPUNIT_XML);
      stepLog.setFound((byte) 1);
      cm.save(stepLog);
      // TODELETE: debug
      if (log.isDebugEnabled()) {
        log.debug("CPPULH: saved stepLog: " + stepLog);
      }
    } catch (final Exception | OutOfMemoryError e) {
      throw IoUtils.createIOException(StringUtils.toString(e), e);
    } finally {
      IoUtils.deleteFilesHard(tempFiles);
    }
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp
   * @return true if log being processed was already archived.
   */
  protected boolean isLogAlreadyArchived(final List archivedLogs, final long builderTimeStamp) throws IOException, AgentFailureException {
    final SimpleFileArchivedLogFinder finder = new SimpleFileArchivedLogFinder(agent, archiveManager);
    finder.setIgnoreTimeStamp(isIgnoreTimeStamp());
    return finder.isAlreadyArchived(archivedLogs, fullyQualifiedResultPath, true, builderTimeStamp);
  }


  private static Document mergeCppUnitLogs(final List tempFiles) throws ParserConfigurationException, SAXException, IOException {
    final Document mergedDocument = XMLUtils.createDomDocument();
    final Element element = mergedDocument.createElement(ARCHIVE_XML_ROOT);
    mergedDocument.appendChild(element);
    for (final Iterator i = tempFiles.iterator(); i.hasNext();) {
      final File file = (File) i.next();
      if (file.isDirectory()) {
        continue;
      }
      final Document documentToMerge = XMLUtils.parseDom(file, false);
      final Element root = documentToMerge.getDocumentElement();
      final String nodeName = root.getNodeName();
      if (log.isDebugEnabled()) {
        log.debug("nodeName: " + nodeName);
      }

      // merge
      final NodeList list = documentToMerge.getElementsByTagName("*");
      final Element rootElement = (Element) list.item(0);
      final Node duplicate = mergedDocument.importNode(rootElement, true);
      mergedDocument.getDocumentElement().appendChild(duplicate);
    }
    return mergedDocument;
  }
}
