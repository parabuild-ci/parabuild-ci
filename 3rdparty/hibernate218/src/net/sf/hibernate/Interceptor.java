//$Id: Interceptor.java,v 1.13 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;
import java.util.Iterator;

import net.sf.hibernate.type.Type;

/**
 * Allows user code to inspect and/or change property values.
 * <br><br>
 * Inspection occurs before property values are written and after they are read
 * from the database.<br>
 * <br>
 * There might be a single instance of <tt>Interceptor</tt> for a <tt>SessionFactory</tt>, or a new instance
 * might be specified for each <tt>Session</tt>. Whichever approach is used, the interceptor must be
 * serializable if the <tt>Session</tt> is to be serializable. This means that <tt>SessionFactory</tt>-scoped
 * interceptors should implement <tt>readResolve()</tt>.<br>
 * <br>
 * The <tt>Session</tt> may not be invoked from a callback (nor may a callback cause a collection or proxy to
 * be lazily initialized).<br>
 *
 * @see SessionFactory#openSession(Interceptor)
 * @see net.sf.hibernate.cfg.Configuration#setInterceptor(Interceptor)
 * @author Gavin King
 */
public interface Interceptor {
	/**
	 * Called just before an object is initialized. The interceptor may change the <tt>state</tt>, which will
	 * be propagated to the persistent object. Note that when this method is called, <tt>entity</tt> will be
	 * an empty uninitialized instance of the class.
	 *
	 * @return <tt>true</tt> if the user modified the <tt>state</tt> in any way.
	 */
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
	/**
	 * Called when an object is detected to be dirty, during a flush. The interceptor may modify the detected
	 * <tt>currentState</tt>, which will be propagated to both the database and the persistent object.
	 * Note that not all flushes end in actual synchronization with the database, in which case the
	 * new <tt>currentState</tt> will be propagated to the object, but not necessarily (immediately) to
	 * the database. It is strongly recommended that the interceptor <b>not</b> modify the <tt>previousState</tt>.
	 *
	 * @return <tt>true</tt> if the user modified the <tt>currentState</tt> in any way.
	 */
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException;
	/**
	 * Called before an object is saved. The interceptor may modify the <tt>state</tt>, which will be used for
	 * the SQL <tt>INSERT</tt> and propagated to the persistent object.
	 *
	 * @return <tt>true</tt> if the user modified the <tt>state</tt> in any way.
	 */
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
	/**
	 *  Called before an object is deleted. It is not recommended that the interceptor modify the <tt>state</tt>.
	 */
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
	/**
	 * Called before a flush
	 */
	public void preFlush(Iterator entities) throws CallbackException;
	/**
	 * Called after a flush that actually ends in execution of the SQL statements required to synchronize
	 * in-memory state with the database.
	 */
	public void postFlush(Iterator entities) throws CallbackException;	
	/**
	 * Called when a transient entity is passed to <tt>saveOrUpdate()</tt>. The return value determines
	 * <ul>
	 * <li><tt>Boolean.TRUE</tt> - the entity is passed to <tt>save()</tt>, resulting in an <tt>INSERT</tt>
	 * <li><tt>Boolean.FALSE</tt> - the entity is passed to <tt>update()</tt>, resulting in an <tt>UPDATE</tt>
	 * <li><tt>null</tt> - Hibernate uses the <tt>unsaved-value</tt> mapping to determine if the object is unsaved
	 * </ul>
	 * @param entity a transient entity
	 * @return Boolean or <tt>null</tt> to choose default behaviour
	 */
	public Boolean isUnsaved(Object entity);
	/**
	 * Called from <tt>flush()</tt>. The return value determines whether the entity is updated
	 * <ul>
	 * <li>an array of property indices - the entity is dirty
	 * <li>and empty array - the entity is not dirty
	 * <li><tt>null</tt> - use Hibernate's default dirty-checking algorithm 
	 * </ul>
	 * @param entity a persistent entity
	 * @return array of dirty property indices or <tt>null</tt> to choose default behaviour
	 */
	public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types);
	/**
	 * Instantiate the entity class. Return <tt>null</tt> to indicate that Hibernate should use
	 * the default constructor of the class. The identifier property of the returned instance 
	 * should be initialized with the given identifier.
	 * 
	 * @param clazz a mapped class
	 * @param id the identifier of the new instance
	 * @return an instance of the class, or <tt>null</tt> to choose default behaviour
	 */
	public Object instantiate(Class clazz, Serializable id) throws CallbackException;
}






