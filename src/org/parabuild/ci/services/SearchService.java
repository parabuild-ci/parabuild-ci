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
package org.parabuild.ci.services;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by simeshev on Oct 14, 2004 at 4:42:54 PM
 */
public interface SearchService extends Service {

  /**
   * Adds a request to perform indexing.
   *
   * @param indexRequest
   */
  void queueIndexRequest(IndexRequest indexRequest);


  /**
   * Performs search.
   *
   * @param queryString
   * @param defaultField
   */
  Hits search(String queryString, String defaultField) throws IOException, ParseException;


  /**
   * Request to perform indexing.
   */
  interface IndexRequest extends Serializable {

    Document getDocumentToIndex();
  }
}
