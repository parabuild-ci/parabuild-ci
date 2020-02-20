package org.parabuild.ci.webui.vcs.repository;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Provides a default implementation for the most common Parabuild actions. See the documentation for all the interfaces
 * this class implements for more detailed information.
 */
public class ParabuildActionSupport extends ActionSupport {

  private static final long serialVersionUID = -7386131671442076841L;

  /**
   * View's title.
   */
  private String title;


  /**
   * Returns view's title.
   *
   * @return view's title.
   */
  public String getTitle() {
    return title;
  }


  /**
   * Sets view's title.
   *
   * @param title view's title.
   */
  public void setTitle(final String title) {
    this.title = title;
  }
}
