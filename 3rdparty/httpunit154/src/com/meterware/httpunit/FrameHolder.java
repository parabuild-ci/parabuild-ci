package com.meterware.httpunit;
/********************************************************************************************************************
 * $Id: FrameHolder.java,v 1.8 2003/02/27 23:35:58 russgold Exp $
 *
 * Copyright (c) 2002-2003, Russell Gold
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import org.xml.sax.SAXException;


/**
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
class FrameHolder {

    private Hashtable   _contents = new Hashtable();
    private Hashtable   _subFrames = new Hashtable();
    private String      _frameName;


    FrameHolder( WebClient client, String name ) {
        _frameName = name;
        DefaultWebResponse blankResponse = new DefaultWebResponse( client, null, WebResponse.BLANK_HTML );
        _contents.put( WebRequest.TOP_FRAME, blankResponse );
        HttpUnitOptions.getScriptingEngine().associate( blankResponse );
    }


    WebResponse get( String target ) {
        final WebResponse response = (WebResponse) _contents.get( getFrameName( target ) );
        if (response != null) return response;
        for (Iterator iterator = _contents.keySet().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            if (name.endsWith( ':' + target )) return (WebResponse) _contents.get( name );
        }
        return null;
    }


    List getActiveFrameNames() {
        List result = new ArrayList();
        for (Enumeration e = _contents.keys(); e.hasMoreElements();) {
            result.add( e.nextElement() );
        }

        return result;
    }


    String getTargetFrame( WebRequest request ) {
        if (WebRequest.NEW_WINDOW.equalsIgnoreCase( request.getTarget() )) {
            return WebRequest.TOP_FRAME;
        } else if (WebRequest.TOP_FRAME.equalsIgnoreCase( request.getTarget() )) {
            return WebRequest.TOP_FRAME;
        } else {
            final String computedTarget = getTargetFrameName( request.getSourceFrame(), request.getTarget() );
            if (_contents.get( computedTarget ) != null) return computedTarget;
            for (Iterator iterator = _contents.keySet().iterator(); iterator.hasNext();) {
                String name = (String) iterator.next();
                if (name.endsWith( ':' + request.getTarget() )) return name;
            }
            return request.getTarget();
        }
    }


    String getFrameName( String target ) {
        if (WebRequest.TOP_FRAME.equalsIgnoreCase( target )) {
            return _frameName;
        } else if (WebRequest.NEW_WINDOW.equalsIgnoreCase( target )) {
            return _frameName;
        } else {
            return target;
        }
    }


    void updateFrames( WebResponse response, String target, RequestContext requestContext ) throws MalformedURLException, IOException, SAXException {
        removeSubFrames( target );
        _contents.put( target, response );

        if (response.isHTML()) {
            if (!response.hasSubframes()) {
                requestContext.addNewResponse( response );
            } else {
                HttpUnitOptions.getScriptingEngine().associate( response );
                createSubFrames( target, response.getFrameNames() );
                WebRequest[] requests = response.getFrameRequests();
                for (int i = 0; i < requests.length; i++) response.getWindow().getSubframeResponse( requests[ i ], requestContext );
            }
        }
    }


    private void removeSubFrames( String targetName ) {
        String[] names = (String[]) _subFrames.get( targetName );
        if (names == null) return;
        for (int i = 0; i < names.length; i++) {
            removeSubFrames( names[ i ] );
            _contents.remove( names[ i ] );
            _subFrames.remove( names[ i ] );
        }
    }


    private void createSubFrames( String targetName, String[] frameNames ) {
        _subFrames.put( targetName, frameNames );
        for (int i = 0; i < frameNames.length; i++) {
            _contents.put( frameNames[ i ], WebResponse.BLANK_RESPONSE );
        }
    }

    /**
     * Returns the qualified name of a target frame.
     */
    private static String getTargetFrameName( String sourceFrameName, final String relativeName ) {
        if (relativeName.equalsIgnoreCase( WebRequest.TOP_FRAME )) return WebRequest.TOP_FRAME;
        if (sourceFrameName.indexOf( ':' ) < 0) return relativeName;
        return WebFrame.getParentFrameName( sourceFrameName ) + ':' + relativeName;
    }

}

