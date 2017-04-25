/*
 * @(#)IncomingMessage.java
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
 *    $Id: IncomingMessage.java,v 1.4 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.msg;

import java.util.HashMap;

import rath.msnm.entity.ServerInfo;
/**
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: IncomingMessage.java,v 1.4 2004/12/24 22:05:53 xrath Exp $ 
 */
public class IncomingMessage extends Message
{
	/*
	 * NON-TR Headers from server
	 *
	 * NLN Substate UserHandle FriendlyName (Online)
	 * FLN UserHandle (Offline)
	 * RNG SessionID SSAddr SP AuthChallengeInfo CalUserHandle CalFriendName 
	 * OUT {StatusCode} (Get out)
	 */
	private static HashMap PUSH_HEADERS = new HashMap();
	static
	{
		PUSH_HEADERS.put( "MSG", "MSG" );
		PUSH_HEADERS.put( "NLN", "NLN" );
		PUSH_HEADERS.put( "FLN", "FLN" );
		PUSH_HEADERS.put( "RNG", "RNG" );
		PUSH_HEADERS.put( "JOI", "JOI" );
		PUSH_HEADERS.put( "BYE", "BYE" );
		PUSH_HEADERS.put( "OUT", "OUT" );
		PUSH_HEADERS.put( "BPR", "BPR" );
		PUSH_HEADERS.put( "LSG", "LSG" );
		PUSH_HEADERS.put( "LST", "LST" );
	}

	private boolean isBodyless = false;

	private IncomingMessage( String header )
	{
		super( header );
	}

	private void fill( String str )
	{
		int len = str.length();
		int preoff = 0;
		int offset = 0;
		while( (offset=str.indexOf(' ',preoff))!=-1 )
		{
			list.add( str.substring(preoff, offset) );

			preoff = offset + 1;
			if( preoff >= len )
				break;
		}
		if( preoff < len )
			list.add( str.substring(preoff) );		
	}

	public static IncomingMessage getInstance( String line )
	{
		if( line.length()<=3 )
		{
			IncomingMessage msg = new IncomingMessage(line);
			msg.isBodyless = true;
			return msg;
		}

		String header = line.substring(0, 3);
		IncomingMessage msg = new IncomingMessage(header);

		if( PUSH_HEADERS.containsKey(header) )
		{
			// No transaction id
			msg.isBodyless = true;
			msg.fill( line.substring(4) );
		}
		else
		{
			// Have transaction id
			int i0 = line.indexOf( ' ', 4 );
			if( i0==-1 )
			{
				msg.setTransactionId( Integer.parseInt(line.substring(4)) );
			}
			else
			{
				msg.setTransactionId( Integer.parseInt(line.substring(4,i0)) );
				msg.fill( line.substring(i0+1) );
			}
		}

		return msg;
	}

	/**
	 * 주어진 index의 hostname:port 형식의 파라미터를 읽어들여서 
	 * parsing하여 ServerInfo 객체를 생성하여 반환해준다.
	 */
	public ServerInfo getServerInfo( int index )
	{
		String str = list.get(index);
		if( str==null )
			return null;

		int i0 = str.indexOf( ':' );
		String host = str.substring( 0, i0 );
		int port = Integer.parseInt( str.substring( i0+1 ) ); 

		return new ServerInfo( host, port );
	}

	/**
	 * 도착한 메시지가 trId가 없는 notify성 메시지인지 
	 * 확인한다.
	 */
	public boolean isNotify()
	{	
		return this.isBodyless;
	}
}
