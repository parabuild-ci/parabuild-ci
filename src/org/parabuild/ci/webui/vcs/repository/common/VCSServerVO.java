package org.parabuild.ci.webui.vcs.repository.common;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.parabuild.ci.webui.vcs.repository.client.server.VCSServerAttributeVO;

import java.util.List;

import static org.parabuild.ci.common.VersionControlUtil.vcsToString;

/**
 * A VO representing a VCS server.
 */
public final class VCSServerVO implements IsSerializable {

  private Integer id;

  private String name;

  private String description;

  private int type;

  private List<VCSServerAttributeVO> attributes;


  public Integer getId() {
    return id;
  }


  public void setId(final Integer id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public int getType() {
    return type;
  }


  public void setType(final int type) {
    this.type = type;
  }


  public String getTypeAsString() {
    return vcsToString(type);
  }


  public List<VCSServerAttributeVO> getAttributes() {
    return attributes;
  }


  public void setAttributes(final List<VCSServerAttributeVO> attributes) {
    this.attributes = attributes;
  }


  @Override
  public String toString() {
    return "VCSServerVO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            '}';
  }
}
