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

import org.apache.commons.logging.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;

import org.parabuild.ci.util.*;

/**
 * Traverses hits.
 */
public final class SearchHitsTraverser {

  private static final Log log = LogFactory.getLog(SearchHitsTraverser.class);


  public void traverse(final Hits hits, final HitsTraverserCallback callback) {
    final int hitsCount = hits.length();
    for (int i = 0; i < hitsCount; i++) {
      try {
        // get document
        final Document document = hits.doc(i);
        final String resultType = document.get(LuceneDocumentFactory.FIELD_DOCUMENT_TYPE);
        if (resultType == null) continue; // NOTE: somehow we don't have doc type.

        // go through each type
        if (resultType.equals(LuceneDocumentFactory.TYPE_SEQUENCE_LOG)) {
          callback.foundStepLog(document);
        } else if (resultType.equals(LuceneDocumentFactory.TYPE_CHANGE_LIST)) {
          callback.foundChangeList(document);
        } else if (resultType.equals(LuceneDocumentFactory.TYPE_BUILD_RESULT)) {
          callback.foundResult(document);
        }

        // other types
      } catch (final Exception e) {
        // ignore any exception in a single item
        log.warn("Exception while traversing hits", e);
        IoUtils.ignoreExpectedException(e);
      }
    }
  }
}