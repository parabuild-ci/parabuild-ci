package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VCSServerServiceAsync {

  /**
   * Saves a server.
   *
   * @param serverClientVO the VO representation of a server to save to the database.
   */
  void saveServer(VCSServerClientVO serverClientVO, AsyncCallback<Void> async);
}
