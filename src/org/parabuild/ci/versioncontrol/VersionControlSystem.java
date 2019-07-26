package org.parabuild.ci.versioncontrol;

/**
 * A container for the list of supported version control systems.
 */
public final class VersionControlSystem {

  public static final String NAME_SCM_ACCUREV = "AccuRev";
  public static final String NAME_BAZAAR = "Bazaar";
  public static final String NAME_SCM_CLEARCASE = "ClearCase";
  public static final String NAME_SCM_CVS = "CVS";
  public static final String NAME_SCM_FILESYSTEM = "File system VCS";
  public static final String NAME_SCM_GENERIC = "Generic VCS";
  public static final String NAME_SCM_GIT = "Git";
  public static final String NAME_SCM_MERCURIAL = "Mercurial";
  public static final String NAME_SCM_MKS = "MKS Source Integrity";
  public static final String NAME_SCM_PERFORCE = "Perforce";
  public static final String NAME_SCM_PVCS = "PVCS";
  public static final String NAME_SCM_REFERENCE = "Build reference";
  public static final String NAME_SCM_STARTEAM = "StarTeam";
  public static final String NAME_SCM_SURROUND = "Surround SCM";
  public static final String NAME_SCM_SVN = "Subversion";
  public static final String NAME_SCM_VAULT = "Vault";
  public static final String NAME_SCM_VSS = "Visual SourceSafe";
  static final String NAME_SCM_UNDEFINED = "Undefined";

  public static final byte SCM_UNDEFINED = 0;
  public static final byte SCM_PERFORCE = 1;
  public static final byte SCM_CVS = 2;
  public static final byte SCM_REFERENCE = 3;
  public static final byte SCM_VSS = 4;
  public static final byte SCM_CLEARCASE = 5;
  public static final byte SCM_SVN = 6;
  public static final byte SCM_SURROUND = 7;
  public static final byte SCM_STARTEAM = 8;
  public static final byte SCM_VAULT = 9;
  public static final byte SCM_PVCS = 10;
  public static final byte SCM_MKS = 11;
  public static final byte SCM_FILESYSTEM = 12;
  public static final byte SCM_GENERIC = 13;
  public static final byte SCM_SYNERGY = 14;
  public static final byte SCM_ACCUREV = 15;
  public static final byte SCM_GIT = 16;
  public static final byte SCM_BAZAAR = 17;
  public static final byte SCM_MERCURIAL = 18;

  /**
   * String lookup table.
   */
  private static final String[] STRING_VCS_NAME = createStringVCSNames();


  private VersionControlSystem() {
  }


  private static String[] createStringVCSNames() {

    // Create the lookup table. Notice the size of the array.
    final String[] result = new String[19];

    // Populate the lookup table.
    result[SCM_UNDEFINED] = NAME_SCM_UNDEFINED;
    result[SCM_ACCUREV] = NAME_SCM_ACCUREV;
    result[SCM_CLEARCASE] = NAME_SCM_CLEARCASE;
    result[SCM_BAZAAR] = NAME_BAZAAR;
    result[SCM_CVS] = NAME_SCM_CVS;
    result[SCM_FILESYSTEM] = NAME_SCM_FILESYSTEM;
    result[SCM_GENERIC] = NAME_SCM_GENERIC;
    result[SCM_GIT] = NAME_SCM_GIT;
    result[SCM_MERCURIAL] = NAME_SCM_MERCURIAL;
    result[SCM_MKS] = NAME_SCM_MKS;
    result[SCM_PERFORCE] = NAME_SCM_PERFORCE;
    result[SCM_PVCS] = NAME_SCM_PVCS;
    result[SCM_REFERENCE] = NAME_SCM_REFERENCE;
    result[SCM_STARTEAM] = NAME_SCM_STARTEAM;
    result[SCM_SURROUND] = NAME_SCM_SURROUND;
    result[SCM_SVN] = NAME_SCM_SVN;
    result[SCM_VAULT] = NAME_SCM_VAULT;
    result[SCM_VSS] = NAME_SCM_VSS;

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
