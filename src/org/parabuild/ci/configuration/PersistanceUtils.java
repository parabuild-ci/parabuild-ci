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

import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.parabuild.ci.common.StringUtils;

public final class PersistanceUtils implements PersistanceConstants {

  private static final Log log = LogFactory.getLog(PersistanceUtils.class);


  /**
   * Utility class constructor.
   */
  private PersistanceUtils() {
  }


  /**
   * Rolls back transaction ignoring exceptions
   *
   * @param transaction
   */
  public static void rollbackHard(final Transaction transaction) {
    try {
      transaction.rollback();
    } catch (final Exception e) {
      // ignore
      log.warn("Exception while rolling back a TX", e);
    }
  }


  /**
   * Closes session hard ignoring exceptions
   */
  public static void closeHard(final Session session) {
    try {
      session.close();
    } catch (final Exception e) {
      // ignore
      log.warn("Exception while closing a session", e);
    }
  }


  /**
   * Flushes session, commits transaction and closes session
   *
   * @param transaction
   * @param session
   *
   * @throws HibernateException
   */
  public static void commitAndClose(final Transaction transaction, final Session session) throws HibernateException {
    session.flush();
    transaction.commit();
    session.close();
  }


  public static void executeDDLs(final Statement st, final String[] ddlsPre) throws SQLException {
    executeDDLs(st, ddlsPre, false);
  }


  public static void executeDDLs(final Statement st, final String[] ddlsPre, final boolean commitEachStatement) throws SQLException {
    for (int i = 0; i < ddlsPre.length; i++) {
      final String ddl = ddlsPre[i];
      if (!StringUtils.isBlank(ddl)) {
        st.execute(ddl);
        if (commitEachStatement) {
          st.getConnection().commit();
        }
      }
    }
  }
}
