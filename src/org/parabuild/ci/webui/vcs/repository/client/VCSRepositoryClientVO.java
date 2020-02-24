package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A repository value object used to pass data around.
 */
public class VCSRepositoryClientVO implements IsSerializable {


  /**
   * Repository ID.
   */
  private Integer id;

  /**
   * Server ID.
   */
  private Integer serverId;

  /**
   * Repository name.
   */
  private String name;

  /**
   * Repository description.
   */
  private String description;


  /**
   * Sets a parent server ID.
   *
   * @param serverId the server ID to set.
   */
  public void setServerId(final Integer serverId) {

    this.serverId = serverId;
  }


  /**
   * Returns a parent server ID.
   *
   * @return the parent server ID.
   */
  public Integer getServerId() {

    return serverId;
  }


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


  public String getDescription() {

    return description;
  }


  public void setDescription(final String description) {

    this.description = description;
  }


  @Override
  public String toString() {
    return "VCSRepositoryClientVO{" +
            "id=" + id +
            ", serverId=" + serverId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
  }
}
