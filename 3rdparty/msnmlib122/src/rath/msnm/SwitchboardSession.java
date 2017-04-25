/*
 * @(#)SwitchboardSession.java
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
 *    $Id: SwitchboardSession.java,v 1.22 2005/05/15 17:16:17 xrath Exp $
 */
package rath.msnm;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;

import rath.msnm.entity.Callback;
import rath.msnm.entity.MsnFriend;
import rath.msnm.entity.ServerInfo;
import rath.msnm.ftp.FileMessageProcessor;
import rath.msnm.ftp.ToSendFile;
import rath.msnm.msg.FileTransferMessage;
import rath.msnm.msg.IncomingMessage;
import rath.msnm.msg.MimeMessage;
import rath.msnm.msg.OutgoingMessage;
/**
 * 친구들과 대화 또는 인스턴트 메시지를 주고 받을때 Channel로 사용되는
 * Session이다. 이 세션은 사용자의 대화가 끝나면 종료된다.
 * <p>
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: SwitchboardSession.java,v 1.22 2005/05/15 17:16:17 xrath Exp $
 */
public class SwitchboardSession extends AbstractProcessor
{
	private static int timeout = 180000;
	private FileMessageProcessor file = null;

	private String sessionId = null;
	private String cookie = null;
	private HashMap friendMap = new HashMap();
	private MsnFriend lastFriend = null;

	private String targetLoginName = null;

	private HashMap slpMessageMap = new HashMap(); 
	private HashMap slpDataMap = new HashMap(); // Key - BaseID, Value=...

	private Object userObject = null;

	public SwitchboardSession( MSNMessenger msn, ServerInfo info, String sessionId )
	{
		super( msn, info, 1 );

		setServerName( "SS" );
		setAutoOutSend( true );

		this.sessionId = sessionId;
		this.file = new FileMessageProcessor(this);
	}

	public void setUserObject( Object o )
	{
		this.userObject = o;
	}

	public Object getUserObject()
	{
		return this.userObject;
	}

	/**
	 * 상대방과의 세션이 열린 직 후 호출되는 메서드이다.
	 */
	protected void sessionOpened()
	{
	}

	/**
	 * 대화를 거는 사람 입장에서 실제로 상대방이 Session에 Join하기
	 * 전에 누구와 연결될것인지를 결정해놓는 메소드이다.
	 */
	public void setTarget( String loginName )
	{
		this.targetLoginName = loginName;
	}

	/**
	 * 누구와의 연결인지 조회한다.
	 */
	public String getTarget()
	{
		return this.targetLoginName;
	}

	/**
	 * 현재 세션 id를 문자열 형태로 반환해준다.
	 */
	public String getSessionId()
	{
		return this.sessionId;
	}

	/**
	 * 현재 세션의 id를 문자열 형태로 설정한다.
	 */
	public void setSessionId( String sessionId )
	{
		this.sessionId = sessionId;
	}

	public String getCookie()
	{
		return this.cookie;
	}

	public void setCookie( String cookie )
	{
		this.cookie = cookie;
	}

	protected void makeConnection() throws IOException
	{
		super.makeConnection();
		setTimeout( timeout );
	}

	/**
	 * 이 Channel에서 얼마간 아무런 메시지도 오가지 않았을 경우
	 * 연결을 종료하게 할 수 있는데, 이곳에 적용되는 Timeout으로 millisecond단위로 설정한다.
	 *
	 * @param  timeout  millisecond단위의 SO_TIMEOUT
	 */
	public void setTimeout( int timeout )
	{
		SwitchboardSession.timeout = timeout;
		if( socket!=null )
		{
			try
			{
				socket.setSoTimeout( timeout );
			}
			catch( IOException e ) {
				System.err.println( "can't assign SO_TIMEOUT value" );
			}
		}
	}

	/**
	 * 이 Channel에서 얼마간 아무런 메시지도 오가지 않았을 경우
	 * 연결을 종료하게 할 수 있는데, 이곳에 적용되는 Timeout으로 millisecond단위로 얻어온다.
	 * <p>
	 * default로 180000(3분)으로 설정되어있다.
	 */
	public int getTimeout()
	{
		return SwitchboardSession.timeout;
	}

	public void init() throws IOException
	{
		Callback cb = Callback.getInstance("processRosterInfo", this.getClass());
		cb.setInfinite();

		OutgoingMessage out = new OutgoingMessage("ANS");
		markTransactionId( out );
		out.add( msn.getLoginName() );
		out.add( cookie );
		out.add( sessionId );
		out.setBackProcess( cb );

		sendMessage( out );
	}

	/**
	 * 특정 사용자를 이 세션에 추가한다.
	 */
	protected void addMsnFriend( MsnFriend friend )
	{
		friendMap.put( friend.getLoginName(), friend );
		this.lastFriend = friend;
	}

	/**
	 * 가장 최근에 이 세션에 들어온 친구를 얻어온다.
	 */
	public MsnFriend getMsnFriend()
	{
		return this.lastFriend;
	}

	/**
	 * 주어진 loginName을 가진 사용자가 이 세션에 물려있는지 아닌지
	 * 확인한다.
	 */
	public boolean isInFriend( String loginName )
	{
		return friendMap.containsKey(loginName);
	}

	/**
	 * 특정 loginName을 가진 사용자를 이 세션에서 제거한다.
	 */
	protected MsnFriend removeMsnFriend( String loginName )
	{
		return (MsnFriend)friendMap.remove( loginName );
	}

	public Collection getMsnFriends()
	{
		return friendMap.values();
	}

	/**
	 * 현재 이 세션에 연결되어있는 친구의 수를 얻어온다.
	 * 자기 자신은 이 숫자에서 제외된다.
	 */
	public int getFriendCount()
	{
		return friendMap.size();
	}

	/**
	 *
	 */
	protected void processMimeMessage( IncomingMessage msg ) throws Exception
	{
		int off = 0;
		int len = msg.getInt(2);
		int readlen = 0;
		byte[] b = new byte[ len ];

		while( off < len )
		{
			readlen = in.read(b, off, len-off);
			if( readlen==-1 )
				break;
			off += readlen;
		}

		MimeMessage mime = MimeMessage.parse( b );

		if( Debug.printMime )
		{
			System.out.println(new String(b, "UTF-8"));
		}

		int kind = mime.getKind();
		switch( kind )
		{
		case MimeMessage.KIND_TYPING_USER:
			processTypingUser( msg, mime );
			break;
		case MimeMessage.KIND_MESSAGE:
			processInstantMessage( msg, mime );
			break;
		case MimeMessage.KIND_FILE_TRANSFER:
			file.processMessage( msg, mime );
			break;
		case MimeMessage.KIND_UNKNOWN:
			break;
		}
	}

	protected void processWhoJoined( IncomingMessage msg ) throws Exception
	{
		String loginName = msg.get(0);
		String friendlyName = msg.get(1);

		MsnFriend friend = msn.getBuddyGroup().getAllowList().get( loginName );
		if( friend==null )
		{
			friend = new MsnFriend(loginName, friendlyName);
		}

		friend.setFriendlyName( friendlyName );
		addMsnFriend( friend );
		msn.fireJoinSessionEvent( this, friend );
	}

	/**
	 * Switchboard Server로부터 notify되는 메시지들을 처리한다.
	 */
	public void processNotifyMessage( IncomingMessage msg ) throws Exception
	{
		String header = msg.getHeader();
		if( header.equals("MSG") )
			processMimeMessage(msg);
		else
		if( header.equals("JOI") )
			processWhoJoined(msg);
		else
		if( header.equals("BYE") )
		{
			String partLoginName = msg.get(0);
			MsnFriend parter = removeMsnFriend( partLoginName );

			if( parter!=null )
				msn.firePartSessionEvent( this, parter );
			if( friendMap.size()==0 )
			{
				isLive = false;
			}
		}
	}

	/**
	 * 스레드가 종료되고 스트림을 닫기 직전에 호출된다.
	 */
	public void cleanUp()
	{
		try
		{
			close();
		}
		catch( IOException e ) {}

		

		friendMap.clear();
		if( sessionId!=null )
			msn.fireSwitchboardSessionEndedEvent( this );
	}

	/**
	 * 이 세션을 종료한다.
	 */
	public void close() throws IOException
	{
		isLive = false;

		OutgoingMessage out = new OutgoingMessage("OUT");
		sendMessage( out );
		interrupt();
	}

	public void processRosterInfo( IncomingMessage msg ) throws IOException
	{
		String header = msg.getHeader();
		if( header.equals("IRO") )
		{
			String destLoginName = msg.get(2);
			String destFriendlyName = msg.get(3);

			MsnFriend friend = msn.getBuddyGroup().getAllowList().get( destLoginName );
			// TODO: by sediah
			if (friend != null) {
				friend.setFriendlyName( destFriendlyName );
				addMsnFriend( friend );
			}
			else
			{
				System.err.println( "* Not found in allow list: " + destLoginName );
				addMsnFriend( new MsnFriend(destLoginName, destFriendlyName) );
			}
		}
		else
		if( header.equals("ANS") )
		{
			removeInfiniteTransactionId( msg.getTransactionId() );
			String returnCode = msg.get(0);
			if( returnCode.equals("OK") )
			{
				msn.fireSwitchboardSessionStartedEvent( this );
				sessionOpened();
			}
		}
	}

	/**
	 * 누군가 자신을 향해 인스턴트 메시지를 날리기 위해 키보드를
	 * 다닥다닥 두들기고 있을때 <b>종종</b> 날라오는 메시지이다.
	 */
	protected void processTypingUser( IncomingMessage msg, MimeMessage mime )
		throws IOException
	{
		// 이 메소드는 이벤트 리스너에 등록해야한다.
		MsnFriend friend = new MsnFriend( msg.get(0), msg.get(1) );
		msn.fireProgressTypingEvent( this, friend, mime.getProperty("TypingUser") );
	}

	/**
	 * 인스턴트 메시지가 도착하였을때 그 메시지를 처리하는 메소드이다.
	 */
	protected void processInstantMessage( IncomingMessage msg, MimeMessage mime )
		throws IOException
	{
		MsnFriend friend = new MsnFriend( msg.get(0) );
		friend.setFriendlyName( msg.get(1) );

		msn.fireInstantMessageEvent( this, friend, mime );
	}

	/**
	 * 이 세션에 있는 모든 사람에게 메시지를 보낸다.
	 */
	public void sendMessage( MimeMessage mime ) throws IOException
	{
		/*
		 * markTransactionId를 반드시 붙여야만 한다.
		 */
		OutgoingMessage out = new OutgoingMessage("MSG");
		markTransactionId( out );
		out.add( "N" );

		sendMimeMessage( out, mime );
	}

	/**
	 * 이 세션에 물린 사람에게 파일을 전송하겠다는 메시지를 보낸다.
	 */
	public void sendFileRequest( ToSendFile file, FileTransferMessage mime ) throws IOException
	{
		this.file.registerSend( file );
		sendMessage( mime );
	}

	/**
	 * 상대방으로부터의 파일 수신 요청을 <b>허락</b>한다.
	 *
	 * @param  cookie  파일 초청시 받았던 쿠키값.
	 * @param  toReceive  다운로드할 내용이 저장될 파일.
	 */
	public void acceptFileReceive( int cookie, java.io.File toReceive ) throws IOException
	{
		this.file.registerReceive( cookie, toReceive );
		sendMessage( FileTransferMessage.createAcceptMessage(cookie) );
	}

	/**
	 * 상대방으로부터의 파일 수신 요청을 <b>거절</b>한다.
	 *
	 * @param  cookie  파일 초청시 받았던 쿠키값.
	 */
	public void rejectFileReceive( int cookie ) throws IOException
	{
		sendMessage( FileTransferMessage.createRejectMessage(cookie) );
	}

	/**
	 * 타이핑 중이라는 메시지를 전송한다.
	 */
	public void sendTypingMessage( MimeMessage mime ) throws IOException
	{
		mime.setKind( MimeMessage.KIND_TYPING_USER );
		sendMessage( mime );
	}

	/**
	 * 인스턴스 메시지를 전송한다.
	 */
	public void sendInstantMessage( MimeMessage mime ) throws IOException
	{
		mime.setKind( MimeMessage.KIND_MESSAGE );
		sendMessage( mime );
	}

	/**
	 * 이 세션으로 친구를 초대한다.
	 */
	public void inviteFriend( String loginName ) throws IOException
	{
		OutgoingMessage out = new OutgoingMessage( "CAL" );
		markTransactionId( out );
		out.add( loginName );

		sendMessage( out );
	}

	public void processError( Throwable e )
	{
		if( !(e instanceof IOException) )
			e.printStackTrace();
	}

	/**
	 * @return
	 */
	public MsnFriend getOwner() {
		return msn.getOwner();
	}

	/**
	 * @return
	 */
	public MsnFriend getLastFriend() {
		return lastFriend;
	}
	
	/*
	public boolean requestFileTransfer(String filename) {
		File file = new File(filename);
		long totalSize = file.length();
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return false;
		}
		int index = filename.lastIndexOf('/');
		if (index != -1)
			filename = filename.substring(index + 1);
		return requestFileTransfer(filename, totalSize, is);
	}
	*/
};
