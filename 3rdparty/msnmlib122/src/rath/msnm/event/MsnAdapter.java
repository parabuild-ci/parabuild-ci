/*
 * @(#)MsnAdapter.java
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
 *    $Id: MsnAdapter.java,v 1.15 2005/05/01 23:55:57 xrath Exp $
 */
package rath.msnm.event;

import java.awt.Image;
import java.util.Properties;

import rath.msnm.SwitchboardSession;
import rath.msnm.entity.MsnFriend;
import rath.msnm.ftp.VolatileDownloader;
import rath.msnm.ftp.VolatileTransferServer;
import rath.msnm.msg.MimeMessage;
/**
 * 비동기적으로 MSN Server에서 도착하는 메시지 이벤트들을
 * 처리할 수 있는 이벤트 리스너 인터페이스를 구현한 텅빈 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: MsnAdapter.java,v 1.15 2005/05/01 23:55:57 xrath Exp $
 */
public class MsnAdapter implements MsnListener
{
	/**
	 * 로그인이 완료되었다는 메시지이다.
	 * 자신의 로그인이름(메일주소)와 닉네임을 던져준다.
	 */
	public void loginComplete( MsnFriend own )
	{

	}

	/**
	 * 존재하지 않는 아이디이거나, 비밀번호가 틀렸을 경우,
	 * 이 이벤트를 발송하고, 연결은 종료된다.
	 * 아무튼 로그인을 할 수 없을때 발생되는 이벤트이다.
	 */
	public void loginError( String header )
	{

	}

	public void listAdd( MsnFriend friend )
	{
		
	}

	/**
	 * 로그인 한 후, 상태를 온라인으로 바꾸었을때, 자신의
	 * Contact list에 있는 사용자중에 상태가 Online(혹은 substate)인
	 * 사용자들을 이 메소드를 통해 임의의 길이로 날려준다.
	 * 문제점은 호출 종료지점을 정확하게 알 수 없다는 것이다.
	 * <p>
	 * 만약 Online Contact list를 가지고 싶다면, MsnFriend 객체를
	 * Map에 저장해두면 편리하다. (Key값은 loginName으로 하면 더 좋다)
	 */
	public void listOnline( MsnFriend friend )
	{

	}

	/**
	 * 자신의 ContactList에 있는 사용자 중에 한명이 온라인이 되거나
	 * 상태를 변경하였을때, NS로부터 날라오는 메시지이다.
	 */
	public void userOnline( MsnFriend friend )
	{

	}

	/**
	 * 자신의 Foward ContactList에 있는 사용자중 온라인이였던 사용자가
	 * 오프라인으로 상태가 변경되었을때 NS로부터 날라오는 메시지이다.
	 */
	public void userOffline( String loginName )
	{

	}

	/**
	 * 누군가가 Switchboard server를 통해서 자신에게 대화요청을
	 * 하였고, 그 사람과의 session이 연결되었다는 것을 알려준다.
	 */
	public void switchboardSessionStarted( SwitchboardSession ss )
	{

	}

	/**
	 * 특정 세션으로 새로운 사용자가 입장하였음을 알려준다.
	 */
	public void whoJoinSession( SwitchboardSession ss, MsnFriend join )
	{

	}

	/**
	 * 특정 세션에서 사용자가 BYE 하고 세션을 끊었음을 알려준다.
	 */
	public void whoPartSession( SwitchboardSession ss, MsnFriend part )
	{

	}

	/**
	 * Switchboard session이 상대방으로부터 종료되었을때 불려진다.
	 */
	public void switchboardSessionEnded( SwitchboardSession ss )
	{

	}

	/**
	 * Switchboard session이 연결을 완료하지 못하고 SS로부터 연결이 끝어져버렸을때
	 * 발생하는 이벤트이다. SwitchboardSession의 Session id는 null일 것이다.
	 */
	public void switchboardSessionAbandon( SwitchboardSession ss, String targetName )
	{

	}

	/**
	 * 특정 Switchboard session에서 typingUser사용자가 메시지를 입력하는
	 * 중일때 발송될때 불려진다.
	 */
	public void progressTyping( SwitchboardSession ss,
		MsnFriend friend, String typingUser )
	{

	}

	/**
	 * 특정 Switchboard session에서 인스턴트 메시지를 받았을때 불려진다.
	 *
	 * @param  ss   해당 switchboard session
	 * @param  msg
	 */
	public void instantMessageReceived( SwitchboardSession ss,
		MsnFriend friend, MimeMessage mime )
	{

	}

	/**
	 * 누군가가 자신에게 파일을 보내려고 함을 알려주는 메소드이다.
	 *
	 * @param  ss  파일을 보내려고 한 사람과 연결된 Switchboard세션
	 * @param  cookie  응답에 필요한 쿠키값.
	 * @param  filename  보내려고하는 파일의 이름
	 * @param  filesize  보내려고하는 파일의 크기(byte단위)
	 * @since  0.3
	 */
	public void filePosted( SwitchboardSession ss, int cookie, String filename, int filesize )
	{

	}

	/**
	 * 보내려고 했던 파일에 대하여 상대방이 Accept해주었음을 알려주는 메소드이다.
	 * 이 이벤트가 발생하였을 경우에 특별히 해줘야할일은 없다.
	 * 단 이 이벤트가 발생한 후, 곧바로 실제로 파일을 전송하기위한 thread가
	 * 생성될 것이며 6891 port가 bind된다는 것을 참고하기 바란다.
	 *
	 * @param  ss  파일을 보내려고 한 사람과 연결된 Switchboard세션
	 * @param  cookie  초청시 사용되었던 쿠키값.
	 */
	public void fileSendAccepted( SwitchboardSession ss, int cookie )
	{

	}

	/**
	 * 보내려고 했던 파일에 대하여 상대방이 Reject했음을 알려주는 메소드이다.
	 * 이 이벤트가 발생하였을 경우에 특별히 해줘야할일은 없다.
	 *
	 * @param  ss  파일을 보내려고 한 사람과 연결된 Switchboard세션
	 * @param  cookie  초청시 사용되었던 쿠키값.
	 * @param  reason  거절된 이유를 나타내는 문자열 코드이다.
	 */
	public void fileSendRejected( SwitchboardSession ss, int cookie, String reason )
	{

	}

	/**
	 * 파일 보내기 작업이 시작되었음을 알려준다.
	 *
	 * @param  server  파일 송신 서버 객체.
	 */
	public void fileSendStarted( VolatileTransferServer server )
	{

	}

	/**
	 * 파일 보내기 작업이 종료되었음을 알려준다.
	 */
	public void fileSendEnded( VolatileTransferServer server )
	{

	}

	/**
	 * 파일 다운로드 작업이 시작되었음을 알려준다.
	 *
	 * @param  downloader  파일 다운로드 thread 객체.
	 */
	public void fileReceiveStarted( VolatileDownloader downloader )
	{

	}

	/**
	 * 파일을 송신하던중 예외가 발생하였음을 알려주는 메소드이다.
	 * 어쨌든, 파일 전송 thread는 종료된다.
	 */
	public void fileSendError( VolatileTransferServer server, Throwable e )
	{

	}

	/**
	 * 파일을 수신하던중 예외가 발생하였음을 알려주는 메소드이다.
	 * 어쨌든, 파일 전송 thread는 종료된다.
	 */
	public void fileReceiveError( VolatileDownloader downloader, Throwable e )
	{

	}

	/**
	 * 누군가 자신을 등록했음을 알려준다. RL만이 등록되어있으므로,
	 * 실제 자신의 FL에 등록하는 것은 자신이 직접해주어야 한다.
	 *
	 * @param  friend  등록한 사람의 LoginName, FriendlyName이 저장.
	 */
	public void whoAddedMe( MsnFriend friend )
	{

	}

	/**
	 * 누군가 자신을 삭제했음을 알려준다. RL에서만 삭제되므로, 구지 알려줄 필요는 없다.
	 *
	 * @param  friend  등록한 사람의 LoginName만이 온다. FriendlyName은 null일 것이다.
	 */
	public void whoRemovedMe( MsnFriend friend )
	{

	}

	/**
	 * BuddyList가 수정되어 Serial number가 변경된 직 후, 발송되는 이벤트이다.
	 */
	public void buddyListModified()
	{

	}

	/**
	 * 사용자 등록을 실패하였을때 발송되는 이벤트이다.
	 *
	 * @param  errorCode  서버로부터 받은 에러 코드 헤더
	 */
	public void addFailed( int errorCode )
	{

	}

	/**
	 * 친숙한 이름(FriendlyName)이 변경되었을때 발송되는 이벤트이다.
	 * 자신이 변경을 했을때도, 이 이벤트가 발송되며, 다른 인간이 변경해도
	 * 발송된다.
	 * <p>
	 * 만약 null이라면, 자기 자신의 이름 변경에 실패한 case이다.
	 */
	public void renameNotify( MsnFriend friend )
	{

	}

	/**
	 * 로그인 시, Synchronization value가 달랐을때, 서버로부터
	 * FL/AL/BL/RL, Group list등을 모두 받게 되는데, 만만치 않은
	 * 작업이므로, 모두 다 Update되었을때 통지되는 이벤트이다.
	 */
	public void allListUpdated()
	{

	}

	/**
	 * 로그아웃 되었음을 알려주는 이벤트이다. 반드시 정상적인 로그아웃만을
	 * 알려주는 것은 아니다. Emergency exception을 통해 NotificationProcessor가
	 * 종료되었을 경우에도 이벤트가 발생한다.
	 * <p>
	 * 즉, 결국 connection이 terminate되었음을 알려주는 것이다.
	 */
	public void logoutNotify()
	{

	}

	/**
	 * HotMail 계정사용시, 로그인시 읽지않은 편지에 대한 정보를 넘겨준다.
	 * Properties는 MSN 서버에서 넘겨준 MIME properties들이며, 이 중
	 * Inbox-Unread property를 사용하면 된다. 이것은 편의상 unread variable로
	 * 전달된다.
	 */
	public void notifyUnreadMail( Properties prop, int unread )
	{

	}

};
