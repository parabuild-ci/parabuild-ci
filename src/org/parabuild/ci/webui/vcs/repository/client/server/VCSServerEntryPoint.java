package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.core.client.EntryPoint;
import org.parabuild.ci.webui.vcs.repository.common.RepositoryUtils;

/**
 * Client-side Java source for the entry-point class for managing VCS repositories.
 */
public final class VCSServerEntryPoint implements EntryPoint {


  /**
   * Set up repository management module.
   */
  @Override
  public void onModuleLoad() {

    final AddVCSServerClickHandler addServerClickHandler = new AddVCSServerClickHandler();
    RepositoryUtils.addButtonClickHandler("add-repository-server-top", addServerClickHandler);
    RepositoryUtils.addButtonClickHandler("add-repository-server-bottom", addServerClickHandler);
  }
}
