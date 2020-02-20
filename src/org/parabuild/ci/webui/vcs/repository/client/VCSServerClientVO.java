package org.parabuild.ci.webui.vcs.repository.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class VCSServerClientVO implements IsSerializable {

  /**
   * Repository ID.
   */
  private Integer id;

  /**
   * Repository name.
   */
  private String name;

  /**
   * Repository type.
   */
  private int type;

  /**
   * Repository description.
   */
  private String description;


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


  @Override
  public String toString() {
    return "VCSServerClientVO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", description='" + description + '\'' +
            '}';
  }
}
