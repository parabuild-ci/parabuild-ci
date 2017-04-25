/*
 * @(#)BASE64.java
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
 *    $Id: BASE64.java,v 1.2 2004/12/24 22:05:53 xrath Exp $
 */
package rath.msnm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
/**
 * BASE64 (RFC 1521)를 직접 구현한 클래스이다.
 * <p>
 * 사용법은 간단하며 다음과 같다.
 * <p>
 * <pre><code>
 * String str = "안녕하세요 오랜만입니다." );
 * 
 * BASE64 b = new BASE64();
 * String encoded = b.encode( str );
 * String decode = b.decodeAsString( encoded );
 *
 * System.out.println( str.equals( decode ) );
 * </code></pre>
 * <p>
 * 위의 코드는 true를 리턴할 것이다.
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0.000, 2002/06/04
 */
public class BASE64
{
	static final char[] MAP = 
	{
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
		'w', 'x', 'y', 'z', '0', '1', '2', '3', 
		'4', '5', '6', '7', '8', '9', '+', '/',
	};

	static final int[] REVERSE_MAP = 
	{
		 0, // Padding for human readable index access
		 0,  0,  0,  0,  0,  0,  0,  0, 
		 0,  0,  0,  0,  0,  0,  0,  0, 
		 0,  0,  0,  0,  0,  0,  0,  0, 
		 0,  0,  0,  0,  0,  0,  0,  0, 
		 0,  0,  0,  0,  0,  0,  0,  0, 
		 0,  0, 62,  0,  0,  0, 63, 52,
		53, 54, 55, 56, 57, 58, 59, 60,
		61,  0,  0,  0,  0,  0,  0,  0, 
		 0,  1,  2,  3,  4,  5,  6,  7, 
		 8,  9, 10, 11, 12, 13, 14, 15,
		16, 17, 18, 19, 20, 21, 22, 23, 
		24, 25,  0,  0,  0,  0,  0,  0, 
		26, 27, 28, 29, 30, 31, 32, 33, 
		34, 35, 36, 37, 38, 39, 40, 41, 
		42, 43, 44, 45, 46, 47, 48, 49, 
		50, 51, 
	};

	private boolean hasCRLF = true;

	public BASE64()
	{
		this( true );
	}

	public BASE64( boolean hasCRLF )
	{
		setCRLF( hasCRLF );
	}

	public void setCRLF( boolean hasCRLF )
	{
		this.hasCRLF = hasCRLF;
	}

	public boolean hasCRLF()
	{
		return this.hasCRLF;
	}

	/**
	 * BASE64 로 인코딩된 문자열을 decode 한다.
	 * 만약 올바르지 않은 BASE64 라도, 되는대로 계속 디코딩하게 된다. 
	 * <p>
	 * 만약 byte[] 리턴되는 것이 귀찮을 경우 decodeAsString 메소드를 
	 * 사용하도록 하라.
	 */
	public byte[] decode( String str )
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(str.length());
		try
		{
			for(int i=0, len=str.length(); i<len; i++)
			{
				char c = str.charAt(i);
				if( c=='\r' || c=='\n' ) continue;
				int v0 = REVERSE_MAP[ str.charAt(i++) ];			
				int v1 = REVERSE_MAP[ str.charAt(i++) ];
				int v2 = REVERSE_MAP[ str.charAt(i++) ];
				int v3 = REVERSE_MAP[ str.charAt(i) ];
				int v = (v0 << 18) | (v1 << 12) | (v2 << 6) | (v3 << 0);
				bos.write( (v >> 16) & 0xff );
				bos.write( (v >>  8) & 0xff );
				bos.write( (v >>  0) & 0xff );
			}
		}
		catch( IndexOutOfBoundsException e ) {}
		return bos.toByteArray();
	}

	/**
	 * BASE64로 encode된 주어진 str 문자열을 decode 한 후,
	 * 시스템 default encoding 으로 문자열로 생성해준다.
	 * <p>
	 * 이 메소드는 내부적으로 decode 메소드를 직접 호출한다.
	 */
	public String decodeAsString( String str )
	{
		return new String( decode(str) );
	}

	/**
	 * 주어진 문자열을 시스템 default encoding으로 byte[] 로 변환 후,
	 * BASE64 로 인코딩한다.
	 */
	public String encode( String str )
	{
		return encode( str.getBytes() );
	}

	/**
	 * 주어진 byte[] 를 BASE64 로 인코딩한다.
	 */
	public String encode( byte[] b )
	{
		StringBuffer sb = new StringBuffer(b.length);
		int padCount = 0;
		ByteArrayInputStream bis = new ByteArrayInputStream( b );
		for(int i=0; i<b.length; i+=3)
		{
			int v0 = bis.read();
			int v1 = bis.read();
			int v2 = bis.read();
			if( v1==-1 ) { v1 = 0; padCount = 2; }
			else
			if( v2==-1 ) { v2 = 0; padCount = 1; }	
			int v = 
				((v0<<16) & 0x00FF0000) | 
				((v1<< 8) & 0x0000FF00) |
				((v2<< 0) & 0x000000FF);
			sb.append( MAP[(v>>18) & 0x3f] );
			sb.append( MAP[(v>>12) & 0x3f] );
			sb.append( MAP[(v>> 6) & 0x3f] );
			sb.append( MAP[(v>> 0) & 0x3f] );

			if( ((i+3)%57)==0 && hasCRLF )
				sb.append( '\n' );
		}
		for(int i=padCount, last=sb.length(); i>0; i--)
			sb.setCharAt( last-i, '=' );
		if( hasCRLF )
			sb.append( '\n' );
		return sb.toString();
	}
}
