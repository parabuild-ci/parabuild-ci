package com.meterware.servletunit;
/********************************************************************************************************************
 * $Id: WebApplication.java,v 1.19 2003/02/27 13:33:45 russgold Exp $
 *
 * Copyright (c) 2001-2003, Russell Gold
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 *******************************************************************************************************************/
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.HttpNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class represents the information recorded about a single web
 * application. It is usually extracted from web.xml.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 * @author <a href="balld@webslingerZ.com">Donald Ball</a>
 **/
class WebApplication {

    private final static SecurityConstraint NULL_SECURITY_CONSTRAINT = new NullSecurityConstraint();

    private final ServletConfiguration SECURITY_CHECK_CONFIGURATION = new ServletConfiguration( SecurityCheckServlet.class.getName() );

    private final ServletMapping SECURITY_CHECK_MAPPING = new ServletMapping( SECURITY_CHECK_CONFIGURATION );

    /** A mapping of resource names to servlet class names. **/
    private ServletMap _servletMapping = new ServletMap();

    private ArrayList _securityConstraints = new ArrayList();

    private boolean _useBasicAuthentication;

    private boolean _useFormAuthentication;

    private String _authenticationRealm = "";

    private URL _loginURL;

    private URL _errorURL;

    private Hashtable _contextParameters = new Hashtable();

    private File _contextDir = null;

    private String _contextPath = null;

    private ServletUnitServletContext _servletContext;


    /**
     * Constructs a default application spec with no information.
     */
    WebApplication() {
        _contextPath = "";
    }


    /**
     * Constructs an application spec from an XML document.
     */
    WebApplication( Document document ) throws MalformedURLException, SAXException {
        this( document, null, "" );
    }


    /**
     * Constructs an application spec from an XML document.
     */
    WebApplication( Document document, String contextPath ) throws MalformedURLException, SAXException {
        this( document, null, contextPath );
    }


    /**
     * Constructs an application spec from an XML document.
     */
    WebApplication( Document document, File file, String contextPath ) throws MalformedURLException, SAXException {
        if (contextPath != null && contextPath.length() > 0 && !contextPath.startsWith( "/" )) throw new IllegalArgumentException( "Context path " + contextPath + " must start with '/'" );
        _contextDir = file;
        _contextPath = contextPath == null ? "" : contextPath;
        registerServlets( document );
        extractSecurityConstraints( document );
        extractContextParameters( document );
        extractLoginConfiguration( document );
        _servletMapping.autoLoadServlets();
    }


    private void extractSecurityConstraints( Document document ) throws SAXException {
        NodeList nl = document.getElementsByTagName( "security-constraint" );
        for (int i = 0; i < nl.getLength(); i++) {
            _securityConstraints.add( new SecurityConstraintImpl( (Element) nl.item( i ) ) );
        }
    }


    String getContextPath() {
        return _contextPath;
    }


    ServletContext getServletContext() {
        if (_servletContext == null) {
            _servletContext = new ServletUnitServletContext( this );
        }
        return _servletContext;
    }


    /**
     * Registers a servlet class to be run.
     **/
    void registerServlet( String resourceName, String servletClassName, Hashtable initParams ) {
        registerServlet( resourceName, new ServletConfiguration( servletClassName, initParams ) );
    }


    /**
     * Registers a servlet to be run.
     **/
    void registerServlet( String resourceName, ServletConfiguration servletConfiguration ) {
        // FIXME - shouldn't everything start with one or the other?
        if (!resourceName.startsWith( "/" ) && !resourceName.startsWith( "*" )) {
            resourceName = "/" + resourceName;
        }
        _servletMapping.put( resourceName, servletConfiguration );
    }


    /**
     * Calls the destroy method for every active servlet.
     */
    void destroyServlets() {
        _servletMapping.destroyServlets();
    }


    ServletMetaData getServletRequest( URL url ) {
        return _servletMapping.get( url );
    }


    /**
     * Returns true if this application uses Basic Authentication.
     */
    boolean usesBasicAuthentication() {
        return _useBasicAuthentication;
    }


    /**
     * Returns true if this application uses form-based authentication.
     */
    boolean usesFormAuthentication() {
        return _useFormAuthentication;
    }


    String getAuthenticationRealm() {
        return _authenticationRealm;
    }


    URL getLoginURL() {
        return _loginURL;
    }


    URL getErrorURL() {
        return _errorURL;
    }


    /**
     * Returns true if the specified path may only be accesses by an authorized user.
     * @param url the application-relative path of the URL
     */
    boolean requiresAuthorization( URL url ) {
        String result;
        String file = url.getFile();
        if (_contextPath.equals( "" )) {
            result = file;
        } else if (file.startsWith( _contextPath )) {
            result = file.substring( _contextPath.length() );
        } else {
            result = null;
        }
        return getControllingConstraint( result ) != NULL_SECURITY_CONSTRAINT;
    }


    /**
     * Returns an array containing the roles permitted to access the specified URL.
     */
    String[] getPermittedRoles( URL url ) {
        String result;
        String file = url.getFile();
        if (_contextPath.equals( "" )) {
            result = file;
        } else if (file.startsWith( _contextPath )) {
            result = file.substring( _contextPath.length() );
        } else {
            result = null;
        }
        return getControllingConstraint( result ).getPermittedRoles();
    }


    private SecurityConstraint getControllingConstraint( String urlPath ) {
        for (Iterator i = _securityConstraints.iterator(); i.hasNext();) {
            SecurityConstraint sc = (SecurityConstraint) i.next();
            if (sc.controlsPath( urlPath )) return sc;
        }
        return NULL_SECURITY_CONSTRAINT;
    }


    File getResourceFile( String path ) {
        if (_contextDir == null) {
            return new File( path.substring(1) );
        } else {
            return new File( _contextDir, path.substring(1) );
        }
    }


    Hashtable getContextParameters() {
        return _contextParameters;
    }


//------------------------------------------------ private members ---------------------------------------------


    private void extractLoginConfiguration( Document document ) throws MalformedURLException, SAXException {
        NodeList nl = document.getElementsByTagName( "login-config" );
        if (nl.getLength() == 1) {
            final Element loginConfigElement = (Element) nl.item( 0 );
            String authenticationMethod = getChildNodeValue( loginConfigElement, "auth-method", "BASIC" );
            _authenticationRealm = getChildNodeValue( loginConfigElement, "realm-name", "" );
            if (authenticationMethod.equalsIgnoreCase( "BASIC" )) {
                _useBasicAuthentication = true;
                if (_authenticationRealm.length() == 0) throw new SAXException( "No realm specified for BASIC Authorization" );
            } else if (authenticationMethod.equalsIgnoreCase( "FORM" )) {
                _useFormAuthentication = true;
                if (_authenticationRealm.length() == 0) throw new SAXException( "No realm specified for FORM Authorization" );
                _loginURL = new URL( "http", "localhost", getChildNodeValue( loginConfigElement, "form-login-page" ) );
                _errorURL = new URL( "http", "localhost", getChildNodeValue( loginConfigElement, "form-error-page" ) );
            }
        }
    }


    private void registerServlets( Document document ) throws SAXException {
        Hashtable nameToClass = new Hashtable();
        NodeList nl = document.getElementsByTagName( "servlet" );
        for (int i = 0; i < nl.getLength(); i++) registerServletClass( nameToClass, (Element) nl.item( i ) );
        nl = document.getElementsByTagName( "servlet-mapping" );
        for (int i = 0; i < nl.getLength(); i++) registerServlet( nameToClass, (Element) nl.item( i ) );
    }


    private void registerServletClass( Dictionary mapping, Element servletElement ) throws SAXException {
        mapping.put( getChildNodeValue( servletElement, "servlet-name" ),
                     new ServletConfiguration( servletElement ) );
    }


    private void registerServlet( Dictionary mapping, Element servletElement ) throws SAXException {
        registerServlet( getChildNodeValue( servletElement, "url-pattern" ),
                         (ServletConfiguration) mapping.get( getChildNodeValue( servletElement, "servlet-name" ) ) );
    }


    private void extractContextParameters( Document document ) throws SAXException {
        NodeList nl = document.getElementsByTagName( "context-param" );
        for (int i = 0; i < nl.getLength(); i++) {
            Element param = (Element) nl.item( i );
            String name = getChildNodeValue( param, "param-name" );
            String value = getChildNodeValue( param, "param-value" );
            _contextParameters.put( name, value );
        }
    }


    private static String getChildNodeValue( Element root, String childNodeName ) throws SAXException {
        return getChildNodeValue( root, childNodeName, null );
    }


    private static String getChildNodeValue( Element root, String childNodeName, String defaultValue ) throws SAXException {
        NodeList nl = root.getElementsByTagName( childNodeName );
        if (nl.getLength() == 1) {
            return getTextValue( nl.item( 0 ) ).trim();
        } else if (defaultValue == null) {
            throw new SAXException( "Node <" + root.getNodeName() + "> has no child named <" + childNodeName + ">" );
        } else {
            return defaultValue;
        }
    }


    private static String getTextValue( Node node ) throws SAXException {
        Node textNode = node.getFirstChild();
        if (textNode == null) return "";
        if (textNode.getNodeType() != Node.TEXT_NODE) throw new SAXException( "No text value found for <" + node.getNodeName() + "> node" );
        return textNode.getNodeValue();
    }


    private static boolean patternMatches( String urlPattern, String urlPath ) {
        return urlPattern.equals( urlPath );
    }


//============================================= SecurityCheckServlet class =============================================


    static class SecurityCheckServlet extends HttpServlet {

        protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
            handleLogin( (ServletUnitHttpRequest) req, resp );
        }


        protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
            handleLogin( (ServletUnitHttpRequest) req, resp );
        }


        private void handleLogin( ServletUnitHttpRequest req, HttpServletResponse resp ) throws IOException {
            final String username = req.getParameter( "j_username" );
            final String password = req.getParameter( "j_password" );
            req.writeFormAuthentication( username, password );
            resp.sendRedirect( req.getOriginalURL().toExternalForm() );
        }

    }
//============================================= ServletConfiguration class =============================================

    final static int DONT_AUTOLOAD = Integer.MIN_VALUE;
    final static int ANY_LOAD_ORDER = Integer.MAX_VALUE;

    class ServletConfiguration {

        private Servlet _servlet;
        private String _className;
        private Hashtable _initParams = new Hashtable();
        private int _loadOrder = DONT_AUTOLOAD;

        ServletConfiguration( String className ) {
            _className = className;
        }


        ServletConfiguration( String className, Hashtable initParams ) {
            _className = className;
            if (initParams != null) _initParams = initParams;
        }


        ServletConfiguration( Element servletElement ) throws SAXException {
            this( getChildNodeValue( servletElement, "servlet-class" ) );
            final NodeList initParams = servletElement.getElementsByTagName( "init-param" );
            for (int i = initParams.getLength() - 1; i >= 0; i--) {
                _initParams.put( getChildNodeValue( (Element) initParams.item( i ), "param-name" ),
                                 getChildNodeValue( (Element) initParams.item( i ), "param-value" ) );
            }
            final NodeList loadOrder = servletElement.getElementsByTagName( "load-on-startup" );
            for (int i = 0; i < loadOrder.getLength(); i++) {
                String order = getTextValue( loadOrder.item(i) );
                try {
                    _loadOrder = Integer.parseInt( order );
                } catch (NumberFormatException e) {
                    _loadOrder = ANY_LOAD_ORDER;
                }
            }
        }


        synchronized Servlet getServlet() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ServletException {
            if (_servlet == null) {
                Class servletClass = Class.forName( getClassName() );
                _servlet = (Servlet) servletClass.newInstance();
                _servlet.init( new ServletUnitServletConfig( _servlet, WebApplication.this, getInitParams() ) );
            }

            return _servlet;
        }


        synchronized void destroyServlet() {
            if (_servlet != null) _servlet.destroy();
        }


        String getClassName() {
            return _className;
        }


        Hashtable getInitParams() {
            return _initParams;
        }


        boolean isLoadOnStartup() {
            return _loadOrder != DONT_AUTOLOAD;
        }


        public int getLoadOrder() {
            return _loadOrder;
        }
    }


//=================================== SecurityConstract interface and implementations ==================================


    interface SecurityConstraint {

        boolean controlsPath( String urlPath );


        String[] getPermittedRoles();
    }


    static class NullSecurityConstraint implements SecurityConstraint {

        private static final String[] NO_ROLES = new String[0];


        public boolean controlsPath( String urlPath ) {
            return false;
        }


        public String[] getPermittedRoles() {
            return NO_ROLES;
        }
    }


    static class SecurityConstraintImpl implements SecurityConstraint {

        SecurityConstraintImpl( Element root ) throws SAXException {
            final NodeList roleNames = root.getElementsByTagName( "role-name" );
            for (int i = 0; i < roleNames.getLength(); i++) _roleList.add( getTextValue( roleNames.item( i ) ) );

            final NodeList resources = root.getElementsByTagName( "web-resource-collection" );
            for (int i = 0; i < resources.getLength(); i++) _resources.add( new WebResourceCollection( (Element) resources.item( i ) ) );
        }


        public boolean controlsPath( String urlPath ) {
            return getMatchingCollection( urlPath ) != null;
        }


        public String[] getPermittedRoles() {
            if (_roles == null) {
                _roles = (String[]) _roleList.toArray( new String[ _roleList.size() ] );
            }
            return _roles;
        }


        private String[]  _roles;
        private ArrayList _roleList = new ArrayList();
        private ArrayList _resources = new ArrayList();


        public WebResourceCollection getMatchingCollection( String urlPath ) {
            for (Iterator i = _resources.iterator(); i.hasNext();) {
                WebResourceCollection wrc = (WebResourceCollection) i.next();
                if (wrc.controlsPath( urlPath )) return wrc;
            }
            return null;
        }


        class WebResourceCollection {

            WebResourceCollection( Element root ) throws SAXException {
                final NodeList urlPatterns = root.getElementsByTagName( "url-pattern" );
                for (int i = 0; i < urlPatterns.getLength(); i++) _urlPatterns.add( getTextValue( urlPatterns.item( i ) ) );
            }


            boolean controlsPath( String urlPath ) {
                for (Iterator i = _urlPatterns.iterator(); i.hasNext();) {
                    String pattern = (String) i.next();
                    if (patternMatches( pattern, urlPath )) return true;
                }
                return false;
            }


            private ArrayList _urlPatterns = new ArrayList();
        }
    }


    static class ServletRequestImpl implements ServletMetaData {

        private URL            _url;
        private String         _servletName;
        private ServletMapping _mapping;


        ServletRequestImpl( URL url, String servletName, ServletMapping mapping ) {
            _url = url;
            _servletName = servletName;
            _mapping = mapping;
        }


        public Servlet getServlet() throws ServletException {
            if (getConfiguration() == null) throw new HttpNotFoundException( "No servlet mapping defined", _url );

            try {
                return getConfiguration().getServlet();
            } catch (ClassNotFoundException e) {
                throw new HttpNotFoundException( _url, e );
            } catch (IllegalAccessException e) {
                throw new HttpInternalErrorException( _url, e );
            } catch (InstantiationException e) {
                throw new HttpInternalErrorException( _url, e );
            } catch (ClassCastException e) {
                throw new HttpInternalErrorException( _url, e );
            }
        }


        public String getServletPath() {
            return _mapping == null ? null : _mapping.getServletPath( _servletName );
        }


        public String getPathInfo() {
            return _mapping == null ? null : _mapping.getPathInfo( _servletName );
        }


        private ServletConfiguration getConfiguration() {
            return _mapping == null ? null : _mapping.getConfiguration();
        }
    }


    static class ServletMapping {

        private ServletConfiguration _configuration;


        ServletConfiguration getConfiguration() {
            return _configuration;
        }


        ServletMapping( ServletConfiguration configuration ) {
            _configuration = configuration;
        }


        String getServletPath( String servletName ) {
            return servletName;
        }


        String getPathInfo( String servletName ) {
            return null;
        }


        public void destroyServlet() {
            getConfiguration().destroyServlet();
        }
    }


    static class PartialMatchServletMapping extends ServletMapping {

        private String _prefix;


        public PartialMatchServletMapping( ServletConfiguration configuration, String prefix ) {
            super( configuration );
            if (!prefix.endsWith( "/*" )) throw new IllegalArgumentException( prefix + " does not end with '/*'" );
            _prefix = prefix.substring( 0, prefix.length()-2 );
        }


        String getServletPath( String servletName ) {
            return _prefix;
        }


        String getPathInfo( String servletName ) {
            return servletName.length() > _prefix.length()
                    ? servletName.substring( _prefix.length() )
                    : null;
        }
    }


    /**
     * A utility class for mapping servlets to url patterns. This implements the
     * matching algorithm documented in section 10 of the JSDK-2.2 reference.
     */
    class ServletMap {

        private final Map _exactMatches = new HashMap();
        private final Map _extensions = new HashMap();
        private final Map _urlTree = new HashMap();
        private ServletMapping _defaultMapping;

        void put( String mapping, ServletConfiguration servletConfiguration ) {
            if (mapping.equals( "/" )) {
                _defaultMapping = new ServletMapping( servletConfiguration );
            } else if (mapping.startsWith( "*." )) {
                _extensions.put( mapping.substring( 2 ), new ServletMapping( servletConfiguration ) );
            } else if (!mapping.startsWith( "/" ) || !mapping.endsWith( "/*" )) {
                _exactMatches.put( mapping, new ServletMapping( servletConfiguration ) );
            } else {
                ParsedPath path = new ParsedPath( mapping );
                Map context = _urlTree;
                while (path.hasNext()) {
                    String part = path.next();
                    if (part.equals( "*" )) {
                        context.put( "*", new PartialMatchServletMapping( servletConfiguration, mapping ) );
                        return;
                    }
                    if (!context.containsKey( part )) {
                        context.put( part, new HashMap() );
                    }
                    context = (Map) context.get( part );
                }
            }
        }


        ServletMetaData get( URL url ) {
            String file = url.getFile();
            if (!file.startsWith( _contextPath )) throw new HttpNotFoundException( "File path does not begin with '" + _contextPath + "'", url );

            String servletName = getServletName( file.substring( _contextPath.length() ) );

            if (servletName.endsWith( "j_security_check" )) {
                return new ServletRequestImpl( url, servletName, SECURITY_CHECK_MAPPING );
            } else {
                return new ServletRequestImpl( url, servletName, getMapping( servletName ) );
            }
        }


        private String getServletName( String urlFile ) {
            if (urlFile.indexOf( '?' ) < 0) {
                return urlFile;
            } else {
                return urlFile.substring( 0, urlFile.indexOf( '?' ) );
            }
        }


        public void destroyServlets() {
            if (_defaultMapping != null) _defaultMapping.destroyServlet();
            destroyServlets( _exactMatches );
            destroyServlets( _extensions );
            destroyServlets( _urlTree );
        }


        private void destroyServlets( Map map ) {
            for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                if (o instanceof ServletMapping) {
                    ServletMapping servletMapping = (ServletMapping) o;
                    servletMapping.destroyServlet();
                } else {
                    destroyServlets( (Map) o );
                }
            }
        }


        void autoLoadServlets() {
            ArrayList autoLoadable = new ArrayList();
            if (_defaultMapping != null && _defaultMapping.getConfiguration().isLoadOnStartup()) autoLoadable.add( _defaultMapping.getConfiguration() );
            collectAutoLoadableServlets( autoLoadable, _exactMatches );
            collectAutoLoadableServlets( autoLoadable, _extensions );
            collectAutoLoadableServlets( autoLoadable, _urlTree );
            if (autoLoadable.isEmpty()) return;

            Collections.sort( autoLoadable, new Comparator() {
                public int compare( Object o1, Object o2 ) {
                    ServletConfiguration sc1 = (ServletConfiguration) o1;
                    ServletConfiguration sc2 = (ServletConfiguration) o2;
                    return (sc1.getLoadOrder() <= sc2.getLoadOrder()) ? -1 : +1;
                }
            });
            for (Iterator iterator = autoLoadable.iterator(); iterator.hasNext();) {
                ServletConfiguration servletConfiguration = (ServletConfiguration) iterator.next();
                try {
                    servletConfiguration.getServlet();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException( "Unable to autoload servlet: " + servletConfiguration.getClassName() + ": " + e );
                }
            }
        }


        private void collectAutoLoadableServlets( Collection collection, Map map ) {
            for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                if (o instanceof ServletMapping) {
                    ServletMapping servletMapping = (ServletMapping) o;
                    if (servletMapping.getConfiguration().isLoadOnStartup()) collection.add( servletMapping.getConfiguration() );
                } else {
                    collectAutoLoadableServlets( collection, (Map) o );
                }
            }
        }


        private ServletMapping getMapping( String url ) {
            if (_exactMatches.containsKey( url )) return (ServletMapping) _exactMatches.get( url );

            Map context = getContextForLongestPathPrefix( url );
            if (context.containsKey( "*" )) return (ServletMapping) context.get( "*" );

            if (_extensions.containsKey( getExtension( url ))) return (ServletMapping) _extensions.get( getExtension( url ) );

            if (_urlTree.containsKey( "/" )) return (ServletMapping) _urlTree.get( "/" );

            if (_defaultMapping != null) return _defaultMapping;

            final String prefix = "/servlet/";
            if (!url.startsWith( prefix )) return null;

            String className = url.substring( prefix.length() );
            try {
                Class.forName( className );
                return new ServletMapping( new ServletConfiguration( className ) );
            } catch (ClassNotFoundException e) {
                return null;
            }
        }


        private Map getContextForLongestPathPrefix( String url ) {
            Map context = _urlTree;

            ParsedPath path = new ParsedPath( url );
            while (path.hasNext()) {
                String part = path.next();
                if (!context.containsKey( part )) break;
                context = (Map) context.get( part );
            }
            return context;
        }


        private String getExtension( String url ) {
            int index = url.lastIndexOf( '.' );
            if (index == -1 || index >= url.length() - 1) {
                return "";
            } else {
                return url.substring( index + 1 );
            }
        }

    }

}


/**
 * A utility class for parsing URLs into paths
 *
 * @author <a href="balld@webslingerZ.com">Donald Ball</a>
 */
class ParsedPath {

    private final String path;
    private int position = 0;
    static final char seperator_char = '/';


    /**
     * Creates a new parsed path for the given path value
     *
     * @param path the path
     */
    ParsedPath( String path ) {
        if (path.charAt( 0 ) != seperator_char) {
            throw new IllegalArgumentException( "Illegal path '" + path + "', does not begin with " + seperator_char );
        }
        this.path = path;
    }


    /**
     * Returns true if there are more parts left, otherwise false
     */
    public final boolean hasNext() {
        return (position < path.length());
    }


    /**
     * Returns the next part in the path
     */
    public final String next() {
        int offset = position + 1;
        while (offset < path.length() && path.charAt( offset ) != seperator_char) {
            offset++;
        }
        String result = path.substring( position + 1, offset );
        position = offset;
        return result;
    }

}
