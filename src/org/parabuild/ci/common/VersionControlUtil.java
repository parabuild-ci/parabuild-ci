package org.parabuild.ci.common;

/**
 * A container for the list of supported version control systems.
 */
public final class VersionControlUtil {

  /**
   * String lookup table indexed by the actual VCS code.
   */
  private static final String[] VCS_NAMES = createStringVCSNames();


  private VersionControlUtil() {

  }


  private static String[] createStringVCSNames() {

    // Create the lookup table. Notice the size of the array.
    final String[] result = new String[19];

    // Populate the lookup table.
    result[VersionControlSystem.SCM_UNDEFINED] = VersionControlSystem.NAME_SCM_UNDEFINED;
    result[VersionControlSystem.SCM_ACCUREV] = VersionControlSystem.NAME_SCM_ACCUREV;
    result[VersionControlSystem.SCM_CLEARCASE] = VersionControlSystem.NAME_SCM_CLEARCASE;
    result[VersionControlSystem.SCM_BAZAAR] = VersionControlSystem.NAME_BAZAAR;
    result[VersionControlSystem.SCM_CVS] = VersionControlSystem.NAME_SCM_CVS;
    result[VersionControlSystem.SCM_FILESYSTEM] = VersionControlSystem.NAME_SCM_FILESYSTEM;
    result[VersionControlSystem.SCM_GENERIC] = VersionControlSystem.NAME_SCM_GENERIC;
    result[VersionControlSystem.SCM_GIT] = VersionControlSystem.NAME_SCM_GIT;
    result[VersionControlSystem.SCM_MERCURIAL] = VersionControlSystem.NAME_SCM_MERCURIAL;
    result[VersionControlSystem.SCM_MKS] = VersionControlSystem.NAME_SCM_MKS;
    result[VersionControlSystem.SCM_PERFORCE] = VersionControlSystem.NAME_SCM_PERFORCE;
    result[VersionControlSystem.SCM_PVCS] = VersionControlSystem.NAME_SCM_PVCS;
    result[VersionControlSystem.SCM_REFERENCE] = VersionControlSystem.NAME_SCM_REFERENCE;
    result[VersionControlSystem.SCM_STARTEAM] = VersionControlSystem.NAME_SCM_STARTEAM;
    result[VersionControlSystem.SCM_SURROUND] = VersionControlSystem.NAME_SCM_SURROUND;
    result[VersionControlSystem.SCM_SVN] = VersionControlSystem.NAME_SCM_SVN;
    result[VersionControlSystem.SCM_VAULT] = VersionControlSystem.NAME_SCM_VAULT;
    result[VersionControlSystem.SCM_VSS] = VersionControlSystem.NAME_SCM_VSS;

    // Return result
    return result;
  }


  /**
   * Converts VCS code to a human-readable VCS name.
   *
   * @param code the code to convert.
   * @return human-readable VCS name.
   * @throws IllegalArgumentException if the code cannot be matched with a VCS.
   */
  public static final String vcsToString(final int code) {

    if (code < 0 || code >= VCS_NAMES.length) {
      throw new IllegalArgumentException("VCS code " + code + " is invalid");
    }

    return VCS_NAMES[code];
  }
}
