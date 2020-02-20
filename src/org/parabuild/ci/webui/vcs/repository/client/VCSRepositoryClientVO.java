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
   * Repository name.
   */
  private String name;

  /**
   * Repository description.
   */
  private String description;

  /**
   * Repository type.
   */
  private int type;


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
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            '}';
  }
}
