//$Id: BasicCollectionPersister.java,v 1.8 2004/12/24 03:06:23 oneovthafew Exp $
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
import net.sf.hibernate.loader.CollectionLoader;
import net.sf.hibernate.loader.Loader;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.sql.Delete;
import net.sf.hibernate.sql.Insert;
import net.sf.hibernate.sql.Update;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.StringHelper;

/**
 * Collection persister for collections of values and many-to-many associations.
 * @author Gavin King
 */
public class BasicCollectionPersister extends AbstractCollectionPersister {
	
	public BasicCollectionPersister(Collection collection, Configuration cfg, SessionFactoryImplementor factory) 
		throws MappingException, CacheException {
		super(collection, cfg, factory);
	}		
	
	/**
	 * Generate the SQL DELETE that deletes all rows
	 */
	protected String generateDeleteString() {
		Delete delete = new Delete()
			.setTableName(qualifiedTableName)
			.setPrimaryKeyColumnNames(keyColumnNames);
		if (hasWhere) delete.setWhere(sqlWhereString);
		return delete.toStatementString();
	}
	
	/**
	 * Generate the SQL INSERT that creates a new row
	 */
	protected String generateInsertRowString() {
		Insert insert = new Insert(null)
			.setTableName(qualifiedTableName)
			.addColumns(keyColumnNames);
		if (hasIndex) insert.addColumns(indexColumnNames);
		if (hasIdentifier) insert.addColumn(identifierColumnName);
		return insert.addColumns(elementColumnNames)
			.toStatementString();
	}

	/**
	 * Generate the SQL UPDATE that updates a row
	 */
	protected String generateUpdateRowString() {
		Update update = new Update()
			.setTableName(qualifiedTableName)
			.addColumns(elementColumnNames);
		if (hasIdentifier) {
			update.setPrimaryKeyColumnNames(rowSelectColumnNames);
		}
		else {
			update.setPrimaryKeyColumnNames( ArrayHelper.join(keyColumnNames, rowSelectColumnNames) );
		}
		return update.toStatementString();
	}
	
	/**
	 * Generate the SQL DELETE that deletes a particular row
	 */
	protected String generateDeleteRowString() {
		final String[] pkColumns;
		if (hasIdentifier) {
			pkColumns = rowSelectColumnNames;
		}
		else {
			pkColumns = ArrayHelper.join(keyColumnNames, rowSelectColumnNames);
		}
		return new Delete()
			.setTableName(qualifiedTableName)
			.setPrimaryKeyColumnNames(pkColumns)
			.toStatementString();
	}

	public boolean consumesAlias() {
		return false;
	}
	
	public boolean isOneToMany() {
		return false;
	}
	
	public boolean isManyToMany() {
		return elementType.isEntityType(); //instanceof AssociationType;
	}
	
	protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session) 
	throws HibernateException {
		
		try {
			PreparedStatement st = null;
			Iterator entries = collection.entries();
			try {
				int i=0;
				int count=0;
				while ( entries.hasNext() ) {
					Object entry = entries.next();
					if ( collection.needsUpdating(entry, i, elementType) ) {
						if (st==null) st = session.getBatcher().prepareBatchStatement( getSQLUpdateRowString() );
						if (!hasIdentifier) writeKey(st, id, true, session);
						collection.writeTo( st, this, entry, i, true );
						session.getBatcher().addToBatch(1);
						count++;
					}
					i++;
				}
				return count;
			}
			catch (SQLException sqle) {
				session.getBatcher().abortBatch(sqle);
				throw sqle;
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not update collection rows: " + MessageHelper.infoString(this, id) );
		}
	}

	/**
	 * Create the <tt>CollectionLoader</tt>
	 * @see net.sf.hibernate.loader.CollectionLoader
	 */
	protected CollectionInitializer createCollectionInitializer(SessionFactoryImplementor factory) throws MappingException {
		Loader nonbatchLoader = new CollectionLoader(this, factory);
		if (batchSize>1) {
			Loader batchLoader = new CollectionLoader(this, batchSize, factory);
			int smallBatchSize = (int) Math.round( Math.sqrt(batchSize) );
			Loader smallBatchLoader = new CollectionLoader(this, smallBatchSize, factory);
			// the strategy for choosing batch or single load:
			return new BatchingCollectionInitializer(this, batchSize, batchLoader, smallBatchSize, smallBatchLoader, nonbatchLoader);
		}
		else {
			// don't do batch loading
			return (CollectionInitializer) nonbatchLoader;
		}
	}
	
	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return StringHelper.EMPTY_STRING;
	}

	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return StringHelper.EMPTY_STRING;
	}
	
	public String selectFragment(String alias, String suffix, boolean includeCollectionColumns) {
		return includeCollectionColumns ? selectFragment(alias) : StringHelper.EMPTY_STRING;
	}

}







