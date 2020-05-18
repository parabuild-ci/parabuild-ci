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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.services.ServiceManager;

/**
 * ThreadLocal hibernate transaction
 * @noinspection ClassHasNoToStringMethod
 */
public final class HibernateTransaction {

  private static final Log log = LogFactory.getLog(HibernateTransaction.class);

  private static final ThreadLocal sessionContext = new ThreadLocal();

  private Transaction tx; // NOPMD (SingularField)
  private Session session;
  private int level;


  public static HibernateTransaction beginTransaction() throws HibernateException {
    HibernateTransaction tlTransaction = (HibernateTransaction)sessionContext.get();
    if (tlTransaction == null) {
      final SessionFactory factory = ServiceManager.getInstance().getConfigurationService().getSessionFactory();
      tlTransaction = new HibernateTransaction();
      tlTransaction.session = factory.openSession();
      tlTransaction.tx = tlTransaction.session.beginTransaction();
      tlTransaction.level = 0;
      sessionContext.set(tlTransaction);
    } else {
      validateTransaction(tlTransaction);
    }
//    if (log.isDebugEnabled()) log.debug("Begin TX, level: " + tlTransaction.level);
    tlTransaction.level++;
    return tlTransaction;
  }


  /**
   * Returns delegate implementing net.sf.hibernate.Session
   *
   * @return Session of this transaction
   *
   * @see Session
   */
  public Session getSession() {
    return new ParabuildSessionImpl(session);
  }


  public static void commitTransaction() throws HibernateException {
    final HibernateTransaction tlTransaction = (HibernateTransaction)sessionContext.get();
    validateTransaction(tlTransaction);
//    if (log.isDebugEnabled()) log.debug("Commit TX, level: " + tlTransaction.level);
    tlTransaction.level--;
    if (tlTransaction.level <= 0) { // NOPMD
      tlTransaction.tx.commit();
      tlTransaction.session.flush();
      tlTransaction.session.close();
      tlTransaction.tx = null;
      tlTransaction.session = null;
      sessionContext.set(null);
    }
  }


  public static void rollbackTransaction() throws HibernateException {
    final HibernateTransaction tlTransaction = (HibernateTransaction)sessionContext.get();
    validateTransaction(tlTransaction);
//    if (log.isDebugEnabled()) log.debug("Rollback TX, level: " + tlTransaction.level);
    tlTransaction.level--;
    if (tlTransaction.level <= 0) {
      tlTransaction.tx.rollback();
      tlTransaction.session.close();
      tlTransaction.tx = null;
      tlTransaction.session = null;
      sessionContext.set(null);
    }
  }


  public static int getLevel() {

    int result = 0;
    final HibernateTransaction tlSession = (HibernateTransaction) sessionContext.get();
    if (tlSession != null) {
      result = tlSession.level;
    }
    return result;
  }


  private static void validateTransaction(final HibernateTransaction tlTransaction) throws HibernateException {
    if (tlTransaction == null) throw new IllegalStateException("There is no active transaction context at commit");
    if (tlTransaction.tx.wasRolledBack()) throw new IllegalStateException("The transaction has been already rolledback");
    if (tlTransaction.tx.wasCommitted()) throw new IllegalStateException("The transaction  has been already commited");
  }


  /**
   *
   */
  protected void finalize() throws Throwable {
    if (tx != null && !tx.wasCommitted() && !tx.wasCommitted()) {
      log.error("Transaction was finalized being active, will roll back hard");
      PersistanceUtils.rollbackHard(tx);
    }
    if (session != null && !session.isOpen()) {
      log.error("Session was finalized being active, will close");
      PersistanceUtils.closeHard(session);
    }
    super.finalize(); // NOPMD - we are required to call it.
  }
}

