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
package org.parabuild.ci.common;

import java.io.*;

/**
 *
 */
public final class CommandLineParserException extends IOException {

  private static final long serialVersionUID = -4087753082694376962L;


  /**
   * Constructs an <code>CommandLineInvalidException</code> with
   * the specified detail message. The error message string
   * <code>s</code> can later be retrieved by the <code>{@link
   * Throwable#getMessage}</code> method of class
   * <code>java.lang.Throwable</code>.
   *
   * @param s the detail message.
   */
  public CommandLineParserException(final String s) {
    super(s);
  }
}
