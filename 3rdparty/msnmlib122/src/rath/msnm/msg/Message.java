/*
 * @(#)Message.java
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
 *    $Id: Message.java,v 1.4 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.msg;

import rath.msnm.entity.Callback;
import rath.msnm.util.StringList;
import rath.msnm.util.Stringator;
/**
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: Message.java,v 1.4 2004/12/24 22:05:53 xrath Exp $ 
 */
public abstract class Message
{
	private String header = null;
	private int trId = -1;
	private Callback callback = null;

	protected StringList list = null;

	protected Message( String header )
	{
		this.header = header;
		this.list = new StringList();
	}

	public int getTransactionId()
	{
		return this.trId;
	}

	public void setTransactionId( int id )
	{
		this.trId = id;
	}

	public int size()
	{
		return list.size();
	}

	public String get( int index )
	{
		return list.get(index);
	}

	public int getInt( int index )
	{
		return list.getInteger(index);
	}

	public String getHeader()
	{
		return this.header;
	}

	public void setHeader( String header )
	{
		this.header = header;
	}

	public void setBackProcess( Callback callback )
	{
		this.callback = callback;
	}

	public Callback getBackProcess()
	{
		return this.callback;
	}

	/**
	 * 이 메시지를 MSN Messenger서버가 인식할 수 있는 
	 * 형태의 String으로 변환해준다.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append( header );
		if( trId!=-1 )
		{
			sb.append( ' ' );
			sb.append( trId );
		}

		if( list.size()!=0 )
		{
			Stringator i = list.iterator();
			while( i.hasNext() )
			{
				sb.append( ' ' );
				sb.append( i.next() );
			}
		}		
		return sb.toString();
	}
}
