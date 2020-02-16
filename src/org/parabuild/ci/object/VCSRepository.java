package org.parabuild.ci.object;

import java.io.Serializable;

/**
 * Stored build configuration
 *
 * @hibernate.class table="VCS_REPOSITORY" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class VCSRepository implements Serializable {

  private static final long serialVersionUID = -2861819645004736188L;

  private int serverId;
  private Integer id;
  private int type;
  private String name;
  private String description;
  private boolean deleted;
  private long timeStamp;


  /**
   * Returns unique ID.
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="null"
   */
  public Integer getId() {
    return id;
  }


  public void setId(final Integer id) {
    this.id = id;
  }


  /**
   * Returns repository type.
   *
   * @return repository type.
   * @hibernate.property column="TYPE" unique="false" null="false"
   */
  public int getType() {
    return type;
  }


  public void setType(final int type) {
    this.type = type;
  }


  /**
   * Returns repository type.
   *
   * @return repository type.
   * @hibernate.property column="VCS_SERVER_ID" unique="false" null="false"
   */
  public int getServerId() {
    return serverId;
  }


  public void setServerId(final int serverId) {
    this.serverId = serverId;
  }


  /**
   * Returns repository name.
   *
   * @return int
   * @hibernate.property column="NAME" unique="false" null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns repository description.
   *
   * @return int
   * @hibernate.property column="DESCRIPTION" unique="false" null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Returns <code>true</code> if the repository was deleted.
   *
   * @return <code>true</code> if the repository was deleted.
   * @hibernate.property column="DELETED" unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * Returns timestamp
   *
   * @return timestamp.
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  @Override
  public String toString() {
    return "VCSRepository{" +
            "serverId=" + serverId +
            ", id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            ", deleted=" + deleted +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
