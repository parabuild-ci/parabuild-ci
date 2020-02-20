package org.parabuild.ci.webui.vcs.repository;

import static org.parabuild.ci.versioncontrol.VersionControlSystem.vcsToString;

/**
 * A VO representing a VCS server.
 */
public final class VCSServerVO {

  private Integer id;
  private String name;
  private String description;
  private int type;


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
