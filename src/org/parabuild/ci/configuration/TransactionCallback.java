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

import net.sf.hibernate.Session;


/**
 * Hibernate transaction callback interface. The main use for this
 * class is to be called by runInHibernate method of
 * ConfigurationManager that supplies Hibernate transaction
 * environment.
 *
 * @see ConfigurationManager#runInHibernate
 * @noinspection ClassHasNoToStringMethod
 */
public abstract class TransactionCallback {

  // NOTE: vimeshev - 09/06/2003 - fields made protected for access
  // convinience for implementing runInTransaction method
  protected Session session = null;


  /**
   * Sets Hibernate sesssion to make it available for runInTransaction
   * method.
   *
   * @param session
   */
  public final void setSession(final Session session) {
    this.session = session;
  }


  /**
   * Callback method to be called by ConfigurationManager's
   * runInHibernate when hibenrate Session and Transaction are set and
   * are available for an implementing method.
   *
   * @return Object or null on accordance with semantics of
   * implementing method.
   *
   * @throws Exception
   */
  public abstract Object runInTransaction() throws Exception;
}



