/*
 * @(#)Callback.java
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
 *    $Id: Callback.java,v 1.3 2004/12/24 22:05:52 xrath Exp $ 
 */
package rath.msnm.entity;

/**
 * TrID를 가지는 msn 프로토콜에 적합한 callback 함수를 가지는 클래스로써,
 * 사용빈도가 높은 클래스이다.
 * 
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: Callback.java,v 1.3 2004/12/24 22:05:52 xrath Exp $ 
 */
public class Callback
{
	private Class cl = null;
	private String methodName = null;
	private boolean infinite = false;
	private long creationTime;

	private Callback()
	{
		creationTime = System.currentTimeMillis();
	}

	/**
	 * 콜백메소드가 생성된 시간을 반환한다.
	 * 콜백이 생성된 후 일정시간이 지나도록 사용되지 않는다면,
	 * 응답이 무시되거나 지연되는 경우이기때문에, 삭제해주지 않으면, 
	 * 계속 메모리에 누적될수가 있다. 
	 * <p>
	 * 이것을 방지하기위하여 생성시간을 알아서 Timeout을 적용시켜야한다.
	 */
	public long getCreationTime()
	{
		return this.creationTime;
	}

	/**
	 * 한번 사용된 후 사라지지 않는 무한 콜백함수인지 아닌지 반환한다.
	 */
	public boolean isInfinite()
	{
		return this.infinite;
	}

	/**
	 * 이 콜백이 같은 trId에 대해 여러번 호출될 경우 설정한다.
	 */
	public void setInfinite()
	{
		this.infinite = true;
	}

	/**
	 * 콜백 메소드 이름을 반환한다.
	 */
	public String getMethodName()
	{
		return this.methodName;
	}

	/**
	 * 콜백 메소드가 존재하는 클래스의 참조를 반환한다.
	 */
	public Class getClassRef()
	{
		return this.cl;
	}

	/**
	 * 콜백 메소드 인스턴스를 새롭게 생성한다.
	 */
	public static Callback getInstance( String methodName, Class cl )
	{
		Callback call = new Callback();
		call.cl = cl;
		call.methodName = methodName;

		return call;
	}
};
