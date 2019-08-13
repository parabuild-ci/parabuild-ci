package org.parabuild.ci.object;

/**
 * Stored build configuration
 *
 * @hibernate.class table="VCS_SERVER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class VCSServer {

  private Integer id;
  private String name;
  private int type;
  private String url;
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
   * @hibernate.property column="URL" unique="false" null="false"
   */
  public String getUrl() {
    return url;
  }


  public void setUrl(final String url) {
    this.url = url;
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
            ", name='" + name + '\'' +
            ", type=" + type +
            ", path='" + url + '\'' +
            ", deleted=" + deleted +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
