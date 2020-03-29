package org.parabuild.ci.webui.vcs.repository.client.server;

import org.parabuild.ci.webui.vcs.repository.client.repository.ParabuildListBox;

import static org.parabuild.ci.common.VersionControlSystem.NAME_BAZAAR;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_ACCUREV;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_CLEARCASE;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_CVS;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_FILESYSTEM;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_GENERIC;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_GIT;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_MERCURIAL;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_MKS;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_PERFORCE;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_PVCS;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_REFERENCE;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_STARTEAM;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_SURROUND;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_SVN;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_VAULT;
import static org.parabuild.ci.common.VersionControlSystem.NAME_SCM_VSS;
import static org.parabuild.ci.common.VersionControlSystem.SCM_ACCUREV;
import static org.parabuild.ci.common.VersionControlSystem.SCM_BAZAAR;
import static org.parabuild.ci.common.VersionControlSystem.SCM_CLEARCASE;
import static org.parabuild.ci.common.VersionControlSystem.SCM_CVS;
import static org.parabuild.ci.common.VersionControlSystem.SCM_FILESYSTEM;
import static org.parabuild.ci.common.VersionControlSystem.SCM_GENERIC;
import static org.parabuild.ci.common.VersionControlSystem.SCM_GIT;
import static org.parabuild.ci.common.VersionControlSystem.SCM_MERCURIAL;
import static org.parabuild.ci.common.VersionControlSystem.SCM_MKS;
import static org.parabuild.ci.common.VersionControlSystem.SCM_PERFORCE;
import static org.parabuild.ci.common.VersionControlSystem.SCM_PVCS;
import static org.parabuild.ci.common.VersionControlSystem.SCM_REFERENCE;
import static org.parabuild.ci.common.VersionControlSystem.SCM_STARTEAM;
import static org.parabuild.ci.common.VersionControlSystem.SCM_SURROUND;
import static org.parabuild.ci.common.VersionControlSystem.SCM_SVN;
import static org.parabuild.ci.common.VersionControlSystem.SCM_VAULT;
import static org.parabuild.ci.common.VersionControlSystem.SCM_VSS;

/**
 * A drop-down list box containing VCS server types.
 */
public final class VCSServerTypeListBox extends ParabuildListBox {

  /**
   * Creates {@link VCSServerTypeListBox} and populates it with a maps of names to codes.
   */
  public VCSServerTypeListBox() {

    addItem(NAME_SCM_GIT, Integer.toString(SCM_GIT));
    addItem(NAME_SCM_PERFORCE, Integer.toString(SCM_PERFORCE));
    addItem(NAME_SCM_SVN, Integer.toString(SCM_SVN));
    addItem(NAME_SCM_ACCUREV, Integer.toString(SCM_ACCUREV));
    addItem(NAME_SCM_CLEARCASE, Integer.toString(SCM_CLEARCASE));
    addItem(NAME_BAZAAR, Integer.toString(SCM_BAZAAR));
    addItem(NAME_SCM_CVS, Integer.toString(SCM_CVS));
    addItem(NAME_SCM_FILESYSTEM, Integer.toString(SCM_FILESYSTEM));
    addItem(NAME_SCM_GENERIC, Integer.toString(SCM_GENERIC));
    addItem(NAME_SCM_MERCURIAL, Integer.toString(SCM_MERCURIAL));
    addItem(NAME_SCM_MKS, Integer.toString(SCM_MKS));
    addItem(NAME_SCM_PVCS, Integer.toString(SCM_PVCS));
    addItem(NAME_SCM_REFERENCE, Integer.toString(SCM_REFERENCE));
    addItem(NAME_SCM_STARTEAM, Integer.toString(SCM_STARTEAM));
    addItem(NAME_SCM_SURROUND, Integer.toString(SCM_SURROUND));
    addItem(NAME_SCM_VAULT, Integer.toString(SCM_VAULT));
    addItem(NAME_SCM_VSS, Integer.toString(SCM_VSS));
  }
}
