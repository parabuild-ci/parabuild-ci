/*
 * @(#)ToSendFile.java
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
 *    $Id: ToSendFile.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.ftp;

import java.io.File;
/**
 * 곧 전송하게 될 파일의 정보들을 캡슐화하는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: ToSendFile.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
public class ToSendFile 
{
	private String cookie; // Key
	private String receiverName = null;
	private File toSendFile = null;

	/**
	 * 보낼 파일에 대한 정보 객체를 생성한다.
	 *
	 * @param  cookie  유일성을 위한 쿠키값
	 * @param  loginName 받을 사람의 LoginName
	 * @param  file  전송하고자 하는 File 객체
	 */
	public ToSendFile( String cookie, String loginName, File file )
	{
		this.cookie = cookie;
		this.receiverName = loginName;
		this.toSendFile = file;
	}

	/**
	 * 설정된 쿠키값을 얻어온다.
	 */
	public String getCookie()
	{
		return this.cookie;
	}

	/**
	 * 설정된 쿠키값을 정수형으로 변환하여 가져온다.
	 */
	public int getCookieInt()
	{
		return Integer.parseInt(this.cookie);
	}

	/** 
	 * 파일을 받게될 사람의 LoginName을 반환한다.
	 */
	public String getReceiverName()
	{
		return this.receiverName;
	}

	/**
	 * 전송될 파일 객체를 얻어온다.
	 */
	public File getFile()
	{
		return this.toSendFile;
	}
}
