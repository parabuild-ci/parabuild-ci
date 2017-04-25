/*
 * @(#)UserStatus.java
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
 *    $Id: UserStatus.java,v 1.3 2004/12/24 22:05:52 xrath Exp $ 
 */
package rath.msnm;
/**
 * 사용자 상태에 대한 constant들이 정의되어있는 인터페이스이다.
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: UserStatus.java,v 1.3 2004/12/24 22:05:52 xrath Exp $ 
 */
public interface UserStatus
{
	/** <b>온라인</b> 상태 */
	public static final String ONLINE = "NLN";
	/** <b>오프라인</b> 상태 */
	public static final String OFFLINE = "FLN";
	/** <b>오프라인으로 표시</b> 상태 */
	public static final String INVISIBLE = "HDN";

	/** <b>다른 용무중</b> 상태 */
	public static final String BUSY = "BSY";
	/** <b>자리 비움</b> 상태 */
	public static final String IDLE = "IDL";
	/** <b>곧 돌아오겠음</b> 상태 */
	public static final String BE_RIGHT_BACK = "BRB";
	/** <b>자동 자리 비움</b> 상태 */
	public static final String AWAY_FROM_COMPUTER = "AWY";
	/** <b>전화통화중</b> 상태 */
	public static final String ON_THE_PHONE = "PHN";
	/** <b>식사중</b> 상태 */
	public static final String ON_THE_LUNCH = "LUN";
};
