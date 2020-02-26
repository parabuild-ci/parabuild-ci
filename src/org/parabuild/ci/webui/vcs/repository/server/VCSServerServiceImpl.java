package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.common.VCSAttribute;
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

          new VCSServerType(VCSAttribute.SCM_ACCUREV, VCSAttribute.NAME_SCM_ACCUREV),
          new VCSServerType(VCSAttribute.SCM_CLEARCASE, VCSAttribute.NAME_SCM_CLEARCASE),
          new VCSServerType(VCSAttribute.SCM_BAZAAR, VCSAttribute.NAME_BAZAAR),
          new VCSServerType(VCSAttribute.SCM_CVS, VCSAttribute.NAME_SCM_CVS),
          new VCSServerType(VCSAttribute.SCM_FILESYSTEM, VCSAttribute.NAME_SCM_FILESYSTEM),
          new VCSServerType(VCSAttribute.SCM_GENERIC, VCSAttribute.NAME_SCM_GENERIC),
          new VCSServerType(VCSAttribute.SCM_GIT, VCSAttribute.NAME_SCM_GIT),
          new VCSServerType(VCSAttribute.SCM_MERCURIAL, VCSAttribute.NAME_SCM_MERCURIAL),
          new VCSServerType(VCSAttribute.SCM_MKS, VCSAttribute.NAME_SCM_MKS),
          new VCSServerType(VCSAttribute.SCM_PERFORCE, VCSAttribute.NAME_SCM_PERFORCE),
          new VCSServerType(VCSAttribute.SCM_PVCS, VCSAttribute.NAME_SCM_PVCS),
          new VCSServerType(VCSAttribute.SCM_REFERENCE, VCSAttribute.NAME_SCM_REFERENCE),
          new VCSServerType(VCSAttribute.SCM_STARTEAM, VCSAttribute.NAME_SCM_STARTEAM),
          new VCSServerType(VCSAttribute.SCM_SURROUND, VCSAttribute.NAME_SCM_SURROUND),
          new VCSServerType(VCSAttribute.SCM_SVN, VCSAttribute.NAME_SCM_SVN),
          new VCSServerType(VCSAttribute.SCM_VAULT, VCSAttribute.NAME_SCM_VAULT),
          new VCSServerType(VCSAttribute.SCM_VSS, VCSAttribute.NAME_SCM_VSS),
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
