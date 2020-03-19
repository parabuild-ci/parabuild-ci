package org.parabuild.ci.webui.vcs.repository.client.server;


import org.parabuild.ci.common.PropertyToInputMap;

/**
 * Property handler for {@link GitServerAttributePanel}.
 */
class VCSServerAttributePropertyHandler implements PropertyToInputMap.PropertyHandler<VCSServerAttributeVO> {

  private static final long serialVersionUID = 5481330629455033021L;


  @Override
  public VCSServerAttributeVO makeProperty(final String propertyName) {

    return new VCSServerAttributeVO(propertyName);
  }


  @Override
  public void setPropertyValue(final VCSServerAttributeVO propertyVO, final String propertyValue) {
    propertyVO.setValue(propertyValue);
  }


  @Override
  public String getPropertyValue(final VCSServerAttributeVO propertyVO) {
    return propertyVO.getValue();
  }


  @Override
  public String getPropertyName(final VCSServerAttributeVO propertyVO) {
    return propertyVO.getName();
  }
}