package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.ui.Label;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildTextBox;

import java.util.ArrayList;

public class GitServerAttributePanel extends VCSServerAttributePanel {


  private static final String GIT_SERVER_URL = "git.server.url";

  private static final String GIT_SERVER_USER = "git.server.user";

  private static final String GIT_SERVER_PASSWORD = "git.server.password";

  private static final String CAPTION_GIT_SERVER_URL = "Git server URL:";

  private static final String CAPTION_GIT_USER = "Git user:";

  private static final String CAPTION_PASSWORD = "Password:";

  private final Label lbServerURL = new ParabuildLabel(CAPTION_GIT_SERVER_URL);

  private final ParabuildTextBox flServerURL = new ParabuildTextBox(100, 70);

  private final Label lbServerUser = new ParabuildLabel(CAPTION_GIT_USER);

  private final ParabuildTextBox flServerUser = new ParabuildTextBox(100, 70);

  private final Label lbServerPassword = new ParabuildLabel(CAPTION_PASSWORD);

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


  @Override
  public boolean validate(final ArrayList<String> errors) {

    final int initialErrorSize = errors.size();
    InputValidator.validateFieldNotBlank(errors, CAPTION_GIT_SERVER_URL, flServerPassword);
    InputValidator.validateFieldNotBlank(errors, CAPTION_GIT_USER, flServerUser);
    InputValidator.validateFieldNotBlank(errors, CAPTION_PASSWORD, flServerPassword);
    return errors.size() > initialErrorSize;
  }


  // Work left:
  // * Saving the properties in the db.
  // * Loading the attributes from the db
}
