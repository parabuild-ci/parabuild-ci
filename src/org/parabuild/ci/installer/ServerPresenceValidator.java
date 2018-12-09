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
package org.parabuild.ci.installer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Validates that a server is running
 */
@SuppressWarnings("TryWithIdenticalCatches")
final class ServerPresenceValidator {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ServerPresenceValidator.class); // NOPMD

  private static final String ETC_CONF_SERVER_XML = "etc/conf/server.xml";


  /**
   * True if server is runnig or false if not
   *
   * @param installationDirectory
   */
  public boolean serverIsRunning(final File installationDirectory) throws IOException {
    // open tomcat config file

    try {
      final File serverXML = new File(installationDirectory, ETC_CONF_SERVER_XML);
      if (!serverXML.exists()) {
        throw new IOException("Could not find configuration file at " + serverXML.toString());
      }

      // get port number

      final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      final Document document = documentBuilder.parse(serverXML);
      final NodeList elementsByTagName = document.getElementsByTagName("Server");
      final Node node = elementsByTagName.item(0);
      final NamedNodeMap attributes = node.getAttributes();
      final Node namedItem = attributes.getNamedItem("port");
      final String portNumber = namedItem.getNodeValue();
      if (!StringUtils.isValidInteger(portNumber)) {
        throw new IOException("Server port number is not a valid integer: " + portNumber);
      }

      // try to connect

      final Socket socket = new Socket();
      try {
        socket.connect(new InetSocketAddress("localhost", Integer.parseInt(portNumber)));
      } catch (final IOException e) {
        // could not connect, assume the server is not running
        return false;
      } finally {
        IoUtils.closeHard(socket);
      }

      return true;
    } catch (final RuntimeException e) {
      throw e;
    } catch (final IOException e) {
      throw e;
    } catch (final Exception e) {
      throw IoUtils.createIOException(e);
    }
  }
}
