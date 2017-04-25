//$Id: SimpleEntityLoader.java,v 1.15 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.type.Type;

/**
 * Loads entity instances one instance per select, without without any outerjoin fetching.
 * <br>
 * The <tt>ClassPersister</tt> must implement <tt>Loadable</tt>. For other entities,
 * create a customized subclass of <tt>Loader</tt>.
 * 
 * @see EntityLoader
 * @author Gavin King
 */
public final class SimpleEntityLoader extends Loader implements UniqueEntityLoader {
	
	private final Loadable[] persister;
	private final Type idType;
	private final String sql;
	private final LockMode[] lockMode;
	
	public SimpleEntityLoader(Loadable persister, String sql, LockMode lockMode) {
		this.persister = new Loadable[] {persister};
		this.idType = persister.getIdentifierType();
		this.sql = sql;
		this.lockMode = new LockMode[] { lockMode };
		postInstantiate();
	}
	
	protected String getSQLString() {
		return sql;
	}
	
	protected Loadable[] getPersisters() {
		return persister;
	}
	
	protected CollectionPersister getCollectionPersister() {
		return null;
	}
	
	protected String[] getSuffixes() {
		return NO_SUFFIX;
	}
	
	public Object load(SessionImplementor session, Serializable id, Object object) throws HibernateException, SQLException {
		List list = loadEntity(session, id, idType, object, id);
		if ( list.size()==1 ) {
			return list.get(0);
		}
		else if ( list.size()==0 ) {
			return null;
		}
		else {
			throw new HibernateException( "More than one row with the given identifier was found: " + id + ", for class: " + persister[0].getClassName() );
		}
	}
	
	
	protected LockMode[] getLockModes(Map lockModes) {
		return lockMode;
	}

	protected Object getResultColumnOrRow(
		Object[] row,
		ResultSet rs,
		SessionImplementor session)
		throws SQLException, HibernateException {
		return row[0];
	}

	protected boolean isSingleRowLoader() {
		return true;
	}

	protected int[] getOwners() {
		return null;
	}

}






