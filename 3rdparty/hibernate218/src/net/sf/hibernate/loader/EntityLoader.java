//$Id: EntityLoader.java,v 1.23 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.OuterJoinLoadable;
import net.sf.hibernate.type.Type;

/**
 * Load an entity using outerjoin fetching to fetch associated entities.
 * <br>
 * The <tt>ClassPersister</tt> must implement <tt>Loadable</tt>. For other entities,
 * create a customized subclass of <tt>Loader</tt>.
 * 
 * @see SimpleEntityLoader
 * @author Gavin King
 */
public class EntityLoader extends AbstractEntityLoader implements UniqueEntityLoader {
	
	private final Type uniqueKeyType;
	private final boolean batchLoader;
	
	public EntityLoader(OuterJoinLoadable persister, int batchSize, SessionFactoryImplementor factory) throws MappingException {
		this( persister, persister.getIdentifierColumnNames(), persister.getIdentifierType(), batchSize, factory );
	}
	
	public EntityLoader(OuterJoinLoadable persister, String[] uniqueKey, Type uniqueKeyType, int batchSize, SessionFactoryImplementor factory) throws MappingException {
		super(persister, factory);
		
		this.uniqueKeyType = uniqueKeyType;
		
		List associations = walkTree(persister, getAlias(), factory);
		initClassPersisters(associations);
		String whereString = whereString( getAlias(), uniqueKey, batchSize ).toString();
		renderStatement(associations, whereString, factory);
		
		postInstantiate();
		
		batchLoader = batchSize > 1;

	}
	
	public Object load(SessionImplementor session, Serializable id, Object optionalObject) throws HibernateException, SQLException {
		return load(session, id, optionalObject, id);
	}
	
	public Object loadByUniqueKey(SessionImplementor session, Serializable id) throws HibernateException, SQLException {
		return load(session, id, null, null);
	}
	
	private Object load(SessionImplementor session, Serializable id, Object optionalObject, Serializable optionalId) throws HibernateException, SQLException {
		List list = loadEntity(session, id, uniqueKeyType, optionalObject, optionalId);
		if ( list.size()==1 ) {
			return list.get(0);
		}
		else if ( list.size()==0 ) {
			return null;
		}
		else {
			if ( getCollectionOwner()>-1 ) {
				return list.get(0);
			}
			else {
				throw new HibernateException( 
					"More than one row with the given identifier was found: " + 
					id + 
					", for class: " + 
					getPersister().getClassName() 
				);
			}
		}
	}

	protected Object getResultColumnOrRow(
		Object[] row,
		ResultSet rs,
		SessionImplementor session)
		throws SQLException, HibernateException {
		return row[row.length-1];
	}

	protected boolean isSingleRowLoader() {
		return !batchLoader;
	}

}






