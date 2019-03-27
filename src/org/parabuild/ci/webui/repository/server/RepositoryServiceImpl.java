package org.parabuild.ci.webui.repository.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.parabuild.ci.webui.repository.client.RepositoryService;
import org.parabuild.ci.webui.repository.client.RepositoryVO;

public class RepositoryServiceImpl extends RemoteServiceServlet implements RepositoryService {

  private static final long serialVersionUID = 2497764302106094743L;


  /**
   * Saves the repository update to the database.
   **/
  @Override
  public void saveRepository(final RepositoryVO repositoryVO) {

    // ... Implement
  }
}
