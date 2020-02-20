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


  public String toString() {
    return "RepositoryVO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}
