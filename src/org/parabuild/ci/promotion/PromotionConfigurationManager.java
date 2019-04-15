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
package org.parabuild.ci.promotion;

import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.PromotionPolicy;
import org.parabuild.ci.object.PromotionPolicyStep;

public final class PromotionConfigurationManager {

  private static final PromotionConfigurationManager instance = new PromotionConfigurationManager();


  public static PromotionConfigurationManager getInstance() {
    return instance;
  }


  private PromotionConfigurationManager() {
  }


  /**
   * @return list of {@link PromotionVO} order by project name and promotion name
   */
  public List getPromotionList() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = new ArrayList(11);
        final List list = session.createQuery(" select prj.name, prm.id, prm.name, prm.description " +
                "   from Project prj, PromotionPolicy prm " +
                "  where prm.projectID = prj.id order by prj.name, prm.name")
                .setCacheable(true)
                .list();
        for (int i = 0; i < list.size(); i++) {
          final Object[] objects = (Object[]) list.get(i);
          result.add(new PromotionVO((String) objects[0], (String) objects[2], (Integer) objects[1], (String) objects[3]));
        }
        return result;
      }
    });
  }


  /**
   * @return list of {@link PromotionPolicyStep} ordered by order line numbers.
   */
  public List getPromotionStepsList(final int promotionID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery(" select prms " +
                "   from PromotionPolicyStep prms " +
                "  where prms.promotionID = ? and prms.deleted=no order by prms.lineNumber");
        query.setInteger(0, promotionID);
        return query.setCacheable(true).list();
      }
    });
  }


  /**
   * Removes promotion policy.
   *
   * @param id
   */
  public void removePolicy(final int id) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from PromotionPolicy where ID = ?", new Integer(id), Hibernate.INTEGER);
        return null;
      }
    });
  }


  /**
   * Returns promotion policy.
   *
   * @param id
   * @return Returns promotion policy or null if not found.
   */
  public PromotionPolicy getPromotionPolicy(final int id) {
    return (PromotionPolicy) ConfigurationManager.getInstance().getObject(PromotionPolicy.class, id);
  }


  public void save(final PromotionPolicy policy) {
    ConfigurationManager.getInstance().saveObject(policy);
  }


  public PromotionPolicyStep getPromotionPolicyStep(final int id) {
    return (PromotionPolicyStep) ConfigurationManager.getInstance().getObject(PromotionPolicyStep.class, id);
  }


  public void removePolicyStep(final int id) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from PromotionPolicyStep where ID = ?", new Integer(id), Hibernate.INTEGER);
        return null;
      }
    });
  }


  public void save(final PromotionPolicyStep step) {
    ConfigurationManager.getInstance().saveObject(step);
  }


  public int getMaxStepLineNumber(final int policyID) {
    return (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery(" select max(prms.lineNumber) " +
                "   from PromotionPolicyStep prms " +
                "  where prms.promotionID = ? and prms.deleted=no");
        query.setInteger(0, policyID);
        return query.setCacheable(true).uniqueResult();
      }
    });
  }
}
