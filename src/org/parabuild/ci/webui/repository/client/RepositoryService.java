package org.parabuild.ci.webui.repository.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * An RPC service interface.
 */
public interface RepositoryService extends RemoteService {

  /**
   * Returns a repository by its ID.
   *
   * @param repositoryVO the VO representation of a repository to save to the database.
   */
  void saveRepository(RepositoryVO repositoryVO);
}
