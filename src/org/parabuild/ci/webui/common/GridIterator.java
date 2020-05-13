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
package org.parabuild.ci.webui.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.ArgumentValidator;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;

import java.io.Serializable;


/**
 * Grid iterator allows inserting components into
 * Panel in a grid maner
 */
public final class GridIterator implements Serializable {

  private static final long serialVersionUID = 2389900943222565037L; // NOPMD

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(GridIterator.class); // NOPMD
  private final Panel container;
  private final int sizeX;
  private Layout cumulativeLayout = makeInitialLayout();


  /**
   * Creates grid iterator
   *
   * @param container
   * @param xSize
   */
  public GridIterator(final Panel container, final int xSize) {
    ArgumentValidator.validateArgumentGTZero(xSize, "horizontal size of the grid");
    ArgumentValidator.validateArgumentNotNull(container, "container");
    this.container = container;
    this.sizeX = xSize;
  }


  /**
   * Adds a component
   *
   * @param component to add
   * @return reference to this grid iterator
   */
  public GridIterator add(final Component component) {
    container.add(component, cumulativeLayout);
    if (cumulativeLayout.positionX == sizeX - 1) {
      moveToNextLine();
    } else {
      cumulativeLayout.positionX++;
    }
    return this;
  }


  /**
   * Adds a component
   *
   * @param component to add
   * @param width     - horizontal component width
   * @return reference to this grid iterator
   */
  public GridIterator add(final Component component, final int width) {
    // set width
    cumulativeLayout.spanX = width;
    // add
    container.add(component, cumulativeLayout);
    // restore default width
    cumulativeLayout.spanX = 1;
    cumulativeLayout.positionX += width;
    if (cumulativeLayout.positionX == sizeX) {
      moveToNextLine();
    }
    return this;
  }


  /**
   * Adds a pair of component to the handled grid.
   * This method is useful when adding to two-column grids.
   * <p/>
   * This method can be used with two column grids.
   *
   * @param comp  - first component
   * @param comp2 - second component
   * @throws IllegalStateException if number of grid columns is not equal 2
   */
  public void addPair(final Component comp, final Component comp2) throws IllegalStateException {
    // REVIEWME: simeshev@parabuilci.org - this contidition will handle only cases for GI sizes 2 and 4
    if (this.sizeX != 2 && this.sizeX != 4 && this.sizeX != 6)
      throw new IllegalArgumentException("Pair can not be added because grid size is not equal 2");
    add(comp).add(comp2);
  }


  /**
   * Moves grid iterator cursor to the next line
   */
  public void moveToNextLine() {
    cumulativeLayout.positionX = 0;
    cumulativeLayout.positionY++;
  }


  /**
   * Adds blank line
   */
  public void addBlankLine() {
    moveToNextLine();
    final Label blank = new Label();
    blank.setHeight(10);
    add(blank);
    moveToNextLine();
  }


  /**
   * @return Current Layout that will be used to add next component
   */
  public Layout getCumulativeLayout() {
    return (Layout) cumulativeLayout.clone();
  }


  /**
   * Resets iterator
   */
  public void reset() {
    cumulativeLayout = makeInitialLayout();
  }


  private static Layout makeInitialLayout() {
    return new Layout(0, 0, 1, 1);
  }


  /**
   * @return number of cells on X axis.
   */
  public int sizeX() {
    return sizeX;
  }

  public String toString() {
    return "GridIterator{" +
            "container=" + container +
            ", sizeX=" + sizeX +
            ", cumulativeLayout=" + cumulativeLayout +
            '}';
  }
}
