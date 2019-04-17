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

import org.parabuild.ci.object.SimpleChange;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;

import java.util.List;
import java.util.StringTokenizer;

/**
 * This table shows content of a change list
 */
public final class ChangelistContentTable extends AbstractFlatTable {

  private static final long serialVersionUID = -2705811976363258166L; // NOPMD

  private static final Color COLOR_EVEN_ROW = new Color(0xF1F1F1);
  private static final Color COLOR_ODD_ROW = Color.White;

  public static final int COLUMN_COUNT = 3;

  public static final int COL_ACTION = 0;
  public static final int COL_FILE = 1;
  public static final int COL_REVISION = 2;

  private List changes = null;
  private final ChangeURLFactory changeURLFactory;
  private final boolean showChangeListFiles;


  /**
   * @param changeURLFactory if not null the table will try to
   *  generate URLs for the file name and for the file version
   *  using this {@link ChangeURLFactory} factory. If null, file
   * @param showChangeListFiles
   */
  public ChangelistContentTable(final ChangeURLFactory changeURLFactory, final boolean showChangeListFiles) {
    super(COLUMN_COUNT, false);
    this.changeURLFactory = changeURLFactory;
    this.showChangeListFiles = showChangeListFiles;
    setWidth("100%");
    setHeaderVisible(false);
    setOddRowBackground(COLOR_ODD_ROW);
    setEvenRowBackground(COLOR_EVEN_ROW);
  }


  /**
   * Pupulates table with List of SimpleChange objects
   *
   * @param changes List of SimpleChange objects
   *
   * @see SimpleChange
   */
  public void populate(final List changes) {
    this.changes = changes;
    populate();
  }


  /**
   * This implementation of this abstract method is called when
   * the table wants to fetch a row with a given rowIndex.
   * Implementing method should fill the data corresponding the
   * given rowIndex.
   *
   * @return this method should return either TBL_ROW_FETCHED or
   *         TBL_NO_MORE_ROWS if the requested row is out of
   *         range.
   *
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= changes.size()) return TBL_NO_MORE_ROWS;
    final Component[] row = getRow(rowIndex);
    final SimpleChange change = (SimpleChange)changes.get(rowIndex);
    ((Label)row[COL_ACTION]).setText(change.getChangeTypeAsString());
    if (changeURLFactory == null) {
      final String filePath = change.getFilePath();
      final String wrappableFilePath = makeWrapable(filePath);
      ((DetailFlow)row[COL_FILE]).setText(showChangeListFiles ? wrappableFilePath : "");
      ((DetailFlow)row[COL_REVISION]).setText(change.getRevision());
    } else {
      ((DetailFlow)row[COL_FILE]).setURL(changeURLFactory.makeChangeFileURL(change), true);
      ((DetailFlow)row[COL_REVISION]).setURL(changeURLFactory.makeChangeRevisionURL(change), false);
    }
    return TBL_ROW_FETCHED;
  }


  private static String makeWrapable(final String filePath) {
    if (1 == 1) return filePath; //FIXME
    final StringBuilder sb = new StringBuilder(300);
    final StringTokenizer st = new StringTokenizer(filePath, "/", true);
    while (st.hasMoreTokens()) {
      final String s = st.nextToken();
      sb.append(s);
      if ("/".equals(s)) {
        sb.append("<wbr/>");
      }
    }
    return sb.toString();
  }


  /**
   * Makes row, should be implemented by successor class
   *
   * @return Component[] representing table row.
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] row = new Component[COLUMN_COUNT];
    row[COL_ACTION] = new DetailLabel();
    row[COL_FILE] = new DetailFlow();
    row[COL_REVISION] = new DetailFlow(Layout.CENTER);
    return row;
  }


  /**
   * @return Component[] representing table header.
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_COUNT];
    header[COL_ACTION] = new TableHeaderLabel("Change", "10%");
    header[COL_FILE] = new TableHeaderLabel("File", "80%");
    header[COL_REVISION] = new TableHeaderLabel("Revision", "10%", Layout.CENTER);
    return header;
  }


  /**
   * Detail label padding is set to 2.
   */
  private static final class DetailLabel extends Label {

    private static final long serialVersionUID = -1986259029237192181L;


    public DetailLabel() {
      setPadding(2);
    }
  }

  private static final class DetailFlow extends Flow {


    private static final long serialVersionUID = 3843198256913517506L;


    public DetailFlow() {
      setPadding(2);
    }


    public DetailFlow(final int alignX) {
      setPadding(2);
      setAlignX(alignX);
    }


    public final void setText(final String text) {
      super.add(new Label(text));
    }


    public final void setURL(final ChangeURL changeURL, final boolean wrap) {
      if (wrap) {
        super.add(new CommonLink(makeWrapable(changeURL.getCaption()), changeURL.getURL()));
      } else {
        super.add(new CommonLink(changeURL.getCaption(), changeURL.getURL()));
      }
    }
  }
}