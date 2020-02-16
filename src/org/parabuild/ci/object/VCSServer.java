package org.parabuild.ci.object;

import java.io.Serializable;

/**
 * Stored build configuration
 *
 * @hibernate.class table="VCS_SERVER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class VCSServer implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 2348840872918917362L;
  private Integer id;
  private String name;
  private String description;
  private int type;
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
   * Returns repository name.
   *
   * @return repository name.
   * @hibernate.property column="NAME" unique="false" null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns repository name.
   *
   * @return repository name.
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
    return "VCSServer{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            ", deleted=" + deleted +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
