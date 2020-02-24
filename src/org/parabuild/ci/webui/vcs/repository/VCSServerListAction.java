package org.parabuild.ci.webui.vcs.repository;

import net.sf.hibernate.Query;
import org.apache.struts2.interceptor.SessionAware;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSServer;
import org.parabuild.ci.repository.VCSServerVO;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

/**
 * Shows a list of repositories.
 */
@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
public final class VCSServerListAction extends ParabuildActionSupport implements SessionAware {

  private static final long serialVersionUID = -7476321456269017100L;
  private static final String SERVER_LIST = "Repository Server List";
  private static final String MODE_USER = "user";

  private final List<VCSServerVO> serverList = new LinkedList<>();

  private Map<String, Object> session;

  private String mode = MODE_USER;


  public VCSServerListAction() {
    setTitle(SERVER_LIST);
  }


  public Map<String, Object> getSession() {
    return session;
  }


  /**
   * Sets the Map of session attributes in the implementing class.
   *
   * @param session a Map of HTTP session attribute name/value pairs.
   */
  @Override
  public void setSession(final Map<String, Object> session) {
    this.session = session;
  }


  public String getMode() {
    return mode;
  }


  public void setMode(final String mode) {
    this.mode = mode;
  }


  public List<VCSServerVO> getServerList() {
    return serverList;
  }


  @Override
  public String execute() {


    // List all repositories
    runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        final Query q = session.createQuery("select srv from VCSServer srv where srv.deleted = no");
        q.setCacheable(true);
        final List list = q.list();
        for (int i = 0; i < list.size(); i++) {

          // Get the record
          final VCSServer server = (VCSServer) list.get(i);

          // Create VOs
          final VCSServerVO serverVO = new VCSServerVO();
          serverVO.setDescription(server.getDescription());
          serverVO.setName(server.getName());
          serverVO.setType(server.getType());
          serverVO.setId(server.getId());
          serverList.add(serverVO);
        }
        return null;
      }
    });

    return SUCCESS;
  }
}
