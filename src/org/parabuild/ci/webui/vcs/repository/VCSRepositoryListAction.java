package org.parabuild.ci.webui.vcs.repository;

import net.sf.hibernate.Query;
import org.apache.struts2.interceptor.SessionAware;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Shows a list of repositories.
 */
public final class VCSRepositoryListAction extends ParabuildActionSupport implements SessionAware {

  private static final long serialVersionUID = -7476321456269017100L;
  private static final String REPOSITORY_LIST = "Repository List";
  private static final String MODE_USER = "user";

  private final List<VCSRepositoryVO> repositoryList = new LinkedList<>();
  private Map<String, Object> session;
  private String mode = MODE_USER;


  public VCSRepositoryListAction() {
    setTitle(REPOSITORY_LIST);
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


  public List<VCSRepositoryVO> getRepositoryList() {
    return repositoryList;
  }


  @Override
  public String execute() {


    // List all repositories
    ConfigurationManager.runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        final Query q = session.createQuery("select repo from VCSRepository repo where repo.deleted = no");
        q.setCacheable(true);

        final List list = q.list();
        for (int i = 0; i < list.size(); i++) {

          // Get the record
          final VCSRepository repository = (VCSRepository) list.get(i);

          // Create VOs
          final VCSRepositoryVO repositoryVO = new VCSRepositoryVO();
          repositoryVO.setDescription(repository.getDescription());
          repositoryVO.setName(repository.getName());
          repositoryVO.setType(repository.getType());
          repositoryVO.setId(repository.getId());

          // Add to the list
          repositoryList.add(repositoryVO);
        }
        return null;
      }
    });

    return SUCCESS;
  }
}
