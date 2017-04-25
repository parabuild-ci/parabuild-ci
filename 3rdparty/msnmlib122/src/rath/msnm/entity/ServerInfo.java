/*
 * @(#)ServerInfo.java
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
 *    $Id: ServerInfo.java,v 1.7 2004/12/24 22:05:52 xrath Exp $
 */
package rath.msnm.entity;
/**
 * XFR등의 접속해야할 서버가 referred될때마다
 * 원격 서버의 위치정보를 나타내는 entity class이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: ServerInfo.java,v 1.7 2004/12/24 22:05:52 xrath Exp $
 */
public class ServerInfo
{
	/**
	 * Default port number for <b>DS</b>(Dispatch server) and
	 * <b>SS</b>(Switchboard server)
	 */
	public static final int DEFAULT_PORT = 1863;

	private String host = null;
	private int port;

	/**
	 * 주어진 host와 DEFAULT_PORT를 가지는 ServerInfo 객체를 생성한다.
	 */
	public ServerInfo( String host )
	{
		this.host = host;
		this.port = DEFAULT_PORT;
	}

	/**
	 * 주어진 host와 port를 가지는 ServerInfo 객체를 생성한다.
	 */
	public ServerInfo( String host, int port )
	{
		this.host = host;
		this.port = port;
	}

	public String getHostAddress()
	{
		return this.host;
	}

	public int getPort()
	{
		return this.port;
	}

	/**
	 * 2001년 11월 7일 현재 MSN messenger의 DS(Dispatch Server)의
	 * Host와 port를 가지는 ServerInfo객체를 생성하여 반환해준다.
	 *
	 * @return  Default DS ServerInfo instance of MSN messenger.
	 * @deprecated 새로운 MSN에서는 DS를 필요로 하지 않는 구조이므로 사용하지 않는다.
	 */
	public static ServerInfo getDefaultDispatchServerInfo()
	{
		return new ServerInfo( "64.4.13.58", DEFAULT_PORT );
	}

	/**
	 *
	 */
	public static ServerInfo getDefaultServerInfo()
	{
        return new ServerInfo( "messenger.hotmail.com", DEFAULT_PORT );
	}

	public String toString()
	{
		return "Host: " + host + ", Port: " + port;
	}
};
