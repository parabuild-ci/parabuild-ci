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

import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.util.IoUtils;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

/**
 * CheckstyleLogComponent is a custom leaf component responsible for
 * displaying build logs presented as Checkstyle XML.
 *
 * @see AbstractXSLRendererComponent
 * @see ArchiveManager#getArchivedLogInputStream
 */
public final class CheckstyleLogComponent extends AbstractXSLRendererComponent {

  private static final long serialVersionUID = -4256625468445813797L; // NOPMD

  private ArchiveManager archiveManager;
  private StepLog stepLog;


  public CheckstyleLogComponent(final ArchiveManager archiveManager, final StepLog stepLog) {
    this.archiveManager = archiveManager;
    this.stepLog = stepLog;
  }


  /**
   * @return StreamSource for XML to transform.
   *
   * @see AbstractXSLRendererComponent#xmlSource()
   */
  protected StreamSource xmlSource() throws IOException {
    return new StreamSource(archiveManager.getArchivedLogInputStream(stepLog));
  }


  /**
   * @return StreamSource for XSL to use to transform XML
   *
   * @see AbstractXSLRendererComponent#xslSource()
   * @see #xmlSource()
   */
  protected StreamSource xslSource() throws IOException {
    return new StreamSource(new StringReader(IoUtils.getResourceAsString("checkstyle-report.xsl")));
  }
}
