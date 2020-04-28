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

import org.apache.lucene.search.Hits;
import org.parabuild.ci.search.SearchHitsTraverser;
import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;

public final class SearchResultComponent extends CustomComponent {

  private static final long serialVersionUID = -57748702723289399L;
  private Hits hits = null;


  public SearchResultComponent(final Hits hits) {
    this.hits = hits;
  }


  /**
   * Oveloaded CustomComponent's render
   */
  public void render(final RenderContext ctx) {
    final WritingHitsTraverserCallback callback = new WritingHitsTraverserCallback(ctx.getWriter());
    final SearchHitsTraverser hitsTraverser = new SearchHitsTraverser();
    hitsTraverser.traverse(hits, callback);
  }
}
