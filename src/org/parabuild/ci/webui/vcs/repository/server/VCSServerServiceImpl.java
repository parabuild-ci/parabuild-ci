package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSServer;
import org.parabuild.ci.object.VCSServerAttribute;
import org.parabuild.ci.repository.VCSRepositoryManager;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerAttributeVO;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerService;
import org.parabuild.ci.webui.vcs.repository.common.VCSServerVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Remote GWT service.
 */
public final class VCSServerServiceImpl extends RemoteServiceServlet implements VCSServerService {

  private static final long serialVersionUID = 2497764302106094743L;


  /**
   * Saves the server config to the database.
   *
   * @param vcsServerVO the VO representation of a repository to save to the database.
   */
  @Override
  public void saveServer(final VCSServerVO vcsServerVO) {

    ConfigurationManager.runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() {

        final ConfigurationManager configurationManager = ConfigurationManager.getInstance();

        // Save server
        final VCSServer vcsServer = new VCSServer();
        vcsServer.setDescription(vcsServerVO.getDescription());
        vcsServer.setName(vcsServerVO.getName());
        vcsServer.setType(vcsServerVO.getType());
        vcsServer.setId(vcsServerVO.getId());
        configurationManager.saveObject(vcsServer);

        // Save attributes
        final int serverId = vcsServer.getId(); // Pick up saved ID if it was new
        final List<VCSServerAttributeVO> attributes = vcsServerVO.getAttributes();
        for (final VCSServerAttributeVO vcsServerAttributeVO : attributes) {

          final VCSServerAttribute vcsServerAttribute = new VCSServerAttribute();
          vcsServerAttribute.setTimeStamp(vcsServerAttributeVO.getTimeStamp());
          vcsServerAttribute.setValue(vcsServerAttributeVO.getValue());
          vcsServerAttribute.setName(vcsServerAttributeVO.getName());
          vcsServerAttribute.setId(vcsServerAttributeVO.getId());
          vcsServerAttribute.setServerId(serverId);

          configurationManager.saveObject(vcsServerAttribute);
        }

        return null;
      }
    });

  }


  @Override
  public VCSServerVO[] getVCSServers() {

    return (VCSServerVO[]) ConfigurationManager.runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        final VCSRepositoryManager vcsRepositoryManager = VCSRepositoryManager.getInstance();
        final List<VCSServer> vcsServers = vcsRepositoryManager.getVCSServers(session);

        final VCSServerVO[] result = new VCSServerVO[vcsServers.size()];

        for (int i = 0; i < vcsServers.size(); i++) {

          final VCSServer vcsServerVO = vcsServers.get(i);
          final VCSServerVO vcsServerClientVO = new VCSServerVO();
          vcsServerClientVO.setDescription(vcsServerVO.getDescription());
          vcsServerClientVO.setName(vcsServerVO.getName());
          vcsServerClientVO.setType(vcsServerVO.getType());
          vcsServerClientVO.setId(vcsServerVO.getId());
          result[i] = vcsServerClientVO;

          // Get attributes
          final List<VCSServerAttribute> vcsServerAttributes = vcsRepositoryManager.getVCSServerAttributes(session);
          final ArrayList<VCSServerAttributeVO> attributes = new ArrayList<VCSServerAttributeVO>(vcsServerAttributes.size());
          for (final VCSServerAttribute vcsServerAttribute : vcsServerAttributes) {
            final VCSServerAttributeVO vcsServerAttributeVO = new VCSServerAttributeVO();
            vcsServerAttributeVO.setTimeStamp(vcsServerAttribute.getTimeStamp());
            vcsServerAttributeVO.setName(vcsServerAttribute.getName());
            vcsServerAttributeVO.setValue(vcsServerAttribute.getValue());
            vcsServerAttributeVO.setId(vcsServerAttribute.getId());
            attributes.add(vcsServerAttributeVO);
          }
          vcsServerClientVO.setAttributes(attributes);
        }

        return result;
      }
    });
  }
}
