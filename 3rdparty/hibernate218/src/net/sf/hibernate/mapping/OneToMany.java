//$Id: OneToMany.java,v 1.11 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.loader.OuterJoinLoader;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.Type;

/**
 * A mapping for a one-to-many association
 * @author Gavin King
 */
public class OneToMany implements Value {
	
	private EntityType type;
	private Table referencingTable;
	private PersistentClass associatedClass;
	
	public EntityType getEntityType() {
		return type;
	}
	
	public OneToMany(PersistentClass owner) throws MappingException {
		this.referencingTable = (owner==null) ? null : owner.getTable();
	}
	
	public void setType(EntityType type) {
		this.type = type;
	}

	public PersistentClass getAssociatedClass() {
		return associatedClass;
	}

	public void setAssociatedClass(PersistentClass associatedClass) {
		this.associatedClass = associatedClass;
	}

	public void createForeignKey() {
		// no foreign key element of for a one-to-many
	}

	public Iterator getColumnIterator() {
		return associatedClass.getKey().getColumnIterator();
	}

	public int getColumnSpan() {
		return associatedClass.getKey().getColumnSpan();
	}

	public Formula getFormula() {
		return null;
	}

	public int getOuterJoinFetchSetting() {
		return OuterJoinLoader.EAGER;
	}

	public Table getTable() {
		return referencingTable;
	}

	public Type getType() {
		return getEntityType();
	}

	public boolean isNullable() {
		return false;
	}

	public boolean isSimpleValue() {
		return false;
	}

	public boolean isUnique() {
		return false;
	}

	public boolean isValid(Mapping mapping) throws MappingException {
		return true;
	}

}







