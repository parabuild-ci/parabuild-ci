package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Client-side Java source for the entry-point class for managing VCS repositories.
 */
public final class VCSServerEntryPoint implements EntryPoint {


  private static final String DYNAMIC_CONTENT = "dynamic-content";

  private static final String ADD_SERVER = "Add server";


  public VCSServerEntryPoint() {

  }


  /**
   * Set up repository management module.
   */
  @Override
  public void onModuleLoad() {

    final AddVCSServerClickHandler addServerClickHandler = new AddVCSServerClickHandler();


    //
    final RootPanel widgets = RootPanel.get(DYNAMIC_CONTENT);


    final HTMLPanel header = new HTMLPanel("h1", "Servers");

    widgets.add(header);

    final Button btnAddServerTop = new Button(ADD_SERVER, addServerClickHandler);

    widgets.add(btnAddServerTop);

    final VCSServerListTable vcsServerListTable = new VCSServerListTable();
    widgets.add(vcsServerListTable);

    final Button btnAddServerBottom = new Button(ADD_SERVER, addServerClickHandler);

    widgets.add(btnAddServerBottom);
  }
}
