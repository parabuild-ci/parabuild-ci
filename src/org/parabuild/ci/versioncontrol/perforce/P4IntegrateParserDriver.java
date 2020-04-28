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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;

import java.io.File;
import java.io.IOException;

/**
 * @see P4IntegrateParser#parse(File)
 */
public interface P4IntegrateParserDriver {

  void foundIntegration(final Integration integration) throws ValidationException, CommandStoppedException, BuildException, IOException, AgentFailureException;
}
