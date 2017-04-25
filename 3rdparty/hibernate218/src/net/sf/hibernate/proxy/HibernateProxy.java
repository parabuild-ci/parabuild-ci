//$Id: HibernateProxy.java,v 1.6 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;

public interface HibernateProxy extends Serializable {
	public Object writeReplace();
}







