//$Id: PathExpressionParser.java,v 1.30 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.hql;

import java.util.LinkedList;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.collection.CollectionPropertyMapping;
import net.sf.hibernate.collection.QueryableCollection;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.persister.PropertyMapping;
import net.sf.hibernate.persister.Queryable;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.sql.QueryJoinFragment;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * Parses an expression of the form foo.bar.baz and builds up an expression
 * involving two less table joins than there are path components.
 */
public class PathExpressionParser implements Parser {
	
	//TODO: this class does too many things! we need a different
	//kind of path expression parser for each of the diffferent
	//ways in which path expressions can occur
	
	//We should actually rework this class to not implement Parser 
	//and just process path expressions in the most convenient way.
	
	//The class is now way to complex!
	
	private int dotcount;
	private String currentName;
	private String currentProperty;
	private String oneToOneOwnerName;
	private QueryJoinFragment join;
	private String[] columns;
	private String collectionName;
	private String collectionOwnerName;
	private String collectionRole;
	private final StringBuffer componentPath = new StringBuffer();
	private Type type;
	private final StringBuffer path = new StringBuffer();
	private boolean ignoreInitialJoin;
	private boolean continuation;
	private int joinType = JoinFragment.INNER_JOIN; //default mode
	private boolean useThetaStyleJoin = true;
	private PropertyMapping currentPropertyMapping;
	
	void setJoinType(int joinType) {
		this.joinType = joinType;
	}
	void setUseThetaStyleJoin(boolean useThetaStyleJoin) {
		this.useThetaStyleJoin = useThetaStyleJoin;
	}

	private void addJoin(String table, String name, String[] rhsCols) throws QueryException {
		String[] lhsCols = currentColumns();
		join.addJoin(table, name, lhsCols, rhsCols, joinType);
	}
	
	String continueFromManyToMany(Class clazz, String[] joinColumns, QueryTranslator q) throws QueryException {
		start(q);
		continuation=true;
		currentName = q.createNameFor(clazz);
		q.addType(currentName, clazz);
		Queryable classPersister = q.getPersister(clazz);
		join.addJoin( classPersister.getTableName(), currentName, joinColumns, classPersister.getIdentifierColumnNames(), joinType );
		currentPropertyMapping = classPersister;
		return currentName;
	}
	
	public void ignoreInitialJoin() {
		ignoreInitialJoin=true;
	}
	
	public void token(String token, QueryTranslator q) throws QueryException {
		
		if (token!=null) path.append(token);
		
		String alias = q.getPathAlias( path.toString() );
		if (alias!=null) {
			reset(q); //reset the dotcount (but not the path)
			currentName = alias; //after reset!
			currentPropertyMapping = q.getPropertyMapping(currentName);
			if (!ignoreInitialJoin) {
				JoinFragment ojf = q.getPathJoin( path.toString() );
				join.addCondition( ojf.toWhereFragmentString() ); //after reset!
				// we don't need to worry about any condition in the ON clause
				// here (toFromFragmentString), since anything in the ON condition 
				// is already applied to the whole query
			}
		}
		else if ( ".".equals(token) ) {
			dotcount++;
		}
		else {
			if ( dotcount==0 ) {
				if (!continuation) {
					if ( !q.isName(token) ) throw new QueryException("undefined alias: " + token);
					currentName=token;
					currentPropertyMapping = q.getPropertyMapping(currentName);
				}
			}
			else if (dotcount==1) {
				if (currentName!=null) {
					currentProperty = token;
				}
				else if (collectionName!=null) {
					//processCollectionProperty(token, q.getCollectionPersister(collectionRole), collectionName);
					continuation = false;
				}
				else {
					throw new QueryException("unexpected");
				}
			}
			else { // dotcount>=2
				
				// Do the corresponding RHS
				Type propertyType = getPropertyType();
				
				if (propertyType==null) {
					throw new QueryException("unresolved property: " + path);
				}
				
				if ( propertyType.isComponentType() ) {
					dereferenceComponent(token);
				}
				else if ( propertyType.isEntityType() ) {
					dereferenceEntity(token, (EntityType) propertyType, q);
				}
				else if ( propertyType.isPersistentCollectionType() ) {
					dereferenceCollection( token, ( (PersistentCollectionType) propertyType ).getRole(), q );
					
				}
				else {
					if (token!=null) throw new QueryException("dereferenced: " + path);
				}
				
			}
		}
	}
	
	private void dereferenceEntity(String propertyName, EntityType propertyType, QueryTranslator q) throws QueryException {
		//NOTE: we avoid joining to the next table if the named property is just the foreign key value
  		
		//if its "id"
		boolean isIdShortcut = ClassPersister.ENTITY_ID.equals(propertyName) && 
			!propertyType.isUniqueKeyReference();
		
		//or its the id property name
		final String idPropertyName;
		try {
			idPropertyName = propertyType.getIdentifierOrUniqueKeyPropertyName( q.getFactory() );
		}
		catch (MappingException me) {
			throw new QueryException(me);
		}
		boolean isNamedIdPropertyShortcut = idPropertyName!=null && 
			idPropertyName.equals(propertyName);
		
		if ( isIdShortcut || isNamedIdPropertyShortcut ) {
			// special shortcut for id properties, skip the join!
			// this must only occur at the _end_ of a path expression
			if ( componentPath.length()>0 ) componentPath.append(StringHelper.DOT);
			componentPath.append(propertyName);					
		}
		else {
			Class entityClass = propertyType.getAssociatedClass();
			String name = q.createNameFor(entityClass);
			q.addType(name, entityClass);
			Queryable memberPersister = q.getPersister(entityClass);
			//String[] keyColNames = memberPersister.getIdentifierColumnNames();
			String[] keyColNames;
			try {
				keyColNames = propertyType.getReferencedColumns( q.getFactory() );
			}
			catch (MappingException me) {
				throw new QueryException(me);
			}
			addJoin( memberPersister.getTableName(), name, keyColNames );
			if ( propertyType.isOneToOne() ) oneToOneOwnerName = currentName;
			currentName = name;
			currentProperty = propertyName;
			q.addPathAliasAndJoin( path.substring( 0, path.toString().lastIndexOf(StringHelper.DOT) ), name, join );
			componentPath.setLength(0);
			currentPropertyMapping = memberPersister;
		}
	}
	
	private void dereferenceComponent(String propertyName) {
		if (propertyName!=null) {
			if ( componentPath.length()>0 ) componentPath.append(StringHelper.DOT);
			componentPath.append(propertyName);
		}
	}
	
	private void dereferenceCollection(String propertyName, String role, QueryTranslator q) throws QueryException {
		collectionRole = role;
		QueryableCollection collPersister = q.getCollectionPersister(role);
		String[] colNames = collPersister.getKeyColumnNames();
		String name = q.createNameForCollection(role);
		addJoin( collPersister.getTableName(), name, colNames );
		if ( collPersister.hasWhere() ) join.addCondition( collPersister.getSQLWhereString(name) );
		collectionName = name;
		collectionOwnerName = currentName;
		currentName = name;
		currentProperty = propertyName;
		componentPath.setLength(0);
		currentPropertyMapping = new CollectionPropertyMapping(collPersister);
	}

	private String getPropertyPath() {
		if (currentProperty==null) {
			return ClassPersister.ENTITY_ID;
		}
		else {
			if ( componentPath.length()>0 ) {
				return new StringBuffer()
					.append(currentProperty)
					.append(StringHelper.DOT)
					.append( componentPath.toString() )
					.toString();
			}
			else {
				return currentProperty;
			}
		}
	}
	
	private PropertyMapping getPropertyMapping() {
		return currentPropertyMapping;
	}
	
	private void setType() throws QueryException {
		if ( currentProperty==null ) {
			type = getPropertyMapping().getType();
		}
		else {
			type = getPropertyType();
		}
	}
	
	protected Type getPropertyType() throws QueryException {
		String propertyPath = getPropertyPath();
		Type propertyType = getPropertyMapping().toType(propertyPath);
		if (propertyType==null)
			throw new QueryException("could not resolve property type: " + propertyPath);
		return propertyType;
	}
	
	protected String[] currentColumns() throws QueryException {
		String propertyPath = getPropertyPath();
		String[] propertyColumns = getPropertyMapping().toColumns(currentName, propertyPath);
		if (propertyColumns==null) throw new QueryException("could not resolve property columns: " + propertyPath);
		return propertyColumns;
	}
	
	private void reset(QueryTranslator q) {
		join = q.createJoinFragment(useThetaStyleJoin);
		dotcount=0;
		currentName=null;
		currentProperty=null;
		collectionName = null;
		collectionRole = null;
		componentPath.setLength(0);
		type = null;
		collectionName = null;
		columns=null;
		expectingCollectionIndex = false;
		continuation = false;
		currentPropertyMapping = null;
	}
	
	public void start(QueryTranslator q) {
		if (!continuation) {
			reset(q);
			path.setLength(0);
		}
	}
	
	public void end(QueryTranslator q) throws QueryException {
		ignoreInitialJoin = false;
		
		Type propertyType = getPropertyType();
		if ( propertyType!=null && propertyType.isPersistentCollectionType() ) {
			collectionRole = ( (PersistentCollectionType) propertyType ).getRole();
			collectionName = q.createNameForCollection(collectionRole);
			prepareForIndex(q);
		}
		else {
			columns = currentColumns();
			setType();
		}
		
		//important!!
		continuation=false;
		
	}
	
	private void prepareForIndex(QueryTranslator q) throws QueryException {
		
		QueryableCollection collPersister = q.getCollectionPersister(collectionRole);

		if ( !collPersister.hasIndex() ) throw new QueryException("unindexed collection before []: " + path);
		String[] indexCols = collPersister.getIndexColumnNames();
		if ( indexCols.length!=1 ) throw new QueryException("composite-index appears in []: " + path);				
		String[] keyCols = collPersister.getKeyColumnNames();
				
		JoinFragment ojf = q.createJoinFragment(useThetaStyleJoin);
		ojf.addCrossJoin( collPersister.getTableName(), collectionName );
		ojf.addFromFragmentString( join.toFromFragmentString() );
		if ( collPersister.isOneToMany() ) {
			Queryable persister = (Queryable) collPersister.getElementPersister();
			ojf.addJoins(
				persister.fromJoinFragment(collectionName, true, false),
				persister.whereJoinFragment(collectionName, true, false)
			);
		}

		if (!continuation)  addJoin( collPersister.getTableName(), collectionName, keyCols );

		join.addCondition(collectionName, indexCols, " = ");
				
		String[] eltCols = collPersister.getElementColumnNames();				
		CollectionElement elem = new CollectionElement();
		elem.elementColumns = StringHelper.qualify(collectionName, eltCols);
		elem.elementType = collPersister.getElementType();
		elem.isOneToMany = collPersister.isOneToMany();
		elem.alias = collectionName;
		elem.joinFragment = join;
		collectionElements.addLast(elem);
		setExpectingCollectionIndex();
				
		q.addCollection(collectionName, collectionRole);
		q.addJoin(collectionName, ojf);
	}
	
	static final class CollectionElement {
		Type elementType;
		boolean isOneToMany;
		String alias;
		String[] elementColumns;
		JoinFragment joinFragment;
		StringBuffer indexValue = new StringBuffer();
	}
	
	private boolean expectingCollectionIndex;
	private LinkedList collectionElements = new LinkedList();
	
	public CollectionElement lastCollectionElement() {
		return (CollectionElement) collectionElements.removeLast();
	}
	
	public void setLastCollectionElementIndexValue(String value) {
		( (CollectionElement) collectionElements.getLast() ).indexValue.append(value) ;
	}
	
	public boolean isExpectingCollectionIndex() {
		return expectingCollectionIndex;
	}
	
	protected void setExpectingCollectionIndex() throws QueryException {
		expectingCollectionIndex = true;
	}
	
	public JoinFragment getWhereJoin() {
		return join;
	}
	
	public String getWhereColumn() throws QueryException {
		if (columns.length!=1) throw new QueryException("path expression ends in a composite value: " + path);
		return columns[0];
	}
	public String[] getWhereColumns() {
		return columns;
	}
	
	public Type getWhereColumnType() {
		return type;
	}
	
	public String getName() {
		return currentName==null ? collectionName : currentName;
	}
	
	public String getCollectionSubquery() throws QueryException {
		//TODO: refactor to .sql package
		return new StringBuffer( "select " )
			.append( StringHelper.join( ", ", currentColumns() ) )
			.append(" from ")
			/*.append(collectionTable)
			.append(' ')
			.append(collectionName)*/
			.append( join.toFromFragmentString().substring(2) ) // remove initial ", "
			.append(" where ")
			.append( join.toWhereFragmentString().substring(5) ) // remove initial " and "
			.toString();
	}
	
	public boolean isCollectionValued() throws QueryException {
		//TODO: is there a better way?
		return collectionName!=null && !getPropertyType().isPersistentCollectionType();
	}
	
	public void addAssociation(QueryTranslator q) {
		q.addJoin( getName(), join );
	}
	
	public String addFromAssociation(QueryTranslator q) throws QueryException {
		if ( isCollectionValued() ) {
			return addFromCollection(q);
		}
		else {
			q.addFrom(currentName, join); 
			return currentName;
		}
	}
	
	public String addFromCollection(QueryTranslator q) throws QueryException {
		Type collectionElementType = getPropertyType();
		
		if ( collectionElementType==null ) throw new QueryException(
			"must specify 'elements' for collection valued property in from clause: " + path
		);
		
		if ( collectionElementType.isEntityType() ) {
			// an association
			QueryableCollection collectionPersister = q.getCollectionPersister(collectionRole);
			Queryable entityPersister = (Queryable) collectionPersister.getElementPersister();
			Class clazz = entityPersister.getMappedClass();
			
			String[] collectionElementColumns = currentColumns();
			
			final String elementName;
			if ( collectionPersister.isOneToMany() ) {
				elementName = collectionName;
				//allow index() function:
				q.decoratePropertyMapping(elementName, collectionPersister);
			}
			else { //many-to-many
				q.addCollection(collectionName, collectionRole);
				elementName = q.createNameFor(clazz);
				String[] keyColumnNames = entityPersister.getIdentifierColumnNames();
				join.addJoin( entityPersister.getTableName(), elementName, collectionElementColumns, keyColumnNames, joinType);
			}
			q.addFrom(elementName, clazz, join);
			currentPropertyMapping = new CollectionPropertyMapping(collectionPersister);
			return elementName;
		}
		else {
			// collections of values
			q.addFromCollection(collectionName, collectionRole, join);
			return collectionName;
		}
		
	}
	
	String getCollectionName() {
		return collectionName;
	}
	String getCollectionRole() {
		return collectionRole;
	}
	
	String getCollectionOwnerName() {
		return collectionOwnerName;
	}
	
	String getOneToOneOwnerName() {
		return oneToOneOwnerName;
	}
	
	String getCurrentProperty() {
		return currentProperty;
	}

	String getCurrentName() {
		return currentName;
	}

	public void fetch(QueryTranslator q, String entityName) throws QueryException {
		if ( isCollectionValued() ) {
			q.setCollectionToFetch( getCollectionRole(), getCollectionName(), getCollectionOwnerName(), entityName );
		}
		else {
			q.addEntityToFetch( entityName, getOneToOneOwnerName() );
		}
	}
}
