package org.parabuild.ci.webui.vcs.repository.client.server;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * VCS server type.
 */
public final class VCSServerType implements IsSerializable {

  private int type;

  private String name;


  public VCSServerType() {
  }


  public VCSServerType(final int type, final String name) {
    this.type = type;
    this.name = name;
  }


  public int getType() {
    return type;
  }


  public void setType(final int type) {
    this.type = type;
  }


  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  @Override
  public String toString() {
    return "VCSServerType{" +
            "type=" + type +
            ", name=" + name +
            '}';
  }
}
