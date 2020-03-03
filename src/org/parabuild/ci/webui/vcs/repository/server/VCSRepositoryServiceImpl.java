package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSRepository;
import org.parabuild.ci.webui.vcs.repository.client.repository.VCSRepositoryClientVO;
import org.parabuild.ci.webui.vcs.repository.client.repository.VCSRepositoryService;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

public class VCSRepositoryServiceImpl extends RemoteServiceServlet implements VCSRepositoryService {

  private static final long serialVersionUID = 2497764302106094743L;


  /**
   * Saves the repository update to the database.
   **/
  @Override
  public void saveRepository(final VCSRepositoryClientVO repositoryVO) {

    runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() {

        final VCSRepository vcsRepository = new VCSRepository();
        vcsRepository.setDescription(repositoryVO.getDescription());
        vcsRepository.setServerId(repositoryVO.getServerId());
        vcsRepository.setName(repositoryVO.getName());

        ConfigurationManager.getInstance().saveObject(vcsRepository);

        return null;
      }
    });
  }
}
