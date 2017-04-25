//$Id: OneToManyLoader.java,v 1.22 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.QueryableCollection;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.persister.OuterJoinLoadable;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.sql.Select;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;

/**
 * Loads one-to-many associations<br>
 * <br>
 * The collection persister must implement <tt>QueryableCOllection<tt>. For
 * other collections, create a customized subclass of <tt>Loader</tt>.
 * 
 * @see CollectionLoader
 * @author Gavin King
 */
public class OneToManyLoader extends OuterJoinLoader implements CollectionInitializer {
	
	private final QueryableCollection collectionPersister;
	private final Type idType;
	
	protected boolean isJoinedFetchEnabled(Type type, boolean mappingDefault, String path, String table, String[] foreignKeyColumns) {
		//disable a join back to this same association
		return super.isJoinedFetchEnabled(type, mappingDefault, path, table, foreignKeyColumns) && (
			!table.equals( collectionPersister.getTableName() ) || 
			!Arrays.equals( foreignKeyColumns, collectionPersister.getKeyColumnNames() )
		);
	}
	
	public OneToManyLoader(QueryableCollection collPersister, SessionFactoryImplementor session) throws MappingException {
		this(collPersister, 1, session);
	}
		
	public OneToManyLoader(QueryableCollection collPersister, int batchSize, SessionFactoryImplementor factory) throws MappingException {
		
		super( factory.getDialect() );
		
		this.collectionPersister = collPersister;
		this.idType = collPersister.getKeyType();
		
		OuterJoinLoadable persister = (OuterJoinLoadable) collPersister.getElementPersister();
		
		final String alias = generateRootAlias( collPersister.getRole() );
		List associations = walkTree(persister, alias, factory);
				
		initStatementString(collPersister, persister, alias, associations, batchSize, factory);
		initClassPersisters(persister, associations);
				
		postInstantiate();
	}
	
	private void initClassPersisters(OuterJoinLoadable persister, List associations) {
		final int joins = associations.size();
		lockModeArray = createLockModeArray(joins+1, LockMode.NONE);

		classPersisters = new Loadable[joins+1];		
		owners = new int[joins+1];
		int i=0;
		Iterator iter = associations.iterator();
		while ( iter.hasNext() ) {
			OuterJoinableAssociation oj = (OuterJoinableAssociation) iter.next(); 
			classPersisters[i] = (Loadable) oj.joinable;
			owners[i] = toOwner(oj, joins, oj.isOneToOne);
			i++;
		}
		classPersisters[joins] = persister;
		owners[joins] = -1;
		
		if ( ArrayHelper.isAllNegative(owners) ) owners = null;
	}

	protected CollectionPersister getCollectionPersister() {
		return collectionPersister;
	}
	
	public void initialize(Serializable id, SessionImplementor session) throws SQLException, HibernateException {
		loadCollection(session, id, idType);
	}
	
	private void initStatementString(
		final QueryableCollection collPersister, 
		final OuterJoinLoadable persister, 
		final String alias, 
		final List associations, 
		final int batchSize, 
		final SessionFactoryImplementor factory) 
	throws MappingException {
		
		final int joins = associations.size();
				
		suffixes = generateSuffixes(joins+1);

		StringBuffer whereString = whereString(alias, collPersister.getKeyColumnNames(), batchSize);
		if ( collPersister.hasWhere() ) whereString.append(" and ").append( collPersister.getSQLWhereString(alias) );
		
		JoinFragment ojf = mergeOuterJoins(associations);
		Select select = new Select()
			.setSelectClause( 
				collPersister.selectFragment( alias, suffixes[joins], true ) + 
				selectString(associations, factory)
			)
			.setFromClause( 
				persister.fromTableFragment(alias) +
				persister.fromJoinFragment(alias, true, true)
			)
			.setWhereClause( whereString.toString() )
			.setOuterJoins(
				ojf.toFromFragmentString(),
				ojf.toWhereFragmentString() +
				persister.whereJoinFragment(alias, true, true)
			);
		if ( collPersister.hasOrdering() ) select.setOrderByClause( collPersister.getSQLOrderByString(alias) );
		
		sql = select.toStatementString();
	}

}
