//$Id: ScheduledEntityAction.java,v 1.11 2004/08/13 00:12:41 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.impl.SessionImpl.Executable;
import net.sf.hibernate.persister.ClassPersister;


abstract class ScheduledEntityAction implements Executable, Serializable {

	private SessionImplementor session;
	private Serializable id;
	private Object instance;

	private transient ClassPersister persister;

	protected ScheduledEntityAction(SessionImplementor session, Serializable id, Object instance, ClassPersister persister) {
		this.session = session;
		this.id = id;
		this.persister = persister;
		this.instance = instance;
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		try {
			persister = session.getPersister(instance);
		}
		catch(MappingException e) {
			throw new IOException("Unable to resolve class persister on deserialization");
		}
	}

	public final Serializable[] getPropertySpaces() {
		return persister.getPropertySpaces();
	}

	protected final SessionImplementor getSession() {
		return session;
	}

	protected final Serializable getId() {
		return id;
	}

	protected final ClassPersister getPersister() {
		return persister;
	}

	protected final Object getInstance() {
		return instance;
	}
	
	public boolean hasAfterTransactionCompletion() {
		return persister.hasCache();
	}

	public void beforeExecutions() {
		throw new AssertionFailure("beforeExecutions() called for non-collection action");
	}

}






