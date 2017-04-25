//$Id: IndexedCollection.java,v 1.10 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.Mapping;

/**
 * Indexed collections include Lists, Maps, arrays and
 * primitive arrays.
 * @author Gavin King
 */
public abstract class IndexedCollection extends Collection {
	
	public static final String DEFAULT_INDEX_COLUMN_NAME = "idx";
	
	private SimpleValue index;
	
	/**
	 * Constructor for IndexedCollection.
	 * @param owner
	 */
	public IndexedCollection(PersistentClass owner) {
		super(owner);
	}

	public SimpleValue getIndex() {
		return index;
	}
	public void setIndex(SimpleValue index) {
		this.index = index;
	}
	public final boolean isIndexed() {
		return true;
	}


	void createPrimaryKey() {
		if ( !isOneToMany() ) {
			PrimaryKey pk = new PrimaryKey();
			pk.addColumns( getKey().getColumnIterator() );
			pk.addColumns( getIndex().getColumnIterator() ); // index should be last column listed
			getCollectionTable().setPrimaryKey(pk);
		}
		else {
			// don't create a unique key, 'cos some
			// databases don't like a UK on nullable
			// columns
			/*ArrayList list = new ArrayList();
			list.addAll( getKey().getConstraintColumns() );
			list.addAll( getIndex().getConstraintColumns() );
			getCollectionTable().createUniqueKey(list);*/
		}
	}
	
	public void validate(Mapping mapping) throws MappingException {
		super.validate(mapping);
		if ( !getIndex().isValid(mapping) ) {
			throw new MappingException( 
				"collection index mapping has wrong number of columns: " + 
				getRole() +
				" type: " + 
				getIndex().getType().getName()
			);
		}
	}

}


