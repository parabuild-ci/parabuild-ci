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

import com.dautelle.xml.sax.Attributes;
import com.dautelle.xml.sax.ContentHandler;
import com.dautelle.xml.sax.RealtimeParser;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Repsonsible for parsing Vault XML output.
 */
public final class VaultOutputParser {

  /**
   * Parses Vault's output.
   *
   * @param file
   *
   * @return
   *
   * @throws IOException
   */
  public Vault parse(final File file) throws IOException {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return parse(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Parses Vault's output.
   *
   * @param is
   *
   * @return
   *
   * @throws IOException
   */
  public Vault parse(final InputStream is) throws IOException {
    try {
      // parse
      final RealtimeParser realtimeParser = new RealtimeParser();
      final VaultOutputContentHandler vaultOutputContentHandler = new VaultOutputContentHandler();
      realtimeParser.setContentHandler(vaultOutputContentHandler);
      realtimeParser.parse(new InputSource(is));
      final Vault parsed = vaultOutputContentHandler.getParsed();

      // analise result
      final Vault.Result rc = parsed.getResult();
      if (rc == null) throw new IOException("Cannot identify Vault result");
      if (!rc.isSuccess()) {
        final Vault.Error error = parsed.getError();
        if (error == null || StringUtils.isBlank(error.getMessage())) {
          throw new IOException("Unknown Vault error");
        } else {
          throw new IOException("Vault error: " + error.getMessage());
        }
      }

      // return result
      return parsed;
    } catch (final SAXException e) {
      throw IoUtils.createIOException(e);
    }
  }


  private static final class VaultOutputContentHandler implements ContentHandler {

    //private static final Log log = LogFactory.getLog(VaultOutputContentHandler.class);

    private final Vault parsed = new Vault();
    private StringBuffer currentCharacters = null;


    public Vault getParsed() {
      return parsed;
    }


    public void setDocumentLocator(final Locator locator) {
    }


    public void startDocument() {
    }


    public void endDocument() {
    }


    public void startPrefixMapping(final CharSequence charSequence, final CharSequence charSequence1) {
    }


    public void endPrefixMapping(final CharSequence charSequence) {
    }


    public void startElement(final CharSequence uri, final CharSequence localName, final CharSequence qName, final Attributes attributes) {
      //if (log.isDebugEnabled()) log.debug("localName = " + localName);
      //if (log.isDebugEnabled()) log.debug("qName = \"" + qName + "\"");
      final String qNameString = qName.toString();
      if ("history".equals(qNameString)) {
        parsed.setHistory(new Vault.History());
      } else if ("item".equals(qNameString)) {
        final Vault.History history = parsed.getHistory();
        if (history == null) return;
        final Vault.Item item = new Vault.Item();
        final CharSequence actionString = attributes.getValue("actionString");
        final CharSequence comment = attributes.getValue("comment");
        final CharSequence date = attributes.getValue("date");
        final CharSequence name = attributes.getValue("name");
        final CharSequence txid = attributes.getValue("txid");
        final CharSequence type = attributes.getValue("type");
        final CharSequence user = attributes.getValue("user");
        final CharSequence version = attributes.getValue("version");
        if (actionString != null) item.setActionString(actionString.toString());
        if (comment != null) item.setComment(comment.toString());
        if (date != null) item.setDate(date.toString());
        if (name != null) item.setName(name.toString());
        if (txid != null) item.setTxid(Integer.parseInt(txid.toString()));
        if (type != null) item.setType(Integer.parseInt(type.toString()));
        if (user != null) item.setUser(user.toString());
        if (version != null) item.setVersion(Integer.parseInt(version.toString()));
        history.addItem(item);
      } else if ("result".equals(qNameString)) {
        final Vault.Result result = new Vault.Result();
        result.setSuccess(attributes.getValue("success").toString());
        parsed.setResult(result);
      } else if ("error".equals(qNameString)) {
        final Vault.Error error = new Vault.Error();
        parsed.setError(error);
        currentCharacters = new StringBuffer(200);
      }
    }


    public void endElement(final CharSequence uri, final CharSequence localName, final CharSequence qName) {
      if ("error".equals(qName.toString())) {
        final Vault.Error error = parsed.getError();
        error.setMessage(StringUtils.truncate(currentCharacters.toString().trim(), 1023));
        currentCharacters = null;
      }
    }


    public void characters(final char[] chars, final int i, final int i1) {
      if (currentCharacters != null) {
        currentCharacters.append(chars, i, i1);
      }
    }


    public void ignorableWhitespace(final char[] chars, final int i, final int i1) {
    }


    public void processingInstruction(final CharSequence charSequence, final CharSequence charSequence1) {
    }


    public void skippedEntity(final CharSequence charSequence) {
    }
  }
}
