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

import java.io.*;
import org.apache.commons.logging.*;
import org.apache.lucene.document.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.search.*;
import org.parabuild.ci.services.*;


public class TestIndexRequest implements SearchService.IndexRequest {

  private static final Log log = LogFactory.getLog(TestIndexRequest.class);
  private final ConfigurationManager cm = ConfigurationManager.getInstance();


  public Document getDocumentToIndex() {
    Document document = null;
    try {
      if (log.isDebugEnabled()) log.debug("Prepare test document");

      // get params
      final BuildRun buildRun = cm.getBuildRun(1);
      final StepLog stepLog = cm.getStepLog(1);
      final StepRun stepRun = cm.getStepRun(1);
      
      // request doc from factory
      document = LuceneDocumentFactory.makeDocument(buildRun, stepRun,
        stepLog, TestHelper.getTestFile("test_ant_successful_build.log"));
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e.toString());
    }
    if (log.isDebugEnabled()) log.debug("document = " + document.toString());
    return document;
  }
}
