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
package org.parabuild.ci.process;

import java.io.*;
import java.util.*;

import org.parabuild.ci.common.*;


/**
 * ProcessParser represents platform-independent interface
 * that allows to parse return of ps on different versions of Unix
 * uniformly.
 */
public interface ProcessParser {

  /**
   * Executes system command that returns list of processes
   * and returns it output
   * @return output of process manager command
   */
  InputStream getProcesses() throws BuildException;

  /**
   * Executes system command that returns list of processes
   * with environment and returns it output
   * @return output of process manager command
   */
  InputStream getProcessesEnvironment() throws BuildException;

  
  /**
   * Kills specified list of processes
   * @param processes list of <code>Integer</code>
   * @returns kill command output 
   */  
  InputStream killProcesses(List processes) throws BuildException;
  

  /**
   * Fills supplied collections
   * with data received from process list command output
   * @param is Input stream to read data from
   * @param ret list to fill with <code>OSProcess</code> objects
   * @param processes map to fill with PID-><code>OSProcess</code>
   * @param tree map to fill with PPID->list of children PID
   */    
  void parse(InputStream is,
                    List ret, 
                    Map processes,
                    Map tree) throws BuildException;

  /**
   * Fills supplied collections
   * with data received from process environment list command output
   * @param is input stream to read data from
   * @param ret map to fill with PID->environment (String)
   */      
  void parseEnvironment(InputStream is, Map ret) throws BuildException;
  

}
