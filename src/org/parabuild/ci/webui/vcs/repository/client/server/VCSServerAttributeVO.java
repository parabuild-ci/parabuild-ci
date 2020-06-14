package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * VCS server attribute.
 */
public final class VCSServerAttributeVO implements IsSerializable {

  private Integer serverId;

  private Integer id;

  private String name;

  private String value;

  private long timeStamp = 1;


  /**
   * A default constructor required by {@link IsSerializable}.
   */
  public VCSServerAttributeVO() {
  }


  /**
   * Creates a named property.
   *
   * @param name property name.
   */
  public VCSServerAttributeVO(final String name) {
    this.name = name;
  }


  public Integer getServerId() {
    return serverId;
  }


  public void setServerId(final Integer serverId) {
    this.serverId = serverId;
  }


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


  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  @Override
  public String toString() {
    return "VCSServerAttributeVO{" +
            "serverId=" + serverId +
            ", id=" + id +
            ", name='" + name + '\'' +
            ", value='" + value + '\'' +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
