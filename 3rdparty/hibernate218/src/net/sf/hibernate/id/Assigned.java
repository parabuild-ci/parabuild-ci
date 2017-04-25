//$Id: Assigned.java,v 1.9 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * <b>assigned</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that returns the current identifier assigned 
 * to an instance.
 * 
 * @author Gavin King
 */

public class Assigned implements IdentifierGenerator {
	
	public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
		if (obj instanceof PersistentCollection) throw new IdentifierGenerationException(
			"Illegal use of assigned id generation for a toplevel collection"
		);
		final Serializable id = session.getPersister(obj).getIdentifier(obj);
		if (id==null) throw new IdentifierGenerationException(
			"ids for this class must be manually assigned before calling save(): " + obj.getClass().getName()
		);
		return id;
	}
}






