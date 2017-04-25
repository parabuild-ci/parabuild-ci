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
package org.parabuild.ci.installer;

import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.NullOutputStream;
import org.parabuild.ci.common.RuntimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Responsible for creating users and groups during
 * installation.
 */
public final class MacOsXUserCreator implements InstallerUserCreator {

  public static final String NIUTIL = "/usr/bin/niutil";


  /**
   * Creates group.
   *
   * @param group name to create
   * @throws IOException
   */
  public final void createGroup(final String group) throws IOException {
    if (isGroupExists(group)) {
      return;
    }
    // REVIEWME: Do we need a group?
  }
  /*
Create a new entry in the local (/) domain under the category /users.

niutil -create / /users/username

Create and set the shell property to bash.

niutil -createprop / /users/username shell /bin/bash

Create and set the user�s full name.

niutil -createprop / /users/username realname "User Name"

Create and set the user�s ID.

niutil -createprop / /users/username uid 503

Create and set the user�s global ID property.

niutil -createprop / /users/username gid 1000

Create and set the user name on the local domain, as opposed to the network domain or another domain.

niutil -createprop / /users/username home /Local/Users/username

Make an entry for the password.

niutil -createprop / /users/username _shadow_passwd

Set the password.

passwd username

To make that user useful, you might want to add them to the admin group.

niutil -appendprop / /groups/admin users username

  */


  /**
   * Creates user.
   *
   * @param user name to create
   * @throws IOException
   */
  public final void createUser(final String user) throws IOException {
    if (!isUserExists(user)) {
      execCmd(NIUTIL + " -create / /users/" + user);
      execCmd(NIUTIL + " -createprop / /users/" + user + " shell /bin/bash");
      execCmd(NIUTIL + " -createprop / /users/" + user + " realname " + user);
      execCmd(NIUTIL + " -createprop / /users/" + user + " uid 401");
      execCmd(NIUTIL + " -createprop / /users/" + user + " home /Users/" + user);
      execCmd("mkdir -p /Users/" + user);
      execCmd(MacOsXDirectoryOwnerChanger.PATH_TO_CHOWN + ' ' + user + " /Users/" + user);
    }
  }


  private void execCmd(final String cmd) throws IOException {
    try {
      final OutputStream stdout = new NullOutputStream();
      final OutputStream stderr = new NullOutputStream();
      RuntimeUtils.execute(null, cmd, null, stdout, stderr);
    } catch (CommandStoppedException e) {
      throw IoUtils.createIOException("Command was stopped", e);
    }
  }


  /**
   * @return true if our user exists
   */
  private boolean isUserExists(final String user) throws IOException {
    boolean userFound = false;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(new File("/etc/passwd")));
      for (String line = br.readLine(); line != null;) {
        if (line.startsWith(user + ':')) {
          userFound = true;
          break;
        }
        line = br.readLine();
      }
    } finally {
      IoUtils.closeHard(br);
    }
    return userFound;
  }


  /**
   * @return true if our group exists
   */
  private boolean isGroupExists(final String group) throws IOException {
    boolean groupFound = false;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(new File("/etc/group")));
      for (String line = br.readLine(); line != null;) {
        if (line.startsWith(group + ':')) {
          groupFound = true;
          break;
        }
        line = br.readLine();
      }
    } finally {
      IoUtils.closeHard(br);
    }
    return groupFound;
  }
}
