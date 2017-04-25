/*
 * @(#)MSNMessenger.java
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
 *    $Id: MSNMessenger.java,v 1.29 2005/05/20 06:15:03 xrath Exp $
 */
package rath.msnm;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import rath.msnm.entity.MsnFriend;
import rath.msnm.entity.ServerInfo;
import rath.msnm.event.MsnAdapter;
import rath.msnm.event.MsnListener;
import rath.msnm.ftp.ToSendFile;
import rath.msnm.ftp.VolatileDownloader;
import rath.msnm.ftp.VolatileTransferServer;
import rath.msnm.msg.FileTransferMessage;
import rath.msnm.msg.MimeMessage;
import rath.msnm.util.BASE64;
/**
 * MSN 메신져 서버에 접속하고 이것저것을 하기 위해
 * Entry point가 되는 클래스이다.
 * login을 요청하고 loginComplete 이벤트가 발생된 후부터
 * 이것저것 메소드를 사용할 수 있다.
 * 그렇지 않으면 NS proc 미생성으로 NullPointerException을 만날것이다.
 * <p>
 * <pre><code>
 *  MSNMessenger msn = new MSNMessenger( "xiguel@hotmail.com", "12341234" );
 *  msn.setInitialStatus( UserStatus.ONLINE );
 *  msn.addMsnListener( new MsnAdapter() {
 *      public void progressTyping( SwitchboardSession ss,
 *          MsnFriend friend, String typingUser )
 *      {
 *          System.out.println( "Typing on " + friend.getLoginName() );
 *      }
 *      public void instantMessageReceived( SwitchboardSession ss,
 *          MsnFriend friend, MimeMessage mime )
 *      {
 *          System.out.println( "*** MimeMessage from " + friend.getLoginName() );
 *          System.out.println( mime.getMessage() );
 *          System.out.println( "*****************************" );
 *      }
 *  });
 *  msn.login();
 * </code></pre>
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: MSNMessenger.java,v 1.29 2005/05/20 06:15:03 xrath Exp $
 */
public class MSNMessenger
{	
	private NotificationProcessor ns = null;
	private BuddyGroup buddyGroup = null;
	private BuddyList forwardList = null;
	private LocalCopy localCopy = null;

	boolean isLogged = false;
	public boolean is911 = false;
	private byte[] bPhoto = null;
	private BufferedImage imgPhoto = null;
	private String ctxPhoto = null;

	private String loginName = null;
	private String password = null;
	private String initStatus = UserStatus.ONLINE;
	private MsnListener base = null;
	protected ArrayList listeners = new ArrayList();

	private MsnFriend owner = null;
	private Hashtable sessionMap = new Hashtable();

	/**
	 * MSNMessenger 객체를 생성한다.
	 */
	public MSNMessenger()
	{
		this( null, null );
	}

	/**
	 * 주어진 account정보로 MSNMessenger 객체를 생성한다.
	 *
	 * @param  loginName  사용할 login 이름. (e.g. windrath@hotmail.com)
	 * @param  password   자신의 password
	 */
	public MSNMessenger( String loginName, String password )
	{
		this.loginName = loginName;
		this.password = password;
		this.owner = new MsnFriend(loginName);

		this.base = new Listener();
		this.buddyGroup = BuddyGroup.getInstance();
		this.forwardList = buddyGroup.getForwardList();
		this.localCopy = new LocalCopy();
	}

	private void initLogon()
	{
		buddyGroup.clear();
		localCopy.setLoginName( loginName );

		/* 최근 시리얼넘버를 NS에 설정한다 */
		localCopy.loadInformation();
		ns.lastFrom = localCopy.getProperty("SerialFrom", "0");
		ns.lastTo   = localCopy.getProperty("SerialTo", "0");
		ns.lastFN   = localCopy.getProperty("FriendlyName", loginName);

		/* 최근 버디 리스트를 가져온다. */
		localCopy.loadBuddies( buddyGroup );
	}

	/**
	 * 새로운 시리얼 번호를 저장한 후, BuddyList이 변경되었다는 이벤트를 발송한다.
	 *
	 * @param serial
	 * @param fireEvent
	 */
	void storeLocalCopy( String from, String to )
	{
		localCopy.setProperty( "SerialFrom", from );
		localCopy.setProperty( "SerialTo", to );
		localCopy.setProperty( "FriendlyName", owner.getFriendlyName() );
		localCopy.storeInformation();
		localCopy.storeBuddies( buddyGroup );

		fireBuddyListModifiedEvent();
	}

	public BuddyGroup getBuddyGroup()
	{
		return this.buddyGroup;
	}

	public LocalCopy getLocalCopy()
	{
		return this.localCopy;
	}

	/**
	 * 로그인 할때의 초기 상태값을 설정한다. 기본값으로는 '온라인'이다.
	 * 이곳에 적용할 수 있는 상태값들은 {@link UserStatus UserStatus} 인터페이스에
	 * 선언되어있는 상수들을 사용하면 된다.
	 */
	public void setInitialStatus( String code )
	{
		this.initStatus = code;
	}

	/**
	 * 현재 설정된 초기 상태코드를 얻어온다.
	 */
	public String getInitialStatus()
	{
		return this.initStatus;
	}

/*
	public void setMyPhoto( File file ) throws Exception
	{
		if( file==null )
		{
			imgPhoto = null;
			bPhoto = null;
			ctxPhoto = null;

			if( owner!=null )
			{
				owner.setPhotoContext( null );
				owner.setPhoto( null );
			}

			if( isLogged )
				ns.setMyStatus( owner.getStatus() );

			return;
		}

		PhotoFormatter format = new PhotoFormatter();

		this.imgPhoto = format.resize(file);
		this.bPhoto = format.getPNGBytes(imgPhoto);
		
		// Test code
		String sha1d = getPhotoSHA1D();
		String filename = getIdHash(loginName);
		String sha1c = getPhotoSHA1C(filename, sha1d);

		StringBuffer sb = new StringBuffer();
		sb.append( "<msnobj Creator=\"" );
		sb.append( loginName );
		sb.append( "\" Size=\"" );
		sb.append( bPhoto.length );
		sb.append( "\" Type=\"3\" Location=\"TFR" );
		sb.append( filename );
		sb.append( "\" Friendly=\"AAA=\" SHA1D=\"" );
		sb.append( sha1d );
		sb.append( "\" SHA1C=\"" );
		sb.append( sha1c );
		sb.append( "\"/>" );

		this.ctxPhoto = sb.toString();

		if( isLogged )
		{
			owner.setPhotoContext( ctxPhoto );
			ns.setMyStatus( owner.getStatus() );
		}
	}

	private String getPhotoSHA1C( String filename, String sha1d ) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update( ("Creator" + loginName).getBytes() );
		md.update( ("Size" + bPhoto.length).getBytes() );
		md.update( "Type3".getBytes() );
		md.update( ("LocationTFR" + filename).getBytes() );
		md.update( "FriendlyAAA=".getBytes() );
		md.update( ("SHA1D" + sha1d).getBytes() );
		return new BASE64(false).encode(md.digest());
	}

	private String getPhotoSHA1D() throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] ret = md.digest( bPhoto );
		return new BASE64(false).encode(ret);	
	}

	private String getIdHash( String id ) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update( id.getBytes() );
		byte[] b = md.digest();

		StringBuffer sb = new StringBuffer();
		for(int i=0; i<4; i++)
		{
			int v = b[i] < 0 ? (int)b[i] + 0x100 : (int)b[i];
			sb.append( Integer.toHexString(v).toUpperCase() );
		}
		sb.append( ".dat" );
		return sb.toString();
	}

	public Image getMyPhoto()
	{
		return this.imgPhoto;
	}

	public byte[] getMyPhotoBytes()
	{
		return this.bPhoto;
	}
*/

	/**
	 * 현재 로그인 된 상태인지 확인한다.
	 */
	public boolean isLoggedIn()
	{
		return this.isLogged;
	}

	/**
	 * 서버로부터 발생되는 이벤트나 메시지들을 처리할 MsnListner
	 * 인터페이스를 설정한다. 원래는 multi-listener를 지원해야하지만,
	 * 현재는 단일 Listener 구조를 사용한다. 그러므로
	 * 반드시 리스너를 설정해야한다.
	 */
	public synchronized void addMsnListener( MsnListener l )
	{
		if( !listeners.contains(l) )
			listeners.add(l);
	}

	/**
	 * 현재 등록된 MSNListener의 수를 반환해준다.
	 */
	public int getListenerCount()
	{
		return listeners.size();
	}

	public void fireListAdd( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireListAdd" );
		base.listAdd( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).listAdd(friend);

	}

	public void fireInstantMessageEvent( SwitchboardSession ss, MsnFriend friend,
		MimeMessage mime )
	{
		fireInstantMessageEventImpl( ss, friend, mime );
	}

	protected void fireInstantMessageEventImpl( SwitchboardSession ss, MsnFriend friend, MimeMessage mime )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireInstantMessageEvent" );
		base.instantMessageReceived(ss, friend, mime);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).instantMessageReceived(ss, friend, mime);
	}

	protected void fireJoinSessionEventImpl( SwitchboardSession ss, MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireJoinSessionEvent" );
		base.whoJoinSession(ss, friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoJoinSession(ss, friend);
	}

	public void fireJoinSessionEvent( SwitchboardSession ss, MsnFriend friend )
	{
		fireJoinSessionEventImpl( ss, friend );
	}

	protected void fireListOnlineEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireListOnlineEvent" );
		base.listOnline(friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).listOnline(friend);
	}

	public void fireListOnlineEvent( MsnFriend friend )
	{
		fireListOnlineEventImpl( friend );	
	}

	protected void fireLoginCompleteEventImpl( MsnFriend own )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireLoginCompleteEvent" );
		base.loginComplete(own);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).loginComplete(own);
	}

	public void fireLoginCompleteEvent( MsnFriend own )
	{
		fireLoginCompleteEventImpl( own );
	}

	protected void firePartSessionEventImpl( SwitchboardSession ss, MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: firePartSessionEvent" );
		base.whoPartSession(ss, friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoPartSession(ss, friend);
	}

	public void firePartSessionEvent( SwitchboardSession ss, MsnFriend friend )
	{
		firePartSessionEventImpl( ss, friend );
	}

	protected void fireProgressTypingEventImpl( SwitchboardSession ss, MsnFriend friend, String typeuser )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireProgressTypingEvent" );
		base.progressTyping(ss, friend, typeuser);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).progressTyping(ss, friend, typeuser);
	}

	public void fireProgressTypingEvent( SwitchboardSession ss, MsnFriend friend, String typeuser )
	{
		fireProgressTypingEventImpl( ss, friend, typeuser );
	}

	protected void fireSwitchboardSessionStartedEventImpl( SwitchboardSession ss )
	{
		if( Debug.printFireEvent )
		{
			System.out.println( "* Event: fireSwitchboardSessionStartedEvent" );
		}	

		base.switchboardSessionStarted(ss);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).switchboardSessionStarted(ss);
	}

	public void fireSwitchboardSessionStartedEvent( SwitchboardSession ss )
	{
		fireSwitchboardSessionStartedEventImpl( ss );
	}

	protected void fireSwitchboardSessionEndedEventImpl( SwitchboardSession ss )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireSwitchboardSessionEndedEvent" );
		base.switchboardSessionEnded(ss);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).switchboardSessionEnded(ss);
	}

	public void fireSwitchboardSessionEndedEvent( SwitchboardSession ss )
	{
		fireSwitchboardSessionEndedEventImpl( ss );
	}

	protected void fireSwitchboardSessionAbandonEventImpl( SwitchboardSession ss, String targetName )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireSwitchboardSessionAbandonEvent" );
	    base.switchboardSessionAbandon(ss, targetName);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).switchboardSessionAbandon(ss, targetName);
	}

	public void fireSwitchboardSessionAbandonEvent( SwitchboardSession ss, String targetName )
	{
		fireSwitchboardSessionAbandonEventImpl( ss, targetName );
	}

	protected void fireUserOnlineEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireUserOnlineEvent" );
		base.userOnline(friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).userOnline(friend);
	}

	public void fireUserOnlineEvent( MsnFriend friend )
	{
		fireUserOnlineEventImpl( friend );
	}

	protected void fireUserOfflineEventImpl( String loginName )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireUserOfflineEvent" );
		base.userOffline(loginName);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).userOffline(loginName);
	}

	public void fireUserOfflineEvent( String loginName )
	{
		fireUserOfflineEventImpl( loginName );
	}

	protected void fireFilePostedEventImpl( SwitchboardSession ss, int cookie, String filename, int filesize )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFilePostedEvent" );
		base.filePosted(ss, cookie, filename, filesize);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).filePosted(ss, cookie, filename, filesize);
	}

	public void fireFilePostedEvent( SwitchboardSession ss, int cookie, String filename, int filesize )
	{
		fireFilePostedEventImpl( ss, cookie, filename, filesize );
	}

	protected void fireFileSendAcceptedEventImpl( SwitchboardSession ss, int cookie )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendAcceptedEvent" );
		base.fileSendAccepted(ss, cookie);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendAccepted(ss, cookie);
	}

	public void fireFileSendAcceptedEvent( SwitchboardSession ss, int cookie )
	{
		fireFileSendAcceptedEventImpl( ss, cookie );
	}

	protected void fireFileSendRejectedEventImpl( SwitchboardSession ss, int cookie, String reason )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendRejectedEvent" );
		base.fileSendRejected(ss, cookie, reason);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendRejected(ss, cookie, reason);
	}

	public void fireFileSendRejectedEvent( SwitchboardSession ss, int cookie, String reason )
	{
		fireFileSendRejectedEventImpl( ss, cookie, reason );
	}

	protected void fireFileSendStartedEventImpl( VolatileTransferServer server )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendStartedEvent" );
		base.fileSendStarted(server);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendStarted(server);
	}

	public void fireFileSendStartedEvent( VolatileTransferServer server )
	{
		fireFileSendStartedEventImpl( server );
	}

	protected void fireFileSendEndedEventImpl( VolatileTransferServer server )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendEndedEvent" );
		base.fileSendEnded(server);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendEnded(server);
	}

	public void fireFileSendEndedEvent( VolatileTransferServer server )
	{
		fireFileSendEndedEventImpl( server );
	}

	protected void fireFileReceiveStartedEventImpl( VolatileDownloader down )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileReceiveStartedEvent" );
		base.fileReceiveStarted(down);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileReceiveStarted(down);
	}

	public void fireFileReceiveStartedEvent( VolatileDownloader down )
	{
		fireFileReceiveStartedEventImpl( down );
	}

	protected void fireFileSendErrorEventImpl( VolatileTransferServer server, Throwable e )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendErrorEvent" );
		base.fileSendError(server, e);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendError(server, e);
	}

	public void fireFileSendErrorEvent( VolatileTransferServer server, Throwable e )
	{
		fireFileSendErrorEventImpl( server, e );
	}

	protected void fireFileReceiveErrorEventImpl( VolatileDownloader down, Throwable e )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileReceiveErrorEvent" );
		base.fileReceiveError(down, e);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileReceiveError(down, e);
	}

	public void fireFileReceiveErrorEvent( VolatileDownloader down, Throwable e )
	{
		fireFileReceiveErrorEventImpl( down, e );
	}

	protected void fireWhoAddedMeEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireWhoAddedMeEvent" );
		base.whoAddedMe( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoAddedMe( friend );
	}

	public void fireWhoAddedMeEvent( MsnFriend friend )
	{
		fireWhoAddedMeEventImpl( friend );
	}

	protected void fireWhoRemovedMeEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireWhoRemovedMeEvent" );
		base.whoRemovedMe( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoRemovedMe( friend );
	}

	public void fireWhoRemovedMeEvent( MsnFriend friend )
	{
		fireWhoRemovedMeEventImpl( friend );
	}

	protected void fireBuddyListModifiedEventImpl()
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireBuddyListModifiedEvent" );
		base.buddyListModified();
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).buddyListModified();
	}

	public void fireBuddyListModifiedEvent()
	{
		fireBuddyListModifiedEventImpl();
	}

	protected void fireAddFailedEventImpl( int errcode )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireAddFailedEvent" );
		base.addFailed( errcode );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).addFailed( errcode );
	}

	public void fireAddFailedEvent( int errcode )
	{
		fireAddFailedEventImpl( errcode );
	}

	protected void fireLoginErrorEventImpl( String header )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireLoginErrorEvent" );
		base.loginError(header);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).loginError(header);
	}

	public void fireLoginErrorEvent( String header )
	{
		fireLoginErrorEventImpl( header );
	}

	protected void fireRenameNotifyEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireRenameNotifyEvent" );
		base.renameNotify( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).renameNotify(friend);
	}

	public void fireRenameNotifyEvent( MsnFriend friend )
	{
		fireRenameNotifyEventImpl( friend );
	}

	protected void fireAllListUpdatedEventImpl()
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireAllListUpdatedEvent" );
		base.allListUpdated();
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).allListUpdated();
	}

	public void fireAllListUpdatedEvent()
	{
		fireAllListUpdatedEventImpl();
	}

	protected void fireLogoutNotifyEventImpl()
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireLogoutNotifyEvent" );

	    base.logoutNotify();
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).logoutNotify();
	}

	public void fireLogoutNotifyEvent()
	{
		fireLogoutNotifyEventImpl();
	}

	protected void fireNotifyUnreadMailImpl( Properties mime, int unread )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireNotifyUnreadMail" );
		base.notifyUnreadMail( mime, unread );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).notifyUnreadMail( mime, unread );
	}

	public void fireNotifyUnreadMail( Properties mime, int unread )
	{
		fireNotifyUnreadMailImpl( mime, unread );
	}

	/**
	 * 해당 이벤트리스너를 해제한다.
	 */
	public synchronized void removeMsnListener( MsnListener l )
	{
		listeners.remove(l);
	}

	/**
	 * 설정된 로그인 이름(LoginName)을 반환한다.
	 */
	public String getLoginName()
	{
		return this.loginName;
	}

	/**
	 * 사용자의 비밀번호를 반환한다.
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * 자기 자신의 MsnFriend 인스턴스를 반환한다.
	 * 만약 로그인하지 않았다면, null을 반환할 것이다.
	 */
	public MsnFriend getOwner()
	{
		return this.owner;
	}

	/**
	 * DS 에 접속한다.
	 * @deprecated 새로운 MSN에서는 DS를 사용하지 않으므로 사용을 금한다.
	 */
	private void dispatch()
	{
		throw new UnsupportedOperationException("DispatchServer not allowed");
	}

	/*
	 * 실제로 NS 에 접속한다.
	 */
	protected void loginImpl()
	{
		this.ns = new NotificationProcessor( this, ServerInfo.getDefaultServerInfo(), 1 );
		initLogon();
		this.ns.start();
	}

	/**
	 * 주어진 이름과 비밀번호로 로그인을 시작한다.
	 * 이럴 경우 생성자에서 받은 이름과 비밀번호는 무시된다.
	 */
	public void login( String username, String password )
	{
		this.loginName = username;
		this.password = password;

		loginImpl();
	}

	/**
	 * 로그인을 시작한다.
	 */
	public void login()
	{
		if( loginName==null || password==null )
			throw new IllegalArgumentException( "Login name and password must not be null" );
		login( this.loginName, this.password );
	}

	/**
	 * 열려있던 모든 Switchboard session과의 연결을 종료하고
	 * DS, NS에 Logout한 후 연결을 종료한다.
	 */
	public void logout()
	{
		Enumeration e = sessionMap.elements();
		while( e.hasMoreElements() )
		{
			SwitchboardSession ss = (SwitchboardSession)e.nextElement();
			ss.interrupt();
			ss.cleanUp();
		}
		sessionMap.clear();
		
		if( ns!=null )
		{
			ns.interrupt();
			try	{
				ns.logout();
			} catch( IOException ex ) {}
			ns = null;
		}
	}

	/**
	 * 자신의 상태를 변경한다. 상태 문자열은 UserStatus 인터페이스에
	 * 정의되어있는 문자열만을 사용하여야만 한다.
	 * 로그인이 끝난 직후에는 Default로 온라인 상태가 되어있을 것이다.
	 */
	public void setMyStatus( String status ) throws IOException
	{
		this.ns.setMyStatus( status );
	}

	/**
	 * 현재 자기 자신의 상태코드값을 얻어온다.
	 */
	public String getMyStatus()
	{
		if( ns==null )
			return UserStatus.OFFLINE;
		return this.ns.getMyStatus();
	}

	public void setMyFriendlyName( String newName ) throws IOException
	{
		this.ns.setMyFriendlyName( newName );
	}

	/**
	 * 대화요청을 건다. 비동기적으로 처리되기 때문에,
	 * 이 메소드의 작업이 끝났다고 해서 연결이 이루어지는 것은 아니다.
	 * 이 메소드는 곧바로 return 된다.
	 * <p>
	 * 보통 연결이 이루어지는데는 2-3초 정도의 시간이 걸린다.
	 */
	public void doCall( String loginName ) throws IOException
	{
		ns.doCallFriend( loginName );
	}

	public void addFriend( String loginName ) throws IOException
	{
		ns.requestAdd( loginName );
	}

	public void addFriendAsList( String loginName, String listKind ) 
		throws IOException, IllegalArgumentException
	{
		ns.requestAddAsList( loginName, listKind );
	}

	public void blockFriend( String loginName ) throws IOException
	{
		ns.requestBlock( loginName, false );
	}

	public void unBlockFriend( String loginName ) throws IOException
	{
		ns.requestBlock( loginName, true );
	}

	public void removeFriend( String loginName ) throws IOException
	{
		ns.requestRemove( loginName );
	}

	public void removeFriendAsList( String loginName, String listKind ) 
		throws IOException, IllegalArgumentException
	{
		ns.requestRemoveAsList( loginName, listKind );
	}

	public void addGroup( String groupName ) throws IOException
	{
		ns.requestCreateGroup( groupName );
	}

	public void removeGroup( String groupIndex ) throws IOException
	{
	    ns.requestRemoveGroup( groupIndex );
	}

	public void renameGroup( String groupIndex, String newName ) throws IOException
	{
	    ns.requestRenameGroup( groupIndex, newName );
	}

	/**
	 * 주어진 친구를 그룹인덱스 old에서 new로 이동한다.
	 */
	public void moveGroupAsFriend( MsnFriend friend, String oldIndex, String newIndex )
		throws IOException
	{
		ns.requestMoveGroup( friend, oldIndex, newIndex );
	}

	/**
	 * doCall과 같은 일을 하지만, 세션이 연결될때까지 계속 기다린다는 것이
	 * 다르다.
	 * <p>
	 * Object.wait 메소드를 사용하여 기다리게 되고, 세션 연결 메시지가 올때까지
	 * 계속 대기하게 된다.
	 */
	public SwitchboardSession doCallWait( String loginName )
		throws IOException, InterruptedException
	{
		return ns.doCallFriendWait( loginName );
	}

	/**
	 * 해당 loginName이 포함된 세션중 무작위로 첫번째 세션을 찾아 반환한다.
	 * 만약 그러한 세션이 존재하지 않는다면, null을 반환한다.
	 *
	 * @return  loginName이 포함된 세션이 없다면 null을 반환.
	 */
	public SwitchboardSession findSwitchboardSession( String loginName )
	{
		Enumeration e = sessionMap.elements();
		while( e.hasMoreElements() )
		{
			SwitchboardSession ss = (SwitchboardSession)e.nextElement();
			if( ss.isInFriend(loginName) )
				return ss;
		}
		return null;
	}

	/**
	 * 해당 loginName과 1:1로 연결된 세션을 찾아준다.
	 * 만약 세션이 존재하지 않는다면, null을 반환한다.
	 */
	public SwitchboardSession findSwitchboardSessionAt( String loginName )
	{
		Enumeration e = sessionMap.elements();
		while( e.hasMoreElements() )
		{
			SwitchboardSession ss = (SwitchboardSession)e.nextElement();
			if( ss.getFriendCount()==1 && ss.isInFriend(loginName) )
				return ss;
		}
		return null;
	}

	/**
	 * 주어진 loginName을 가진 사용자에게 MIME 메시지를 전달한다.
	 * 만약 loginName을 가진 사용자와의 열린 session이 존재하지 않는다면,
	 * 즉시 false를 반환할 것이다.
	 *
	 * @return true - 성공적으로 전송하였을때,
	 *         false - 보내기가 실패했을때.
	 */
	public boolean sendMessage( String loginName, MimeMessage msg ) throws IOException
	{
		SwitchboardSession ss = findSwitchboardSession(loginName);
		if( ss!=null )
		{
			ss.sendMessage( msg );
			return true;
		}
		return false;
	}

	/**
	 * 주어진 loginName을 가진 사용자에게 MIME 메시지를 전달한다.
	 * 대신 loginName이 여러개일 경우 sessionId와 일치하는 세션으로만 전송한다.
	 * 만약 일치하는 세션이 없다면 메시지는 전달되지 않을 것이며,
	 * sessionId가 null이라면 첫번째 발견되는 임의의 세션에 전달될 것이다.
	 * <p>
	 * 물론 sessionId가 null이고 loginName을 포함하는 세션이 존재하지 않는다면
	 * 전송되지 않고 false를 반환한다.
	 *
	 * @return true - 성공적으로 전송하였을때,
	 *         false - 보내기가 실패했을때.
	 */
	public boolean sendMessage( String loginName, MimeMessage msg, String sessionId )
		throws IOException
	{
		SwitchboardSession ss = (SwitchboardSession)sessionMap.get(sessionId);
		if( ss==null )
		{
			return sendMessage(loginName, msg);
		}
		ss.sendMessage( msg );
		return true;
	}

	/**
	 * 주어진 loginName을 가진 사용자에게 MIME 메시지를 전달한다.
	 * 만약 일치하는 세션이 없다면 메시지는 전달되지 않을 것이며,
	 * session이 null이라면 전송되지 않을 것이다.
	 *
	 * @return true - 성공적으로 전송하였을때,
	 *         false - 보내기가 실패했을때.
	 */
	public boolean sendMessage( MimeMessage msg, SwitchboardSession session )
		throws IOException
	{
		if( session!=null )
		{
			session.sendMessage( msg );
			return true;
		}
		return false;
	}

	/**
	 * 해당 sessionId 세션으로 파일을 전송하기 위해 loginName에게
	 * 파일을 송신요청 메시지를 보낸다.
	 *
	 */
	public void sendFileRequest( String loginName, File file, String sessionId )
		throws IOException
	{
		if( sessionId==null )
			throw new IllegalArgumentException( "session id must not be null" );

		sendFileRequest( loginName, file, (SwitchboardSession)sessionMap.get(sessionId) );
	}

	/**
	 * 해당 sessionId 세션으로 파일을 전송하기 위해 loginName에게
	 * 파일을 송신요청 메시지를 보낸다.
	 *
	 */
	public void sendFileRequest( String loginName, File file, SwitchboardSession session )
		throws IOException
	{
		if( session==null )
			throw new IllegalArgumentException( "session must not be null" );

		FileTransferMessage msg = FileTransferMessage.createInviteMessage(file);
		ToSendFile toSend = new ToSendFile( msg.getProperty("Invitation-Cookie"), loginName, file );
		session.sendFileRequest( toSend, msg );
	}

	public List getOpenedSwitchboardSessions() 
	{
		ArrayList list = new ArrayList();
		list.addAll( sessionMap.values() );
		return list;
	}

	/////////////////////////////////////////////////////////////////////

	private class Listener extends MsnAdapter
	{
		public void renameNotify( MsnFriend own )
		{
			if( own!=null && owner.getLoginName().equals(own.getLoginName()) )
				owner.setFriendlyName( own.getFriendlyName() );
		}
		public void loginComplete( MsnFriend own )
		{
			isLogged = true;
			owner = own;
			/*
			if( ctxPhoto!=null )
				owner.setPhotoContext( ctxPhoto );
			*/
		}
		public void logoutNotify()
		{
			isLogged = false;
		}

		public void listOnline( MsnFriend friend )
		{
			/*
			 * add 메소드는, 이미 존재하는 사용자라면 값만 변경하도록 되어있다.
			 */
			forwardList.add( friend );
		}

		public void userOnline( MsnFriend friend )
		{
			forwardList.add( friend );
		}

		public void userOffline( String loginName )
		{
			forwardList.setOffline( loginName );
		}

		public void switchboardSessionStarted( SwitchboardSession ss )
		{
			String sid = ss.getSessionId();
			if( sid==null )
				return;

			sessionMap.put( sid, ss );
			/* 세션이 시작되었다고 상대방이 반드시 들어온것은 절대 아니다. */
		}

		public void switchboardSessionEnded( SwitchboardSession ss )
		{
			String sid = ss.getSessionId();
			sessionMap.remove( sid );
		}

		public void whoAddedMe( MsnFriend friend )
		{
			System.out.println( friend + " Add me." );
		}

		public void whoRemovedMe( MsnFriend friend )
		{
			System.out.println( friend.getLoginName() + " remove me." );
		}
	}
}
