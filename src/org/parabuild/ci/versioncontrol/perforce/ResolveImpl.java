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
package org.parabuild.ci.versioncontrol.perforce;

/**
 */
public final class ResolveImpl implements Resolve {

  private final String localTarget;
  private final String operation;
  private final String source;
  private final String sourceRevStart;
  private final String sourceRevEnd;
  private final int yours;
  private final int theirs;
  private final int both;
  private final int conflicting;
  private final String target;
  private final String result;


  public ResolveImpl(final String localTarget, final String operation, final String source, final String sourceRevStart, final String sourceRevEnd, final int yours, final int theirs, final int both, final int conflicting, final String target, final String result) {
    this.localTarget = localTarget;
    this.operation = operation;
    this.source = source;
    this.sourceRevStart = sourceRevStart;
    this.sourceRevEnd = sourceRevEnd;
    this.yours = yours;
    this.theirs = theirs;
    this.both = both;
    this.conflicting = conflicting;
    this.target = target;
    this.result = result;
  }


  public String getLocalTarget() {
    return localTarget;
  }


  public String getOperation() {
    return operation;
  }


  public String getSource() {
    return source;
  }


  public String getSourceRevStart() {
    return sourceRevStart;
  }


  public String getSourceRevEnd() {
    return sourceRevEnd;
  }


  public int getYours() {
    return yours;
  }


  public int getTheirs() {
    return theirs;
  }


  public int getBoth() {
    return both;
  }


  public int getConflicting() {
    return conflicting;
  }


  public String getTarget() {
    return target;
  }


  public String getResult() {
    return result;
  }


  public String toString() {
    return "ResolveImpl{" +
      "localTarget='" + localTarget + '\'' +
      ", operation='" + operation + '\'' +
      ", source='" + source + '\'' +
      ", sourceRevStart='" + sourceRevStart + '\'' +
      ", sourceRevEnd='" + sourceRevEnd + '\'' +
      ", yours='" + yours + '\'' +
      ", theirs='" + theirs + '\'' +
      ", both='" + both + '\'' +
      ", conflicting='" + conflicting + '\'' +
      ", target='" + target + '\'' +
      ", result='" + result + '\'' +
      '}';
  }
}
