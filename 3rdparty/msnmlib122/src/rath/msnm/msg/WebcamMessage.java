/*
 * @(#)FileTransferMessage.java
 *
 * Copyright (c) 2001-2002, JangHo Hwang
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
 *    $Id: WebcamMessage.java,v 1.2 2004/12/28 19:19:03 xrath Exp $
 */
package rath.msnm.msg;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import rath.msnm.SwitchboardSession;

/**
 * @author Jerome Saada jchatirc@free.fr
 * @version $Id: WebcamMessage.java,v 1.2 2004/12/28 19:19:03 xrath Exp $
 */
public class WebcamMessage extends MimeMessage
{
	private static final Random random = new Random(System.currentTimeMillis());
	private static String host = null;

	private WebcamMessage()
	{
		setKind( KIND_FILE_TRANSFER );
	}
	
	public static WebcamMessage createInviteMessage(SwitchboardSession session,String host,int port)
	{
		
		int cookie = random.nextInt( 999998 ) + 1;
		
		WebcamMessage msg = new WebcamMessage();
		msg.setProperty( "Application-Name", "viewing webcam");
		msg.setProperty( "Application-GUID", "2A23868E-B45F-401d-B8B0-1E16B774A5B7" );
		msg.setProperty( "Session-Protocol","SM1");
		msg.setProperty( "Invitation-Command", "INVITE" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie) );
		msg.setProperty( "Session-ID",session.getSessionId() );
		msg.setProperty( "Port", String.valueOf(port));
		msg.setProperty( "IP", host );
		return msg;
	}
	
	public static WebcamMessage createAcceptMessage(SwitchboardSession session,int cookie ,String host,String port )
	{
		if( cookie < 1 )
			throw new IllegalArgumentException( "cookie must larger than 0" );
		
		WebcamMessage msg = new WebcamMessage();
		msg.setProperty( "Launch-Application", "TRUE" );
		msg.setProperty( "IP-Address", host );
		msg.setProperty( "Session-Protocol","SM1");
		msg.setProperty( "Request-Data", "IP-Address:" );
		msg.setProperty( "Invitation-Command", "ACCEPT" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie));
		msg.setProperty( "Session-ID",session.getSessionId());
		msg.setProperty( "Port", port );
		return msg;
	}

	public static WebcamMessage createRejectMessage(SwitchboardSession session,int cookie )
	{
		if( cookie < 1 )
			throw new IllegalArgumentException( "cookie must larger than 0" );
		
		WebcamMessage msg = new WebcamMessage();
		msg.setProperty( "Session-Protocol","SM1");
		msg.setProperty( "Cancel-Code","REJECT");
		msg.setProperty( "Invitation-Command", "CANCEL" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie));
		msg.setProperty( "Session-ID",session.getSessionId());	
		return msg;
	}
	public byte[] getBytes() throws UnsupportedEncodingException
	{
		StringBuffer buf = createMimeHeader( STR_FILE_TRANSFER + "; charset=UTF-8" );
		buf.append( "\r\n" );
		fillMimeProperties( buf );
		buf.append( "\r\n" );
		String msg = buf.toString();
		return msg.getBytes("UTF-8");
	}
	
	public static String getIP() throws UnknownHostException{
		InetAddress localaddr = InetAddress.getLocalHost () ;				
		InetAddress[] localaddrs = InetAddress.getAllByName(localaddr.getHostName () ) ;
		for ( int i=0 ; i<localaddrs.length ; i++ ) {
			if ( ! localaddrs[ i ].equals( localaddr ) ){
				String host=localaddrs[i].getHostAddress ();
				return host;
			}
		}
		return null;
	}
	
	public static String getIP2(){
		try {
            return host= InetAddress.getLocalHost().getHostAddress();	
		} catch( UnknownHostException e) {
			return null;
		}
	}
	
	public static String getIP3(){
		try {
			InetAddress addr = InetAddress.getLocalHost();
			byte[] ipAddr = addr.getAddress();
			addr = InetAddress.getByName(addr.getHostName());
			ipAddr = addr.getAddress();
	    
	        String ipAddrStr = "";
	        for (int i=0; i<ipAddr.length; i++) {
	            if (i > 0) {
	                ipAddrStr += ".";
	            }
	            ipAddrStr += ipAddr[i]&0xFF;
	        }
	        return ipAddrStr;
	    } catch (UnknownHostException e) {
	    	
	    }
	    return null;
	}
	

  public static void main (String[] args)
	{
		try	{
			System.err.println ("host:" +	getIP());	
			System.err.println ("host:" +	getIP2());
			System.err.println ("host:" +	getIP3());
		}
		catch ( UnknownHostException e ) {
			System.err.println ( "Can't detect localhost : " + e) ;
		}
		
	} 

}
