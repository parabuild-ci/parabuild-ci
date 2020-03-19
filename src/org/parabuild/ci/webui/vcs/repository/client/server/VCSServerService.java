package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.parabuild.ci.webui.vcs.repository.common.VCSServerVO;

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
  void saveServer(VCSServerVO serverClientVO);

  /**
   * Returns a list of VCSServers.
   *
   * @return a list of VCSServers.
   */
  VCSServerVO[] getVCSServers();
}
