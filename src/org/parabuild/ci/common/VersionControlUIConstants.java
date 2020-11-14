package org.parabuild.ci.common;

/**
 * An interface to hold UI captions.
 */
public interface VersionControlUIConstants {

  /**
   * Git captions.
   */
  String CAPTION_GIT_DEPOT_PATH = "Git repository path:";
  String CAPTION_GIT_PASSWORD = "Git password:";
  String CAPTION_GIT_PATH_TO_EXE = "Path to git executable:";
  String CAPTION_GIT_REPOSITORY = "Git repository:";
  String CAPTION_GIT_BRANCH = "Git branch:";
  String CAPTION_GIT_SETTINGS = "Git Settings";

  String DEFAULT_UNIX_GIT_COMMAND = "/usr/bin/git";
  String[] VALID_GIT_URL_PROTOCOLS = {"ssh://", "git://", "http://", "https://", "file://"};
}
