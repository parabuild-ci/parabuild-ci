package org.parabuild.ci.versioncontrol;

import junit.framework.TestCase;

import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_BAZAAR;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_ACCUREV;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_CLEARCASE;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_CVS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_FILESYSTEM;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_GENERIC;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_GIT;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_MERCURIAL;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_MKS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_PERFORCE;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_PVCS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_REFERENCE;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_STARTEAM;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_SURROUND;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_SVN;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_UNDEFINED;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_VAULT;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.NAME_SCM_VSS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_ACCUREV;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_BAZAAR;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_CLEARCASE;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_CVS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_FILESYSTEM;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_GENERIC;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_GIT;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_MERCURIAL;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_MKS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_PERFORCE;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_PVCS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_REFERENCE;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_STARTEAM;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_SURROUND;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_SVN;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_UNDEFINED;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_VAULT;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.SCM_VSS;
import static org.parabuild.ci.versioncontrol.VersionControlSystem.vcsToString;

/**
 * A tester for {@link VersionControlSystem}.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class VersionControlSystemTest extends TestCase {


  public void testVcsToString() {

    assertEquals(NAME_SCM_ACCUREV, vcsToString(SCM_ACCUREV));
    assertEquals(NAME_BAZAAR, vcsToString(SCM_BAZAAR));
    assertEquals(NAME_SCM_CLEARCASE, vcsToString(SCM_CLEARCASE));
    assertEquals(NAME_SCM_CVS, vcsToString(SCM_CVS));
    assertEquals(NAME_SCM_FILESYSTEM, vcsToString(SCM_FILESYSTEM));
    assertEquals(NAME_SCM_GENERIC, vcsToString(SCM_GENERIC));
    assertEquals(NAME_SCM_GIT, vcsToString(SCM_GIT));
    assertEquals(NAME_SCM_MERCURIAL, vcsToString(SCM_MERCURIAL));
    assertEquals(NAME_SCM_MKS, vcsToString(SCM_MKS));
    assertEquals(NAME_SCM_PERFORCE, vcsToString(SCM_PERFORCE));
    assertEquals(NAME_SCM_PVCS, vcsToString(SCM_PVCS));
    assertEquals(NAME_SCM_REFERENCE, vcsToString(SCM_REFERENCE));
    assertEquals(NAME_SCM_STARTEAM, vcsToString(SCM_STARTEAM));
    assertEquals(NAME_SCM_SURROUND, vcsToString(SCM_SURROUND));
    assertEquals(NAME_SCM_SVN, vcsToString(SCM_SVN));
    assertEquals(NAME_SCM_VAULT, vcsToString(SCM_VAULT));
    assertEquals(NAME_SCM_VSS, vcsToString(SCM_VSS));
    assertEquals(NAME_SCM_UNDEFINED, vcsToString(SCM_UNDEFINED));
  }


  public void testVcsToStringBoundaries() {

    boolean upperThrown = false;
    try {
      vcsToString(19);
    } catch (final IllegalArgumentException e) {
      upperThrown = true;
    }

    assertTrue(upperThrown);

    boolean lowerThrown = false;
    try {
      vcsToString(-1);
    } catch (final IllegalArgumentException e) {
      lowerThrown = true;
    }

    assertTrue(lowerThrown);
  }
}