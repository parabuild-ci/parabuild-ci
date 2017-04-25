//$Id: Subclass.java,v 1.15 2004/08/07 05:47:40 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.Collections;
import java.util.Iterator;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.util.JoinedIterator;

/**
 * A sublass in a table-per-subclass, or table-per-concrete-class
 * inheritance hierarchy.
 * @author Gavin King
 */
public class Subclass extends PersistentClass {
	
	private PersistentClass superclass;
	private SimpleValue key;
	private Class classPersisterClass;
	
	public Subclass(PersistentClass superclass) {
		this.superclass = superclass;
	}
	
	public CacheConcurrencyStrategy getCache() {
		return getSuperclass().getCache();
	}
	
	public RootClass getRootClass() {
		return getSuperclass().getRootClass();
	}
	
	public PersistentClass getSuperclass() {
		return superclass;
	}
	
	public Property getIdentifierProperty() {
		return getSuperclass().getIdentifierProperty();
	}
	public SimpleValue getIdentifier() {
		return getSuperclass().getIdentifier();
	}
	public boolean hasIdentifierProperty() {
		return getSuperclass().hasIdentifierProperty();
	}
	public Value getDiscriminator() {
		return getSuperclass().getDiscriminator();
	}
	public boolean isMutable() {
		return getSuperclass().isMutable();
	}
	public boolean isInherited() {
		return true;
	}
	public boolean isPolymorphic() {
		return true;
	}
	
	public void addNewProperty(Property p) throws MappingException {
		super.addNewProperty(p);
		getSuperclass().addSubclassProperty(p);
	}
	public void setTable(Table table) {
		super.setTable(table);
		getSuperclass().addSubclassTable(table);
	}
	public Iterator getPropertyClosureIterator() {
		return new JoinedIterator( new Iterator[] {
			getPropertyIterator(),
			getSuperclass().getPropertyClosureIterator() }
		);
	}
	public Iterator getTableClosureIterator() {
		return new JoinedIterator( new Iterator[] {
			getSuperclass().getTableClosureIterator(),
			Collections.singleton( getTable() ).iterator()
		} );
	}
	protected void addSubclassProperty(Property p) {
		super.addSubclassProperty(p);
		getSuperclass().addSubclassProperty(p);
	}
	
	protected void addSubclassTable(Table table) {
		super.addSubclassTable(table);
		getSuperclass().addSubclassTable(table);
	}
	
	public boolean isVersioned() {
		return getSuperclass().isVersioned();
	}
	public Property getVersion() {
		return getSuperclass().getVersion();
	}
	
	public boolean hasEmbeddedIdentifier() {
		return getSuperclass().hasEmbeddedIdentifier();
	}
	public Class getClassPersisterClass() {
		if (classPersisterClass==null) {
			return getSuperclass().getClassPersisterClass();
		}
		else {
			return classPersisterClass;
		}
	}
	
	public Table getRootTable() {
		return getSuperclass().getRootTable();
	}
	
	public SimpleValue getKey() {
		if (key==null) {
			return getIdentifier();
		}
		else {
			return key;
		}
	}
	
	public boolean isExplicitPolymorphism() {
		return getSuperclass().isExplicitPolymorphism();
	}
	
	public void setKey(SimpleValue key) {
		this.key = key;
	}

	public void setSuperclass(PersistentClass superclass) {
		this.superclass = superclass;
	}
	
	public String getWhere() {
		return getSuperclass().getWhere();
	}

	public void validate(Mapping mapping) throws MappingException {
		super.validate(mapping);
		if ( key!=null && !key.isValid(mapping) ) {
			throw new MappingException(
				"subclass key mapping has wrong number of columns: " + 
				getMappedClass().getName() +
				" type: " + 
				key.getType().getName()
			);
		}
	}
	
	public boolean isJoinedSubclass() {
		return getTable()!=getRootTable();
	}
	
	public void createForeignKey() {
		if ( !isJoinedSubclass() ) throw new AssertionFailure("not a joined-subclass");
		getKey().createForeignKeyOfClass( getSuperclass().getMappedClass() );
	}

	public void setClassPersisterClass(Class classPersisterClass) {
		this.classPersisterClass = classPersisterClass;
	}

	public boolean isDiscriminatorInsertable() {
		return getSuperclass().isDiscriminatorInsertable();
	}

}






