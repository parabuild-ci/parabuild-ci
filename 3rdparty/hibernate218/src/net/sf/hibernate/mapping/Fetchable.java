//$Id: Fetchable.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.mapping;

/**
 * Any mapping with an outer-join attribute
 * @author Gavin King
 */
public interface Fetchable {
	public int getOuterJoinFetchSetting();
	public void setOuterJoinFetchSetting(int joinedFetch);
}
