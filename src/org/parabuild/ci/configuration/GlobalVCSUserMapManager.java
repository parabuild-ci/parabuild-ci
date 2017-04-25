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

import net.sf.hibernate.Query;
import org.parabuild.ci.object.GlobalVCSUserMap;

import java.util.List;

/**
 * GlobalVersionControlUserMapingManager
 * <p/>
 *
 * @author Slava Imeshev
 * @since Dec 27, 2008 3:56:16 PM
 */
public final class GlobalVCSUserMapManager {

  private static final GlobalVCSUserMapManager instance = new GlobalVCSUserMapManager();


  public static GlobalVCSUserMapManager getInstance() {
    return instance;
  }


  public GlobalVCSUserMap getMapping(final Integer id) {
    return (GlobalVCSUserMap) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select m from GlobalVCSUserMap m where m.ID = ?");
        q.setCacheable(true);
        q.setInteger(0, id.intValue());
        return q.uniqueResult();
      }
    });
  }


  public void deleteMapping(final GlobalVCSUserMap map) {
    ConfigurationManager.getInstance().deleteObject(map);
  }


  public List getAllMappings() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select m from GlobalVCSUserMap m order by m.vcsUserName");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public void saveMapping(final GlobalVCSUserMap map) {
    ConfigurationManager.getInstance().saveObject(map);
  }


  public String toString() {
    return "GlobalVCSUserMapManager{}";
  }
}
