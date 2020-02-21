package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * An RPC service interface.
 */
@RemoteServiceRelativePath("service/server")
public interface VCSServerService extends RemoteService {

  /**
   * Returns a repository by its ID.
   *
   * @param serverClientVO the VO representation of a repository to save to the database.
   */
  void saveServer(VCSServerClientVO serverClientVO);
}
