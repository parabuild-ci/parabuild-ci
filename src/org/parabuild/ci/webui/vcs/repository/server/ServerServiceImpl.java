package org.parabuild.ci.webui.vcs.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSServer;
import org.parabuild.ci.webui.vcs.repository.client.ServerService;
import org.parabuild.ci.webui.vcs.repository.client.VCSServerClientVO;

public class ServerServiceImpl extends RemoteServiceServlet implements ServerService {

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

        System.out.println("vcsServer = " + vcsServer);

        return null;
      }
    });

  }
}
