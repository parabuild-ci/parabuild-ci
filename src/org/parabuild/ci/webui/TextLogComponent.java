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
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.util.IoUtils;
import viewtier.cdk.CustomComponent;
import viewtier.cdk.RenderContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * TextLogComponent is a custom leaf component responsible for
 * displaying Text build logs.
 */
public final class TextLogComponent extends CustomComponent {

  private static final long serialVersionUID = -4256625468445813797L; // NOPMD
  private static final Log log = LogFactory.getLog(TextLogComponent.class);

  private String fileNameToShow;

  private final ArchiveManager archiveManager;
  private final StepLog stepLog;
  private final TextLogLineRenderer textLogLineRenderer;


  /**
   * Constructor.
   *
   * @param archiveManager
   * @param stepLog
   */
  public TextLogComponent(final ArchiveManager archiveManager, final StepLog stepLog,
                          final TextLogLineRenderer textLogLineRenderer) {
    this.archiveManager = archiveManager;
    this.stepLog = stepLog;
    this.textLogLineRenderer = textLogLineRenderer;
  }


  /**
   * Constructor.
   *
   * @param archiveManager
   * @param stepLog
   */
  public TextLogComponent(final ArchiveManager archiveManager, final StepLog stepLog, final String fileNameToShow,
                          final TextLogLineRenderer textLogLineRenderer) {
    this.archiveManager = archiveManager;
    this.stepLog = stepLog;
    this.fileNameToShow = fileNameToShow;
    this.textLogLineRenderer = textLogLineRenderer;
  }


  /**
   * Oveloaded CustomComponent's render
   */
  public void render(final RenderContext ctx) {

    BufferedReader br = null;
    try {
      // get is
      final InputStream is = getArchivedLogInputStream();
      if (is == null) {
        showLogDoesNotExistMsg(ctx);
        return;
      }

      // log exists
      final PrintWriter pw = ctx.getWriter();
      pw.println();
//      pw.println("<pre style=\"font-family : \"Courier New\", Courier, monospace; font-size: 10px; color: #000000; white-space: pre;\">");
      pw.println("<pre style=\"font-family : \"Courier New\", Courier, monospace; font-size: 10px; color: #000000;\">");
      br = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = br.readLine()) != null) {
        pw.println(textLogLineRenderer.render(line));
      }
      pw.println();
      pw.println("</pre>");
    } catch (final Exception e) {
      showUnexpectedErrorMsg(ctx, e);
    } finally {
      IoUtils.closeHard(br);
    }
  }


  private InputStream getArchivedLogInputStream() throws IOException {
    if (fileNameToShow == null) {
      return archiveManager.getArchivedLogInputStream(stepLog);
    } else {
      return archiveManager.getArchivedLogInputStream(stepLog, fileNameToShow);
    }
  }


  private static void showLogDoesNotExistMsg(final RenderContext ctx) {
    ctx.getWriter().println("Requested log not found");
  }


  private static void showUnexpectedErrorMsg(final RenderContext ctx, final Exception e) {
    try {
      final PrintWriter pw = ctx.getWriter();
      pw.println("There was an unexpected error while retrieving log");
      log.error("Error getting log", e);
    } catch (final Exception e1) {
      if (log.isWarnEnabled()) {
        log.warn("Ignored exception, no where to go from here", e1);
      }
    }
  }


  public String toString() {
    return "TextLogComponent{" +
            "fileNameToShow='" + fileNameToShow + '\'' +
            ", archiveManager=" + archiveManager +
            ", stepLog=" + stepLog +
            ", textLogLineRenderer=" + textLogLineRenderer +
            '}';
  }
}
