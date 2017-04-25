/*
 * @(#)FileMessageProcessor.java
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
 *    $Id: FileMessageProcessor.java,v 1.5 2005/05/11 19:49:31 xrath Exp $ 
 */
package rath.msnm.ftp;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import rath.msnm.SwitchboardSession;
import rath.msnm.msg.IncomingMessage;
import rath.msnm.msg.MimeMessage;
import rath.msnm.msg.FileTransferMessage;
/**
 * 파일 송수신에 사용되는 각 트랜잭션들을 관리하고 인증에 관련된 Cookie나 
 * 실제로 파일을 전송하는 thread를 띄우는등의 역할을 모두 처리하는 클래스이다.
 * <p>
 * 아래는 이 클래스가 할 수 있는 일을 나열한다.
 * <ul>
 * 
 * </ul>
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: FileMessageProcessor.java,v 1.5 2005/05/11 19:49:31 xrath Exp $ 
 */
public class FileMessageProcessor 
{
	private static boolean _auto = true;
	private static boolean _autoReject = false;

	private SwitchboardSession session = null;
	private Hashtable toSendFileMap = new Hashtable();
	private Hashtable toReceiveFileMap = new Hashtable();

	/**
	 +* session에서 일어나는 모든 File에 전송에 관련된 메시지를 
	 * 처리하고 관리하는 객체를 생성한다.
	 */
	public FileMessageProcessor( SwitchboardSession session )
	{
		this.session = session;
	}

	/**
	 * 파일 송수신에 관련된 메시지들을 처리하는 모든 메소드들에 대한
	 * Entry point가 되는 메소드이다.
	 *
	 * @param  msg  상대방으로부터 받은 메시지
	 * @param  mime  실제 파일전송에 관련된 내용이 담긴 Mime 메시지로 FileTransferMessage이다. 
	 *               단, FileTransferMessage의 인스턴스는 아니므로 cast는 할 수 없다.
	 */
	public void processMessage( IncomingMessage msg, MimeMessage mime ) 
	{			
		// mime.getProperties().list( System.out );
		String command = mime.getProperty("Invitation-Command");
		if( command==null || !mime.hasProperty("Invitation-Cookie") ) 
			return;

		boolean isLastInTransaction = mime.hasProperty("AuthCookie");

		try
		{
			if( command.equals("INVITE") )
			{
				processInvite(mime);
			}
			else
			if( command.equals("ACCEPT") )
			{
				if( !isLastInTransaction )
					processAccept(mime);
				else
					processDownload(mime);
			}
			else
			if( command.equals("CANCEL") )
			{
				processCancel(mime);
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( Throwable e )
		{
			e.printStackTrace();
			System.err.println( "MIME message violation: FileTransfer(" +e+ ")" );
		}
	}

	/**
	 * 상대방으로부터 도착한 파일전송 요청을 처리한다.
	 * 이 메소드에서는 MIME 메시지에서 필요한 정보만을 골라 MsnFileEvent를 발송하는 일을 한다.
	 * 만약, ${@link #setAutoReceive setAutoReceive} 메소드로 자동수신 옵션을 true로 해놓았다면
	 * 이벤트를 발송하지 않고 곧바로 수신허락 메시지를 송신한다.
	 * <p>
	 * 자동 수신모드가 설정되어있을 경우, 수신되는 파일은 기본적으로 current directory에 
	 * 저장될 것이다. 만약 이것을 수정하고 싶다면, System property의 
	 * <b>msnm.file.download.dir</b>에 받고자하는 디렉토리를 설정하면 된다.
	 */
	protected void processInvite( MimeMessage mime ) throws IOException
	{
		String cookie = mime.getProperty("Invitation-Cookie");
		int cookieInt = Integer.parseInt(cookie);

		if( _autoReject )
		{
			session.rejectFileReceive(cookieInt);
			return;
		}

		String filename = mime.getProperty("Application-File");
		String strFileSize = null;
		strFileSize = mime.getProperty("Application-FileSize");
		if( strFileSize==null )
		{
			return;
		}
		int filesize = Integer.parseInt(strFileSize);

		if( _auto )
		{
			session.acceptFileReceive(cookieInt, 
				new File(System.getProperty("msnm.file.download.dir","."), filename));
			return;
		}
		
		session.msn.fireFilePostedEvent( session, cookieInt, filename, filesize );
	}

	/**
	 * 파일 자동수신 모드를 설정한다. 
	 */
	public static void setAutoReceive( boolean auto )
	{
		_auto = auto;
	}

	public static void setAutoReject( boolean autoReject )
	{
		_autoReject = autoReject;
	}

	/**
	 * 파일 자동수신 모드인지 확인한다. default값은 true이다.
	 */
	public static boolean isAutoReceive()
	{
		return _auto;
	}

	/**
	 * 파일 송신 요청을 했다는 사실을 이 session에 등록한다. 
	 * 이 메소드는 프로그래머가 직접 호출할 필요는 없다. 
	 * public으로 선언된 이유는 단지, rath.msnm.SwitchboardSession 클래스의
	 * sendFileRequest메소드에서 registerSend를 호출해야될 필요가 있기 때문이다.
	 * <p>
	 * 등록에 대한 해제는 수신 허락/거절 메시지가 오면 자동으로 된다.
	 * 만약 도착하지 않는다면, 영원히 Map에서 해제되지 않는다.
	 *
	 * @param  cookie  송신할 파일트랜잭션에 대한 쿠키값
	 */
	public void registerSend( ToSendFile file )
	{
		toSendFileMap.put( file.getCookie(), file );
	}

	/** 
	 * 파일 수신을 허락했다는 사실을 이 session에 등록한다.
	 * 이 메소드는 프로그래머가 직접 호출할 필요는 없다.
	 * <p>
	 * 등록에 대한 해제는 최종 송신 허가메시지가 오면 자동으로 된다.
	 * 만약 도착하지 않는다면, 영원히 Map에서 해제되지 않는다.
	 */
	public void registerReceive( int cookie, File file )
	{
		toReceiveFileMap.put( String.valueOf(cookie), file );
	}

	/**
	 * 송신요청을 수신자가 '허락'하였다는 메시지를 처리한다.
	 * <p>
	 * 이벤트를 발송시킨 후, File을 전송하기 위한 FileServer thread를 생성하고
	 * 시작한다. 그 후 registerSend로 등록된 내부 Map에서 제거된다.
	 */
	public void processAccept( MimeMessage mime ) throws FileNotFoundException, IOException
	{
		String cookie = mime.getProperty("Invitation-Cookie");
		ToSendFile toSend = (ToSendFile)toSendFileMap.remove(cookie);
		if( toSend!=null )
		{
			int cookieInt = Integer.parseInt(cookie);
			FileTransferMessage msg = FileTransferMessage.createTransferMessage(cookieInt);

			VolatileTransferServer vts = VolatileTransferServer.getInstance( session.msn, toSend, msg );
			vts.cookie = cookie;

			session.sendMessage( msg );
			session.msn.fireFileSendAcceptedEvent( session, cookieInt );

			vts.start();
		}
	}

	/**
	 * 송신요청을 수신자가 '거절'하였다는 메시지를 처리한다.
	 * <p>
	 * 이벤트를 발송시킨 후, registerSend로 등록된 내부 Map에서 제거된다.
	 */
	public void processCancel( MimeMessage mime )
	{
		String cookie = mime.getProperty("Invitation-Cookie");
		ToSendFile toSend = (ToSendFile)toSendFileMap.remove(cookie);
		if( toSend!=null )
		{
			int cookieInt = Integer.parseInt(cookie);
			session.msn.fireFileSendRejectedEvent( session, cookieInt, mime.getProperty("Cancel-Code") );
		}
	}

	/**
	 * toReceiveMap에 요청된 cookie가 있는지 조사하여 무결성을 지켜야만 한다.
	 * 
	 * 송신측에서 '이제 받아가라~' 하는 메시지에 대한 처리를 해주어야 한다.
	 * Mime 메시지의 정보에 따라 AuthCookie, host, port 정보를 가지고 송신자의 
	 * 서버에 접속하여 파일을 다운로드 하고, BYE~
	 */
	public void processDownload( MimeMessage mime )
	{
		String cookie = mime.getProperty("Invitation-Cookie");
		File toReceive = (File)toReceiveFileMap.remove(cookie);		
		if( toReceive!=null )
		{
			/* 받을 파일이 이미 존재해도, 덮어쓸까말까에 대한 것은 무시한다.
			   이것은 Application쪽에서 해결해야만 한다. */
			VolatileDownloader vd = VolatileDownloader.getInstance( session.msn,
				toReceive, mime );
			vd.cookie = cookie;
			vd.start();		
		}
	}
}
