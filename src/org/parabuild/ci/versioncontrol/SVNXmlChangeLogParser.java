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
package org.parabuild.ci.versioncontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SimpleChange;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * SVNXMLChangeLogParser
 * <p/>
 *
 * @author Slava Imeshev
 * @since Nov 19, 2009 2:04:20 PM
 */
public final class SVNXmlChangeLogParser extends SVNChangeLogParser {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SVNXmlChangeLogParser.class); // NOPMD
  private final SimpleDateFormat simpleDateFormat = createSvnChangeLogDateFormatter();


  public SVNXmlChangeLogParser(final int maxChangeListSize) {
    super(maxChangeListSize);
  }


  public List parseChangeLog(final InputStream is) throws IOException {
    int changeListCounter = 0;
    final List result = new ArrayList(11);
    final SAXReader saxReader = new SAXReader();
    try {
      final Document document = saxReader.read(is);
      final Element root = document.getRootElement();

      // iterate through child elements of root
      for (Iterator i = root.elementIterator(); i.hasNext();) {
        final Element element = (Element) i.next();
        final String elementName = element.getName();
        if (elementName.equals("logentry")) {
          final ChangeList changeList = new ChangeList();
          changeList.setChanges(new HashSet(5));

          // Get revision
          final String revision = element.attributeValue("revision");
          changeList.setNumber(revision);

          // Get author
          final Element authorElement = element.element("author");
          final String author = authorElement == null ? "" : authorElement.getStringValue();
          changeList.setUser(author);

          // Get Date
          final Element dateElement = element.element("date");
          final String stringDate = dateElement == null ? "" : dateElement.getStringValue();
          if ("<no date>".equals(stringDate) || "(no date)".equals(stringDate)) {
            continue;
          }
          final int zIndex = stringDate.indexOf('Z');
          if (zIndex < 0) {
            throw new IOException("Unexpected date format in Subversion log in XML format:" + stringDate);
          }
          final Date date = simpleDateFormat.parse(stringDate.substring(0, zIndex));
          changeList.setCreatedAt(date);

          // Get changes
          final Element pathsElement = element.element("paths");
          final Iterator iterator = pathsElement.elementIterator("path");
          while (iterator.hasNext()) {
            final Element pathElement = (Element) iterator.next();
            final Change change = new Change();
            change.setFilePath(pathElement.getStringValue());
            change.setRevision(revision);
            setChangeType(change, pathElement.attributeValue("action"));
            if (changeList.getOriginalSize() < maxChangeListSize) {
              //if (log.isDebugEnabled()) log.debug("pathLine: " + pathLine);
              changeList.getChanges().add(change);
            } else {
              changeList.setTruncated(true);
            }
            changeList.incrementOriginalSize();
          }

          // Get message
          final Element msgElement = element.element("msg");
          final String message = msgElement == null ? "" : msgElement.getStringValue();
          changeList.setDescription(message);

          // Add
          if (StringUtils.isBlank(ignoreChangeListNumber) || !changeList.getNumber().equals(ignoreChangeListNumber)) {
            if (StringUtils.isBlank(subSubdirectory)) {
              result.add(changeList);
              changeListCounter++;
            } else {
              // Delete any directory changes that have a given directory as a parent
              final Set tempChanges = new HashSet(changeList.getChanges());
              for (final Iterator iter = tempChanges.iterator(); iter.hasNext();) {
                final String filePath = ((SimpleChange) iter.next()).getFilePath();
                if (filePath.startsWith(subSubdirectory) && filePath.indexOf('/', subSubdirectory.length() + 1) >= 0) {
                  iter.remove();
                }
              }
              if (!tempChanges.isEmpty()) {
                result.add(changeList);
                changeListCounter++;
              }
            }
          }
          if (changeListCounter >= maxChangeLists) {
            break;
          }
        }
      }
    } catch (DocumentException e) {
      throw IoUtils.createIOException(e);
    } catch (ParseException e) {
      throw IoUtils.createIOException(e);
    }
    return result;
  }


  public static SimpleDateFormat createSvnChangeLogDateFormatter() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US);
  }
}
