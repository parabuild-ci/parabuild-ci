package com.meterware.servletunit;
/********************************************************************************************************************
* $Id: ServletUnitContext.java,v 1.5 2003/03/17 01:35:44 russgold Exp $
*
* Copyright (c) 2000-2003, Russell Gold
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


import java.util.*;
import java.util.Hashtable;

class ServletUnitContext {

    ServletUnitContext() {
        this( null );
    }

    ServletUnitContext( String contextPath ) {
        _contextPath = (contextPath != null ? contextPath : "");
    }


	Set getSessionIDs() {
		return _sessions.keySet();
	}
	
    /**
     * Returns the session with the specified ID, if any.
     **/
    ServletUnitHttpSession getSession( String id ) {
        return (ServletUnitHttpSession) _sessions.get( id );
    }


    /**
     * Creates a new session with a unique ID.
     **/
    ServletUnitHttpSession newSession() {
        ServletUnitHttpSession result = new ServletUnitHttpSession();
        _sessions.put( result.getId(), result );
        return result;
    }

    /**
     * Returns the contextPath
     */
    String getContextPath() {
        return _contextPath;
    }


//------------------------------- private members ---------------------------


    private Hashtable _sessions = new Hashtable();

    private String _contextPath = null;


}
