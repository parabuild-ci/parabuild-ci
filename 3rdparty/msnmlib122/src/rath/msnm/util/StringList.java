/*
 * @(#)StringList.java
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
 *    $Id: StringList.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 */
package rath.msnm.util;
/**
 * Vector처럼 쓰는 것이지만, 내용물이 String이라는 제약이 있다.
 * 대신에, Cast하는데 필요한 비용을 절감할 수 있다.
 * 또한 생성자에 초기 capacity를 지정하지 않으면
 * 생성하지 못하게 함으로써, 경각심을 일으켜주는 클래스이다.
 * <p>
 * 또한 어느 메소드도 synchronized되어 있지 않으므로,
 * 접근 속도도 synchronized 키워드를 사용한 것보다 4배 빠르다.
 *
 * @version $Id: StringList.java,v 1.3 2004/12/24 22:05:53 xrath Exp $ 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 */
public class StringList implements java.io.Serializable, Cloneable
{
	private String[] strings = null;
	private int elementCount;

	/**
	 * capacity가 4인 StringList 객체를 생성한다.
	 *
	 * @param    capacity     Initial capacity를 지정한다.
	 */
	public StringList()
	{
		strings = new String[4];
	}

	/**
	 * StringList 객체를 생성한다.
	 *
	 * @param    capacity     Initial capacity를 지정한다.
	 */
	public StringList(int capacity)
	{
		strings = new String[capacity];
	}

	private void ensureCapacity( int minCapacity )
	{
		int oldCapacity = strings.length;
		if (minCapacity > oldCapacity)
		{
			String[] oldData = strings;
			int newCapacity = oldCapacity * 2;
			if( newCapacity < minCapacity )
			{
				newCapacity = minCapacity;
			}
			strings = new String[newCapacity];
			System.arraycopy(oldData, 0, strings, 0, elementCount+1-1 );
		}
	}

	/**
	 * 현재 내용물의 크기를 반환한다.
	 *
	 * @return    String내용물의 크기.
	 */
	public int size()
	{
		return elementCount;
	}

	/**
	 * index에 위치한 문자열을 반환한다.
	 *
	 * @param    index    가져올 문자열의 위치.
	 * @return    특정 위치에 있는 문자열
	 */
	public String get( int index )
	{
		return strings[index];
	}

	public int getInteger( int index )
	{
		return Integer.parseInt( strings[index] );
	}

	public boolean getBoolean( int index )
	{
		return Boolean.valueOf( strings[index] ).booleanValue();
	}

	/**
	 * index에 위치한 문자열을 제거한다.
	 * 제거한다는 것은 null로 만든다는 것이다.
	 * 막말로 remove를 한다고해서 size가 변하지는 않을 것이다.
	 *
	 * @param    index    제거할 문자열의 위치.
	 */
	public void remove( int index )
	{
		strings[index] = null;
	}

	/**
	 * 특정 위치에 특정 문자열을 위치시킨다.
	 *
	 * @param    index    교체시킬 위치
	 * @param    str    교체할 문자열
	 */
	public void setAt(int index, String str)
	{
		strings[index] = str;
	}

	/**
	 * StringArray에 문자열을 하나 추가한다.
	 * 공간이 모자를 경우 ensureCapacity메소드를 통해서
	 * 내부 배열의 크기를 확장한다.
	 *
	 * @param    str    추가할 문자열
	 */
	public void add( String str )
	{
		ensureCapacity(elementCount+1);
		strings[elementCount++] = str;
	}

	public void add( Object o )
	{
		add( String.valueOf(o) );
	}

	/**
	 * StringArray이 존재하는 모든 문자열을
	 * 제거한다.
	 */
	public void removeAll()
	{
		for(int i=0; i<elementCount; i++)
			strings[i] = null;
		elementCount = 0;
	}

	/**
	 * 현재 리스트에 들어가있는 내용을
	 * String 배열로 복사하여 반환한다.
	 *
	 * @return   String[] 형태의 내용물.
	 */
	public String[] toArray()
	{
		String[] str = new String[ elementCount ];
		System.arraycopy( strings, 0, str, 0, elementCount );
		return str;
	}

	/**
	 * 현재 StringList에 있는 모든 자료를
	 * Iterator형식으로 반환한다.
	 */
	public Stringator iterator()
	{
		return new Stringator()
		{
			private int offset = 0;

			public boolean hasNext()
			{
				return offset<elementCount;
			}

			public String next()
			{
				return strings[offset++];
			}

			public void remove()
			{
				strings[offset] = null;
			}
		};
	}

    /**
     * 복사한다. 
     */
    public synchronized Object clone()
    {
        try
        {
            StringList v = (StringList)super.clone();
            v.strings = new String[elementCount];
            System.arraycopy(strings, 0, v.strings, 0, elementCount);
            return v;
        }
        catch( CloneNotSupportedException e )
        {
            throw new InternalError();
        }
    }

	/**
	 * 이 StringArray이 나타낼 수 있는 모든 문자열을 반환한다.
	 *
	 * @return    이 StringArray이 나타낼 수 있는 문자열.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "[" );
		for(int i=0; i<elementCount; i++)
		{
			sb.append( strings[i] );
			if( (i+1)!=elementCount )
				sb.append( ", " );
		}
		sb.append( "]" );
		return sb.toString();
	}
}
