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

import java.util.*;
import junit.framework.*;
import org.apache.commons.logging.*;

import net.sf.hibernate.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

/**
 */
public class SSTestHibernateTransaction extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestHibernateTransaction.class);


  public SSTestHibernateTransaction(final String s) {
    super(s);
  }


  public void test_beginTransaction() throws Exception {
    // get #1
    final HibernateTransaction tx1 = HibernateTransaction.beginTransaction();
    assertNotNull(tx1);
    final Session session1 = tx1.getSession();
    assertNotNull(session1);
    assertEquals(1, HibernateTransaction.getLevel());
    HibernateTransaction.commitTransaction();
    assertEquals(0, HibernateTransaction.getLevel());
  }


  public void test_beginNestedTransaction() throws Exception {
    // get #1
    final HibernateTransaction tx1 = HibernateTransaction.beginTransaction();
    assertNotNull(tx1);
    final Session session1 = tx1.getSession();
    assertNotNull(session1);
    assertEquals(1, HibernateTransaction.getLevel());

    // get #2
    final HibernateTransaction tx2 = HibernateTransaction.beginTransaction();
    assertNotNull(tx2);
    final Session session2 = tx2.getSession();
    assertEquals(2, HibernateTransaction.getLevel());

    // should be the same
    assertEquals(tx1, tx2);

    HibernateTransaction.commitTransaction();
    assertEquals(1, HibernateTransaction.getLevel());

    HibernateTransaction.commitTransaction();
    assertEquals(0, HibernateTransaction.getLevel());
  }


  /**
   * Tests ThreadLocal transaction concept
   */
  public void test_transctionConcept() throws Exception {
    final int buildID = TestHelper.TEST_CVS_VALID_BUILD_ID;
    Session session = null;
    try {
      final HibernateTransaction hs = HibernateTransaction.beginTransaction();
      session = hs.getSession();
      final List result = session.find("from BuildSequence as bs order by bs.sequenceID where bs.buildID = ?",
        new Integer(buildID), Hibernate.INTEGER);
      HibernateTransaction.commitTransaction();
    } catch (RuntimeException e) {
      HibernateTransaction.rollbackTransaction();
      throw e;
    } catch (Exception e) {
      HibernateTransaction.rollbackTransaction();
      throw new UnexpectedErrorException(e);
    }
  }


  public void test_nestedTxThrowsException() throws Throwable {

    //
    // one more commit
    //
    HibernateTransaction.beginTransaction();
    HibernateTransaction.commitTransaction();
    try {
      // commit again
      HibernateTransaction.commitTransaction();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }

    //
    // one more commit
    //
    HibernateTransaction.beginTransaction();
    HibernateTransaction.rollbackTransaction();
    try {
      // commit again
      HibernateTransaction.commitTransaction();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }

  }


  public void test_commitRollback() throws HibernateException {
    //
    // one more rollback
    //
    HibernateTransaction.beginTransaction();
    HibernateTransaction.commitTransaction();
    try {
      // rollback again
      HibernateTransaction.rollbackTransaction();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }
  }


  public void test_rollbackRollback() throws HibernateException {
    //
    // one more rollback
    //
    HibernateTransaction.beginTransaction();
    HibernateTransaction.rollbackTransaction();
    try {
      // rollback again
      HibernateTransaction.rollbackTransaction();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }
  }


  public void test_rollbackAlone() throws HibernateException {
    //
    // rollback alone
    //
    try {
      HibernateTransaction.rollbackTransaction();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }
  }


  public void test_commitAlone() throws HibernateException {
    //
    // commit alone
    //
    try {
      HibernateTransaction.commitTransaction();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestHibernateTransaction.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    if (log.isDebugEnabled()) log.debug("setUp");
    validateNoActiveTx();
  }


  protected void tearDown() throws Exception {
    super.tearDown();
  }


  private void validateNoActiveTx() {
    assertEquals(0, HibernateTransaction.getLevel());
  }
}
