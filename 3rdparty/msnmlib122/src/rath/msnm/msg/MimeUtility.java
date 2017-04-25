/*
 * @(#)MimeUtility.java
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
 *    $Id: MimeUtility.java,v 1.6 2004/08/04 07:01:46 xrath Exp $
 */
package rath.msnm.msg;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
/**
 *
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: MimeUtility.java,v 1.6 2004/08/04 07:01:46 xrath Exp $
 */
public class MimeUtility
{
	static URLCodec codec = null;
	static 
	{
		String vmspec = System.getProperty("java.specification.version");
		float version = 1.3f;
		try
		{
			version = Float.parseFloat(vmspec);
		}
		catch( NumberFormatException e )
		{
			
		}

		if( version >= 1.4f )
			codec = new Wrapper();
		else
			codec = new ManualImpl();
	}

	public static String getURLEncodedString( String str ) 
		throws UnsupportedEncodingException
	{
		return codec.getURLEncodedString( str );
	}

	public static String getURLEncodedString( String str, String charset )
		throws UnsupportedEncodingException
	{
		return codec.getURLEncodedString( str, charset );
	}

	public static String getURLDecodedString( String str ) 
		throws UnsupportedEncodingException
	{
		return codec.getURLDecodedString( str );
	}

	public static String getURLDecodedString( String str, String charset )
		throws UnsupportedEncodingException
	{
		return codec.getURLDecodedString( str, charset );
	}

	static interface URLCodec
	{
		public String getURLEncodedString( String str ) 
			throws UnsupportedEncodingException;
		public String getURLEncodedString( String str, String charset )
			throws UnsupportedEncodingException;
		public String getURLDecodedString( String str ) 
			throws UnsupportedEncodingException;
		public String getURLDecodedString( String str, String charset )
			throws UnsupportedEncodingException;
	}

	static class Wrapper implements URLCodec
	{
		public String getURLEncodedString( String str ) throws UnsupportedEncodingException
		{
			return getURLEncodedString( str, System.getProperty("file.encoding") );
		}
		public String getURLEncodedString( String str, String charset )
			throws UnsupportedEncodingException
		{
			return URLEncoder.encode(str, charset);
		}
		public String getURLDecodedString( String str ) throws UnsupportedEncodingException
		{
			return getURLDecodedString( str, System.getProperty("file.encoding") );
		}
		public String getURLDecodedString( String str, String charset )
			throws UnsupportedEncodingException
		{
			return URLDecoder.decode(str, charset);
		}
	}

	static class ManualImpl implements URLCodec
	{
		public String getURLEncodedString( String str )
			throws UnsupportedEncodingException
		{
			return getURLEncodedString( str, System.getProperty("file.encoding") );
		}

		public String getURLEncodedString( String str, String encoding )
			throws UnsupportedEncodingException
		{
			StringBuffer sb = new StringBuffer();
			byte[] b = str.getBytes(encoding);
			for(int i=0; i<b.length; i++)
			{
				sb.append( "%" );
				int value = (int)(b[i] < 0 ? b[i]+0x100 : b[i]);
				String hexa = Integer.toHexString(value);
				if( hexa.length()==1 )
					sb.append("0");
				sb.append( hexa.toUpperCase() );
			}
			return sb.toString();
		}

		public String getURLDecodedString( String str )
			throws UnsupportedEncodingException
		{
			return getURLDecodedString( str, System.getProperty("file.encoding") );
		}

		public String getURLDecodedString( String str, String encoding )
			throws UnsupportedEncodingException
		{
			StringBuffer sb = new StringBuffer();
			ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
			boolean isURLEncode = false;
			for(int i=0, len=str.length(); i<len; i++)
			{
				if( str.charAt(i)=='%' )
				{
					int v = -1;
					try
					{
						v = Integer.valueOf(str.substring(i+1, i+3), 0x10).intValue();
						bos.write( v );
						isURLEncode = true;
						i+=2;
					}
					catch( NumberFormatException e ) 
					{
						sb.append( str.charAt(i) );
					}
				}
				else
				{
					if( isURLEncode )
					{
						isURLEncode = false;
						sb.append( new String(bos.toByteArray(), encoding) );
						bos.reset();
					}
					sb.append( str.charAt(i) );
				}
			}
			if( isURLEncode )
				sb.append( new String(bos.toByteArray(), encoding) );
			return sb.toString();
		}
	}
};
