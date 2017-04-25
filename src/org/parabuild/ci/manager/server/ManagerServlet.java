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
package org.parabuild.ci.manager.server;

import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Deployer;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Session;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.servlets.Constants;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.StringManager;
import org.apache.naming.resources.ProxyDirContext;
import org.apache.naming.resources.WARDirContext;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Servlet that enables remote management of the web applications installed
 * within the same virtual host as this web application is.  Normally, this
 * functionality will be protected by a security constraint in the web
 * application deployment descriptor.  However, this requirement can be
 * relaxed during testing.
 * <p>
 * This servlet examines the value returned by <code>getPathInfo()</code>
 * and related query parameters to determine what action is being requested.
 * The following actions and parameters (starting after the servlet path)
 * are supported:
 * <ul>
 * <li><b>/install?config={config-url}</b> - Install and start a new
 * web application, based on the contents of the context configuration
 * file found at the specified URL.  The <code>docBase</code> attribute
 * of the context configuration file is used to locate the actual
 * WAR or directory containing the application.</li>
 * <li><b>/install?config={config-url}&war={war-url}/</b> - Install and start
 * a new web application, based on the contents of the context
 * configuration file found at <code>{config-url}</code>, overriding the
 * <code>docBase</code> attribute with the contents of the web
 * application archive found at <code>{war-url}</code>.</li>
 * <li><b>/install?path=/xxx&war={war-url}</b> - Install and start a new
 * web application attached to context path <code>/xxx</code>, based
 * on the contents of the web application archive found at the
 * specified URL.</li>
 * <li><b>/list</b> - List the context paths of all currently installed web
 * applications for this virtual host.  Each context will be listed with
 * the following format <code>path:status:sessions</code>.
 * Where path is the context path.  Status is either running or stopped.
 * Sessions is the number of active Sessions.</li>
 * <li><b>/reload?path=/xxx</b> - Reload the Java classes and resources for
 * the application at the specified path, but do not reread the web.xml
 * configuration files.</li>
 * <li><b>/remove?path=/xxx</b> - Shutdown and remove the web application
 * attached to context path <code>/xxx</code> for this virtual host.</li>
 * <li><b>/resources?type=xxxx</b> - Enumerate the available global JNDI
 * resources, optionally limited to those of the specified type
 * (fully qualified Java class name), if available.</li>
 * <li><b>/roles</b> - Enumerate the available security role names and
 * descriptions from the user database connected to the <code>users</code>
 * resource reference.
 * <li><b>/serverinfo</b> - Display system OS and JVM properties.
 * <li><b>/sessions?path=/xxx</b> - List session information about the web
 * application attached to context path <code>/xxx</code> for this
 * virtual host.</li>
 * <li><b>/start?path=/xxx</b> - Start the web application attached to
 * context path <code>/xxx</code> for this virtual host.</li>
 * <li><b>/stop?path=/xxx</b> - Stop the web application attached to
 * context path <code>/xxx</code> for this virtual host.</li>
 * <li><b>/undeploy?path=/xxx</b> - Shutdown and remove the web application
 * attached to context path <code>/xxx</code> for this virtual host,
 * and remove the underlying WAR file or document base directory.
 * (<em>NOTE</em> - This is only allowed if the WAR file or document
 * base is stored in the <code>appBase</code> directory of this host,
 * typically as a result of being placed there via the <code>/deploy</code>
 * command.</li>
 * </ul>
 * <p>Use <code>path=/</code> for the ROOT context.</p>
 * <p>The syntax of the URL for a web application archive must conform to one
 * of the following patterns to be successfully deployed:</p>
 * <ul>
 * <li><b>file:/absolute/path/to/a/directory</b> - You can specify the absolute
 * path of a directory that contains the unpacked version of a web
 * application.  This directory will be attached to the context path you
 * specify without any changes.</li>
 * <li><b>jar:file:/absolute/path/to/a/warfile.war!/</b> - You can specify a
 * URL to a local web application archive file.  The syntax must conform to
 * the rules specified by the <code>JarURLConnection</code> class for a
 * reference to an entire JAR file.</li>
 * <li><b>jar:http://hostname:port/path/to/a/warfile.war!/</b> - You can specify
 * a URL to a remote (HTTP-accessible) web application archive file.  The
 * syntax must conform to the rules specified by the
 * <code>JarURLConnection</code> class for a reference to an entire
 * JAR file.</li>
 * </ul>
 * <p/>
 * <b>NOTE</b> - Attempting to reload or remove the application containing
 * this servlet itself will not succeed.  Therefore, this servlet should
 * generally be deployed as a separate web application within the virtual host
 * to be managed.
 * <p/>
 * <b>NOTE</b> - For security reasons, this application will not operate
 * when accessed via the invoker servlet.  You must explicitly map this servlet
 * with a servlet mapping, and you will always want to protect it with
 * appropriate security constraints as well.
 * <p/>
 * The following servlet initialization parameters are recognized:
 * <ul>
 * <li><b>debug</b> - The debugging detail level that controls the amount
 * of information that is logged by this servlet.  Default is zero.
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.34 $ $Date: 2004/08/26 21:38:13 $
 */

public class ManagerServlet
        extends HttpServlet implements ContainerServlet {


  // ----------------------------------------------------- Instance Variables


  /**
   * The Context container associated with our web application.
   */
  protected Context context = null;


  /**
   * The debugging detail level for this servlet.
   */
  protected int debug = 1;


  /**
   * File object representing the directory into which the deploy() command
   * will store the WAR and context configuration files that have been
   * uploaded.
   */
  protected File deployed = null;


  /**
   * The Deployer container that contains our own web application's Context,
   * along with the associated Contexts for web applications that we
   * are managing.
   */
  protected Deployer deployer = null;


  /**
   * The global JNDI <code>NamingContext</code> for this server,
   * if available.
   */
  protected javax.naming.Context global = null;


  /**
   * The string manager for this package.
   */
  protected static final StringManager sm = StringManager.getManager(Constants.Package);


  /**
   * The Wrapper container associated with this servlet.
   */
  protected Wrapper wrapper = null;


  // ----------------------------------------------- ContainerServlet Methods


  /**
   * Return the Wrapper with which we are associated.
   */
  public Wrapper getWrapper() {

    return this.wrapper;

  }


  /**
   * Set the Wrapper with which we are associated.
   *
   * @param wrapper The new wrapper
   */
  public void setWrapper(final Wrapper wrapper) {

    this.wrapper = wrapper;
    if (wrapper == null) {
      context = null;
      deployer = null;
    } else {
      context = (Context) wrapper.getParent();
      deployer = (Deployer) context.getParent();
    }

  }


  // --------------------------------------------------------- Public Methods


  /**
   * Finalize this servlet.
   */
  public void destroy() {

    // No actions necessary

  }


  /**
   * Process a GET request for the specified resource.
   *
   * @param request  The servlet request we are processing
   * @param response The servlet response we are creating
   * @throws IOException      if an input/output error occurs
   * @throws ServletException if a servlet-specified error occurs
   */
  public void doGet(final HttpServletRequest request,
                    final HttpServletResponse response)
          throws IOException, ServletException {

    // Verify that we were not accessed using the invoker servlet
    if (request.getAttribute(Globals.INVOKED_ATTR) != null) {
      throw new UnavailableException
              (sm.getString("managerServlet.cannotInvoke"));
    }

    // Identify the request parameters that we need
    String command = request.getPathInfo();
    if (command == null) {
      command = request.getServletPath();
    }
    final String config = request.getParameter("config");
    final String path = request.getParameter("path");
    final String type = request.getParameter("type");
    final String war = request.getParameter("war");

    // Prepare our output writer to generate the response message
    final Locale locale = Locale.getDefault();
    final String charset = context.getCharsetMapper().getCharset(locale);
    response.setLocale(locale);
    response.setContentType("text/plain; charset=" + charset);
    final PrintWriter writer = response.getWriter();

    // Process the requested command (note - "/deploy" is not listed here)
    if (command == null) {
      writer.println(sm.getString("managerServlet.noCommand"));
    } else if (command.equals("/install")) {
      install(writer, config, path, war);
    } else if (command.equals("/list")) {
      list(writer);
    } else if (command.equals("/reload")) {
      reload(writer, path);
    } else if (command.equals("/remove")) {
      remove(writer, path);
    } else if (command.equals("/resources")) {
      resources(writer, type);
    } else if (command.equals("/roles")) {
      roles(writer);
    } else if (command.equals("/serverinfo")) {
      serverinfo(writer);
    } else if (command.equals("/sessions")) {
      sessions(writer, path);
    } else if (command.equals("/start")) {
      start(writer, path);
    } else if (command.equals("/stop")) {
      stop(writer, path);
    } else if (command.equals("/undeploy")) {
      undeploy(writer, path);
    } else {
      writer.println(sm.getString("managerServlet.unknownCommand",
              command));
    }

    // Finish up the response
    writer.flush();
    writer.close();

  }


  /**
   * Process a PUT request for the specified resource.
   *
   * @param request  The servlet request we are processing
   * @param response The servlet response we are creating
   * @throws IOException      if an input/output error occurs
   * @throws ServletException if a servlet-specified error occurs
   */
  public void doPut(final HttpServletRequest request,
                    final HttpServletResponse response)
          throws IOException, ServletException {

    // Verify that we were not accessed using the invoker servlet
    if (request.getAttribute(Globals.INVOKED_ATTR) != null) {
      throw new UnavailableException
              (sm.getString("managerServlet.cannotInvoke"));
    }

    // Identify the request parameters that we need
    String command = request.getPathInfo();
    if (command == null) {
      command = request.getServletPath();
    }
    final String path = request.getParameter("path");

    // Prepare our output writer to generate the response message
    response.setContentType("text/plain");
    final Locale locale = Locale.getDefault();
    response.setLocale(locale);
    final PrintWriter writer = response.getWriter();

    // Process the requested command
    if (command == null) {
      writer.println(sm.getString("managerServlet.noCommand"));
    } else if (command.equals("/deploy")) {
      deploy(writer, path, request);
    } else {
      writer.println(sm.getString("managerServlet.unknownCommand",
              command));
    }

    // Saving configuration
    final Server server = ServerFactory.getServer();
    if (server != null && server instanceof StandardServer) {
      try {
        ((StandardServer) server).store();
      } catch (Exception e) {
        writer.println(sm.getString("managerServlet.saveFail",
                e.getMessage()));
      }
    }

    // Finish up the response
    writer.flush();
    writer.close();

  }


  /**
   * Initialize this servlet.
   */
  public void init() throws ServletException {

    // Ensure that our ContainerServlet properties have been set
    if (wrapper == null || context == null) {
      throw new UnavailableException
              (sm.getString("managerServlet.noWrapper"));
    }

    // Verify that we were not accessed using the invoker servlet
    String servletName = getServletConfig().getServletName();
    if (servletName == null) {
      servletName = "";
    }
    if (servletName.startsWith("org.apache.catalina.INVOKER.")) {
      throw new UnavailableException
              (sm.getString("managerServlet.cannotInvoke"));
    }

    // Set our properties from the initialization parameters
    try {
      final String value = getServletConfig().getInitParameter("debug");
      debug = Integer.parseInt(value);
    } catch (Throwable t) {
      log(t.toString(), t);
    }

    // Acquire global JNDI resources if available
    final Server server = ServerFactory.getServer();
    if (server != null && server instanceof StandardServer) {
      global = ((StandardServer) server).getGlobalNamingContext();
    }

    // Calculate the directory into which we will be deploying applications
    deployed = (File) getServletContext().getAttribute
            ("javax.servlet.context.tempdir");

    // Log debugging messages as necessary
    if (debug >= 1) {
      log("init: Associated with Deployer '" +
              deployer.getName() + '\'');
      if (global != null) {
        log("init: Global resources are available");
      }
    }

  }


  // -------------------------------------------------------- Private Methods


  /**
   * Deploy a web application archive (included in the current request)
   * at the specified context path.
   *
   * @param writer  Writer to render results to
   * @param path    Context path of the application to be installed
   * @param request Servlet request we are processing
   */
  protected synchronized void deploy(final PrintWriter writer, String path,
                                     final HttpServletRequest request) {

    if (debug >= 1) {
      log("deploy: Deploying web application at '" + path + '\'');
    }

    // Validate the requested context path
    if (path == null || path.length() == 0 || !path.startsWith("/")) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.equals("/")) {
      path = "";
    }
    final String basename;
    if (path.length() == 0) {
      basename = "_";
    } else {
      basename = path.substring(1);
    }
    if (deployer.findDeployedApp(path) != null) {
      writer.println
              (sm.getString("managerServlet.alreadyContext", displayPath));
      return;
    }

    // Upload the web application archive to a local WAR file
    final File localWar = new File(deployed, basename + ".war");
    if (debug >= 2) {
      log("Uploading WAR file to " + localWar);
    }
    try {
      uploadWar(request, localWar);
    } catch (IOException e) {
      log("managerServlet.upload[" + displayPath + ']', e);
      writer.println(sm.getString("managerServlet.exception",
              e.toString()));
      return;
    }

    // Extract the nested context deployment file (if any)
    final File localXml = new File(deployed, basename + ".xml");
    if (debug >= 2) {
      log("Extracting XML file to " + localXml);
    }
    try {
      extractXml(localWar, localXml);
    } catch (IOException e) {
      log("managerServlet.extract[" + displayPath + ']', e);
      writer.println(sm.getString("managerServlet.exception",
              e.toString()));
      return;
    }

    // Deploy this web application
    try {
      final URL warURL =
              new URL("jar:file:" + localWar.getAbsolutePath() + "!/");
      URL xmlURL = null;
      if (localXml.exists()) {
        xmlURL = new URL("file:" + localXml.getAbsolutePath());
      }
      if (xmlURL != null) {
        deployer.install(xmlURL, warURL);
      } else {
        deployer.install(path, warURL);
      }
    } catch (Throwable t) {
      log("ManagerServlet.deploy[" + displayPath + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
      localWar.delete();
      localXml.delete();
      return;
    }

    // Acknowledge successful completion of this deploy command
    writer.println(sm.getString("managerServlet.installed",
            displayPath));

  }


  /**
   * Install an application for the specified path from the specified
   * web application archive.
   *
   * @param writer Writer to render results to
   * @param config URL of the context configuration file to be installed
   * @param path   Context path of the application to be installed
   * @param war    URL of the web application archive to be installed
   */
  protected void install(final PrintWriter writer, final String config,
                         String path, String war) {

    if (war != null && war.length() == 0) {
      war = null;
    }

    if (debug >= 1) {
      if (config != null && config.length() > 0) {
        if (war != null) {
          log("install: Installing context configuration at '" +
                  config + "' from '" + war + '\'');
        } else {
          log("install: Installing context configuration at '" +
                  config + '\'');
        }
      } else {
        log("install: Installing web application at '" + path +
                "' from '" + war + '\'');
      }
    }

    // See if directory/war is relative to host appBase
    if (war != null && war.indexOf('/') < 0) {
      // Identify the appBase of the owning Host of this Context (if any)
      if (context.getParent() instanceof Host) {
        final String appBase = ((Host) context.getParent()).getAppBase();
        File appBaseDir = new File(appBase);
        if (!appBaseDir.isAbsolute()) {
          appBaseDir = new File(System.getProperty("catalina.base"),
                  appBase);
        }
        final File file = new File(appBaseDir, war);
        try {
          final URL url = file.toURL();
          war = url.toString();
          if (war.toLowerCase().endsWith(".war")) {
            war = "jar:" + war + "!/";
          }
        } catch (MalformedURLException e) {
          log(e.toString(), e);
        }
      }
    }

    if (config != null && config.length() > 0) {

      if (war != null &&
              !war.startsWith("file:") && !war.startsWith("jar:")) {
        writer.println(sm.getString("managerServlet.invalidWar", war));
        return;
      }

      try {
        if (war == null) {
          deployer.install(new URL(config), null);
        } else {
          deployer.install(new URL(config), new URL(war));
        }
        writer.println(sm.getString("managerServlet.configured",
                config));
      } catch (Throwable t) {
        log("ManagerServlet.configure[" + config + ']', t);
        writer.println(sm.getString("managerServlet.exception",
                t.toString()));
      }

    } else {

      if (war == null ||
              !war.startsWith("file:") && !war.startsWith("jar:")) {
        writer.println(sm.getString("managerServlet.invalidWar", war));
        return;
      }

      if (path == null || path.length() == 0) {
        int end = war.length();
        String filename = war.toLowerCase();
        if (filename.endsWith("!/")) {
          filename = filename.substring(0, filename.length() - 2);
          end -= 2;
        }
        if (filename.endsWith(".war")) {
          filename = filename.substring(0, filename.length() - 4);
          end -= 4;
        }
        if (filename.endsWith("/")) {
          filename = filename.substring(0, filename.length() - 1);
          end--;
        }
        final int beg = filename.lastIndexOf('/') + 1;
        if (beg < 0 || end < 0 || beg >= end) {
          writer.println(sm.getString("managerServlet.invalidWar", war));
          return;
        }
        path = '/' + war.substring(beg, end);
        if (path.equals("/ROOT")) {
          path = "/";
        }
      }

      if (path == null || path.length() == 0 || !path.startsWith("/")) {
        writer.println(sm.getString("managerServlet.invalidPath",
                path));
        return;
      }
      final String displayPath = path;
      if ("/".equals(path)) {
        path = "";
      }

      try {
        final Context context = deployer.findDeployedApp(path);
        if (context != null) {
          writer.println
                  (sm.getString("managerServlet.alreadyContext",
                          displayPath));
          return;
        }
        deployer.install(path, new URL(war));
        writer.println(sm.getString("managerServlet.installed",
                displayPath));
      } catch (Throwable t) {
        log("ManagerServlet.install[" + displayPath + ']', t);
        writer.println(sm.getString("managerServlet.exception",
                t.toString()));
      }

    }

  }


  /**
   * Render a list of the currently active Contexts in our virtual host.
   *
   * @param writer Writer to render to
   */
  protected void list(final PrintWriter writer) {

    if (debug >= 1) {
      log("list: Listing contexts for virtual host '" +
              deployer.getName() + '\'');
    }

    writer.println(sm.getString("managerServlet.listed",
            deployer.getName()));
    final String[] contextPaths = deployer.findDeployedApps();
    for (int i = 0; i < contextPaths.length; i++) {
      final Context context = deployer.findDeployedApp(contextPaths[i]);
      String displayPath = contextPaths[i];
      if (displayPath.length() == 0) {
        displayPath = "/";
      }
      if (context != null) {
        if (context.getAvailable()) {
          writer.println(sm.getString("managerServlet.listitem",
                  displayPath,
                  "running",
                  String.valueOf(context.getManager().findSessions().length),
                  context.getDocBase()));
        } else {
          writer.println(sm.getString("managerServlet.listitem",
                  displayPath,
                  "stopped",
                  "0",
                  context.getDocBase()));
        }
      }
    }
  }


  /**
   * Reload the web application at the specified context path.
   *
   * @param writer Writer to render to
   * @param path   Context path of the application to be restarted
   */
  protected void reload(final PrintWriter writer, String path) {

    if (debug >= 1) {
      log("restart: Reloading web application at '" + path + '\'');
    }

    if (path == null || !path.startsWith("/") && path.length() == 0) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.equals("/")) {
      path = "";
    }

    try {
      final Context context = deployer.findDeployedApp(path);
      if (context == null) {
        writer.println(sm.getString("managerServlet.noContext", displayPath));
        return;
      }
      DirContext resources = context.getResources();
      if (resources instanceof ProxyDirContext) {
        resources = ((ProxyDirContext) resources).getDirContext();
      }
      if (resources instanceof WARDirContext) {
        writer.println(sm.getString("managerServlet.noReload", displayPath));
        return;
      }
      // It isn't possible for the manager to reload itself
      if (context.getPath().equals(this.context.getPath())) {
        writer.println(sm.getString("managerServlet.noSelf"));
        return;
      }
      context.reload();
      writer.println(sm.getString("managerServlet.reloaded", displayPath));
    } catch (Throwable t) {
      log("ManagerServlet.reload[" + displayPath + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

  }


  /**
   * Remove the web application at the specified context path.
   *
   * @param writer Writer to render to
   * @param path   Context path of the application to be removed
   */
  protected void remove(final PrintWriter writer, String path) {

    if (debug >= 1) {
      log("remove: Removing web application at '" + path + '\'');
    }

    if (path == null || !path.startsWith("/") && path.length() == 0) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.equals("/")) {
      path = "";
    }

    try {
      final Context context = deployer.findDeployedApp(path);
      if (context == null) {
        writer.println(sm.getString("managerServlet.noContext", displayPath));
        return;
      }
      // It isn't possible for the manager to remove itself
      if (context.getPath().equals(this.context.getPath())) {
        writer.println(sm.getString("managerServlet.noSelf"));
        return;
      }
      deployer.remove(path, true);
      writer.println(sm.getString("managerServlet.removed", displayPath));
    } catch (Throwable t) {
      log("ManagerServlet.remove[" + displayPath + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

  }


  /**
   * Render a list of available global JNDI resources.
   *
   * @param type Fully qualified class name of the resource type of interest,
   *             or <code>null</code> to list resources of all types
   */
  protected void resources(final PrintWriter writer, final String type) {

    if (debug >= 1) {
      if (type != null) {
        log("resources:  Listing resources of type " + type);
      } else {
        log("resources:  Listing resources of all types");
      }
    }

    // Is the global JNDI resources context available?
    if (global == null) {
      writer.println(sm.getString("managerServlet.noGlobal"));
      return;
    }

    // Enumerate the global JNDI resources of the requested type
    if (type != null) {
      writer.println(sm.getString("managerServlet.resourcesType",
              type));
    } else {
      writer.println(sm.getString("managerServlet.resourcesAll"));
    }

    Class clazz = null;
    try {
      if (type != null) {
        clazz = Class.forName(type);
      }
    } catch (Throwable t) {
      log("ManagerServlet.resources[" + type + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
      return;
    }

    printResources(writer, "", global, type, clazz);

  }


  /**
   * List the resources of the given context.
   */
  protected void printResources(final PrintWriter writer, final String prefix,
                                final javax.naming.Context namingContext,
                                final String type, final Class clazz) {

    try {
      final NamingEnumeration items = namingContext.listBindings("");
      while (items.hasMore()) {
        final Binding item = (Binding) items.next();
        if (item.getObject() instanceof javax.naming.Context) {
          printResources
                  (writer, prefix + item.getName() + '/',
                          (javax.naming.Context) item.getObject(), type, clazz);
        } else {
          if (clazz != null &&
                  !clazz.isInstance(item.getObject())) {
            continue;
          }
          writer.print(prefix + item.getName());
          writer.print(':');
          writer.print(item.getClassName());
          // Do we want a description if available?
          writer.println();
        }
      }
    } catch (Throwable t) {
      log("ManagerServlet.resources[" + type + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

  }


  /**
   * Render a list of security role names (and corresponding descriptions)
   * from the <code>org.apache.catalina.UserDatabase</code> resource that is
   * connected to the <code>users</code> resource reference.  Typically, this
   * will be the global user database, but can be adjusted if you have
   * different user databases for different virtual hosts.
   *
   * @param writer Writer to render to
   */
  protected void roles(final PrintWriter writer) {

    if (debug >= 1) {
      log("roles:  List security roles from user database");
    }

    // Look up the UserDatabase instance we should use
    final UserDatabase database;
    try {
      final InitialContext ic = new InitialContext();
      database = (UserDatabase) ic.lookup("java:comp/env/users");
    } catch (NamingException e) {
      writer.println(sm.getString("managerServlet.userDatabaseError"));
      log("java:comp/env/users", e);
      return;
    }
    if (database == null) {
      writer.println(sm.getString("managerServlet.userDatabaseMissing"));
      return;
    }

    // Enumerate the available roles
    writer.println(sm.getString("managerServlet.rolesList"));
    final Iterator roles = database.getRoles();
    if (roles != null) {
      while (roles.hasNext()) {
        final Role role = (Role) roles.next();
        writer.print(role.getRolename());
        writer.print(':');
        if (role.getDescription() != null) {
          writer.print(role.getDescription());
        }
        writer.println();
      }
    }


  }


  /**
   * Writes System OS and JVM properties.
   *
   * @param writer Writer to render to
   */
  protected void serverinfo(final PrintWriter writer) {
    if (debug >= 1) {
      log("serverinfo");
    }
    try {
      final StringBuffer props = new StringBuffer(100);
      props.append("Tomcat Version: ");
      props.append(ServerInfo.getServerInfo());
      props.append("\nOS Name: ");
      props.append(System.getProperty("os.name"));
      props.append("\nOS Version: ");
      props.append(System.getProperty("os.version"));
      props.append("\nOS Architecture: ");
      props.append(System.getProperty("os.arch"));
      props.append("\nJVM Version: ");
      props.append(System.getProperty("java.runtime.version"));
      props.append("\nJVM Vendor: ");
      props.append(System.getProperty("java.vm.vendor"));
      writer.println(props.toString());
    } catch (Throwable t) {
      getServletContext().log("ManagerServlet.serverinfo", t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }
  }


  /**
   * Session information for the web application at the specified context path.
   * Displays a profile of session MaxInactiveInterval timeouts listing number
   * of sessions for each 10 minute timeout interval up to 10 hours.
   *
   * @param writer Writer to render to
   * @param path   Context path of the application to list session information for
   */
  protected void sessions(final PrintWriter writer, String path) {

    if (debug >= 1) {
      log("sessions: Session information for web application at '" + path + '\'');
    }

    if (path == null || !path.startsWith("/") && path.length() == 0) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.equals("/")) {
      path = "";
    }
    try {
      final Context context = deployer.findDeployedApp(path);
      if (context == null) {
        writer.println(sm.getString("managerServlet.noContext", displayPath));
        return;
      }
      writer.println(sm.getString("managerServlet.sessions", displayPath));
      writer.println(sm.getString("managerServlet.sessiondefaultmax",
              String.valueOf(context.getManager().getMaxInactiveInterval() / 60)));
      final Session[] sessions = context.getManager().findSessions();
      final int[] timeout = new int[60];
      int notimeout = 0;
      for (int i = 0; i < sessions.length; i++) {
        final int time = sessions[i].getMaxInactiveInterval() / (10 * 60);
        if (time < 0) {
          notimeout++;
        } else if (time >= timeout.length) {
          timeout[timeout.length - 1]++;
        } else {
          timeout[time]++;
        }
      }
      if (timeout[0] > 0) {
        writer.println(sm.getString("managerServlet.sessiontimeout",
                "<10", String.valueOf(timeout[0])));
      }
      for (int i = 1; i < timeout.length - 1; i++) {
        if (timeout[i] > 0) {
          writer.println(sm.getString("managerServlet.sessiontimeout",
                  String.valueOf(i * 10) + " - <" + (i + 1) * 10,
                  String.valueOf(timeout[i])));
        }
      }
      if (timeout[timeout.length - 1] > 0) {
        writer.println(sm.getString("managerServlet.sessiontimeout",
                ">=" + timeout.length * 10,
                String.valueOf(timeout[timeout.length - 1])));
      }
      if (notimeout > 0) {
        writer.println(sm.getString("managerServlet.sessiontimeout",
                "unlimited", String.valueOf(notimeout)));
      }
    } catch (Throwable t) {
      log("ManagerServlet.sessions[" + displayPath + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

  }


  /**
   * Start the web application at the specified context path.
   *
   * @param writer Writer to render to
   * @param path   Context path of the application to be started
   */
  protected void start(final PrintWriter writer, String path) {

    if (debug >= 1) {
      log("start: Starting web application at '" + path + '\'');
    }

    if (path == null || !path.startsWith("/") && path.length() == 0) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.equals("/")) {
      path = "";
    }

    try {
      final Context context = deployer.findDeployedApp(path);
      if (context == null) {
        writer.println(sm.getString("managerServlet.noContext", displayPath));
        return;
      }
      deployer.start(path);
      if (context.getAvailable()) {
        writer.println
                (sm.getString("managerServlet.started", displayPath));
      } else {
        writer.println
                (sm.getString("managerServlet.startFailed", displayPath));
      }
    } catch (Throwable t) {
      getServletContext().log
              (sm.getString("managerServlet.startFailed", displayPath), t);
      writer.println
              (sm.getString("managerServlet.startFailed", displayPath));
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

  }


  /**
   * Stop the web application at the specified context path.
   *
   * @param writer Writer to render to
   * @param path   Context path of the application to be stopped
   */
  protected void stop(final PrintWriter writer, String path) {

    if (debug >= 1) {
      log("stop: Stopping web application at '" + path + '\'');
    }

    if (path == null || !path.startsWith("/") && path.length() == 0) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.equals("/")) {
      path = "";
    }

    try {
      final Context context = deployer.findDeployedApp(path);
      if (context == null) {
        writer.println(sm.getString("managerServlet.noContext", displayPath));
        return;
      }
      // It isn't possible for the manager to stop itself
      if (context.getPath().equals(this.context.getPath())) {
        writer.println(sm.getString("managerServlet.noSelf"));
        return;
      }
      deployer.stop(path);
      writer.println(sm.getString("managerServlet.stopped", displayPath));
    } catch (Throwable t) {
      log("ManagerServlet.stop[" + displayPath + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

  }


  /**
   * Undeploy the web application at the specified context path.
   *
   * @param writer Writer to render to
   * @param path   Context path of the application to be removed
   */
  protected void undeploy(final PrintWriter writer, String path) {

    if (debug >= 1) {
      log("undeploy: Undeploying web application at '" + path + '\'');
    }

    if (path == null || !path.startsWith("/") && path.length() == 0) {
      writer.println(sm.getString("managerServlet.invalidPath", path));
      return;
    }
    final String displayPath = path;
    if (path.trim().equals("/")) {
      path = "";
    }

    try {

      // Validate the Context of the specified application
      final Context context = deployer.findDeployedApp(path);
      if (context == null) {
        writer.println(sm.getString("managerServlet.noContext", displayPath));
        return;
      }

      // Identify the appBase of the owning Host of this Context (if any)
      File appBaseDir = null;
      if (context.getParent() instanceof Host) {
        final String appBase = ((Host) context.getParent()).getAppBase();
        appBaseDir = new File(appBase);
        if (!appBaseDir.isAbsolute()) {
          appBaseDir = new File(System.getProperty("catalina.base"),
                  appBase);
        }
      }

      // Validate the docBase path of this application
      final String docBase = context.getDocBase();
      System.out.println("DEBUG: docBase: " + docBase);
      File docBaseDir = new File(docBase);
      if (!docBaseDir.isAbsolute()) {
        docBaseDir = new File(appBaseDir, docBase);
      }
      System.out.println("DEBUG: docBaseDir: " + docBaseDir);
      final String docBasePath = docBaseDir.getCanonicalPath();
      System.out.println("DEBUG: docBasePath: " + docBasePath);
      System.out.println("DEBUG: displayPath: " + displayPath);
      System.out.println("DEBUG: appBaseDir.getCanonicalPath(): " + appBaseDir.getCanonicalPath());
      if (!docBasePath.startsWith(appBaseDir.getCanonicalPath())) {
        writer.println(sm.getString("managerServlet.noDocBase", displayPath));
        return;
      }
//     [java] DEBUG: docBaseDir: C:\WORK\mor2\dev\bt\temp\test_run_builder\etc\app\parabuild.war
//     [java] DEBUG: docBasePath: C:\WORK\mor2\dev\bt\temp\test_run_builder\etc\app\parabuild.war
//     [java] DEBUG: deployedPath: C:\WORK\mor2\dev\bt\temp\test_run_builder\etc\work\Standalone\localhost\manager
      // Remove this web application and its associated docBase
      if (debug >= 2) {
        log("Undeploying document base " + docBasePath);
      }
      // It isn't possible for the manager to undeploy itself
      if (context.getPath().equals(this.context.getPath())) {
        writer.println(sm.getString("managerServlet.noSelf"));
        return;
      }
      deployer.remove(path);
      if (docBaseDir.isDirectory()) {
        undeployDir(docBaseDir);
      } else {
        docBaseDir.delete();  // Delete the WAR file
      }
      final String docBaseXmlPath =
              docBasePath.substring(0, docBasePath.length() - 4) + ".xml";
      final File docBaseXml = new File(docBaseXmlPath);
      docBaseXml.delete();
      writer.println(sm.getString("managerServlet.undeployed",
              displayPath));

    } catch (Throwable t) {
      log("ManagerServlet.undeploy[" + displayPath + ']', t);
      writer.println(sm.getString("managerServlet.exception",
              t.toString()));
    }

    // Saving configuration
    final Server server = ServerFactory.getServer();
    if (server != null && server instanceof StandardServer) {
      try {
        ((StandardServer) server).store();
      } catch (Exception e) {
        writer.println(sm.getString("managerServlet.saveFail",
                e.getMessage()));
      }
    }

  }


  // -------------------------------------------------------- Support Methods


  /**
   * Extract the context configuration file from the specified WAR,
   * if it is present.  If it is not present, ensure that the corresponding
   * file does not exist.
   *
   * @param war File object representing the WAR
   * @param xml File object representing where to store the extracted
   *            context configuration file (if it exists)
   * @throws IOException if an i/o error occurs
   */
  protected static void extractXml(final File war, final File xml) throws IOException {

    xml.delete();
    JarFile jar = null;
    JarEntry entry;
    InputStream istream = null;
    BufferedOutputStream ostream = null;
    try {
      jar = new JarFile(war);
      entry = jar.getJarEntry("META-INF/context.xml");
      if (entry == null) {
        return;
      }
      istream = jar.getInputStream(entry);
      ostream =
              new BufferedOutputStream(new FileOutputStream(xml), 1024);
      final byte[] buffer = new byte[1024];
      while (true) {
        final int n = istream.read(buffer);
        if (n < 0) {
          break;
        }
        ostream.write(buffer, 0, n);
      }
      ostream.flush();
      ostream.close();
      ostream = null;
      istream.close();
      istream = null;
      entry = null;
      jar.close();
      jar = null;
    } catch (IOException e) {
      xml.delete();
      throw e;
    } finally {
      if (ostream != null) {
        try {
          ostream.close();
        } catch (Throwable ignored) {
        }
      }
      if (istream != null) {
        try {
          istream.close();
        } catch (Throwable ignored) {
        }
      }
      entry = null;
      if (jar != null) {
        try {
          jar.close();
        } catch (Throwable ignored) {
        }
      }
    }

  }


  /**
   * Delete the specified directory, including all of its contents and
   * subdirectories recursively.
   *
   * @param dir File object representing the directory to be deleted
   * @noinspection ZeroLengthArrayAllocation
   */
  protected static void undeployDir(final File dir) {

    String[] files = dir.list();
    if (files == null) {
      files = new String[0];
    }
    for (int i = 0; i < files.length; i++) {
      final File file = new File(dir, files[i]);
      if (file.isDirectory()) {
        undeployDir(file);
      } else {
        file.delete();
      }
    }
    dir.delete();

  }


  /**
   * Upload the WAR file included in this request, and store it at the
   * specified file location.
   *
   * @param request The servlet request we are processing
   * @param war     The file into which we should store the uploaded WAR
   * @throws IOException if an I/O error occurs during processing
   */
  protected static void uploadWar(final HttpServletRequest request, final File war)
          throws IOException {

    war.delete();
    ServletInputStream istream = null;
    BufferedOutputStream ostream = null;
    try {
      istream = request.getInputStream();
      ostream =
              new BufferedOutputStream(new FileOutputStream(war), 1024);
      final byte[] buffer = new byte[1024];
      while (true) {
        final int n = istream.read(buffer);
        if (n < 0) {
          break;
        }
        ostream.write(buffer, 0, n);
      }
      ostream.flush();
      ostream.close();
      ostream = null;
      istream.close();
      istream = null;
    } catch (IOException e) {
      war.delete();
      throw e;
    } finally {
      if (ostream != null) {
        try {
          ostream.close();
        } catch (Throwable ignored) {
        }
      }
      if (istream != null) {
        try {
          istream.close();
        } catch (Throwable ignored) {
        }
      }
    }

  }


}
