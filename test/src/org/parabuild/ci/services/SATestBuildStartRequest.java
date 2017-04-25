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
package org.parabuild.ci.services;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Tester for {@link BuildStartRequest}.
 */
public final class SATestBuildStartRequest extends TestCase {


  private BuildStartRequest buildStartRequest;


  public SATestBuildStartRequest(final String name) {
    super(name);
  }


  public void testAddParameters() throws Exception {

    final List list = new ArrayList(2);
    list.add(new BuildStartRequestParameter("TEST0", "TEST0", "TEST0", 0));
    list.add(new BuildStartRequestParameter("TEST1", "TEST1", "TEST1", 1));

    buildStartRequest.addParameters(list);

    assertEquals(2, buildStartRequest.parameterList().size());
  }


  public void setUp() throws Exception {

    super.setUp();

    buildStartRequest = new BuildStartRequest();
  }


  public void tearDown() throws Exception {

    buildStartRequest = null;

    super.tearDown();
  }


  public String toString() {
    return "SATestBuildStartRequest{" +
            "buildStartRequest=" + buildStartRequest +
            "} " + super.toString();
  }
}
