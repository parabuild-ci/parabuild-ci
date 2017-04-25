package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * "Batch" loads collections, using multiple foreign key values in the
 * SQL <tt>where</tt> clause.
 * 
 * @see CollectionLoader
 * @see OneToManyLoader
 * @author Gavin King
 */
public class BatchingCollectionInitializer implements CollectionInitializer {
	
	private static final Log log = LogFactory.getLog(BatchingCollectionInitializer.class);
	
	private final Loader nonBatchLoader;
	private final Loader batchLoader;
	private final Loader smallBatchLoader;
	private final int batchSize;
	private final int smallBatchSize;
	private final CollectionPersister collectionPersister;
	
	public BatchingCollectionInitializer(CollectionPersister collPersister, int batchSize, Loader batchLoader, int smallBatchSize, Loader smallBatchLoader, Loader nonBatchLoader) {
		this.batchLoader = batchLoader;
		this.nonBatchLoader = nonBatchLoader;
		this.batchSize = batchSize;
		this.collectionPersister = collPersister;
		this.smallBatchLoader = smallBatchLoader;
		this.smallBatchSize = smallBatchSize;
	}

	public void initialize(Serializable id, SessionImplementor session)
		throws SQLException, HibernateException {
		Serializable[] batch = session.getCollectionBatch(collectionPersister, id, batchSize);
		if ( smallBatchSize==1 || batch[smallBatchSize-1]==null ) {
			nonBatchLoader.loadCollection( session, id, collectionPersister.getKeyType() );
		}
		else if ( batch[batchSize-1]==null ) {
			if ( log.isDebugEnabled() ) log.debug( "batch loading collection role (small batch): " + collectionPersister.getRole() );
			Serializable[] smallBatch = new Serializable[smallBatchSize];
			System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
			smallBatchLoader.loadCollectionBatch( session, smallBatch, collectionPersister.getKeyType() );
			log.debug("done batch load");
		}
		else {
			if ( log.isDebugEnabled() ) log.debug( "batch loading collection role: " + collectionPersister.getRole() );
			batchLoader.loadCollectionBatch( session, batch, collectionPersister.getKeyType() );
			log.debug("done batch load");
		}
	}

}
