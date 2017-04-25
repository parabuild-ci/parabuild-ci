/*
 * @(#)StreamingFile.java
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
 *    $Id: StreamingFile.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.ftp;

import java.io.InputStream;
/**
 * Input의 소스를 FileStream이 아닌 다른 것으로 교체할 수 있게된
 * java.io.File 인척하는 클래스이다.
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: StreamingFile.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
public class StreamingFile extends java.io.File
{
	private String filename = null;
	private long size;
	private InputStream in = null;

	public StreamingFile( String filename, long size, InputStream src )
	{
		super( filename );

		this.filename = filename;
		this.size = size;
		this.in = src;
	}

	/**
	 * 스트리밍이므로 언제나 true를 반환한다.
	 */
	public boolean exists()
	{
		return true;
	}

	/**
	 * 생성자에서 받은 이름을 그대로 반환한다.
	 */
	public String getName()
	{
		return this.filename;
	}

	/**
	 * 생성자에서 받은 이름을 그대로 반환한다.
	 */
	public String getAbsolutePath()
	{
		return getName();
	}

	/**
	 * 생성자에서 받은 크기를 그대로 반환한다.
	 */
	public long length()
	{
		return this.size;
	}

	/**
	 * 생성자에서 받은 InputStream을 그대로 반환한다.
	 */
	public InputStream getInputStream()
	{
		return this.in;
	}
}
