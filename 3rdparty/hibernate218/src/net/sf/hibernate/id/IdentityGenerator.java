//$Id: IdentityGenerator.java,v 1.6 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * The IdentityGenerator for autoincrement/identity key generation.
 * <br><br>
 * Indicates to the <tt>Session</tt> that identity (ie. identity/autoincrement
 * column) key generation should be used.
 * 
 * @author Christoph Sturm
 */
public class IdentityGenerator implements IdentifierGenerator {
	
	public Serializable generate(SessionImplementor s, Object obj) throws SQLException, HibernateException {
		return IdentifierGeneratorFactory.IDENTITY_COLUMN_INDICATOR;
	}

}






