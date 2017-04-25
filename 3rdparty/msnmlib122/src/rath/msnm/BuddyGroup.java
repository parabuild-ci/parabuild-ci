/*
 * @(#)BuddyGroup.java
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
 *    $Id: BuddyGroup.java,v 1.6 2005/05/08 20:51:48 xrath Exp $ 
 */
package rath.msnm;

/**
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: BuddyGroup.java,v 1.6 2005/05/08 20:51:48 xrath Exp $ 
 */
public class BuddyGroup
{
	public static final int FORWARD = 0x01;
	public static final int ALLOW = 0x02;
	public static final int BLOCK = 0x04;
	public static final int REVERSE = 0x08;
	public static final int NEWBIE = 0x10;

	private final BuddyList listForward;
	private final BuddyList listAllow;
	private final BuddyList listBlock;
	private final BuddyList listReverse;
	private final BuddyList listAll;
	private final GroupList groupList;

	protected BuddyGroup()
	{
		listForward = new BuddyList("FL");
		listAllow = new BuddyList("AL");
		listBlock = new BuddyList("BL");
		listReverse = new BuddyList("RL");
		listAll = new BuddyList("LST");

		groupList = new GroupList();
	}

	public GroupList getGroupList()
	{
		return this.groupList;
	}

	public BuddyList getAllList()
	{
		return this.listAll;
	}

	public BuddyList getForwardList()
	{
		return this.listForward;
	}

	public BuddyList getAllowList()
	{
		return this.listAllow;
	}

	public BuddyList getBlockList()
	{
		return this.listBlock;	
	}

	public BuddyList getReverseList()
	{
		return this.listReverse;
	}

	public static boolean isListForward( int id )
	{
		if( (id & FORWARD) == FORWARD )
			return true;
		return false;
	}

	public static boolean isListAllow( int id )
	{
		if( (id & ALLOW) == ALLOW )
			return true;
		return false;
	}

	public static boolean isListBlock( int id )
	{
		if( (id & BLOCK) == BLOCK )
			return true;
		return false;
	}

	public static boolean isListReverse( int id )
	{
		if( (id & REVERSE) == REVERSE )
			return true;
		return false;
	}

	public static boolean isNewbie( int id )
	{
		if( (id & NEWBIE) == NEWBIE )
			return true;
		return false;
	}

	/**
	 *
	 * @deprecated Not used in MSNP9
	 */
	public BuddyList getListAsCode( String code )
	{
		if( code.equals("FL") )
			return listForward;
		else
		if( code.equals("AL") )
			return listAllow;
		else
		if( code.equals("BL") )
			return listBlock;
		else
		if( code.equals("RL") )
			return listReverse;
		return null;
	}

	public void clear()
	{
		this.listAllow.clear();
		this.listBlock.clear();
		this.listForward.clear();
		this.listReverse.clear();
	}

	public static BuddyGroup getInstance()
	{
		return new BuddyGroup();
	}
}
