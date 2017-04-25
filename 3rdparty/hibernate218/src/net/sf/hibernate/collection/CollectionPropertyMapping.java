package net.sf.hibernate.collection;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.persister.PropertyMapping;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * @author Gavin King
 */
public class CollectionPropertyMapping implements PropertyMapping {
	
	public static final String COLLECTION_SIZE = "size";
	public static final String COLLECTION_ELEMENTS = "elements";
	public static final String COLLECTION_INDICES = "indices";
	public static final String COLLECTION_MAX_INDEX = "maxIndex";
	public static final String COLLECTION_MIN_INDEX = "minIndex";
	public static final String COLLECTION_MAX_ELEMENT = "maxElement";
	public static final String COLLECTION_MIN_ELEMENT = "minElement";

	private final QueryableCollection memberPersister;
	
	public CollectionPropertyMapping(QueryableCollection memberPersister) {
		this.memberPersister = memberPersister;
	}

	public Type toType(String propertyName) throws QueryException {
		if ( propertyName.equals(COLLECTION_ELEMENTS) ) {
			return memberPersister.getElementType();
		}
		else if ( propertyName.equals(COLLECTION_INDICES) ) {
			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection before .indices");
			return memberPersister.getIndexType();
		}
		else if ( propertyName.equals(COLLECTION_SIZE) ) {
			return Hibernate.INTEGER;
		}
		else if ( propertyName.equals(COLLECTION_MAX_INDEX) ) {
			return memberPersister.getIndexType();
		}
		else if ( propertyName.equals(COLLECTION_MIN_INDEX) ) {
			return memberPersister.getIndexType();
		}
		else if ( propertyName.equals(COLLECTION_MAX_ELEMENT) ) {
			return memberPersister.getElementType();
		}
		else if ( propertyName.equals(COLLECTION_MIN_ELEMENT) ) {
			return memberPersister.getElementType();
		}
		else {
			//return memberPersister.getPropertyType(propertyName);
			throw new QueryException("expecting 'elements' or 'indices' after: " + propertyName);
		}
	}

	public String[] toColumns(String alias, String propertyName) throws QueryException {
		if ( propertyName.equals(COLLECTION_ELEMENTS) ) {
			String[] cols = memberPersister.getElementColumnNames();
			return StringHelper.qualify(alias, cols);
		}
		else if ( propertyName.equals(COLLECTION_INDICES) ) {
			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection before .indices");
			String[] cols = memberPersister.getIndexColumnNames();
			return StringHelper.qualify(alias, cols);
		}
		else if ( propertyName.equals(COLLECTION_SIZE) ) {
			return new String[] { "count(*)" };
		}
		else if ( propertyName.equals(COLLECTION_MAX_INDEX) ) {
			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection before .maxIndex");
			String[] cols = memberPersister.getIndexColumnNames();
			if ( cols.length!=1 ) throw new QueryException("composite collection index in maxIndex");
			return new String[] { "max(" + cols[0] + StringHelper.CLOSE_PAREN };
		}
		else if ( propertyName.equals(COLLECTION_MIN_INDEX) ) {
			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection before .minIndex");
			String[] cols = memberPersister.getIndexColumnNames();
			if ( cols.length!=1 ) throw new QueryException("composite collection index in minIndex");
			return new String[] { "min(" + cols[0] + StringHelper.CLOSE_PAREN };
		}
		else if ( propertyName.equals(COLLECTION_MAX_ELEMENT) ) {
			String[] cols = memberPersister.getElementColumnNames();
			if ( cols.length!=1 ) throw new QueryException("composite collection element in maxElement");
			return new String[] { "max(" + cols[0] + StringHelper.CLOSE_PAREN };
		}
		else if ( propertyName.equals(COLLECTION_MIN_ELEMENT) ) {
			String[] cols = memberPersister.getElementColumnNames();
			if ( cols.length!=1 ) throw new QueryException("composite collection element in minElement");
			return new String[] { "min(" + cols[0] + StringHelper.CLOSE_PAREN };
		}
		else {
			//return memberPersister.toColumns(alias, propertyName);
			throw new QueryException("expecting 'elements' or 'indices' after: " + propertyName);
		}
	}

	public Type getType() {
		//return memberPersister.getType();
		return memberPersister.getCollectionType();
	}

}
