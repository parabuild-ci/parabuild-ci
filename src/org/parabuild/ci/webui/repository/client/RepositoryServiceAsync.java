package org.parabuild.ci.webui.repository.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RepositoryServiceAsync {

  void saveRepository(RepositoryVO repositoryVO, AsyncCallback callback);
}
