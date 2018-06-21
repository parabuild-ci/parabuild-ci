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
package org.parabuild.ci.common;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Kostya
 */
public final class XMLUtils {

  private XMLUtils() {
  }


  public static String toString(final Document document) throws TransformerException {
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    final StringWriter sw = new StringWriter(300);
    transformer.transform(new DOMSource(document), new StreamResult(sw));
    return sw.toString();
  }


  public static Document createDomDocument() throws ParserConfigurationException {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
  }


  public static void writeDom2File(final Document document, final String filename) throws IOException {
    writeDom2File(document, new File(filename));
  }


  public static void writeDom2File(final Document document, final File file) throws IOException {
    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(new DOMSource(document), new StreamResult(os));
    } catch (final TransformerException e) {
      final IOException ioException = new IOException(StringUtils.toString(e));
      ioException.initCause(e);
      throw ioException;
    } finally {
      IoUtils.closeHard(os);
    }
  }


  public static Document parseDom(final File file, final boolean validating) throws SAXException, ParserConfigurationException, IOException {
    if (!(file.isFile() && file.exists())) throw new IOException("File \"" + file.getCanonicalPath() + "\" does not exist or invalid");
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(validating);
    return factory.newDocumentBuilder().parse(file);
  }


  /**
   * Merges list of XML files presented by fileList into a single
   * XML Document with a given root.
   *
   * @param fileList list of File objects
   * @param rootElementName - name of a root element
   *
   * @return Document
   */
  public static Document merge(final List fileList, final String rootElementName) throws ParserConfigurationException, SAXException, IOException {
    final Document mergedDocument = createDomDocument();
    final Element element = mergedDocument.createElement(rootElementName);
    mergedDocument.appendChild(element);
    for (final Iterator i = fileList.iterator(); i.hasNext();) {
      final File file = (File)i.next();
      if (file.isDirectory()) continue;
      final Document documentToMerge = parseDom(file, false);
      final NodeList list = documentToMerge.getElementsByTagName("*");
      final Element rootElement = (Element)list.item(0);
      final Node duplicate = mergedDocument.importNode(rootElement, true);
      mergedDocument.getDocumentElement().appendChild(duplicate);
    }
    return mergedDocument;
  }


  /**
   * Helper method
   *
   * @param document
   * @param xPath to evaluate to int value
   */
  public static int intValueOf(final Document document, final String xPath) throws JaxenException {
    return new DOMXPath(xPath).numberValueOf(document).intValue();
  }


  public static int intValueOf(final org.dom4j.Document document, final String xPath) throws JaxenException {
    return new Dom4jXPath(xPath).numberValueOf(document).intValue();
  }


  public static Document mergeXMLFiles(final List tempFiles, final String rootElementName) throws ParserConfigurationException, SAXException, IOException {
    final Document mergedDocument = createDomDocument();
    final Element element = mergedDocument.createElement(rootElementName);
    mergedDocument.appendChild(element);
    for (final Iterator i = tempFiles.iterator(); i.hasNext();) {
      final File file = (File)i.next();
      if (file.isDirectory()) continue;
      final Document documentToMerge = parseDom(file, false);
      // NOTE: vimeshev - cleanup to reduce XML size.
      final NodeList elementsByTagName = documentToMerge.getElementsByTagName("properties");
      final int length = elementsByTagName.getLength();
      final Element root = documentToMerge.getDocumentElement();
      for (int j = 0; j < length; j++) {
        final Node node = elementsByTagName.item(j);
        root.removeChild(node);
      }
      // merge
      final NodeList list = documentToMerge.getElementsByTagName("*");
      final Element rootElement = (Element)list.item(0);
      final Node duplicate = mergedDocument.importNode(rootElement, true);
      mergedDocument.getDocumentElement().appendChild(duplicate);
    }
    return mergedDocument;
  }
}
