/*
 * @(#)TWN.java
 *
 * Copyright (c) 2001-2003, JangHo Hwang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	3. Neither the name of the JangHo Hwang nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *    $Id: TWN.java,v 1.7 2005/05/19 23:43:15 xrath Exp $
 */
package rath.msnm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.util.*;

//import javax.net.ssl.HttpsURLConnection;
/**
 * SSL based Passport Login class :)
 * <p>
 * TWN class is execute on JDK 1.4 or later for SSL class(javax.net.ssl.HttpsURLConnection).
 * 
 * @author Jang-Ho Hwang, imrath@empal.com
 * @version 1.0.000, 2003/10/16 
 */
public class TWN
{
    static
    {
        java.security.Security.addProvider(
            new com.sun.net.ssl.internal.ssl.Provider());
        System.setProperty( "java.protocol.handler.pkgs", 
            "com.sun.net.ssl.internal.www.protocol" );
    }

	public TWN()
	{

	}

	private static String getContent( InputStream in, int len ) throws IOException
	{
		byte[] buf = new byte[ 32767 ];

		int off = 0;
		while( off < buf.length )
		{
			int readlen = in.read(buf, off, buf.length-off);
			if( readlen<1 )
				break;
			off += readlen;
		}
		in.close();

		return new String(buf, 0, off);
	}

	public static String getTNP( String userid, String password, String tpf )
		throws IOException
	{
		return getTNPImpl(userid, password, tpf, 1);
	}

	private static String getTNPImpl( String userid, String password, String tpf, int count )
		throws IOException
	{
/*
		String domain = "login.passport.com";
		if( userid.endsWith("@hotmail.com") )
			domain = "loginnet.passport.com";
		else
		if( userid.endsWith("@msn.com") )
			domain = "msnialogin.passport.com";
*/
		String domain = "loginnet.passport.com";

		URL url0 = new URL(
			"https://" + domain + "/login2.srf");
		
		HttpURLConnection con0 = (HttpURLConnection)url0.openConnection();
		con0.setRequestMethod( "GET" );
		con0.setUseCaches( false );
		con0.setDoInput( true );

		con0.setRequestProperty( "Host", domain );
		String author = 
			"Passport1.4 OrgVerb=GET,OrgURL=http://messenger.msn.com," + 
			"sign-in=" + URLEncoder.encode(userid, "EUC-KR") + ",pwd=" + 
			password + "," + tpf;
		con0.setRequestProperty( "Authorization", author );

		String ret = getContent( con0.getInputStream(), con0.getContentLength() );		
		con0.disconnect();

		String auth = con0.getHeaderField("Authentication-Info");
		if( auth==null )
			return "t=0&p=0";

		String da_status = getValueFromKey(auth, "da-status"); // success, failed, redir
		String fromPP = getValueFromKey(auth, "from-PP");
		return fromPP;
	}

	private static String getValueFromKey( String str, String key )
	{
		int i0 = str.indexOf(key);
		if( i0==-1 )
			return null;
		int i1 = str.indexOf(',', i0+1);
		if( i1==-1 )
			i1 = str.length();
		int i2 = str.indexOf('=', i0+1);

		String ret = str.substring(i2+1, i1);
		ret = ret.replaceAll("'", "");
		return ret;
	}
}
