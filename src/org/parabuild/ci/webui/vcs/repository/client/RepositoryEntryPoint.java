package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.core.client.EntryPoint;
import org.parabuild.ci.webui.vcs.repository.common.RepositoryUtils;

/**
 * Client-side Java source for the entry-point class for managing VCS repositories.
 */
public final class RepositoryEntryPoint implements EntryPoint {


  public RepositoryEntryPoint() {

  }


  /**
   * Set up repository management module.
   */
  @Override
  public void onModuleLoad() {

    final AddRepositoryClickHandler addRepositoryClickHandler = new AddRepositoryClickHandler();
    RepositoryUtils.addButtonClickHandler("add-repository-top", addRepositoryClickHandler);
    RepositoryUtils.addButtonClickHandler("add-repository-bottom", addRepositoryClickHandler);
  }
}
