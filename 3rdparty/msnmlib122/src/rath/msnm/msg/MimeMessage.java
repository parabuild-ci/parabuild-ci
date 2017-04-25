/*
 * @(#)MimeMessage.java
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
 *    $Id: MimeMessage.java,v 1.17 2005/05/06 17:03:51 xrath Exp $
 */
package rath.msnm.msg;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * MIME 메시지들을 관리하는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: MimeMessage.java,v 1.17 2005/05/06 17:03:51 xrath Exp $
 */
public class MimeMessage
{
	public static final int KIND_PROFILE = 0;
	public static final int KIND_TYPING_USER = 1;
	public static final int KIND_MESSAGE = 2;
	public static final int KIND_FILE_TRANSFER = 3;
	public static final int KIND_MAIL_NOTIFY = 4;
//	public static final int KIND_P2P_MESSAGE = 5;
//	public static final int KIND_PTOP_MESSAGE = 6;
	public static final int KIND_UNKNOWN = 10;

	public static final String STR_PROFILE = "text/x-msmsgsprofile";
	public static final String STR_TYPING_USER = "text/x-msmsgscontrol";
	public static final String STR_MESSAGE = "text/plain";
	public static final String STR_FILE_TRANSFER = "text/x-msmsgsinvite";
	public static final String STR_MAIL_NOTIFY = "text/x-msmsgsinitialemailnotification";
//	public static final String STR_P2P_MESSAGE = "application/x-msnmsgrp2p-old";
//	public static final String STR_PTOP_MESSAGE = "application/x-msnmsgrp2p";

	private int kind = 0;

	private String message = null;
	private String ef = "";
	private String fn = "굴림";
	private Color fontColor = null;
	private Properties prop = new Properties();

	public MimeMessage()
	{
		this( null );
	}

	/**
	 * 주어진 메시지를 가지는 MIME 메시지 인스턴스를 생성한다.
	 */
	public MimeMessage( String message )
	{
		setMessage( message );
		this.fontColor = getRandomColor();
	}

	public MimeMessage( String message, Color fontColor )
	{
		setMessage( message );
		this.fontColor = fontColor;
		if( this.fontColor==null )
			this.fontColor = getRandomColor();
	}

	public MimeMessage( String message, Color fontColor, String font )
	{
		setMessage( message );
		this.fontColor = fontColor;
		this.fn = font;
		if( this.fontColor==null )
			this.fontColor = getRandomColor();
	}

	/**
	 * 이 MimeMessage의 type을 반환해준다.
	 * 현재는 로그인시 받는 profile 정보(KIND_PROFILE),
	 * Typing 중에 전송되는 typing 정보(KIND_TYPING_USER),
	 * 인스턴트 메시지가 전송될때 사용되는 정보(KIND_MESSAGE) 만으로
	 * 구성되어있다.
	 * 만약 알 수 이 외의 메시지가 올 경우는 KIND_UNKNOWN이
	 * 반환될 것이다.
	 */
	public int getKind()
	{
		return this.kind;
	}

	public void setKind( int kind )
	{
		this.kind = kind;
	}

	public String getFontName()
	{
		return this.fn;
	}

	public void setFontName( String fn )
	{
		this.fn = fn;
	}

	public Color getFontColor()
	{
		return this.fontColor;
	}

	public void setFontColor( Color c )
	{
		this.fontColor = c;
	}

	/**
	 * 전송하고자 하는 메시지를 설정한다.
	 * 다소 어색하지만, 이 메소드는 KIND_MESSAGE를 사용할 경우에만
	 * 사용된다.
	 */
	public void setMessage( String msg )
	{
		this.message = msg;
	}

	/**
	 * KIND_MESSAGE 종류일 때 도착한 인스턴트 메시지 내용을
	 * 얻어오는데 사용된다. 만약 KIND_UNKNOWN이였다면,
	 * raw 형태의 전체 문자열이 반환될 것이다.
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * 주어진 line을 파싱하여 property를 하나 추가한다.
	 * 만약 파싱할 수 없다면 false를 리턴할 것이고,
	 * 정상적인 형식을 가지고 있다면, property에 추가가 된 후
	 * true를 반환할 것이다.
	 */
	protected boolean addProperty( String line )
	{
		int i0 = line.indexOf( ": " );
		if( i0==-1 )
			return false;
		String key = line.substring( 0, i0 );
		String value = line.substring( i0+2 );

		prop.setProperty( key, value );
		return true;
	}

	/**
	 * 프로퍼티를 설정한다.
	 *
	 * @param  key  프로퍼티 키.
	 * @param  value  프로퍼티 값.
	 */
	public void setProperty( String key, String value )
	{
		this.prop.setProperty( key, value );
	}

	/**
	 * 현재 설정되어있는 프로퍼티들을 담고 있는 Properties 객체를 가져온다.
	 */
	public Properties getProperties()
	{
		return this.prop;
	}

	/**
	 * 특정 Mime header 값을 가져온다.
	 */
	public String getProperty( String key )
	{
		return this.prop.getProperty(key);
	}

	/**
	 * 주어진 key가 현재 mime properties에 존재하는지에 대한 여부를 얻어온다.
	 */
	public boolean hasProperty( String key )
	{
		return this.prop.containsKey(key);
	}

	public static class Unit
	{
		int mark = 0;
		String line = null;
	};

	/**
	 *
	 * @return \r\n이 발견된 offset + 1, 만약 발견되지 않을 경우 -1.
	 */
	protected static void readLine( byte[] b, Unit unit ) throws UnsupportedEncodingException
	{
		unit.line = null;
		for(int i=unit.mark; i<b.length; i++)
		{
			if( b[i]==10 )
			{
				unit.line = new String(b, unit.mark, i-unit.mark-1, "UTF-8");
				unit.mark = i+1;
				return;
			}
		}
		unit.mark = -1;
	}

	/**
	 * UTF-8로 구성된 raw 형태의 MIME 메시지를 받아 MIME 형식에 맞춰
	 * 파싱/분석하여 데이터를 채운다.
	 *
	 * @param  raw  String형태의 MIME 전체 메시지.
	 */
	public static MimeMessage parse( byte[] raw ) throws Exception
	{
		MimeMessage msg = new MimeMessage();

		Unit unit = new Unit();
		readLine(raw, unit);
		msg.addProperty( unit.line ); // MIME-Version
		readLine(raw, unit);
		msg.addProperty( unit.line ); // Content-Type

		String contentType = msg.prop.getProperty("Content-Type");

		if( contentType.equals(STR_TYPING_USER) )
		{
			msg.kind = KIND_TYPING_USER;
			readLine(raw, unit);
			msg.addProperty( unit.line );
		}
		else
		if( contentType.startsWith(STR_PROFILE) )
		{
			msg.kind = KIND_PROFILE;
			while( true )
			{
				readLine(raw, unit);
				if( unit.line==null )
					break;
				if( !msg.addProperty(unit.line) )
					break;
			}
		}
		else
		if( contentType.startsWith(STR_MAIL_NOTIFY) )
		{
			msg.kind = KIND_MAIL_NOTIFY;
			while( true )
			{
				readLine(raw, unit);
				if( unit.line==null )
					break;
				msg.addProperty(unit.line);
			}
		}
		else
		if( contentType.startsWith(STR_FILE_TRANSFER) )
		{
			/*
			 * FileTransferMessage로 변환할 수 있도록 한다.
			 */
			msg.kind = KIND_FILE_TRANSFER;
			while( true )
			{
				readLine(raw, unit);
				if( unit.line==null )
					break;
				if( unit.line.trim().length()==0 )
					continue;
				if( !msg.addProperty(unit.line) )
					break;
			}
		}
		else
		if( contentType.startsWith(STR_MESSAGE) )
		{
			msg.kind = KIND_MESSAGE;
			readLine(raw, unit);

			if( unit.line.trim().length() > 0 )
			{
				msg.addProperty( unit.line );
				readLine(raw, unit); // Temporary skip line.
			}

			msg.message = new String( raw, unit.mark, raw.length-unit.mark, "UTF-8" );
		}
		else
		{
			msg.kind = KIND_UNKNOWN;
			System.out.println( "UNKNOWN-Content: " + contentType );
			msg.message = new String(raw, "UTF-8");
			System.out.println( "Raw message: " );
			System.out.println( "-------------------------------------------" );
			System.out.println( raw );
			System.out.println( "-------------------------------------------" );
		}

		return msg;
	}

	private Color getRandomColor()
	{
		int r = (int)(Math.random() * 256.0D);
		int g = (int)(Math.random() * 256.0D);
		int b = (int)(Math.random() * 256.0D);
		
		if( r > 0xa0 ) r = 0xa0;
		if( g > 0xa0 ) g = 0xa0;
		if( b > 0xa0 ) b = 0xa0;
		return new Color( r, g, b );
	}

	private String getStringAsColor( Color c )
	{
		return Integer.toHexString(
			(c.getBlue()<<16) | (c.getGreen()<<8) | (c.getRed()<<0) );
	}

	public String getEffectCode()
	{
		return this.ef;
	}

	public void setEffectCode( String ef )
	{
		this.ef = ef;
	}

	/**
	 * Mime format을 가지는 UTF-8 base에 URL encoded된 byte array로
	 * 변형해 준다.
	 */
	public byte[] getInstantMessageBytes() throws UnsupportedEncodingException
	{
		StringBuffer sb = new StringBuffer();
		sb.append(
			"MIME-Version: 1.0" +
			"\r\n" +
			"Content-Type: text/plain; charset=UTF-8" +
			"\r\n" +
			"X-MMS-IM-Format: " +
			"FN=" );
		sb.append( MimeUtility.getURLEncodedString(this.fn, "UTF-8") );
		sb.append( "; EF=" );
		sb.append( ef );
		sb.append( "; CO=" );
		sb.append( getStringAsColor(this.fontColor) );
		sb.append(
			"; " +
			"CS=0" +
			"; " +
			"PF=22" +
			"\r\n\r\n" );
		sb.append( this.message );

		return sb.toString().getBytes("UTF-8");
	}

	public byte[] getTypingMessageBytes() throws UnsupportedEncodingException
	{
		StringBuffer sb = createMimeHeader( STR_TYPING_USER );
		sb.append( "TypingUser: " );
		sb.append( message );
		sb.append( "\r\n\r\n" );
		return sb.toString().getBytes("UTF-8");
	}

	/**
	 * 특정 Content-Type을 가지는 Mime header로 mime message를 생성해준다.
	 */
	protected StringBuffer createMimeHeader( String header )
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "MIME-Version: 1.0\r\n" +
			"Content-Type: " );
		sb.append( header );
		sb.append( "\r\n" );
		return sb;
	}

	/**
 	 * 현재 설정된 모든 Properties를 buf에 추가하고, 마지막에 \r\n를 한번 더 붙여주게 된다.
	 */
	protected StringBuffer fillMimeProperties( StringBuffer buf )
	{
		for( Enumeration e = prop.keys(); e.hasMoreElements(); )
		{
			String key = (String)e.nextElement();
			String value = prop.getProperty(key);

			buf.append( key );
			buf.append( ": " );
			buf.append( value );
			buf.append( "\r\n" );
		}
		buf.append( "\r\n" );
		return buf;
	}

	/**
	 * 설정된 종류(Kind)에 따라 적절하게 byte변환해준다.
	 */
	public byte[] getBytes() throws UnsupportedEncodingException
	{
		switch(this.kind)
		{
		case KIND_MESSAGE:
			return getInstantMessageBytes();
		case KIND_TYPING_USER:
			return getTypingMessageBytes();
		}
		return null;
	}
};
