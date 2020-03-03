package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSServer;
import org.parabuild.ci.repository.VCSRepositoryManager;
import org.parabuild.ci.repository.VCSServerVO;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerClientVO;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerService;

import static org.parabuild.ci.common.VersionControlUtil.vcsToString;

public class VCSServerServiceImpl extends RemoteServiceServlet implements VCSServerService {

  private static final long serialVersionUID = 2497764302106094743L;


  /**
   * Saves the server config to the database.
   *
   * @param serverClientVO the VO representation of a repository to save to the database.
   */
  @Override
  public void saveServer(final VCSServerClientVO serverClientVO) {

    ConfigurationManager.runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() {

        // Create a persistence object
        final VCSServer vcsServer = new VCSServer();
        vcsServer.setDescription(serverClientVO.getDescription());
        vcsServer.setName(serverClientVO.getName());
        vcsServer.setType(serverClientVO.getType());

        // Save
        ConfigurationManager.getInstance().saveObject(vcsServer);

        return null;
      }
    });

  }


  @Override
  public VCSServerClientVO[] getVCSServers() {

    final VCSServerVO[] vcsServers = VCSRepositoryManager.getInstance().getVCSServers();
    final VCSServerClientVO[] result = new VCSServerClientVO[vcsServers.length];

    for (int i = 0; i < vcsServers.length; i++) {

      final VCSServerVO vcsServerVO = vcsServers[i];

      final VCSServerClientVO vcsServerClientVO = new VCSServerClientVO();
      vcsServerClientVO.setTypeAsString(vcsToString(vcsServerVO.getType()));
      vcsServerClientVO.setDescription(vcsServerVO.getDescription());
      vcsServerClientVO.setName(vcsServerVO.getName());
      vcsServerClientVO.setType(vcsServerVO.getType());
      vcsServerClientVO.setId(vcsServerVO.getId());

      result[i] = vcsServerClientVO;
    }

    return result;
  }
}
