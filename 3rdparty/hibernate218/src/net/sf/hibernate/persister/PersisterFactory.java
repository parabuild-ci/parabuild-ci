//$Id: PersisterFactory.java,v 1.5 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.persister;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.collection.BasicCollectionPersister;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.OneToManyPersister;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.mapping.PersistentClass;

/**
 * Factory for <tt>ClassPersister</tt> and <tt>CollectionPersister</tt> instances
 * 
 * @author Gavin King 
 */
public final class PersisterFactory {
	
	//TODO: make ClassPersisters *not* depend on SessionFactoryImplementor
	//interface, if possible
	
	private PersisterFactory() {}
	
	private static final Class[] PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
		PersistentClass.class, SessionFactoryImplementor.class
	};
	
	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ? Should it not be enough with associated class ?
	// or why does ClassPersister's not get access to configuration ?
	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
			Collection.class, Configuration.class, SessionFactoryImplementor.class
		};
	
	public static ClassPersister createClassPersister(PersistentClass model, SessionFactoryImplementor factory) throws HibernateException {
		Class persisterClass = model.getClassPersisterClass();
		if (persisterClass==null || persisterClass==EntityPersister.class) {
			return new EntityPersister(model, factory);
		}
		else if (persisterClass==NormalizedEntityPersister.class) {
			return new NormalizedEntityPersister(model, factory);
		}
		else {
			return create(persisterClass, model, factory);
		}
	}

	public static CollectionPersister createCollectionPersister(Configuration cfg, Collection model, SessionFactoryImplementor factory) throws HibernateException {
		Class persisterClass = model.getCollectionPersisterClass();
		if(persisterClass==null) { // default behavior
			return model.isOneToMany() ?
				(CollectionPersister) new OneToManyPersister(model, cfg, factory) :
				(CollectionPersister) new BasicCollectionPersister(model, cfg, factory);	
		} 
		else {
			return create(persisterClass, cfg, model, factory);
		}
		
	}	
	
	private static ClassPersister create(Class persisterClass, PersistentClass model, SessionFactoryImplementor factory) 
	throws HibernateException {
		Constructor pc;
		try {
			pc = persisterClass.getConstructor(PERSISTER_CONSTRUCTOR_ARGS);
		}
		catch (Exception e) {
			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
		}
		
		try {
			return (ClassPersister) pc.newInstance( new Object[] { model, factory } );
		}
		catch (InvocationTargetException ite) {
			Throwable e = ite.getTargetException();
			if (e instanceof HibernateException) {
				throw (HibernateException) e;
			}
			else {
				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
			}
		}
		catch (Exception e) {
			throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
		}
	}

	private static CollectionPersister create(Class persisterClass, Configuration cfg, Collection model, SessionFactoryImplementor factory) throws HibernateException {
		Constructor pc;
		try {
			pc = persisterClass.getConstructor(COLLECTION_PERSISTER_CONSTRUCTOR_ARGS);
		}
		catch (Exception e) {
			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
		}
		
		try {
			return (CollectionPersister) pc.newInstance( new Object[] { model, cfg, factory } );
		}
		catch (InvocationTargetException ite) {
			Throwable e = ite.getTargetException();
			if (e instanceof HibernateException) {
				throw (HibernateException) e;
			}
			else {
				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
			}
		}
		catch (Exception e) {
			throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
		}
	}
	
	
}
