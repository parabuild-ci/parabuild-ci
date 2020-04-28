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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AccurevShowWspacesLogParser
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 13, 2009 2:23:38 PM
 */
final class AccurevShowWspacesLogParser {


  List parseLog(final File logFile) throws MalformedURLException, DocumentException {
    final List result = new ArrayList(11);
    final SAXReader saxReader = new SAXReader();
    final Document document = saxReader.read(logFile);
    final Element rootElement = document.getRootElement();

    final Iterator iter = rootElement.elementIterator();
    while (iter.hasNext()) {
      final Element element = (Element) iter.next();
      if (!"Element".equals(element.getName())) {
        continue;
      }
      final String name = element.attributeValue("Name");
      final String storage = element.attributeValue("Storage");
      final String depot = element.attributeValue("depot");
      final String host = element.attributeValue("Host");
      final AccurevWorkspace workspace = new AccurevWorkspace(name, storage, depot, host);
      result.add(workspace);
    }
    return result;
  }


  public String toString() {
    return "AccurevShowWspacesLogParser{}";
  }
}
