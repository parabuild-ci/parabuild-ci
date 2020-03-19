package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.ui.Label;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;

public class GitServerAttributePanel extends VCSServerAttributePanel {


  private static final String GIT_SERVER_URL = "git.server.url";
  private static final String GIT_SERVER_USER = "git.server.user";
  private static final String GIT_SERVER_PASSWORD = "git.server.password";

  private final Label lbServerURL = new ParabuildLabel("Git server URL:");
  private final ParabuildTextBox flServerURL = new ParabuildTextBox(100, 70);

  private final Label lbServerUser = new ParabuildLabel("Git user:");
  private final ParabuildTextBox flServerUser = new ParabuildTextBox(100, 70);

  private final Label lbServerPassword = new ParabuildLabel("Password:");
  private final ParabuildTextBox flServerPassword = new ParabuildTextBox(100, 70);


  /**
   * Creates {@link GitServerAttributePanel}.
   */
  public GitServerAttributePanel() {

    // 2 columns
    super(2);

    // Add widgets
    flexTableIterator().addPair(lbServerURL, flServerURL);
    flexTableIterator().addPair(lbServerUser, flServerUser);
    flexTableIterator().addPair(lbServerPassword, flServerPassword);

    // Bind to property
    propertyToInputMap().bindPropertyNameToInput(GIT_SERVER_URL, flServerURL);
    propertyToInputMap().bindPropertyNameToInput(GIT_SERVER_USER, flServerUser);
    propertyToInputMap().bindPropertyNameToInput(GIT_SERVER_PASSWORD, flServerPassword);
  }


  // Work left:
  // * Getting the properties from the panel
  // * Saving the properties in the db.
  // * Loading the attributes from the db
}
