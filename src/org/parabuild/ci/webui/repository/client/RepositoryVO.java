package org.parabuild.ci.webui.repository.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A repository value object used to pass data around.
 */
public class RepositoryVO implements IsSerializable {

  /**
   * Repository ID.
   */
  private Integer id;

  /**
   * Repository name.
   */
  private String name;


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


  public String toString() {
    return "RepositoryVO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}
