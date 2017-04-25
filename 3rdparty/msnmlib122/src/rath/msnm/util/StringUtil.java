/*
 * @(#)StringUtil.java
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
 *    $Id: StringUtil.java,v 1.5 2004/12/24 22:05:53 xrath Exp $
 */
package rath.msnm.util;

import java.security.MessageDigest;
/**
 * String 조작에 관한 Utility성 정적메소드들을 가지고 있는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: StringUtil.java,v 1.5 2004/12/24 22:05:53 xrath Exp $
 */
public class StringUtil
{
	/**
	 * 주어진 origin 문자열에서 src 문자열을 모두 찾아 dest 문자열로 변경해준다.
	 */
    public static String replaceString( String origin, String src, String dest )
    {
        if( origin==null ) return null;
        StringBuffer sb = new StringBuffer( origin.length() );

        int srcLength = src.length();
        int destLength = dest.length();

        int preOffset = 0;
        int offset = 0;
        while( (offset=origin.indexOf( src, preOffset ))!=-1 )
        {
            sb.append( origin.substring( preOffset,offset ) );
            sb.append( dest );
            preOffset = offset + srcLength;
        }
        sb.append( origin.substring( preOffset, origin.length() ) );

        return sb.toString();
    }

	/**
	 * 주어진 문자를 MD5로 digest한 후 HEXA형태로 변환해준다.
	 */
	public static String md5( String str )
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			md.update( str.getBytes() );

			byte[] b = md.digest();
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<b.length; i++)
			{
				int v = (int)b[i];
				v = v < 0 ? 0x100+v : v;
				String cc = Integer.toHexString(v);
				if( cc.length()==1 )
					sb.append( '0' );
				sb.append( cc );
			}

			return sb.toString();
		}
		catch( Exception e ) {}
		return "";
	}
}
