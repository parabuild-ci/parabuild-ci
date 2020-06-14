package org.parabuild.ci.webui.vcs.repository.client.repository;

import com.google.gwt.core.client.EntryPoint;
import org.parabuild.ci.webui.vcs.repository.common.RepositoryUtils;

/**
 * Client-side Java source for the entry-point class for managing VCS repositories.
 */
public final class VCSRepositoryEntryPoint implements EntryPoint {


  /**
   * Set up repository management module.
   */
  @Override
  public void onModuleLoad() {

    final AddVCSRepositoryClickHandler addVCSRepositoryClickHandler = new AddVCSRepositoryClickHandler();
    RepositoryUtils.addButtonClickHandler("add-repository-top", addVCSRepositoryClickHandler);
    RepositoryUtils.addButtonClickHandler("add-repository-bottom", addVCSRepositoryClickHandler);
  }
}
