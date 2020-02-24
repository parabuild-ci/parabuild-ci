package org.parabuild.ci.webui.vcs.repository;

import com.google.gwt.user.client.rpc.IsSerializable;

import static org.parabuild.ci.versioncontrol.VersionControlSystem.vcsToString;

/**
 * A repository value object used to pass data around.
 */
public class VCSRepositoryVO implements IsSerializable {

  /**
   * Repository ID.
   */
  private Integer id;

  /**
   * Repository type.
   */
  private int type;

  /**
   * Repository name.
   */
  private String name;


  /**
   * Repository description.
   */
  private String description;
  private String serverName;


  /**
   * Returns repository ID.
   *
   * @return repository ID.
   */
  public Integer getId() {
    return id;
  }


  /**
   * Sets repository ID.
   *
   * @param id repository ID.
   */
  public void setId(final Integer id) {
    this.id = id;
  }


  /**
   * Returns repository name.
   *
   * @return repository name.
   */
  public String getName() {
    return name;
  }


  /**
   * Sets repository name.
   *
   * @param name repository name.
   */
  public void setName(final String name) {
    this.name = name;
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


  public void setDescription(final String description) {
    this.description = description;
  }


  public String getDescription() {
    return description;
  }


  public void setServerName(final String serverName) {
    this.serverName = serverName;
  }


  public String getServerName() {
    return serverName;
  }


  @Override
  public String toString() {
    return "VCSRepositoryVO{" +
            "id=" + id +
            ", type=" + type +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", serverName='" + serverName + '\'' +
            '}';
  }
}
