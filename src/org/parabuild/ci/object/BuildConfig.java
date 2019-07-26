/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.object;

import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.Session;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.versioncontrol.VersionControlSystem;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Stored build configuration
 *
 * @hibernate.class table="BUILD_CONFIG" dynamic-update="true"
 * polymorphism="explicit"
 * @hibernate.discriminator column="DISCRIMINATOR" type="string"
 * @hibernate.cache usage="read-write"
 */
public class BuildConfig implements Serializable, ObjectConstants, Lifecycle {

  private static final long serialVersionUID = -8907298511657059064L; // NOPMD

  public static final Comparator ID_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final BuildConfig bc1 = (BuildConfig) o1;
      final BuildConfig bc2 = (BuildConfig) o2;
      return Integer.compare(bc1.activeBuildID, bc2.activeBuildID);
    }
  };

  public static final byte SCHEDULE_TYPE_AUTOMATIC = 1;
  public static final byte SCHEDULE_TYPE_RECURRENT = 2;
  public static final byte SCHEDULE_TYPE_MANUAL = 3;

  /**
   * Parallel builds run when their leading builds run.
   * <p/>
   * Parallel builds are limited to {@link VersionControlSystem#SCM_REFERENCE} SCM.
   */
  public static final byte SCHEDULE_TYPE_PARALLEL = 4;

  public static final byte ACCESS_PUBLIC = 1;
  public static final byte ACCESS_PRIVATE = 2;

  private int buildID = UNSAVED_ID;
  private int activeBuildID = UNSAVED_ID;
  private int builderID = BuilderConfiguration.UNSAVED_ID;
  private boolean sourceControlEmail = false;
  private byte access = ACCESS_PUBLIC;
  private byte scheduleType = SCHEDULE_TYPE_AUTOMATIC;
  private byte sourceControl = VersionControlSystem.SCM_UNDEFINED;
  private long timeStamp = 0;
  private String buildName = null;
  private String emailDomain = "";
  private boolean subordinate = false;


  /**
   * Default constructor.
   */
  public BuildConfig() {
  }


  /**
   * Copy constructor.
   *
   * @param buildConfig to create this build config from.
   */
  public BuildConfig(final BuildConfig buildConfig) {
    access = buildConfig.access;
    setBuildName(buildConfig.buildName);
    emailDomain = buildConfig.emailDomain;
    activeBuildID = buildConfig.activeBuildID;
    scheduleType = buildConfig.scheduleType;
    sourceControl = buildConfig.sourceControl;
    sourceControlEmail = buildConfig.sourceControlEmail;
    subordinate = buildConfig.subordinate;
    timeStamp = buildConfig.timeStamp;
    builderID = buildConfig.builderID;
  }


  /**
   * The getter method for this build ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public final int getBuildID() {
    return buildID;
  }


  public final void setBuildID(final int id) {
    this.buildID = id;
  }


  /**
   * Returns originating build ID - that is, a build ID that was
   * used to create this build config.
   * <p/>
   * Active builds have this value equal buildID.
   *
   * @return int
   * @hibernate.property column="ORIGNL_CONFIG_ID" unique="false"
   * null="false"
   */
  public final int getActiveBuildID() {
    return activeBuildID;
  }


  public final void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * Returns build name
   *
   * @return String
   * @hibernate.property column="NAME" unique="true"
   * null="false"
   */
  public final String getBuildName() {
    return buildName;
  }


  /**
   * Sets build name
   *
   * @param buildName build name, can not be null or blank
   */
  public final void setBuildName(final String buildName) {
    ArgumentValidator.validateArgumentNotBlank(buildName, "build name");
    this.buildName = buildName;
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public final long getTimeStamp() {
    return timeStamp;
  }


  public final void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Returns build type.
   *
   * @return the build type.
   * @hibernate.property column="SCHEDULE" null="false"
   */
  public final byte getScheduleType() {
    return scheduleType;
  }


  /**
   * Sets schedule type.
   *
   * @param scheduleType the schedule type.
   */
  public final void setScheduleType(final byte scheduleType) {
    this.scheduleType = scheduleType;
  }


  /**
   * Returns access type
   *
   * @return int
   * @hibernate.property column="ACCESS" unique="false"
   * null="false"
   */
  public final byte getAccess() {
    return access;
  }


  public final void setAccess(final byte access) {
    this.access = access;
  }


  /**
   * Returns change manager type
   *
   * @return int
   * @hibernate.property column="SCM" unique="false"
   * null="false"
   */
  public final byte getSourceControl() {
    return sourceControl;
  }


  public final void setSourceControl(final byte sourceControl) {
    this.sourceControl = sourceControl;
  }


  /**
   * Returns whether a build respects version control email
   *
   * @return String
   * @hibernate.property column="SCM_EMAIL" type="yes_no"
   * unique="false" null="false"
   */
  public final boolean getSourceControlEmail() {
    return sourceControlEmail;
  }


  public final void setSourceControlEmail(final boolean sourceControlEmail) {
    this.sourceControlEmail = sourceControlEmail;
  }


  /**
   * Returns startupService status
   *
   * @return String
   * @hibernate.property column="EMAIL_DOMAIN" unique="false"
   * null="false"
   */
  public final String getEmailDomain() {
    return emailDomain;
  }


  public final void setEmailDomain(final String emailDomain) {
    this.emailDomain = emailDomain;
  }


  /**
   * @return returns a build farm ID.
   * @hibernate.property column="BUILDER_ID" unique="false"
   * null="false"
   */
  public int getBuilderID() {
    return builderID;
  }


  public void setBuilderID(final int builderID) {
    this.builderID = builderID;
  }


  /**
   * Returns true if this build is a subordinate build.
   *
   * @return String
   * @hibernate.property column="SUBORDINATE"  type="yes_no"
   * unique="false" null="false"
   */
  public final boolean isSubordinate() {
    return subordinate;
  }


  public final void setSubordinate(final boolean subordinate) {
    this.subordinate = subordinate;
  }


  /**
   * Returns true if the schedule type is scheduled.
   *
   * @return <code>true</code> if the schedule type is scheduled.
   */
  public boolean isScheduled() {
    return scheduleType == SCHEDULE_TYPE_RECURRENT;
  }


  /**
   * Returns schedule type in a human readable format
   */
  public final String getScheduleTypeAsString() {
    return getScheduleTypeAsString(this.scheduleType);
  }


 /**
   * Returns a schedule type in a human readable format.
   * @return the schedule type in a human readable format.
   */
   public static String getScheduleTypeAsString(final byte scheduleType) {
    if (scheduleType == SCHEDULE_TYPE_AUTOMATIC) {
      return "Automatic";
    }
    if (scheduleType == SCHEDULE_TYPE_MANUAL) {
      return "Manual";
    }
    if (scheduleType == SCHEDULE_TYPE_RECURRENT) {
      return "Scheduled";
    }
    if (scheduleType == SCHEDULE_TYPE_PARALLEL) {
      return "Parallel";
    }
    return "Undefined";
  }


  /**
   * Returns an access type in a human readable format.
   *
   * @return the access type in a human readable format.
   */
  public final String getAccessAsString() {
    if (this.access == ACCESS_PRIVATE) {
      return "Private";
    }
    if (this.access == ACCESS_PUBLIC) {
      return "Public";
    }
    return "Undefined";
  }


  /**
   * Returns a source control type in a human readable format.
   *
   * @return the source control type in a human readable format.
   */
  public final String getChangeManagerAsString() {
    if (this.sourceControl == VersionControlSystem.SCM_PERFORCE) {
      return "P4";
    }
    if (this.sourceControl == VersionControlSystem.SCM_CVS) {
      return "CVS";
    }
    if (this.sourceControl == VersionControlSystem.SCM_VSS) {
      return "VSS";
    }
    if (this.sourceControl == VersionControlSystem.SCM_SVN) {
      return "SVN";
    }
    return "Undefined";
  }


  public final boolean onDelete(final Session session) {
    return NO_VETO;
  }


  public final void onLoad(final Session session, final Serializable serializable) {
  }


  public final boolean onSave(final Session session) throws CallbackException {
    if (scheduleType < 0) {
      throw new CallbackException("Schedule type is not set");
    }
    if (scheduleType == SCHEDULE_TYPE_PARALLEL && sourceControl != VersionControlSystem.SCM_REFERENCE) {
      throw new CallbackException("Parallel build support only reference source control");
    }
    return NO_VETO;
  }


  public final boolean onUpdate(final Session session) {
    return NO_VETO;
  }


  public final String toString() {
    return "BuildConfig{" +
            "buildID=" + buildID +
            ", activeBuildID=" + activeBuildID +
            ", sourceControlEmail=" + sourceControlEmail +
            ", access=" + access +
            ", scheduleType=" + scheduleType +
            ", sourceControl=" + sourceControl +
            ", timeStamp=" + timeStamp +
            ", buildName='" + buildName + '\'' +
            ", emailDomain='" + emailDomain + '\'' +
            ", subordinate=" + subordinate +
            '}';
  }
}
