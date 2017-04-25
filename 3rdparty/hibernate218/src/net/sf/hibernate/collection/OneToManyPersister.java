//$Id: OneToManyPersister.java,v 1.7 2004/11/11 20:42:29 steveebersole Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.loader.BatchingCollectionInitializer;
import net.sf.hibernate.loader.CollectionInitializer;
import net.sf.hibernate.loader.Loader;
import net.sf.hibernate.loader.OneToManyLoader;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.persister.Joinable;
import net.sf.hibernate.persister.OuterJoinLoadable;
import net.sf.hibernate.sql.Update;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.StringHelper;

/**
 * Collection persister for one-to-many associations.
 * @author Gavin King
 */
public class OneToManyPersister extends AbstractCollectionPersister {

	public OneToManyPersister(
		Collection collection,
		Configuration cfg,
		SessionFactoryImplementor factory)
		throws MappingException, CacheException {
		super(collection, cfg, factory);
	}
	
	/**
	 * Generate the SQL UPDATE that updates all the foreign keys to null
	 */
	protected String generateDeleteString() {
		Update update = new Update()
			.setTableName(qualifiedTableName)
			.addColumns(keyColumnNames, "null")
			.setPrimaryKeyColumnNames(keyColumnNames);
		if (hasIndex) update.addColumns(indexColumnNames, "null");
		if (hasWhere) update.setWhere(sqlWhereString);
		return update.toStatementString();
	}
	
	/**
	 * Generate the SQL UPDATE that updates a foreign key to a value
	 */
	protected String generateInsertRowString() {
		Update update = new Update()
			.setTableName(qualifiedTableName)
			.addColumns(keyColumnNames);
		if (hasIndex) update.addColumns(indexColumnNames); 
		//identifier collections not supported for 1-to-many 
		return update.setPrimaryKeyColumnNames(elementColumnNames)
			.toStatementString();
	}
	
	/**
	 * Not needed for one-to-many association
	 */
	protected String generateUpdateRowString() {
		return null;
	}
	
	/**
	 * Generate the SQL UPDATE that updates a particular row's foreign 
	 * key to null
	 */
	protected String generateDeleteRowString() {
		final String[] pkColumns;
		if (hasIdentifier) {
			pkColumns = rowSelectColumnNames;
		}
		else {
			pkColumns = ArrayHelper.join(keyColumnNames, rowSelectColumnNames);
		}
		Update update = new Update()
			.setTableName(qualifiedTableName)
			.addColumns(keyColumnNames, "null");
		if (hasIndex) update.addColumns(indexColumnNames, "null");
		return update.setPrimaryKeyColumnNames(pkColumns)
			.toStatementString();
	}

	public boolean consumesAlias() {
		return true;
	}
	
	public boolean isOneToMany() {
		return true;
	}
	
	public boolean isManyToMany() {
		return false;
	}
	
	protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session) 
	throws HibernateException {

		// we finish all the "removes" first to take care of possible unique 
		// constraints and so that we can take better advantage of batching
		
		try {
			// update removed rows fks to null
			int count=0;
			try {
				PreparedStatement st = null;
				int i=0;
				Iterator entries = collection.entries();
				while ( entries.hasNext() ) {
					Object entry = entries.next();
					if ( collection.needsUpdating(entry, i, elementType) ) {  // will still be issued when it used to be null
						if (st==null) st = session.getBatcher().prepareBatchStatement( getSQLDeleteRowString() );
						writeKey(st, id, false, session);
						writeIndex(st, collection.getIndex(entry, i), false, session);
						session.getBatcher().addToBatch(-1);
						count++;
					}
					i++;
				}
			}
			catch (SQLException sqle) {
				session.getBatcher().abortBatch(sqle);
				throw sqle;
			}
			// now update all changed or added rows fks
			try {
				PreparedStatement st = null;
				int i=0;
				Iterator entries = collection.entries();
				while ( entries.hasNext() ) {
					Object entry = entries.next();
					if ( collection.needsUpdating(entry, i, elementType) ) {
						if (st==null) st = session.getBatcher().prepareBatchStatement( getSQLInsertRowString() );
						writeKey(st, id, false, session);
						collection.writeTo(st, this, entry, i, false);
						session.getBatcher().addToBatch(1);
						count++;
					}
					i++;
				}
			}
			catch (SQLException sqle) {
				session.getBatcher().abortBatch(sqle);
				throw sqle;
			}
			return count;
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not update collection rows: " + MessageHelper.infoString(this, id) );
		}
	}

	public String selectFragment(String alias, String suffix, boolean includeCollectionColumns) {
		OuterJoinLoadable ojl = (OuterJoinLoadable) getElementPersister();
		StringBuffer buf = new StringBuffer();
		if (includeCollectionColumns) {
			buf.append( selectFragment(alias) ) //super impl will ignore suffix for collection columns!
			.append(StringHelper.COMMA_SPACE);
		}
		return buf.append( ojl.selectFragment(alias, suffix) ) //use suffix for the entity columns
			.toString();
	}
	
	/**
	 * Create the <tt>OneToManyLoader</tt>
	 * @see net.sf.hibernate.loader.OneToManyLoader
	 */
	protected CollectionInitializer createCollectionInitializer(SessionFactoryImplementor factory) throws MappingException {
		Loader nonbatchLoader = new OneToManyLoader(this, factory);
		if (batchSize>1) {
			Loader batchLoader = new OneToManyLoader(this, batchSize, factory);
			int smallBatchSize = (int) Math.round( Math.sqrt(batchSize) );
			Loader smallBatchLoader = new OneToManyLoader(this, smallBatchSize, factory);
			// the strategy for choosing batch or single load:
			return new BatchingCollectionInitializer(this, batchSize, batchLoader, smallBatchSize, smallBatchLoader, nonbatchLoader);
		}
		else {
			// don't do batch loading
			return (CollectionInitializer) nonbatchLoader;
		}
	}
	
	public String fromJoinFragment(
		String alias,
		boolean innerJoin,
		boolean includeSubclasses) {
		return ( (Joinable) getElementPersister() ).fromJoinFragment(alias, innerJoin, includeSubclasses);
	}

	public String whereJoinFragment(
		String alias,
		boolean innerJoin,
		boolean includeSubclasses) {
		return ( (Joinable) getElementPersister() ).whereJoinFragment(alias, innerJoin, includeSubclasses);
	}

}
