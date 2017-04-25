//$Id: RootClass.java,v 1.19 2004/08/07 05:47:40 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.Collections;
import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.engine.Mapping;

/**
 * The root class of a table-per-subclass, or table-per-concrete-class
 * inheritance hierarchy.
 * @author Gavin King
 */
public class RootClass extends PersistentClass {
	
	public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
	public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
	
	private Property identifierProperty; //may be final
	private SimpleValue identifier; //may be final
	private Property version; //may be final
	private boolean polymorphic;
	private CacheConcurrencyStrategy cache;
	private SimpleValue discriminator; //may be final
	private boolean mutable;
	private boolean embeddedIdentifier = false; // may be final
	private boolean explicitPolymorphism;
	private Class classPersisterClass;
	private boolean forceDiscriminator;
	private String where;
	private boolean discriminatorInsertable = true;

	public Property getIdentifierProperty() {
		return identifierProperty;
	}
	public SimpleValue getIdentifier() {
		return identifier;
	}
	public boolean hasIdentifierProperty() {
		return identifierProperty!=null;
	}
	
	public Value getDiscriminator() {
		return discriminator;
	}
	
	public boolean isInherited() {
		return false;
	}
	public boolean isPolymorphic() {
		return polymorphic;
	}
	
	public void setPolymorphic(boolean polymorphic) {
		this.polymorphic = polymorphic;
	}
	
	public RootClass getRootClass() {
		return this;
	}
	public Iterator getPropertyClosureIterator() {
		return getPropertyIterator();
	}
	public Iterator getTableClosureIterator() {
		return Collections.singleton( getTable() ).iterator();
	}
	
	public void addSubclass(Subclass subclass) throws MappingException {
		super.addSubclass(subclass);
		setPolymorphic(true);
	}
	
	public boolean isExplicitPolymorphism() {
		return explicitPolymorphism;
	}
	
	public Property getVersion() {
		return version;
	}
	public void setVersion(Property version) {
		this.version = version;
	}
	public boolean isVersioned() {
		return version!=null;
	}
	
	public CacheConcurrencyStrategy getCache() {
		return cache;
	}
	
	public boolean isMutable() {
		return mutable;
	}
	public boolean hasEmbeddedIdentifier() {
		return embeddedIdentifier;
	}
	
	public Class getClassPersisterClass() {
		return classPersisterClass;
	}
	
	public Table getRootTable() {
		return getTable();
	}
	
	public void setClassPersisterClass(Class persister) {
		this.classPersisterClass = persister;
	}
	
	public PersistentClass getSuperclass() {
		return null;
	}
	
	public SimpleValue getKey() {
		return getIdentifier();
	}

	public void setDiscriminator(SimpleValue discriminator) {
		this.discriminator = discriminator;
	}

	public void setEmbeddedIdentifier(boolean embeddedIdentifier) {
		this.embeddedIdentifier = embeddedIdentifier;
	}

	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
		this.explicitPolymorphism = explicitPolymorphism;
	}

	public void setIdentifier(SimpleValue identifier) {
		this.identifier = identifier;
	}

	public void setIdentifierProperty(Property identifierProperty) {
		this.identifierProperty = identifierProperty;
	}

	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}

	public void setCache(CacheConcurrencyStrategy cache) {
		this.cache = cache;
	}

	public boolean isForceDiscriminator() {
		return forceDiscriminator;
	}

	public void setForceDiscriminator(boolean forceDiscriminator) {
		this.forceDiscriminator = forceDiscriminator;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String string) {
		where = string;
	}
	
	public boolean isJoinedSubclass() {
		return false;
	}

	public void validate(Mapping mapping) throws MappingException {
		super.validate(mapping);
		if ( !getIdentifier().isValid(mapping) ) {
			throw new MappingException(
				"identifier mapping has wrong number of columns: " + 
				getMappedClass().getName() +
				" type: " + 
				getIdentifier().getType().getName()
			);
		}
	}
	public boolean isDiscriminatorInsertable() {
		return discriminatorInsertable;
	}
	public void setDiscriminatorInsertable(boolean insertable) {
		this.discriminatorInsertable = insertable;
	}

}






