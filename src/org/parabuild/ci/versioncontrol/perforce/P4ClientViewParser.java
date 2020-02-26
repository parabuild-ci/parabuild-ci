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
package org.parabuild.ci.versioncontrol.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class presents an Parabuild P4 client view. Client view
 * is stored in the  database as a String - this class validates
 * and parses it.
 */
public final class P4ClientViewParser implements CommonConstants {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(P4ClientViewParser.class); // NOPMD

  private static final String DOUBLE_SLASH = "//";

  private final boolean replaceClientName;
  private final int clientPrefixLength;
  private final String clientPrefix;
  private final String clientName;
  private boolean validateClientPath = true;


  public P4ClientViewParser() {
    this(false);
  }


  public P4ClientViewParser(final boolean replaceClientName) {
    this(replaceClientName, "parabuild");
  }


  public P4ClientViewParser(final boolean replaceClientName, final String clientName) {
    this.replaceClientName = replaceClientName;
    this.clientName = clientName;
    this.clientPrefix = DOUBLE_SLASH + clientName;
    this.clientPrefixLength = clientPrefix.length();
  }


  /**
   * Parses String client view and return a set of
   * P4ClientViews.
   *
   * @param relativeBuildDir  String optional relative build dir.
   * @param clientViewToParse String client view to parse
   * @return P4ClientView
   * @throws ValidationException if clientViewToParse is not
   *                             valid
   */
  public P4ClientView parse(final String relativeBuildDir, final String clientViewToParse) throws ValidationException {

    P4ClientViewLine firstLine = null;
    // set advanced mode
    final boolean simpleMode = StringUtils.isBlank(relativeBuildDir);
    // resulting lines
    final Set alreadyProcessed = new HashSet(11);
    final List resultLines = new ArrayList(11);
    // break the spec to string lines
    final List lines = StringUtils.multilineStringToList(clientViewToParse);
//    if (log.isDebugEnabled()) log.debug("lines: " + lines);
    // go through each line
    final Pattern pattern = Pattern.compile("(?:^|\\s)(?:-//|\\+//|//)", Pattern.CASE_INSENSITIVE);
    for (final Iterator i = lines.iterator(); i.hasNext();) {
      final String line = (String) i.next();
      //if (log.isDebugEnabled()) log.debug("line: " + line);
      // parse line
      if (!StringUtils.isBlank(line)) {
        final String s = line.trim().replace('\\', '/');

        // tokenize and do basic validation
        final List paths = new ArrayList(3);
        int prevStart = -1;
        int start = -1;
        final Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
          start = matcher.start();
          //if (log.isDebugEnabled()) log.debug("start: " + start);
          if (prevStart >= 0) {
            paths.add(s.substring(prevStart, start).trim());
          }
          prevStart = start;
        }
        if (prevStart >= 0) {
          paths.add(s.substring(prevStart, s.length()).trim());
        }

//        for (int j = 0; j < paths.size(); j++) {
//          final String token = (String)paths.get(j);
//          if (StringUtils.isBlank(token)) continue;
//          paths.add(DOUBLE_SLASH + token.trim());
//        }

        if (paths.isEmpty() || paths.size() > 2) {
          throw new ValidationException("Line \"" + s + "\" is not a correct Parabuild client view.");
        }

        // get iter
        final Iterator pathInter = paths.iterator();

        // depot part
        final String depotPath = (String) pathInter.next();
        if (!depotPath.startsWith(DOUBLE_SLASH) && !depotPath.startsWith("-//") && !depotPath.startsWith("+//")) {
          throw new ValidationException("The depot path \"" + depotPath + "\" is incorrect. It should start with \"//\", \"-//\" or \"+//\"");
        }

        // invalidate case //depot/...
        final int depotPathFistSlash = depotPath.charAt(0) == '-' || depotPath.charAt(0) == '+' ? depotPath.indexOf('/', 3) : depotPath.indexOf('/', 2);
        if (depotPath.indexOf('/', depotPathFistSlash + 1) == -1) {
          throw new ValidationException("The depot path \"" + depotPath + "\" is too general, please use a more specific path.");
        }

        // ends with dots?
        if (simpleMode && !depotPath.endsWith(STR_TRIPPLE_DOTS)) {
          throw new ValidationException("The depot path \"" + depotPath + "\" is incorrect. It should end with \"/...\"");
        }

        // client path
        String clientPath = null;
        if (pathInter.hasNext()) {
          clientPath = (String) pathInter.next();
        } else {
          // make up a client path from depot path
          // NOTE: vimeshev - if depot path looks like //depot/my/source/line/...
          // then the client path will be //parabuild/my/source/line.
          clientPath = clientPrefix + depotPath.substring(depotPathFistSlash);
        }

        if (clientPath.startsWith(DOUBLE_SLASH)) {
          if (!clientPath.startsWith(clientPrefix + '/')) {
            if (replaceClientName) {
//              if (log.isDebugEnabled()) log.debug("clientPath: " + clientPath);
              clientPath = clientPrefix + '/' + clientPath.substring(clientPath.indexOf('/', 3) + 1);
            } else if (validateClientPath) {
              throw makeInvalidClientPathException(clientPath);
            }
          }
        } else {
          throw makeInvalidClientPathException(clientPath);
        }

        if (simpleMode && !clientPath.endsWith(STR_TRIPPLE_DOTS)) {
          throw new ValidationException("The client path \"" + clientPath + "\" is incorrect. It should end with \"/...\"");
        }

        // set first line
        final P4ClientViewLine viewLine = new P4ClientViewLine(depotPath, clientPath);
        if (firstLine == null) {
          firstLine = viewLine;
        }

        // add to result
        if (!alreadyProcessed.contains(viewLine)) {
          resultLines.add(viewLine);
          alreadyProcessed.add(viewLine);
//        if (log.isDebugEnabled()) log.debug("added viewLine: " + viewLine);
        }
      }
    }

    // validate is not blank
    if (resultLines.isEmpty()) {
      throw new ValidationException("Depot view cannot be empty");
    }

    // validate a single line is not an exclusion
    if (resultLines.size() == 1 && (firstLine.getDepotSide().startsWith("-") || firstLine.getDepotSide().startsWith("+"))) {
      throw new ValidationException("Depot view cannot consist from exclusion or inclusion only");
    }

    // indentify relative build dir if necessary
    final String resultRelativeBuildDir;
    if (simpleMode) {
      // validate
      if (firstLine.getDepotSide().startsWith("-") || firstLine.getDepotSide().startsWith("+")) {
        throw new ValidationException("First line of the depot view cannot be an exclusion or inclusion");
      }
      // compute relative build dir
      final int relPathStart = clientPrefixLength + 1;
      final int relPathEnd = firstLine.getClientSide().lastIndexOf(STR_TRIPPLE_DOTS);
      resultRelativeBuildDir = relPathStart <= relPathEnd ? firstLine.getClientSide().substring(relPathStart, relPathEnd) : "";
    } else {
      // if the mode is not "simple", we just use/trust the provided path
      resultRelativeBuildDir = relativeBuildDir;
      // validation the path is relative?
      final File relativeCheck = new File(resultRelativeBuildDir);
      if (relativeCheck.isAbsolute()) {
        throw new ValidationException("Build directory is not a relative directory");
      }
    }

    // create result
//    if (log.isDebugEnabled()) log.debug("resultLines: " + resultLines);
//    if (log.isDebugEnabled()) log.debug("resultClientView: " + resultClientView);
    return new P4ClientView(resultRelativeBuildDir, resultLines);
  }


  public String getClientPrefix() {
    return clientPrefix;
  }


  public int getClientPrefixLength() {
    return clientPrefixLength;
  }


  /**
   * If set, paraser will validate that the client part of
   * the view starts with a required value. If not, client
   * part is left as is.
   *
   * @param validateClientPath
   */
  public void setValidateClientPath(final boolean validateClientPath) {
    this.validateClientPath = validateClientPath;
  }


  private ValidationException makeInvalidClientPathException(final String clientPath) {
    return new ValidationException("The client path \"" + clientPath + "\" is incorrect. It should start with \"" + clientPrefix + '\"');
  }


  public String toString() {
    return "P4ClientViewParser{" +
            "replaceClientName=" + replaceClientName +
            ", clientPrefixLength=" + clientPrefixLength +
            ", clientPrefix='" + clientPrefix + '\'' +
            ", clientName='" + clientName + '\'' +
            ", validateClientPath=" + validateClientPath +
            '}';
  }
}
