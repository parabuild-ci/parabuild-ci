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
package org.parabuild.ci.cli;

import org.apache.axis.client.Stub;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.parabuild.ci.Version;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.webservice.Parabuild;
import org.parabuild.ci.webservice.ParabuildService;
import org.parabuild.ci.webservice.ParabuildServiceLocator;

import javax.xml.rpc.ServiceException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Command line client.
 */
public final class CommandLineClient {

  public static final String OPTION_HELP = "h";
  public static final String OPTION_HOST = "a";
  public static final String OPTION_USER = "u";
  public static final String OPTION_PASSWORD = "p";
  public static final String OPTION_RELEASE_NOTES_FILE = "f";
  public static final String OPTION_BUILD_ID = "b";
  public static final String OPTION_COMMAND = "c";

  public static final String OPTION_LONG_HELP = "help";
  public static final String OPTION_LONG_HOST = "address";
  public static final String OPTION_LONG_USER = "user";
  public static final String OPTION_LONG_PASSWORD = "password";
  public static final String OPTION_LONG_RELEASE_NOTES_FILE = "f";
  public static final String OPTION_LONG_BUILD_ID = "build";
  public static final String OPTION_LONG_COMMAND = "command";
  public static final String COMMAND_START = "start";
  public static final String COMMAND_STOP = "stop";
  public static final String COMMAND_CLEAN = "clean";

  public static final String OPTION_GET_RELEASE_NOTES = "r";


  public static void main(final String[] args) {
    int errorCode = 0;
    try { // NOPMD
      final CommandLineClient commandLineClient = new CommandLineClient();
      errorCode = commandLineClient.process(args);
    } catch (final Exception e) {
      System.err.println("Error: " + StringUtils.toString(e)); // NOPMD
      errorCode = 2;
    }
    System.exit(errorCode);
  }


  /**
   * Main entry method.
   *
   * @param args
   */
  private int process(final String[] args) throws ParseException, ServiceException, RemoteException {
    // add options
    final Options options = new Options();
    final Option helpOption = new Option(OPTION_HELP, OPTION_LONG_HELP, false, "Prints help on using command line interface for Parabuild");
    final Option hostOption = new Option(OPTION_HOST, OPTION_LONG_HOST, true, "Parabuild server address and port in format host:port");
    final Option userOption = new Option(OPTION_USER, OPTION_LONG_USER, true, "Parabuild server user name");
    final Option passwordOption = new Option(OPTION_PASSWORD, OPTION_LONG_PASSWORD, true, "Parabuild server user password");
    final Option buildIDOption = new Option(OPTION_BUILD_ID, OPTION_LONG_BUILD_ID, true, "Integer build configuration ID");
    final Option commandOption = new Option(OPTION_COMMAND, OPTION_LONG_COMMAND, true, "Command to execute: start, stop or clean");
    // set required
//    hostOption.setRequired(true);
//    userOption.setRequired(true);
//    passwordOption.setRequired(true);
//    buildIDOption.setRequired(true);
    // add to the list
    options.addOption(helpOption);
    options.addOption(hostOption);
    options.addOption(userOption);
    options.addOption(passwordOption);
    options.addOption(buildIDOption);
    options.addOption(commandOption);

    // parse
    final CommandLineParser parser = new PosixParser();
    final CommandLine cmd = parser.parse(options, args);

    // check if there is request for help
    if (cmd.hasOption(OPTION_HELP)) { // NOPMD
      printlnToStdout(""); // NOPMD

      printlnToStdout("Usage:");
      printlnToStdout("");
      printlnToStdout("   paracmd.bat -a <host:port> -u <user> -p <password> -b <integer build id> -c <command>");
      printlnToStdout("");
      printlnToStdout("Notes: \"stop\" command stops running build; \"start\" command starts build run; \"clean\"");
      printlnToStdout("command requests clean checkout for the next buid run.");
//      printHelp("Line syntax", "header", options, "Footer");
      return 0;
    }

    // get server address
    String address = null;
    if (cmd.hasOption(OPTION_HOST)) {
      address = cmd.getOptionValue(OPTION_HOST);
      if (StringUtils.isBlank(address)) {
        printRequiredOptionValueMissing("address");
        return 1;
      }
    } else {
      printRequiredOptionsMissing();
      return 1;
    }

    // get user
    String user = null;
    if (cmd.hasOption(OPTION_USER)) {
      user = cmd.getOptionValue(OPTION_USER);
      if (StringUtils.isBlank(user)) {
        printRequiredOptionValueMissing("user name");
        return 1;
      }
    } else {
      printRequiredOptionsMissing();
      return 1;
    }

    // get user password
    String password = null;
    final String optionName = OPTION_PASSWORD;
    if (cmd.hasOption(optionName)) {
      password = cmd.getOptionValue(optionName);
      if (StringUtils.isBlank(password)) {
        printRequiredOptionValueMissing("password");
        return 1;
      }
    } else {
      printRequiredOptionsMissing();
      return 1;
    }

    // get command
    String command = null;
    if (cmd.hasOption(OPTION_COMMAND)) {
      command = cmd.getOptionValue(OPTION_COMMAND);
      if (StringUtils.isBlank(command)) {
        printRequiredOptionValueMissing("command");
        return 1;
      }
    } else {
      printRequiredOptionsMissing();
      return 1;
    }

    // get build ID
    String buildIDString = null;
    if (cmd.hasOption(OPTION_BUILD_ID)) {
      buildIDString = cmd.getOptionValue(OPTION_BUILD_ID);
      if (StringUtils.isBlank(buildIDString)) {
        printRequiredOptionValueMissing("build id");
        return 1;
      } else if (!StringUtils.isValidInteger(buildIDString)) {
        throw new ServiceException("Build ID should be a valid integer.");
      }
    } else {
      printRequiredOptionsMissing();
      return 1;
    }

    if (command.equals(COMMAND_CLEAN)) {
      getParabuildService(user, password, address).startBuild(Integer.parseInt(buildIDString));
      printlnToStdout("Request to perform clean checkout for build ID \"" + buildIDString + "\" has been sent to Parabuild server at \"" + address + '\"');
    } else if (command.equals(COMMAND_STOP)) {
      getParabuildService(user, password, address).stopBuild(Integer.parseInt(buildIDString));
      printlnToStdout("Request to stop build ID \"" + buildIDString + "\" has been sent to Parabuild server at \"" + address + '\"');
    } else if (command.equals(COMMAND_START)) {
      getParabuildService(user, password, address).startBuild(Integer.parseInt(buildIDString));
      printlnToStdout("Request to start build ID \"" + buildIDString + "\" has been sent to Parabuild server at \"" + address + '\"');
    } else {
      throw new ServiceException("Unknown command: \"" + command + '\"');
    }


    return 0;
  }


  private Parabuild getParabuildService(final String user, final String password, final String address) throws ServiceException, RemoteException {
    final ParabuildService parabuildServiceLocator = new ParabuildServiceLocator();
    final URL url;
    try {
      url = new URL("http://" + address + "/parabuild/integration/webservice/Parabuild");
    } catch (final MalformedURLException e) {
      throw new ServiceException("Parabuild port and host is invalid: \"" + address + '\"');
    }
    final Parabuild svc = parabuildServiceLocator.getParabuild(url);
    final Stub stub = (Stub) svc;
    stub.setTimeout(15 * 1000);
    stub.setUsername(user);
    stub.setPassword(password);
    validateServerVersion(svc);
    return svc;
  }


  private static void validateServerVersion(final Parabuild svc) throws RemoteException, ServiceException {
    final String serverVersion = svc.serverVersion();
    final String clientVersion = Version.versionToString(true);
    if (!serverVersion.equals(clientVersion)) {
      throw new ServiceException("Version mismatch: client version is \""
              + clientVersion + "\", server version is \"" + serverVersion + "\".");
    }
  }


  private static void printlnToStdout(final String string) {
    System.out.println(string);  // NOPMD
  }


  private static void printlnToStderr(final String string) {
    System.err.println(string); // NOPMD
  }


  private static void printRequiredOptionsMissing() {
    printlnToStderr("Reqired options are missing. Try 'paracmd --help' for info.");
  }


  private static void printRequiredOptionValueMissing(final String optionName) {
    printlnToStderr("Reqired \"" + optionName + "\" is missing. Try 'paracmd --help' for info.");
  }


  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static void printHelp(final String lineSyntax, final String header, final Options options, final String footer) {
    final PrintWriter pw = new PrintWriter(System.out);
    final HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.printHelp(pw, 30, lineSyntax, header, options, 4, 4, footer);
  }
}
