/*
 * @(#)Group.java
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
 *    $Id: Group.java,v 1.6 2005/05/01 23:55:57 xrath Exp $ 
 */
package rath.msnm.entity;

import java.io.Serializable;
import rath.msnm.util.StringUtil;
/**
 * MSN 메신져의 <b>그룹</b>을 represent 하는 class
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: Group.java,v 1.6 2005/05/01 23:55:57 xrath Exp $ 
 */
public class Group implements Serializable
{
	private String name = null;
	private String fName = null;
	private String index;

	public Group( String name )
	{
		this.name = name;
	}

	public Group( String name, String index )
	{
		setName( name );
		setIndex( index );
	}

	public void setName( String name )
	{
		this.name = name;
		if( name!=null )		
		{
			fName = StringUtil.replaceString(this.name, "%20", " ");
			fName = StringUtil.replaceString(this.name, "%25", "%");
		}
	}

	public String getName()
	{
		return this.name;	
	}

	public String getFormattedName()
	{
		return this.fName;
	}

	public void setIndex( String index )
	{
		this.index = index;
	}

	public String getIndex()
	{
		return this.index;
	}

	public boolean equals( Object o )
	{
		if( this==o ) return true;
		if( o==null ) return false;

		if( o instanceof Group )
		{
			Group g = (Group)o;
			return g.index.equals(this.index);
		}
		return false;
	}
	
	public int hashCode()
	{
		return this.index.hashCode();
	}

	public String toString()
	{
		return fName;	
	}
}
