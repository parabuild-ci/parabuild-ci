//$Id: Collection.java,v 1.21 2004/09/26 13:03:59 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.Comparator;
import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.EmptyIterator;

/**
 * Mapping for a collection. Subclasses specialize to particular 
 * collection styles.
 * @author Gavin King
 */
public abstract class Collection implements Fetchable, Value {
	
	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
	
	private SimpleValue key;
	private Value element;
	private Table collectionTable;
	private String role;
	private boolean lazy;
	private boolean inverse;
	private CacheConcurrencyStrategy cache;
	private String orderBy;
	private String where;
	private PersistentClass owner;
	private boolean sorted;
	private Comparator comparator;
	private boolean orphanDelete;
	private int batchSize=1;
	private int joinedFetch;
	private Class collectionPersisterClass;
	
	protected Collection(PersistentClass owner) {
		this.owner = owner;
	}
	
	public boolean isSet() {
		return false;
	}
	
	public SimpleValue getKey() {
		return key;
	}
	public Value getElement() {
		return element;
	}
	public boolean isIndexed() {
		return false;
	}
	public Table getCollectionTable() {
		return collectionTable;
	}
	public void setCollectionTable(Table table) {
		this.collectionTable = table;
	}
	public boolean isSorted() {
		return sorted;
	}
	public Comparator getComparator() {
		return comparator;
	}
	public boolean isLazy() {
		return lazy;
	}
	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}
	
	public String getRole() {
		return role;
	}
	public abstract PersistentCollectionType getCollectionType();	
	
	public boolean isPrimitiveArray() {
		return false;
	}
	
	public boolean isArray() {
		return false;
	}
	
	public boolean isOneToMany() {
		return element instanceof OneToMany;
	}
	
	public CacheConcurrencyStrategy getCache() {
		return cache;
	}
	
	public boolean isInverse() {
		return inverse;
	}
	
	public Class getOwnerClass() {
		return owner.getMappedClass();
	}
	
	/**
	 * Returns the orderBy.
	 * @return String
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * Sets the cache.
	 * @param cache The cache to set
	 */
	public void setCache(CacheConcurrencyStrategy cache) {
		this.cache = cache;
	}

	/**
	 * Sets the comparator.
	 * @param comparator The comparator to set
	 */
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * Sets the element.
	 * @param element The element to set
	 */
	public void setElement(Value element) {
		this.element = element;
	}

	/**
	 * Sets the key.
	 * @param key The key to set
	 */
	public void setKey(SimpleValue key) {
		this.key = key;
	}

	/**
	 * Sets the orderBy.
	 * @param orderBy The orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Sets the role.
	 * @param role The role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Sets the sorted.
	 * @param sorted The sorted to set
	 */
	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	/**
	 * Sets the inverse.
	 * @param inverse The inverse to set
	 */
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	/**
	 * Returns the owner.
	 * @return PersistentClass
	 */
	public PersistentClass getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 * @param owner The owner to set
	 */
	public void setOwner(PersistentClass owner) {
		this.owner = owner;
	}

	/**
	 * Returns the where.
	 * @return String
	 */
	public String getWhere() {
		return where;
	}

	/**
	 * Sets the where.
	 * @param where The where to set
	 */
	public void setWhere(String where) {
		this.where = where;
	}

	public boolean isIdentified() {
		return false;
	}

	public boolean hasOrphanDelete() {
		return orphanDelete;
	}

	public void setOrphanDelete(boolean orphanDelete) {
		this.orphanDelete = orphanDelete;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int i) {
		batchSize = i;
	}

	public int getOuterJoinFetchSetting() { 
		return joinedFetch;
	}
	
	public void setOuterJoinFetchSetting(int joinedFetch) { 
		this.joinedFetch=joinedFetch;
	}
	
	public void setCollectionPersisterClass(Class persister) {
		this.collectionPersisterClass = persister;
	}
	
	public Class getCollectionPersisterClass() {
		return collectionPersisterClass;
	}
	
	public void validate(Mapping mapping) throws MappingException {
		if ( !getKey().isValid(mapping) ) {
			throw new MappingException( 
				"collection foreign key mapping has wrong number of columns: " + 
				getRole() +
				" type: " + 
				getKey().getType().getName()
			);
		}
		if ( !getElement().isValid(mapping) ) {
			throw new MappingException( 
				"collection element mapping has wrong number of columns: " + 
				getRole() +
				" type: " + 
				getElement().getType().getName()
			);
		}
	}
	
	public Iterator getColumnIterator() {
		return EmptyIterator.INSTANCE;
	}
	public int getColumnSpan() {
		return 0;
	}
	public Formula getFormula() {
		return null;
	}
	public Type getType() {
		return getCollectionType();
	}
	public boolean isNullable() {
		return true;
	}
	public boolean isUnique() {
		return false;
	}
	public Table getTable() {
		return owner.getTable();
	}
	
	public void createForeignKey() {}
	
	public boolean isSimpleValue() {
		return false;
	}

	public boolean isValid(Mapping mapping) throws MappingException {
		return true;
	}

	private void createForeignKeys() {
		if ( !isInverse() ) { // for inverse collections, let the "other end" handle it
			getElement().createForeignKey();
			getKey().createForeignKeyOfClass( getOwner().getMappedClass() );
		}
	}
	
	abstract void createPrimaryKey();
	
	public void createAllKeys() {
		createForeignKeys();
		if ( !isInverse() ) createPrimaryKey();
	}

}







