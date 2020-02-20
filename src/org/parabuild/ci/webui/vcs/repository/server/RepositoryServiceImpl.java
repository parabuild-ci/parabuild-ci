package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSRepository;
import org.parabuild.ci.webui.vcs.repository.client.VCSRepositoryClientVO;
import org.parabuild.ci.webui.vcs.repository.client.RepositoryService;

public class RepositoryServiceImpl extends RemoteServiceServlet implements RepositoryService {

  private static final long serialVersionUID = 2497764302106094743L;


  /**
   * Saves the repository update to the database.
   **/
  @Override
  public void saveRepository(final VCSRepositoryClientVO repositoryVO) {

    // ... Implement

    ConfigurationManager.runInHibernate(new TransactionCallback() {
      @Override
      public Object runInTransaction() throws Exception {

        // ...
        final VCSRepository vcsRepository = new VCSRepository();
        vcsRepository.setName(repositoryVO.getName());
        vcsRepository.setType(repositoryVO.getType());
        vcsRepository.setDescription(repositoryVO.getDescription());

        ConfigurationManager.getInstance().saveObject(vcsRepository);

        return null;
      }
    });
  }
}
