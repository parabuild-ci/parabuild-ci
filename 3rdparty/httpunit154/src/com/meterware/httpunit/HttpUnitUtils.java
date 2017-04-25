package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: HttpUnitUtils.java,v 1.13 2003/05/23 03:24:49 russgold Exp $
*
* Copyright (c) 2000-2002, Russell Gold
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
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Utility code shared by httpunit and servletunit.
 **/
public class HttpUnitUtils {

    public static final int DEFAULT_TEXT_BUFFER_SIZE = 2048;
    public static final int DEFAULT_BUFFER_SIZE = 128;
    public static final String DEFAULT_CHARACTER_SET = "iso-8859-1";


    /**
     * Returns the content type and encoding as a pair of strings.
     * If no character set is specified, the second entry will be null.
     **/
    public static String[] parseContentTypeHeader( String header ) {
        String[] result = new String[] { "text/plain", null };
        StringTokenizer st = new StringTokenizer( header, ";=" );
        result[0] = st.nextToken();
        while (st.hasMoreTokens()) {
            String parameter = st.nextToken();
            if (st.hasMoreTokens()) {
                String value = stripQuotes( st.nextToken() );
                if (parameter.trim().equalsIgnoreCase( "charset" )) result[1] = value;
            }
        }
        return result;
    }


    public static String stripQuotes( String value ) {
        if (value.startsWith( "'" ) || value.startsWith( "\"" )) value = value.substring( 1 );
        if (value.endsWith( "'" ) || value.endsWith( "\"" )) value = value.substring( 0, value.length()-1 );
        return value;
    }

    /**
     * Returns an interpretation of the specified URL-encoded string.
     * FIXME: currently assumes iso-8859-1 character set.
     **/
    public static String decode( String byteString ) {
        char[] chars = byteString.toCharArray();
        StringBuffer sb = new StringBuffer(chars.length);
        char[] hexNum = { '0', '0', '0' };

        int i = 0;
        while (i < chars.length) {
            if (chars[i] == '+') {
                i++;
                sb.append( ' ' );
            } else if (chars[i] == '%') {
                i++;
                hexNum[1] = chars[i++];
                hexNum[2] = chars[i++];
                sb.append( (char) Integer.parseInt( new String( hexNum ), 16 ) );
            } else {
                sb.append( chars[i++] );
            }
        }
        return sb.toString();
    }


    /**
     * creates a parser using JAXP API.
     */
    public static DocumentBuilder newParser() throws SAXException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            // redirect the new exception for code compatibility
            throw new SAXException( ex );
        }
    }


    /**
     * Returns a string array created by appending a string to an existing array. The existing array may be null.
     **/
    static String[] withNewValue( String[] oldValue, String newValue ) {
        String[] result;
        if (oldValue == null) {
            result = new String[] { newValue };
        } else {
            result = new String[ oldValue.length+1 ];
            System.arraycopy( oldValue, 0, result, 0, oldValue.length );
            result[ oldValue.length ] = newValue;
        }
        return result;
    }


    /**
     * Returns a string array created by appending an object to an existing array. The existing array may be null.
     **/
    static Object[] withNewValue( Object[] oldValue, Object newValue ) {
        Object[] result;
        if (oldValue == null) {
            result = new Object[] { newValue };
        } else {
            result = new Object[ oldValue.length+1 ];
            System.arraycopy( oldValue, 0, result, 0, oldValue.length );
            result[ oldValue.length ] = newValue;
        }
        return result;
    }


    /**
     * Return true if the first string contains the second.
     * Case sensitivity is according to the setting of HttpUnitOptions.matchesIgnoreCase
     */
    static boolean contains( String string, String substring ) {
        if (HttpUnitOptions.getMatchesIgnoreCase()) {
            return string.toUpperCase().indexOf( substring.toUpperCase() ) >= 0;
        } else {
            return string.indexOf( substring ) >= 0;
        }
    }


    /**
     * Return true if the first string starts with the second.
     * Case sensitivity is according to the setting of HttpUnitOptions.matchesIgnoreCase
     */
    static boolean hasPrefix( String string, String prefix ) {
        if (HttpUnitOptions.getMatchesIgnoreCase()) {
            return string.toUpperCase().startsWith( prefix.toUpperCase() );
        } else {
            return string.startsWith( prefix );
        }
    }


    /**
     * Return true if the first string equals the second.
     * Case sensitivity is according to the setting of HttpUnitOptions.matchesIgnoreCase
     */
    static boolean matches( String string1, String string2 ) {
        if (HttpUnitOptions.getMatchesIgnoreCase()) {
            return string1.equalsIgnoreCase( string2 );
        } else {
            return string1.equals( string2 );
        }
    }


    static boolean isJavaScriptURL( String urlString ) {
        return urlString.toLowerCase().startsWith( "javascript:" );
    }
}