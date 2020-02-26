package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSServer;
import org.parabuild.ci.repository.VCSRepositoryManager;
import org.parabuild.ci.repository.VCSServerVO;
import org.parabuild.ci.webui.vcs.repository.client.VCSServerClientVO;
import org.parabuild.ci.webui.vcs.repository.client.VCSServerService;
import org.parabuild.ci.webui.vcs.repository.client.VCSServerType;

import static org.parabuild.ci.common.VersionControlSystem.vcsToString;

public class VCSServerServiceImpl extends RemoteServiceServlet implements VCSServerService {

  private static final long serialVersionUID = 2497764302106094743L;

  /**
   * Static cache of VCS types.
   */
  private static final VCSServerType[] VCS_SERVER_TYPES = {

          new VCSServerType(VersionControlSystem.SCM_ACCUREV, VersionControlSystem.NAME_SCM_ACCUREV),
          new VCSServerType(VersionControlSystem.SCM_CLEARCASE, VersionControlSystem.NAME_SCM_CLEARCASE),
          new VCSServerType(VersionControlSystem.SCM_BAZAAR, VersionControlSystem.NAME_BAZAAR),
          new VCSServerType(VersionControlSystem.SCM_CVS, VersionControlSystem.NAME_SCM_CVS),
          new VCSServerType(VersionControlSystem.SCM_FILESYSTEM, VersionControlSystem.NAME_SCM_FILESYSTEM),
          new VCSServerType(VersionControlSystem.SCM_GENERIC, VersionControlSystem.NAME_SCM_GENERIC),
          new VCSServerType(VersionControlSystem.SCM_GIT, VersionControlSystem.NAME_SCM_GIT),
          new VCSServerType(VersionControlSystem.SCM_MERCURIAL, VersionControlSystem.NAME_SCM_MERCURIAL),
          new VCSServerType(VersionControlSystem.SCM_MKS, VersionControlSystem.NAME_SCM_MKS),
          new VCSServerType(VersionControlSystem.SCM_PERFORCE, VersionControlSystem.NAME_SCM_PERFORCE),
          new VCSServerType(VersionControlSystem.SCM_PVCS, VersionControlSystem.NAME_SCM_PVCS),
          new VCSServerType(VersionControlSystem.SCM_REFERENCE, VersionControlSystem.NAME_SCM_REFERENCE),
          new VCSServerType(VersionControlSystem.SCM_STARTEAM, VersionControlSystem.NAME_SCM_STARTEAM),
          new VCSServerType(VersionControlSystem.SCM_SURROUND, VersionControlSystem.NAME_SCM_SURROUND),
          new VCSServerType(VersionControlSystem.SCM_SVN, VersionControlSystem.NAME_SCM_SVN),
          new VCSServerType(VersionControlSystem.SCM_VAULT, VersionControlSystem.NAME_SCM_VAULT),
          new VCSServerType(VersionControlSystem.SCM_VSS, VersionControlSystem.NAME_SCM_VSS),
  };


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

    System.out.println("result = " + result);

    return result;
  }


  @Override
  public VCSServerType[] getVCSServerTypes() {

    return VCS_SERVER_TYPES.clone();
  }
}
