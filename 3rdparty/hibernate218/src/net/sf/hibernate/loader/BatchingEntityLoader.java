package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.type.Type;

/**
 * "Batch" loads entities, using multiple primary key values in the
 * SQL <tt>where</tt> clause.
 * 
 * @see EntityLoader
 * @author Gavin King
 */
public class BatchingEntityLoader implements UniqueEntityLoader {
	
	private static final Log log = LogFactory.getLog(BatchingEntityLoader.class);
	
	private final Loader nonBatchLoader;
	private final Loader batchLoader;
	private final Loader smallBatchLoader;
	private final int batchSize;
	private final int smallBatchSize;
	private final ClassPersister persister;
	private final Type idType;
	
	public BatchingEntityLoader(ClassPersister persister, int batchSize, Loader batchLoader, int smallBatchSize, Loader smallBatchLoader, Loader nonBatchLoader) {
		this.batchLoader = batchLoader;
		this.nonBatchLoader = nonBatchLoader;
		this.batchSize = batchSize;
		this.persister = persister;
		this.smallBatchLoader = smallBatchLoader;
		this.smallBatchSize = smallBatchSize;
		idType = persister.getIdentifierType();
	}

	public Object load(SessionImplementor session, Serializable id, Object optionalObject)
		throws SQLException, HibernateException {
		Serializable[] batch = session.getClassBatch( persister.getMappedClass(), id, batchSize );
		List list;
		if ( smallBatchSize==1 || batch[smallBatchSize-1]==null ) {
			return ( (UniqueEntityLoader) nonBatchLoader ).load(session, id, optionalObject);
		}
		else if ( batch[batchSize-1]==null ) {
			if ( log.isDebugEnabled() ) log.debug( "batch loading entity (smaller batch): " + persister.getMappedClass().getName() );
			Serializable[] smallBatch = new Serializable[smallBatchSize];
			System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
			list = smallBatchLoader.loadEntityBatch(session, smallBatch, idType, optionalObject, id);
			log.debug("done batch load");
		}
		else {
			if ( log.isDebugEnabled() ) log.debug( "batch loading entity: " + persister.getMappedClass().getName() );
			list = batchLoader.loadEntityBatch(session, batch, idType, optionalObject, id);
			log.debug("done batch load");
		}
		
		// get the right object from the list ... would it be easier to just call getEntity() ??
		Iterator iter = list.iterator();
		while ( iter.hasNext() ) {
			Object obj = iter.next();
			if ( id.equals( session.getEntityIdentifier(obj) ) ) return obj;
		}
		return null;
	}

}
