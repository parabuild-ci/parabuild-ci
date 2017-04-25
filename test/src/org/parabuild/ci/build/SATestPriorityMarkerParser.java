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
package org.parabuild.ci.build;

import junit.framework.TestCase;
import org.parabuild.ci.services.BuildStartRequestParameter;

import java.util.List;

/**
 * Tester for {@link PriorityMarkerParser}.
 */
public final class SATestPriorityMarkerParser extends TestCase {


  private PriorityMarkerParser priorityMarkerParser;


  public SATestPriorityMarkerParser(final String name) {
    super(name);
  }


  public void testParseChangeListDescriptionDoesntFindMarker() {

    assertNull(priorityMarkerParser.parseChangeListDescription(""));
    assertNull(priorityMarkerParser.parseChangeListDescription(" "));
    assertNull(priorityMarkerParser.parseChangeListDescription(" no marker "));
    assertNull(priorityMarkerParser.parseChangeListDescription('-' + PriorityMarkerParser.PARABUILD_PRIORITY));
    assertNull(priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY + '-'));
    assertNull(priorityMarkerParser.parseChangeListDescription('-' + PriorityMarkerParser.PARABUILD_PRIORITY + '-'));
  }


  public void testParseChangeListDescription() {

    assertEquals(0, priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY).size());
    assertEquals(0, priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY + '=').size());
    assertEquals(0, priorityMarkerParser.parseChangeListDescription(' ' + PriorityMarkerParser.PARABUILD_PRIORITY + '=').size());
    assertEquals(0, priorityMarkerParser.parseChangeListDescription(' ' + PriorityMarkerParser.PARABUILD_PRIORITY + "= ").size());
    assertEquals(1, priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY + "=TEST1=a;").size());

    assertEquals(2, priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY + "=TEST1=a;TEST2=b").size());
  }


  public void testParseChangeListDescriptionFindsParameter() {

    final List list = priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY + "=TEST=a");

    assertEquals(1, list.size());

    final BuildStartRequestParameter parameter = (BuildStartRequestParameter) list.get(0);
    assertEquals("TEST", parameter.getName());
    assertEquals(1, parameter.getValues().size());
    assertEquals("a", parameter.getValues().get(0));
  }


  public void testParseChangeListDescriptionFindsParameters() {

    final List list = priorityMarkerParser.parseChangeListDescription(PriorityMarkerParser.PARABUILD_PRIORITY + "=TEST1=a;TEST2=b");

    assertEquals(2, list.size());

    final BuildStartRequestParameter parameter0 = (BuildStartRequestParameter) list.get(0);
    assertEquals("TEST1", parameter0.getName());
    assertEquals(1, parameter0.getValues().size());
    assertEquals("a", parameter0.getValues().get(0));

    final BuildStartRequestParameter parameter1 = (BuildStartRequestParameter) list.get(1);
    assertEquals("TEST2", parameter1.getName());
    assertEquals(1, parameter1.getValues().size());
    assertEquals("b", parameter1.getValues().get(0));
  }


  public void setUp() throws Exception {

    super.setUp();

    priorityMarkerParser = new PriorityMarkerParser();
  }


  public void tearDown() throws Exception {

    priorityMarkerParser = null;

    super.tearDown();
  }


  public String toString() {
    return "SATestPriorityMarkerParser{" +
            "priorityMarkerParser=" + priorityMarkerParser +
            "} " + super.toString();
  }
}
