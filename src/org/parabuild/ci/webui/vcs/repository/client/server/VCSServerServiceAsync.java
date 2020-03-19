package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.parabuild.ci.webui.vcs.repository.common.VCSServerVO;

public interface VCSServerServiceAsync {

  /**
   * Saves a server.
   *
   * @param serverClientVO the VO representation of a server to save to the database.
   */
  void saveServer(VCSServerVO serverClientVO, AsyncCallback<Void> async);

  /**
   * Returns a list of VCSServers.
   */
  void getVCSServers(AsyncCallback<VCSServerVO[]> async);
}
