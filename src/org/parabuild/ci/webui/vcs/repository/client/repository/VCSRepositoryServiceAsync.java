package org.parabuild.ci.webui.vcs.repository.client.repository;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VCSRepositoryServiceAsync {

  void saveRepository(VCSRepositoryClientVO repositoryVO, AsyncCallback<Void> callback);
}
