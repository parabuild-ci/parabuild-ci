//$Id: IdentifierCollection.java,v 1.5 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.Mapping;

/**
 * A collection with a synthetic "identifier" column
 */
public abstract class IdentifierCollection extends Collection {
	
	public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
	
	private SimpleValue identifier;
	
	public IdentifierCollection(PersistentClass owner) {
		super(owner);
	}

	public SimpleValue getIdentifier() {
		return identifier;
	}
	public void setIdentifier(SimpleValue identifier) {
		this.identifier = identifier;
	}
	public final boolean isIdentified() {
		return true;
	}

	void createPrimaryKey() {
		if ( !isOneToMany() ) {
			PrimaryKey pk = new PrimaryKey();
			pk.addColumns( getIdentifier().getColumnIterator() );
			getCollectionTable().setPrimaryKey(pk);
		}
		else {
			// don't create a unique key, 'cos some
			// databases don't like a UK on nullable
			// columns
			//getCollectionTable().createUniqueKey( getIdentifier().getConstraintColumns() );
		}
		// create an index on the key columns??
	}
	
	public void validate(Mapping mapping) throws MappingException {
		super.validate(mapping);
		if ( !getIdentifier().isValid(mapping) ) {
			throw new MappingException( 
				"collection id mapping has wrong number of columns: " + 
				getRole() +
				" type: " + 
				getIdentifier().getType().getName()
			);
		}
	}

}







