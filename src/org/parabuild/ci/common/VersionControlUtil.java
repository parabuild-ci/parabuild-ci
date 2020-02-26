package org.parabuild.ci.common;

/**
 * A container for the list of supported version control systems.
 */
public final class VersionControlUtil {

  /**
   * String lookup table.
   */
  private static final String[] STRING_VCS_NAME = createStringVCSNames();


  private VersionControlUtil() {
  }


  private static String[] createStringVCSNames() {

    // Create the lookup table. Notice the size of the array.
    final String[] result = new String[19];

    // Populate the lookup table.
    result[VCSAttribute.SCM_UNDEFINED] = VCSAttribute.NAME_SCM_UNDEFINED;
    result[VCSAttribute.SCM_ACCUREV] = VCSAttribute.NAME_SCM_ACCUREV;
    result[VCSAttribute.SCM_CLEARCASE] = VCSAttribute.NAME_SCM_CLEARCASE;
    result[VCSAttribute.SCM_BAZAAR] = VCSAttribute.NAME_BAZAAR;
    result[VCSAttribute.SCM_CVS] = VCSAttribute.NAME_SCM_CVS;
    result[VCSAttribute.SCM_FILESYSTEM] = VCSAttribute.NAME_SCM_FILESYSTEM;
    result[VCSAttribute.SCM_GENERIC] = VCSAttribute.NAME_SCM_GENERIC;
    result[VCSAttribute.SCM_GIT] = VCSAttribute.NAME_SCM_GIT;
    result[VCSAttribute.SCM_MERCURIAL] = VCSAttribute.NAME_SCM_MERCURIAL;
    result[VCSAttribute.SCM_MKS] = VCSAttribute.NAME_SCM_MKS;
    result[VCSAttribute.SCM_PERFORCE] = VCSAttribute.NAME_SCM_PERFORCE;
    result[VCSAttribute.SCM_PVCS] = VCSAttribute.NAME_SCM_PVCS;
    result[VCSAttribute.SCM_REFERENCE] = VCSAttribute.NAME_SCM_REFERENCE;
    result[VCSAttribute.SCM_STARTEAM] = VCSAttribute.NAME_SCM_STARTEAM;
    result[VCSAttribute.SCM_SURROUND] = VCSAttribute.NAME_SCM_SURROUND;
    result[VCSAttribute.SCM_SVN] = VCSAttribute.NAME_SCM_SVN;
    result[VCSAttribute.SCM_VAULT] = VCSAttribute.NAME_SCM_VAULT;
    result[VCSAttribute.SCM_VSS] = VCSAttribute.NAME_SCM_VSS;

    // Return result
    return result;
  }


  /**
   * Converts VCS code to a human-readable VCS name.
   *
   * @param code the code to convert.
   * @return human-readable VCS name.
   */
  public static final String vcsToString(final int code) throws IllegalArgumentException {

    if (code < 0 || code >= STRING_VCS_NAME.length) {
      throw new IllegalArgumentException("VCS code " + code + " is invalid");
    }

    return STRING_VCS_NAME[code];
  }
}
