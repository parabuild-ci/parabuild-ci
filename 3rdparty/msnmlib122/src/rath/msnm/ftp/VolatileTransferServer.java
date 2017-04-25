/*
 * @(#)VolatileTransferServer.java
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
 *    $Id: VolatileTransferServer.java,v 1.5 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.ftp;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

import rath.msnm.MSNMessenger;
import rath.msnm.msg.FileTransferMessage;
/**
 * 메신져에서 서로 파일전송을 할때, 송신측에서 특정 port를 bind하고 
 * 새롭게 연결을 맺어 메시지를 주고 받는데에 사용되는 server이다.
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: VolatileTransferServer.java,v 1.5 2004/12/24 22:05:53 xrath Exp $ 
 */
public class VolatileTransferServer extends Thread implements VolatileTransfer
{
	String cookie;

	private final MSNMessenger msn;
	private boolean isLive = true;
	private int port;
	private String authCookie = null;

	private String peerLoginName = null;
	private File file = null;
	private int filesize = 0;
	private volatile int offset = 0;

	private ServerSocket serverSocket = null;
	private OutputStream out = null;
	private Socket socket = null;
	private BufferedReader in = null;

	private Thread binaryThread = null;

	private VolatileTransferServer( MSNMessenger msn )
	{
		this.msn = msn;
	}
	
	/**
	 * VolatileTransferServer의 인스턴스를 생성한다.
	 */
	public static VolatileTransferServer getInstance( MSNMessenger msn, ToSendFile tosend, 
		FileTransferMessage msg ) throws FileNotFoundException
	{
		int port = Integer.parseInt(msg.getProperty("Port"));
		String authCookie = msg.getProperty("AuthCookie");
		String loginName = tosend.getReceiverName();
		File file = tosend.getFile();
		if( !file.exists() )
			throw new FileNotFoundException(file.getAbsolutePath());

		VolatileTransferServer vts = new VolatileTransferServer(msn);
		vts.port = port;
		vts.authCookie = authCookie;
		vts.peerLoginName = loginName;
		vts.file = file;
		vts.filesize = (int)file.length();
		return vts;
	}

	/**
	 * 이 서버가 파일송신에 사용하는 port번호를 반환한다.
	 */
	public int getPort()
	{
		return this.port;
	}

	/** 
	 * 파일을 수신받는 사람의 LoginName을 반환한다.
	 */
	public String getReceiverName()
	{
		return this.peerLoginName;
	}

	/** 
	 * 접속된 수신자가 올바른 수신자인지 검사하기 위한 인증쿠기값을 반환한다.
	 */
	public String getAuthCookie()
	{
		return this.authCookie;
	}

	public String getCookie()
	{
		return this.cookie;
	}

	/**
	 * 전송되는 파일객체를 반환한다.
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
	 * 현재 실제로 상대peer에 전송된 byte의 수를 반환한다.
	 */
	public int getPostedLength()
	{
		return this.offset;
	}

	public int getCommitPercent()
	{
		return (int)( ((double)offset / (double)filesize) * 100.0D );
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

			while( isLive )
			{
				String line = in.readLine();
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
			if( binaryThread!=null )
				binaryThread.interrupt();
			close();
			fireEnd();
		}
	}

	public void processMessage( String header, String body ) throws Throwable
	{
		if( header.equals("VER") )
		{
			sendMessage( header, body );
		}
		else
		if( header.equals("USR") )
		{
			int i0 = body.indexOf(' ');
			if(i0==-1)
			{
				close();
				return;
			}
			String loginName = body.substring(0, i0);
			String authCookie = body.substring(i0+1);

			if( !loginName.equals(peerLoginName) ||
				!authCookie.equals(this.authCookie) )
			{
				close();
				return;
			}
			sendMessage( "FIL", String.valueOf(file.length()) );
		}
		else
		if( header.equals("TFR") )
		{
			binaryThread = new Thread( new Runnable() {
				public void run()
				{
					try
					{
						sendFileContent();
					}
					catch( Throwable e )
					{
						fireError(e);
					}
				}
			});
			binaryThread.start();
		}
		else
		if( header.equals("CCL") )
		{
			if( binaryThread!=null )
			{
				binaryThread.interrupt();
				binaryThread = null;
				isLive = false;
			}
		}
		else
		if( header.equals("BYE") )
		{
			isLive = false;
		}
	}

	public void sendMessage( String header, String body ) throws IOException
	{
		StringBuffer sb = new StringBuffer(40);
		sb.append( header );
		sb.append( ' ' );
		sb.append( body );
		sb.append( "\r\n" );
		out.write( sb.toString().getBytes() );
		out.flush();
	}

	public void sendFileContent() throws IOException, InterruptedException
	{
		int filesize = (int)file.length();
		
		byte[] buf = new byte[ 2048 ];
		InputStream in = null;
		if( this.file instanceof StreamingFile )
			in = ((StreamingFile)this.file).getInputStream();
		else
			in = new FileInputStream(this.file);
		Thread currentThread = Thread.currentThread();
		int readlen;
		while( (readlen=in.read(buf, 3, 2045))>0 )
		{
			buf[0] = 0;
			buf[1] = (byte)((readlen>>0) & 0xff);
			buf[2] = (byte)((readlen>>8) & 0xff);
			out.write( buf, 0, readlen+3 );
			offset += readlen;
			out.flush();

			if( currentThread.isInterrupted() )
			{
				in.close();
				throw new InterruptedException("thread interrupted");
			}
		}
		in.close();
		out.write( 0 );
		out.write( 0 );
		out.write( 0 );
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
		if( serverSocket!=null )
		{
			try{ serverSocket.close(); } catch( Exception e ) {}
		}
	}
	
	protected void fireStart()
	{
		msn.fireFileSendStartedEvent( this );
	}

	/**
	 * 파일 전송도중 예기치 못한 예외가 발생하였을때, 불려진다. 
	 * 이 메소드에서는 예외 이벤트를 발송해줄 것이다.
	 */
	protected void fireError( Throwable e )
	{
		msn.fireFileSendErrorEvent( this, e );
	}

	protected void fireEnd()
	{
		msn.fireFileSendEndedEvent( this );
	}

	/**
	 * 주어진 ServerSocket으로 설정된 port를 bind하고 관련 Stream을 생성한다.
	 * accept를 수행하므로 이 메소드는 blocking된다.
	 */
	protected void makeConnection() throws IOException
	{
		this.serverSocket = new ServerSocket(this.port, 1);
		this.serverSocket.setSoTimeout( 30000 );
		
		this.socket = serverSocket.accept();
		this.out = socket.getOutputStream();
		this.in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
	}
}
