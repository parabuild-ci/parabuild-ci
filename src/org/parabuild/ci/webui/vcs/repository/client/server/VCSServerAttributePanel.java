package org.parabuild.ci.webui.vcs.repository.client.server;

import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.webui.vcs.repository.common.ParabuildFlexTable;

import java.util.List;

/**
 * VCS server attribute panel serves as a container for entering captions, fields
 */
public abstract class VCSServerAttributePanel extends ParabuildFlexTable {

  private final PropertyToInputMap<VCSServerAttributeVO> propertyToInputMap = new PropertyToInputMap<VCSServerAttributeVO>(new VCSServerAttributePropertyHandler());


  /**
   * Creates {@link ParabuildFlexTable}.
   *
   * @param columnCount number of columns managed by {@link #flexTableIterator()}.
   */
  public VCSServerAttributePanel(final int columnCount) {

    super(columnCount);
  }


  protected final PropertyToInputMap<VCSServerAttributeVO> propertyToInputMap() {

    return propertyToInputMap;
  }


  private List<VCSServerAttributeVO> getUpdatedProperties() {

    return propertyToInputMap.getUpdatedProperties();
  }


  /**
   * Validates attributes.
   *
   * @param inputValidator errors list to add if there are errors.
   * @return true if valid.
   */
  public abstract boolean validate(InputValidator inputValidator);
}
