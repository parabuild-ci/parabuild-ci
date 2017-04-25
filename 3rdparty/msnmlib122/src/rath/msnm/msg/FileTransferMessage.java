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
 *    $Id: FileTransferMessage.java,v 1.6 2004/12/24 22:05:53 xrath Exp $
 */
package rath.msnm.msg;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * 파일전송에 관련한 MIME 메시지들을 손쉽게 생성하고, 다룰 수 있게해주는
 * 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: FileTransferMessage.java,v 1.6 2004/12/24 22:05:53 xrath Exp $
 */
public class FileTransferMessage extends MimeMessage
{
	private static final Random random = new Random(System.currentTimeMillis());

	private FileTransferMessage()
	{
		setKind( KIND_FILE_TRANSFER );
	}

	/**
	 * 파일 송신 요청 메시지를 생성한다. 송신측에서 생성하는 메시지이다.
	 * <p>
	 * 임의로 생성된 쿠키값을 알고자 한다면, 생성된 파일전송메시지 객체에
	 * getProperty("Invitation-Cookie") 를 invoke하여 알아볼 수 있다.
	 *
	 * @param  toSend  전송하고자 하는 파일
	 */
	public synchronized static FileTransferMessage createInviteMessage( File toSend )
	{
		int cookie = random.nextInt( 999998 ) + 1;
		return createInviteMessage( toSend, cookie );
	}

	/**
	 * 파일 송신 요청 메시지를 생성한다. 송신측에서 생성하는 메시지이다.
	 *
	 * @param  toSend  전송하고자 하는 파일
	 * @param  cookie  초대 쿠키값. 0보다 큰 숫자여야만 한다.
	 */
	public static FileTransferMessage createInviteMessage( File toSend,
		int cookie )
	{
		if( cookie < 1 )
			throw new IllegalArgumentException( "cookie must larger than 0" );
		if( !toSend.exists() )
			throw new IllegalArgumentException( toSend.getName() + " not found" );

		FileTransferMessage msg = new FileTransferMessage();
		msg.setProperty( "Application-Name", "파일 전송" );
		msg.setProperty( "Application-GUID", "{5D3E02AB-6190-11d3-BBBB-00C04F795683}" );
		msg.setProperty( "Invitation-Command", "INVITE" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie) );
		msg.setProperty( "Application-File", toSend.getName() );
		msg.setProperty( "Application-FileSize", String.valueOf(toSend.length()) );
		return msg;
	}

	/**
	 * 파일 수신을 허락하는 메시지를 생성한다. 수신측에서 생성하는 메시지이다.
	 *
	 * @param  cookie  송신자로부터 받은 쿠키값.
	 */
	public static FileTransferMessage createAcceptMessage( int cookie )
	{
		FileTransferMessage msg = new FileTransferMessage();
		msg.setProperty( "Invitation-Command", "ACCEPT" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie) );
		msg.setProperty( "Launch-Application", "FALSE" );
		msg.setProperty( "Request-Data", "IP-Address:" );
		return msg;
	}

	/**
	 * 파일 수신을 거부하는 메시지를 생성한다. 수신측에서 생성하는 메시지이다.
	 *
	 * @param  cookie  송신자로부터 받은 쿠키값.
	 */
	public static FileTransferMessage createRejectMessage( int cookie )
	{
		FileTransferMessage msg = new FileTransferMessage();
		msg.setProperty( "Invitation-Command", "CANCEL" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie) );
		msg.setProperty( "Cancel-Code", "REJECT" );
		return msg;
	}

	/**
	 * 서로간의 파일 송/수신 허락이 떨어졌으면 송신측에서 파일을 전송할
	 * 자신 서버의 정보를 보내는 메시지이다. <br>
	 * 생성될때 임의의 인증쿠키를 하나 더 생성하는데, 이 값은 AuthCookie 라는
	 * key로 생성되므로, getProperty 메소드를 통해 반드시 확인하도록 한다.
	 * <p>
	 * 이 메시지를 수신하면 곧바로 수신측에서 접속하게 되므로 bind를 한 후에
	 * 전송해야만 한다.
	 * <p>
	 * 또한 자신의 ip는 InetAddress.getLocalHost()를 사용하여 얻어낸다.<br>
	 * port 번호는 기본값인 6891을 새용하게 된다.
	 * <p>
	 * 만약 해당 포트번호가 사용중이라면, 6891~7000사이의 다른 포트번호를 사용한다.
	 *
	 * @param  cookie  초기 전송하였던 쿠키값.
	 */
	public static FileTransferMessage createTransferMessage( int cookie )
	{
		return createTransferMessage( cookie, 6891 );
	}

	/**
	 * 서로간의 파일 송/수신 허락이 떨어졌으면 송신측에서 파일을 전송할
	 * 자신 서버의 정보를 보내는 메시지이다. <br>
	 * 생성될때 임의의 인증쿠키를 하나 더 생성하는데, 이 값은 AuthCookie 라는
	 * key로 생성되므로, getProperty 메소드를 통해 반드시 확인하도록 한다.
	 * <p>
	 * 이 메시지를 수신하면 곧바로 수신측에서 접속하게 되므로 bind를 한 후에
	 * 전송해야만 한다.
	 * <p>
	 * 또한 자신의 ip는 InetAddress.getLocalHost()를 사용하여 얻어낸다.<br>
	 * 만약 원하는 port번호가 이미 bind된것이라면 7000번까지 1씩 증가하며 bind가
	 * 될때까지 반복하게 된다.
	 *
	 * @param  cookie  초기 전송하였던 쿠키값.
	 * @param  port    파일 전송에 사용할 포트 번호.
	 */
	public static FileTransferMessage createTransferMessage( int cookie, int port )
	{
		String host = null;
		try
		{
			host = InetAddress.getLocalHost().getHostAddress();
			host = System.getProperty("jmsn.file.host", host);
		}
		catch( UnknownHostException e )
		{
			throw new Error("Can't get localhost address");
		}

		FileTransferMessage msg = new FileTransferMessage();
		msg.setProperty( "Invitation-Command", "ACCEPT" );
		msg.setProperty( "Invitation-Cookie", String.valueOf(cookie) );
		msg.setProperty( "AuthCookie", createAuthCookie() );
		msg.setProperty( "Launch-Application", "FALSE" );
		msg.setProperty( "Request-Data", "IP-Address:" );
		msg.setProperty( "IP-Address", host );
		msg.setProperty( "Port", String.valueOf(msg.getAvailablePort(port)) );
		return msg;
	}

	/**
	 * 주어진 port부터 port+100까지 1씩 증가하면서 모든 포트를 검사하여 bind가능한
	 * port 번호를 반환한다.
	 *
	 * @return  bind가능한 포트번호. 만약 가능한 포트번호가 하나도 없다면 -1을 반환한다.
	 */
	protected synchronized int getAvailablePort( int port )
	{
		for(int i=port, limit=port+100; i<limit; i++)
		{
			ServerSocket socket = null;
			try
			{
				socket = new ServerSocket(i);
			}
			catch( IOException e ) {}
			finally
			{
				try	{
					socket.close();
				} catch( Exception e ) {}
			}
			if( socket!=null )
				return i;
		}
		return -1;
	}

	/**
	 * 8자리의 임의의 인증 쿠키값을 생성해낸다.
	 */
	private static String createAuthCookie()
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<8; i++)
			sb.append( (int)(Math.random()*9.0D) + 1 );
		return sb.toString();
	}

	public byte[] getBytes() throws UnsupportedEncodingException
	{
		if( getKind()!=KIND_FILE_TRANSFER )
			return super.getBytes();

		StringBuffer buf = createMimeHeader( STR_FILE_TRANSFER + "; charset=UTF-8" );
		buf.append( "\r\n" );
		fillMimeProperties( buf );
		buf.append( "\r\n" );
		String msg = buf.toString();
		return msg.getBytes("UTF-8");
	}
}
