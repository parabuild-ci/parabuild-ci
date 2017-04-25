package com.meterware.servletunit;
/********************************************************************************************************************
 * $Id: WebXMLString.java,v 1.3 2003/02/19 19:18:00 russgold Exp $
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
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;


/**
 * A class which allows dynamic creation of Servlet configuration XML
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
class WebXMLString {

    private ArrayList _servlets = new ArrayList();
    private ArrayList _mappings = new ArrayList();
    private ArrayList _servletNames = new ArrayList();

    private String _loginConfig = "";
    private Hashtable _resources = new Hashtable();
    private Hashtable _initParams = new Hashtable();
    private Hashtable _contextParams = new Hashtable();
    private Hashtable _loadOnStartup = new Hashtable();

    ByteArrayInputStream asInputStream() throws UnsupportedEncodingException {
        return new ByteArrayInputStream( asText().getBytes( "UTF-8" ) );
    }


    String asText() {
        StringBuffer result = new StringBuffer( "<?xml version='1.0' encoding='UTF-8'?>\n<web-app>\n" );

        for (Iterator i = _contextParams.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            result.append( "  <context-param>\n    <param-name>" ).append( entry.getKey() );
            result.append( "</param-name>\n    <param-value>" ).append( entry.getValue() ).append( "</param-value>\n  </context-param>\n" );
        }
        for (int i = _servlets.size() - 1; i >= 0; i--) {
            Object name = _servletNames.get( i );
            result.append( "  <servlet>\n    <servlet-name>" ).append( name ).append( "</servlet-name>\n" );
            result.append( "    <servlet-class>" ).append( ((Class) _servlets.get( i )).getName() ).append( "</servlet-class>\n" );
            appendParams( result, "init-param", (Hashtable) _initParams.get( name ) );
            appendLoadOnStartup( result, _loadOnStartup.get( name ) );
            result.append( "  </servlet>\n" );
        }
        for (int i = _mappings.size() - 1; i >= 0; i--) {
            result.append( "  <servlet-mapping>\n    <servlet-name>" ).append( _servletNames.get( i ) ).append( "</servlet-name>\n" );
            result.append( "    <url-pattern>" ).append( _mappings.get( i ) ).append( "</url-pattern>\n  </servlet-mapping>\n" );
        }
        for (Enumeration e = _resources.elements(); e.hasMoreElements();) {
            result.append( ((WebResourceSpec) e.nextElement()).asText() );
        }
        result.append( _loginConfig );
        result.append( "</web-app>" );
        return result.toString();
    }


    private void appendLoadOnStartup( StringBuffer result, Object startupOrder ) {
        if (startupOrder == null) return;
        result.append( "    <load-on-startup" );
        if (startupOrder instanceof Number) result.append( ">" ).append( startupOrder ).append( "</load-on-startup>\n" );
        else result.append( "/>\n" );
    }


    private void appendParams( StringBuffer result, String tagName, Hashtable params ) {
        if (params == null) return;
        for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.append( "    <" ).append( tagName ).append( ">\n      <param-name>" ).append( entry.getKey() );
            result.append( "</param-name>\n      <param-value>" ).append( entry.getValue() ).append( "</param-value>\n    </" );
            result.append( tagName ).append( ">\n" );
        }
    }


    void addContextParam( String name, String value ) {
        _contextParams.put( name, value );
    }


    void addServlet( String urlPattern, Class servletClass ) {
        addServlet( "servlet_" + _servlets.size(), urlPattern, servletClass );
    }


    void addServlet( String name, String urlPattern, Class servletClass ) {
        _servlets.add( servletClass );
        _mappings.add( urlPattern );
        _servletNames.add( name );
    }


    void addServlet( String name, String urlPattern, Class servletClass, Properties initParams ) {
        _initParams.put( name, initParams );
        addServlet( name, urlPattern, servletClass );
    }


    void setLoadOnStartup( String servletName ) {
        _loadOnStartup.put( servletName, Boolean.TRUE );
    }


    void setLoadOnStartup( String servletName, int i ) {
        _loadOnStartup.put( servletName, new Integer(i) );
    }


    void requireBasicAuthorization( String realmName ) {
        _loginConfig = "  <login-config>\n" +
                "    <auth-method>BASIC</auth-method>\n" +
                "    <realm-name>" + realmName + "</realm-name>\n" +
                "  </login-config>\n";
    }


    void requireBasicAuthentication( String realmName ) {
        _loginConfig = "  <login-config>\n" +
                "    <auth-method>BASIC</auth-method>\n" +
                "    <realm-name>" + realmName + "</realm-name>\n" +
                "  </login-config>\n";
    }


    void requireFormAuthentication( String realmName, String loginPagePath, String errorPagePath ) {
        _loginConfig = "  <login-config>\n" +
                "    <auth-method>FORM</auth-method>\n" +
                "    <realm-name>" + realmName + "</realm-name>\n" +
                "    <form-login-config>" +
                "      <form-login-page>" + loginPagePath + "</form-login-page>\n" +
                "      <form-error-page>" + errorPagePath + "</form-error-page>\n" +
                "    </form-login-config>" +
                "  </login-config>\n";
    }


    void addSecureURL( String resourceName, String urlPattern ) {
        getWebResource( resourceName ).addURLPattern( urlPattern );
    }


    void addAuthorizedRole( String resourceName, String roleName ) {
        getWebResource( resourceName ).addAuthorizedRole( roleName );
    }


    private WebResourceSpec getWebResource( String resourceName ) {
        WebResourceSpec result = (WebResourceSpec) _resources.get( resourceName );
        if (result == null) {
            result = new WebResourceSpec( resourceName );
            _resources.put( resourceName, result );
        }
        return result;
    }
}


class WebResourceSpec {

    WebResourceSpec( String name ) {
        _name = name;
    }


    void addURLPattern( String urlPattern ) {
        _urls.add( urlPattern );
    }


    void addAuthorizedRole( String roleName ) {
        _roles.add( roleName );
    }


    String asText() {
        StringBuffer sb = new StringBuffer();
        sb.append( "  <security-constraint>\n" );
        sb.append( "    <web-resource-collection>\n" );
        sb.append( "      <web-resource-name>" ).append( _name ).append( "</web-resource-name>\n" );
        for (Iterator i = _urls.iterator(); i.hasNext();) {
            sb.append( "      <url-pattern>" ).append( i.next() ).append( "</url-pattern>\n" );
        }
        sb.append( "    </web-resource-collection>\n" );
        sb.append( "    <auth-constraint>\n" );
        for (Iterator i = _roles.iterator(); i.hasNext();) {
            sb.append( "      <role-name>" ).append( i.next() ).append( "</role-name>\n" );
        }
        sb.append( "    </auth-constraint>\n" );
        sb.append( "  </security-constraint>\n" );
        return sb.toString();
    }


    private String _name;
    private ArrayList _urls = new ArrayList();
    private ArrayList _roles = new ArrayList();
}