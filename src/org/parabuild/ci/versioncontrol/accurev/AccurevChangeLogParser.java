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
package org.parabuild.ci.versioncontrol.accurev;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Accurev change log parser.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 8:52:08 PM
 */
final class AccurevChangeLogParser {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AccurevChangeLogParser.class); // NOPMD
  private int maxChangeListSize;
  private int maxChangeLists = 1;


  AccurevChangeLogParser(final int maxChangeListSize) {
    this.maxChangeListSize = maxChangeListSize;
  }


  public void setMaxChangeLists(final int maxChangeLists) {
    this.maxChangeLists = maxChangeLists;
  }


  public List parseChangeLog(final File logFile) throws MalformedURLException, DocumentException {
    final List result = new ArrayList(11);
    final SAXReader saxReader = new SAXReader();
    final Document document = saxReader.read(logFile);
    final Element rootElement = document.getRootElement();
//    if (LOG.isDebugEnabled()) {
//      LOG.debug("rootElement: " + rootElement);
//    }
    // Iterate transactions
    int changeListCounter = 0;
    final Iterator iter = rootElement.elementIterator();
    while (iter.hasNext() && changeListCounter++ < maxChangeLists) {
      final Element transactionElement = (Element) iter.next();
      if (!transactionElement.getName().equals("transaction")) {
        continue;
      }
//      if (LOG.isDebugEnabled()) {
//        LOG.debug("transactionElement: " + transactionElement);
//      }
      // Create change list
      final String type = transactionElement.attributeValue("type");
      final byte changeType = toChangeType(type);
      if (changeType == Change.TYPE_UNKNOWN) {
        continue;
      }
      final String id = transactionElement.attributeValue("id");
      final String time = transactionElement.attributeValue("time");
      final String user = transactionElement.attributeValue("user");
      final Element elementComment = transactionElement.element("comment");
      final String comment = elementComment.getText();
      final ChangeList changeList = new ChangeList();
      changeList.setCreatedAt(new Date(Long.parseLong(time) * 1000L));
      changeList.setDescription(comment);
      changeList.setNumber(id);
      changeList.setUser(user);
      // Iterate versions
      int changeCounter = 0;
      final Set changes = new HashSet(11);
      final Iterator versionIter = transactionElement.elementIterator("version");
      while (versionIter.hasNext() && changeCounter++ < maxChangeListSize) {
        final Element versionElement = (Element) versionIter.next();
        final String path = versionElement.attributeValue("path");
        final String version = versionElement.attributeValue("virtualNamedVersion");
        final Change change = new Change(path, version, changeType);
        changes.add(change);
      }
      changeList.setChanges(changes);
      // Add to result
      result.add(changeList);
    }
    return result;
  }


  private byte toChangeType(final String type) {
    if (type.equals("add")) {
      return Change.TYPE_ADDED;
    }
    if (type.equals("keep")) {
      return Change.TYPE_KEEP;
    }
    if (type.equals("promote")) {
      return Change.TYPE_PROMOTE;
    }
    if (type.equals("defunct")) {
      return Change.TYPE_DEFUNCT;
    }
    return Change.TYPE_UNKNOWN;
  }


  public String toString() {
    return "AccurevChangeLogParser{" +
            "maxChangeListSize=" + maxChangeListSize +
            ", maxChangeLists=" + maxChangeLists +
            '}';
  }
}
