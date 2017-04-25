/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.configuration;

import java.io.*;
import java.sql.*;
import java.util.*;

import net.sf.hibernate.*;
import net.sf.hibernate.type.*;

/**
 */
public final class ParabuildSessionImpl implements Session, Serializable {

  private static final long serialVersionUID = -121279693054868421L; // NOPMD

  private Session s = null;


  public ParabuildSessionImpl(final Session s) {
    this.s = s;
  }


  public Transaction beginTransaction() {
    throw new IllegalStateException("Transaction can be started only by HibernateTransaction");
  }


  public Connection close() {
    throw new IllegalStateException("Session is not allowed to be closed directly. ");
  }


  public Connection connection() throws HibernateException {
    return s.connection();
  }


  public boolean contains(final Object o) {
    return s.contains(o);
  }


  public Criteria createCriteria(final Class aClass) {
    return s.createCriteria(aClass);
  }


  public Query createFilter(final Object o, final String s) throws HibernateException {
    return this.s.createFilter(o, s);
  }


  public Query createQuery(final String s) throws HibernateException {
    return this.s.createQuery(s);
  }


  public void delete(final Object o) throws HibernateException {
    s.delete(o);
  }


  public int delete(final String s) throws HibernateException {
    return this.s.delete(s);
  }


  public int delete(final String s, final Object o, final Type type) throws HibernateException {
    return this.s.delete(s, o, type);
  }


  public int delete(final String s, final Object[] objects, final Type[] types) throws HibernateException {
    return this.s.delete(s, objects, types);
  }


  public Connection disconnect() throws HibernateException {
    return s.disconnect();
  }


  public void evict(final Object o) throws HibernateException {
    s.evict(o);
  }


  public Collection filter(final Object o, final String s) throws HibernateException {
    return this.s.filter(o, s);
  }


  public Collection filter(final Object o, final String s, final Object o1, final Type type) throws HibernateException {
    return this.s.filter(o, s, o1, type);
  }


  public Collection filter(final Object o, final String s, final Object[] objects, final Type[] types) throws HibernateException {
    return this.s.filter(o, s, objects, types);
  }


  public List find(final String s) throws HibernateException {
    return this.s.find(s);
  }


  public List find(final String s, final Object o, final Type type) throws HibernateException {
    return this.s.find(s, o, type);
  }


  public List find(final String s, final Object[] objects, final Type[] types) throws HibernateException {
    return this.s.find(s, objects, types);
  }


  public void flush() throws HibernateException {
    s.flush();
  }


  public LockMode getCurrentLockMode(final Object o) throws HibernateException {
    return s.getCurrentLockMode(o);
  }


  public FlushMode getFlushMode() {
    return s.getFlushMode();
  }


  public Serializable getIdentifier(final Object o) throws HibernateException {
    return s.getIdentifier(o);
  }


  public Query getNamedQuery(final String s) throws HibernateException {
    return this.s.getNamedQuery(s);
  }


  public boolean isConnected() {
    return s.isConnected();
  }


  public boolean isOpen() {
    return s.isOpen();
  }


  public Iterator iterate(final String s) throws HibernateException {
    return this.s.iterate(s);
  }


  public Iterator iterate(final String s, final Object o, final Type type) throws HibernateException {
    return this.s.iterate(s, o, type);
  }


  public Iterator iterate(final String s, final Object[] objects, final Type[] types) throws HibernateException {
    return this.s.iterate(s, objects, types);
  }


  public Object load(final Class aClass, final Serializable serializable) throws HibernateException {
    return s.load(aClass, serializable);
  }


  public Object load(final Class aClass, final Serializable serializable, final LockMode lockMode) throws HibernateException {
    return s.load(aClass, serializable, lockMode);
  }


  public void load(final Object o, final Serializable serializable) throws HibernateException {
    s.load(o, serializable);
  }


  public void lock(final Object o, final LockMode lockMode) throws HibernateException {
    s.load(o, lockMode);
  }


  public void reconnect() throws HibernateException {
    s.reconnect();
  }


  public void reconnect(final Connection connection) throws HibernateException {
    s.reconnect(connection);
  }


  public void refresh(final Object o) throws HibernateException {
    s.refresh(o);
  }


  public void refresh(final Object o, final LockMode lockMode) throws HibernateException {
    s.refresh(o, lockMode);
  }


  public Serializable save(final Object o) throws HibernateException {
    return s.save(o);
  }


  public void save(final Object o, final Serializable serializable) throws HibernateException {
    s.save(o, serializable);
  }


  public void saveOrUpdate(final Object o) throws HibernateException {
    s.saveOrUpdate(o);
  }


  public void setFlushMode(final FlushMode flushMode) {
    s.setFlushMode(flushMode);
  }


  public void update(final Object o) throws HibernateException {
    s.update(o);
  }


  public void update(final Object o, final Serializable serializable) throws HibernateException {
    s.update(o, serializable);
  }


  /**
   * Cancel execution of the current query. May be called from one thread
   * to stop execution of a query in another thread. Use with care!
   */
  public void cancelQuery() throws HibernateException {
    s.cancelQuery();
  }


  /**
   * Completely clear the session. Evict all loaded instances and cancel all pending
   * saves, updates and deletions. Do not close open iterators or instances of
   * <tt>ScrollableResults</tt>.
   */
  public void clear() {
    s.clear();
  }


  /**
   * Create a new instance of <tt>Query</tt> for the given SQL string.
   *
   * @param sql a query expressed in SQL
   * @param returnAlias a table alias that appears inside <tt>{}</tt> in the SQL string
   * @param returnClass the returned persistent class
   */
  public Query createSQLQuery(final String sql, final String returnAlias, final Class returnClass) {
    return s.createSQLQuery(sql, returnAlias, returnClass);
  }


  /**
   * Create a new instance of <tt>Query</tt> for the given SQL string.
   *
   * @param sql a query expressed in SQL
   * @param returnAliases an array of table aliases that appear inside <tt>{}</tt> in the SQL string
   * @param returnClasses the returned persistent classes
   */
  public Query createSQLQuery(final String sql, final String[] returnAliases, final Class[] returnClasses) {
    return s.createSQLQuery(sql, returnAliases, returnClasses);
  }


  /**
   * Return the persistent instance of the given entity class with the given identifier,
   * or null if there is no such persistent instance. (If the instance, or a proxy for the
   * instance, is already associated with the session, return that instance or proxy.)
   *
   * @param clazz a persistent class
   * @param id an identifier
   * @return a persistent instance or null
   * @throws HibernateException
   */
  public Object get(final Class clazz, final Serializable id) throws HibernateException {
    return s.get(clazz, id);
  }


  /**
   * Return the persistent instance of the given entity class with the given identifier,
   * or null if there is no such persistent instance. Obtain the specified lock mode
   * if the instance exists.
   *
   * @param clazz a persistent class
   * @param id an identifier
   * @param lockMode the lock mode
   * @return a persistent instance or null
   * @throws HibernateException
   */
  public Object get(final Class clazz, final Serializable id, final LockMode lockMode) throws HibernateException {
    return s.get(clazz, id, lockMode);
  }


  /**
   * Get the <tt>SessionFactory</tt> that created this instance.
   * @see SessionFactory
   */
  public SessionFactory getSessionFactory() {
    return s.getSessionFactory();
  }


  /**
   * Does this <tt>Session</tt> contain any changes which must be
   * synchronized with the database? Would any SQL be executed if
   * we flushed this session?
   *
   * @return boolean
   */
  public boolean isDirty() throws HibernateException {
    return s.isDirty();
  }


  /**
   * Persist all reachable transient objects, reusing the current identifier
   * values.
   *
   * @param object a transient instance of a persistent class
   */
  public void replicate(final Object object, final ReplicationMode replicationMode) throws HibernateException {
    s.replicate(object, replicationMode);
  }


  /**
   * Copy the state of the given object onto the persistent object with the same
   * identifier. If there is no persistent instance currently associated with
   * the session, it will be loaded. Return the persistent instance. If the
   * given instance is unsaved or does not exist in the database, save it and
   * return it as a newly persistent instance. Otherwise, the given instance
   * does not become associated with the session.
   *
   * @param object a transient instance with state to be copied
   * @return an updated persistent instance
   */
  public Object saveOrUpdateCopy(final Object object) throws HibernateException {
    return s.saveOrUpdateCopy(object);
  }


  /**
   * Copy the state of the given object onto the persistent object with the
   * given identifier. If there is no persistent instance currently associated
   * with the session, it will be loaded. Return the persistent instance. If
   * there is no database row with the given identifier, save the given instance
   * and return it as a newly persistent instance. Otherwise, the given instance
   * does not become associated with the session.
   *
   * @param object a persistent or transient instance with state to be copied
   * @param id the identifier of the instance to copy to
   * @return an updated persistent instance
   */
  public Object saveOrUpdateCopy(final Object object, final Serializable id) throws HibernateException {
    return s.saveOrUpdateCopy(object, id);
  }
}
