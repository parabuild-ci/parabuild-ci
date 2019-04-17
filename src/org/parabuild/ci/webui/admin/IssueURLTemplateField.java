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
package org.parabuild.ci.webui.admin;

import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.webui.common.*;

/**
 * Field specific to entering Issue URL template.
 */
public final class IssueURLTemplateField extends CommonField {

  private static final long serialVersionUID = -7448734923520648780L;


  public IssueURLTemplateField() {
    super(80, 80);
  }


  public void validate(final List errors) {
    if (StringUtils.isBlank(getValue())) return;
    final IssueURLGenerator issueURLGenerator = new IssueURLGenerator(getValue());
    if (!issueURLGenerator.isTemplateValid()) {
      errors.add("Issue URL template is invalid. It should be a valid URL containing a required template parameter ${issue.key}");
    }
  }
}
