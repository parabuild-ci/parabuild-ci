//$Id: BuyNow.java,v 1.3 2004/06/04 01:27:33 steveebersole Exp $
package org.hibernate.auction;

/**
 * @author Gavin King
 */
public class BuyNow extends Bid {
	public boolean isBuyNow() {
		return true;
	}
	public String toString() {
		return super.toString() + " (buy now)";
	}
}
