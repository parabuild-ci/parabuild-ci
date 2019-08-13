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

  private long serverId;
  private Integer id;
  private String name;
  private int type;
  private String path;
  private boolean deleted;
  private long timeStamp;


  /**
   * Returns unique ID.
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
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
  public long getServerId() {
    return serverId;
  }


  public void setServerId(final long serverId) {
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
   * Returns DNS name.
   *
   * @return DNS name.
   * @hibernate.property column="PATH" unique="false" null="false"
   */
  public String getPath() {
    return path;
  }


  public void setPath(final String path) {
    this.path = path;
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
            "id=" + id +
            ", serverId=" + serverId +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", path='" + path + '\'' +
            ", deleted=" + deleted +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
