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
package org.parabuild.ci.security;

import java.io.*;

/**
 *
 */
final class MergeRightsImpl implements MergeRights, Serializable {

  private static final long serialVersionUID = -4478940540320970936L;

  private boolean allowedToListCommands = false;
  private boolean allowedToDeleteMerge = false;
  private boolean allowedToResumeMerge = false;
  private boolean allowedToStopMerge = false;
  private boolean allowedToStartMerge = false;
  private boolean allowedToViewMerge = false;


  public boolean isAllowedToViewMerge() {
    return allowedToViewMerge;
  }


  public void setAllowedToViewMerge(final boolean allowedToViewMerge) {
    this.allowedToViewMerge = allowedToViewMerge;
  }


  public boolean isAllowedToStartMerge() {
    return allowedToStartMerge;
  }


  public void setAllowedToStartMerge(final boolean allowedToStartMerge) {
    this.allowedToStartMerge = allowedToStartMerge;
  }


  public void setAllowedToListCommands(final boolean allowedToListCommands) {
    this.allowedToListCommands = allowedToListCommands;
  }


  public void setAllowedToDeleteMerge(final boolean allowedToDeleteMerge) {
    this.allowedToDeleteMerge = allowedToDeleteMerge;
  }


  public void setAllowedToResumeMerge(final boolean allowedToResumeMerge) {
    this.allowedToResumeMerge = allowedToResumeMerge;
  }


  public void setAllowedToStopMerge(final boolean allowedToStopMerge) {
    this.allowedToStopMerge = allowedToStopMerge;
  }


  public boolean isAllowedToListCommands() {
    return allowedToListCommands;
  }


  public boolean isAllowedToDeleteMerge() {
    return allowedToDeleteMerge;
  }


  public boolean isAllowedToResumeMerge() {
    return allowedToResumeMerge;
  }


  public boolean isAllowedToStopMerge() {
    return allowedToStopMerge;
  }
}
