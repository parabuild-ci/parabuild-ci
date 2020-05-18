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
package org.parabuild.ci.manager.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * AgentCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 24, 2009 11:57:28 AM
 */
public abstract class AgentCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(AgentCommand.class); // NOPMD


  // ----------------------------------------------------- Instance Variables


  // ------------------------------------------------------------- Properties


  /**
   * The login password for the <code>Agent Manager</code> application.
   */
  protected String password;


  /**
   * The URL of the <code>Agent Manager</code> application to be used.
   */
  protected String url;


  public String getPassword() {
    return this.password;
  }


  public void setPassword(final String password) {
    this.password = password;
  }


  public String getUrl() {
    return this.url;
  }


  public void setUrl(final String url) {
    this.url = url;
  }


  /**
   * The login username for the <code>Manager</code> application.
   */
  protected String username;


  public String getUsername() {
    return this.username;
  }


  public void setUsername(final String username) {
    this.username = username;
  }


  // --------------------------------------------------------- Public Methods


  /**
   * Execute the specified command.  This logic only performs the common
   * attribute validation required by all subclasses; it does not perform
   * any functional logic directly.
   *
   * @throws IllegalArgumentException if a validation error occurs
   */
  public void execute() throws IOException {

    if (username == null || password == null || url == null) {
      throw new IllegalArgumentException("Must specify all of 'username', 'password', and 'url'");
    }

  }


  // ------------------------------------------------------ Protected Methods


  /**
   * Execute the specified command, based on the configured properties.
   *
   * @param command Command to be executed
   * @throws IOException if an error occurs
   */
  public void execute(final String command) throws IOException {

    execute(command, null, null, -1);

  }


  /**
   * Execute the specified command, based on the configured properties.
   * The input stream will be closed upon completion of this task, whether
   * it was executed successfully or not.
   *
   * @param command       Command to be executed
   * @param istream       InputStream to include in an HTTP PUT, if any
   * @param contentType   Content type to specify for the input, if any
   * @param contentLength Content length to specify for the input, if any
   * @throws IOException if an error occurs
   */
  public void execute(final String command, final InputStream istream,
                      final String contentType, final int contentLength)
          throws IOException {

    InputStreamReader reader = null;
    try {

      // Create a connection for this command
      final URLConnection conn = new URL(url + "/manager" + command).openConnection();
      final HttpURLConnection httpURLConnection = (HttpURLConnection) conn;

      // Set up standard connection characteristics
      httpURLConnection.setAllowUserInteraction(false);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setUseCaches(false);
      if (istream != null) {
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        if (contentType != null) {
          httpURLConnection.setRequestProperty("Content-Type", contentType);
        }
        if (contentLength >= 0) {
          httpURLConnection.setRequestProperty("Content-Length", String.valueOf(contentLength));
        }
      } else {
        httpURLConnection.setDoOutput(false);
        httpURLConnection.setRequestMethod("GET");
      }
      httpURLConnection.setRequestProperty("User-Agent", "Parabuild-Agent-Command/1.0");

      // Set up an authorization header with our credentials
      final String input = username + ':' + password;
      httpURLConnection.setRequestProperty("Authorization", "Basic " + new String(StringUtils.encode(input.getBytes())));

      // Establish the connection with the server
      httpURLConnection.connect();

      // Send the request data (if any)
      if (istream != null) {
        final BufferedOutputStream ostream = new BufferedOutputStream(httpURLConnection.getOutputStream(), 1024);
        final byte[] buffer = new byte[1024];
        while (true) {
          final int n = istream.read(buffer);
          if (n < 0) {
            break;
          }
          ostream.write(buffer, 0, n);
        }
        ostream.flush();
        IoUtils.closeHard(ostream);
        IoUtils.closeHard(istream);
      }

      // Process the response message
      reader = new InputStreamReader(httpURLConnection.getInputStream());
      final StringBuilder buff = new StringBuilder(1000);
      String error = null;
      boolean first = true;
      while (true) {
        final int ch = reader.read();
        if (ch < 0) {
          break;
        } else if (ch == '\r' || ch == '\n') {
          final String line = buff.toString();
          buff.setLength(0);
          if (LOG.isDebugEnabled()) {
            LOG.debug("line: " + line);
          }
          if (first) {
            if (!line.startsWith("OK -")) {
              error = line;
            }
            first = false;
          }
        } else {
          buff.append((char) ch);
        }
      }
      if (buff.length() > 0) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("buff: " + buff);
        }
      }
      if (error != null) {
        throw new IOException(error);
      }

    } finally {
      IoUtils.closeHard(reader);
      IoUtils.closeHard(istream);
    }
  }


  public String toString() {
    return "AgentCommand{" +
            "password='" + password + '\'' +
            ", url='" + url + '\'' +
            ", username='" + username + '\'' +
            '}';
  }
}
