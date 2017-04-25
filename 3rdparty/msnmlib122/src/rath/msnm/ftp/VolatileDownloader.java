/*
 * @(#)VolatileDownloader.java
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
 *    $Id: VolatileDownloader.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.ftp;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

import rath.msnm.MSNMessenger;
import rath.msnm.msg.MimeMessage;
/**
 * 파일을 수신요청을 허락했을때, 실제로 파일을 다운로드하는 클래스이다.
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: VolatileDownloader.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
public class VolatileDownloader extends Thread implements VolatileTransfer
{
	String cookie = null;

	private final MSNMessenger msn;
	private boolean isLive = true;

	private String host = null;
	private int port;
	private String authCookie = null;
	private String loginName = null;
	private File file = null;
	private int filesize = -1;
	private volatile int offset;

	private Socket socket = null;
	private InputStream rawIn = null;
	private InputStreamReader in = null;
	private PrintWriter out = null;

	private VolatileDownloader( MSNMessenger msn )
	{
		this.msn = msn;
	}

	/**
	 * VolatileDownloader의 인스턴스를 생성한다.
	 */
	public static VolatileDownloader getInstance( MSNMessenger msn, File toReceive, 
		MimeMessage msg ) 
	{
		String host = msg.getProperty("IP-Address");
		int port = Integer.parseInt(msg.getProperty("Port"));
		String authCookie = msg.getProperty("AuthCookie");
		String loginName = msn.getLoginName();

		if( host==null || authCookie==null )
			throw new IllegalArgumentException("Insufficient mime property");

		VolatileDownloader vd = new VolatileDownloader(msn);
		vd.host = host;
		vd.port = port;
		vd.authCookie = authCookie;
		vd.loginName = loginName;
		vd.file = toReceive;
		return vd;
	}

	/**
	 * 송신측 Host 주소를 얻어온다.
	 */
	public String getHostAddress()
	{
		return this.host;
	}

	/**
	 * 송신측 port 번호를 얻어온다.
	 */
	public int getPort()
	{
		return this.port;
	}

	/** 
	 * 신원확인을 위해 전송할 목적으로 설정된 인증쿠키값을 얻어온다.
	 */
	public String getAuthCookie()
	{
		return this.authCookie;
	}

	/**	
	 * 이 파일전송 세션에 대한 쿠키값을 얻어온다.
	 */
	public String getCookie()
	{
		return this.cookie;
	}

	/**
	 * 수신될 파일 객체를 반환한다. 
	 */
	public File getFile()
	{
		return this.file;
	}

	public String getFilename()
	{
		return this.file.getName();
	}

	/**
	 * 현재 실제로 수신된 byte 수를 반환한다.
	 */
	public int getCurrentFileSize()
	{
		return this.offset;
	}

	public int getCommitPercent()
	{
		return (int)(((double)offset / (double)filesize) * 100.0D);
	}

	/**
	 * 받을 파일의 전체 크기를 얻어온다. 이 수치는 ProgressBar등을 
	 * 구현하는데 유용한 값이 되어 줄 것이다.
	 * 만약 -1이 반환된다면, 아직 파일크기 정보를 얻지 못한것이니, 
	 * 조금 기다린 후 다시 수행하기 바란다.
	 */
	public int getFileSize()
	{
		return this.filesize;
	}

	/**
	 * Thread main loop 처리 부분이다.
	 */
	public final void run()
	{
		try
		{
			fireStart();
			makeConnection();

			sendMessage( "VER", "MSNFTP" );

			while( isLive )
			{
				String line = readLine();
				if( line==null )
					break;
				
				String header = line.substring( 0, 3 );
				String body = "";
				if( line.length()>4 )
					body = line.substring(4);

				processMessage( header, body );
			}

		}
		catch( Throwable e )
		{
			fireError( e );
		}
		finally
		{
			close();
		}
	}

	/**
	 * InputStreamReader를 통해 한줄을 읽어주는 메소드. 
	 * BufferedReader를 사용하면 buffering되는 과정에서 binary file data를
	 * 읽어버리는 경우가 발생할 위험이 있으므로, 직접 구현되었다.
	 */
	private String readLine() throws IOException
	{
		CharArrayWriter caw = new CharArrayWriter(40);
		int buf;
		while( (buf=in.read())!=-1 )
		{
			if( buf=='\r' ) 
				continue;

			if( buf=='\n' )
				return caw.toString();
			caw.write( buf );
		}
		if( caw.size()!=0 )
			return caw.toString();
		return null;
	}

	public void processMessage( String header, String body ) throws Throwable
	{
		if( header.equals("VER") )
		{
			sendMessage( "USR", loginName + " " + authCookie );
		}
		else
		if( header.equals("FIL") )
		{
			this.filesize = Integer.parseInt(body);
			sendMessage( "TFR" );
			getFileContent();
			sendMessage( "BYE", "16777989" );
			isLive = false;
		}
	}

	public void sendMessage( String header ) throws IOException
	{
		StringBuffer sb = new StringBuffer(6);
		sb.append( header );
		sb.append( "\r\n" );
		out.print( sb.toString() );
		out.flush();
	}

	public void sendMessage( String header, String body ) throws IOException
	{
		StringBuffer sb = new StringBuffer(64);
		sb.append( header );
		sb.append( ' ' );
		sb.append( body );
		sb.append( "\r\n" );
		out.print( sb.toString() );
		out.flush();
	}

	public void getFileContent() throws IOException
	{
		byte[] buf = new byte[ 2045 ]; 
		FileOutputStream fos = new FileOutputStream(this.file);
		try
		{
			while( true )
			{
				int pad = rawIn.read();
				int size = rawIn.read() | (rawIn.read()<<8);
				if( size==0 ) 
					break;
				int block, readlen = 0;
				while( (block=rawIn.read(buf, readlen, size-readlen))!=-1 )
				{
					readlen += block;
					if( readlen==size ) break;
				}
				if( readlen!=size )
					throw new IOException( "stream closed" );
				offset += readlen;
				fos.write( buf, 0, readlen ); // IndexOutOfBounds error!
				fos.flush();
				if( offset >= filesize )
					break;
			}
		}
		finally
		{
			try { fos.close(); } catch( IOException e ) {}
		}
	}

	public void close() 
	{
		isLive = false;
		if( in!=null )
		{
			try{ in.close(); } catch( Exception e ) {}
		}
		if( out!=null )
		{
			try{ out.close(); } catch( Exception e ) {}
		}
		if( socket!=null )
		{
			try{ socket.close(); } catch( Exception e ) {}
		}
	}

	protected void fireStart()
	{
		msn.fireFileReceiveStartedEvent( this );
	}

	/**
	 * 파일 전송도중 예기치 못한 예외가 발생하였을때, 불려진다. 
	 * 이 메소드에서는 예외 이벤트를 발송해줄 것이다.
	 */
	protected void fireError( Throwable e )
	{
		msn.fireFileReceiveErrorEvent( this, e );
	}

	/**
	 * 서버에 접속하고 스트림들을 생성한다.
	 */
	protected void makeConnection() throws IOException
	{	
		this.socket = new Socket( this.host, this.port );
		this.rawIn = socket.getInputStream();
		this.in = new InputStreamReader(rawIn);
		this.out = new PrintWriter(socket.getOutputStream());
	}
}
