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

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ThreadUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.remote.WrapperScriptGenerator;
import org.parabuild.ci.remote.WrapperScriptGeneratorFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;


/**
 * This class is responsible for executing commands outside of
 * JVM.
 *
 * @noinspection TryWithIdenticalCatches
 */
public class RemoteCommand implements Cleanable {

  private static final Log LOG = LogFactory.getLog(RemoteCommand.class); // NOPMD
  private static final String TEMP_FILE_PREFIX = ".auto";

  // Command pool
  private static final int POOL_KEEP_ALIVE_MILLIS = 10000;
  private static final int POOL_MAX_SIZE = Integer.MAX_VALUE;
  private static final int POOL_INITIAL_THREAD_COUNT = 30;
  private static final PooledExecutor COMMAND_POOL = ThreadUtils.makeThreadPool(POOL_KEEP_ALIVE_MILLIS, POOL_MAX_SIZE, POOL_INITIAL_THREAD_COUNT, "CommandPool");

  private static final String STR_CMD_C = "cmd /c ";
  private static final String STR_COMMAND_C = "command /c ";
  public static final String STR_SH_C = "sh -c ";

  private boolean mergedOutput = false;
  private final Map env = new HashMap(5);
  private int timeoutSecs = Integer.MAX_VALUE;
  private String command = null;
  private String remoteTempInputFile = null;
  private TimeoutCallback timeoutCallback = null;
  protected File mergedFile = null;
  protected File stderrFile = null;
  protected File stdoutFile = null;
  protected final Agent agent;
  protected final ProcessSignatureRegistry signatureRegistry = new ProcessSignatureRegistry();
  protected InputStream is = null;
  protected String remoteCurrentDir = null;

  /**
   * Executor handler allows us to refer to remote command asynchronuously.
   */
  private int handle = 0;
  private final int retryTimes;
  private final int retryItervalSecs;
  private final List ignoreErrorPatterns;

  private TailBufferSize tailBufferSize = new TailBufferSizeImpl(0, 0);


  public RemoteCommand(final Agent agent, final boolean addAutomaticSignatureToEnvironment) {
    this(agent, addAutomaticSignatureToEnvironment, 0, 10, Collections.emptyList());
  }


  public RemoteCommand(final Agent agent, final boolean addAutomaticSignatureToEnvironment, final int retryTimes,
                       final int retryItervalSecs, final List ignoreErrorPatterns) {
    this.agent = agent;
    this.retryTimes = validRetryTimes(retryTimes);
    this.retryItervalSecs = validRetryInterval(retryItervalSecs);
    this.ignoreErrorPatterns = Collections.unmodifiableList(ignoreErrorPatterns);
    if (addAutomaticSignatureToEnvironment) {
      final RemoteCommandTimeStamp timeStamp = new RemoteCommandTimeStamp();
      this.signatureRegistry.register(timeStamp.value());
      this.addEnvironment(timeStamp.name(), timeStamp.value());
    }
  }


  /**
   * Sets executor handle.
   *
   * @param handle
   */
  public void setHandle(final int handle) {
    this.handle = handle;
  }


  public int getHandle() {
    return handle;
  }


  public final void addEnvironment(final String name, final String value) {
    if (StringUtils.isBlank(name)) {
      return;
    }
    env.put(name, value);
  }


  public final void addEnvironment(final Map environment) {
    env.putAll(environment);
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
  }


  /**
   * Callback method - this method is called right after call to
   * execute.
   * <p/>
   * This method can be overriden by children to accomodate
   * post-execute processing such as command log analisys e.t.c.
   *
   * @param resultCode - execute command result code.
   */
  protected void postExecute(final int resultCode) throws IOException, AgentFailureException {
  }


  /**
   * Executes command
   *
   * @throws IOException
   */
  public final int execute() throws IOException, CommandStoppedException, AgentFailureException {
    final Random random = new Random(System.currentTimeMillis());
    for (int i = 0; i <= retryTimes; i++) {
      try {
        return executeOnce();
      } catch (final CommandStoppedException e) {
        if (LOG.isDebugEnabled()) LOG.debug("e: " + e, e);
        throw e;
      } catch (final RuntimeException e) {
        if (LOG.isDebugEnabled()) LOG.debug("e: " + e, e);
        throw e;
      } catch (final IOException e) {
        if (LOG.isDebugEnabled()) LOG.debug("e: " + e, e);
        if (patternPresent(e) && i < retryTimes - 1) {
          delayRandomly(random);
          cleanup();
        } else {
          throw e;
        }
      } catch (final AgentFailureException e) {
        if (LOG.isDebugEnabled()) LOG.debug("e: " + e, e);
        if (patternPresent(e) && i < retryTimes - 1) {
          delayRandomly(random);
          cleanup();
        } else {
          throw e;
        }
      }
    }

    throw new IOException("Command should never reach this point");
  }


  /**
   * Executes the command once.
   *
   * @return result code.
   * @throws CommandStoppedException
   * @throws IOException
   * @throws AgentFailureException
   */
  private int executeOnce() throws CommandStoppedException, IOException, AgentFailureException {
    // first, check for interruption
    ThreadUtils.checkIfInterrupted();

    // bullet-proof clean up
    remoteTempInputFile = null;

    //
    final RemoteCommandManager remoteCommandManager = RemoteCommandManager.getInstance();

    // proceed
    TimeoutWatchdog watchdog = null;
    String wrapperScriptFile = null;
    try {

      //
      // call preExecute callback
      //
      preExecute();

      //
      // create remote temp file if needed
      //
      if (is != null) {
        ByteArrayOutputStream bos = null;
        try {
          bos = new ByteArrayOutputStream(1000);
          IoUtils.copyInputToOuputStream(is, bos);
          remoteTempInputFile = agent.createTempFile(TEMP_FILE_PREFIX, ".scm", bos.toString());
        } finally {
          IoUtils.closeHard(is);
          IoUtils.closeHard(bos);
          is = null;
        }
      }

      //
      // register with build command registry
      //
      remoteCommandManager.registerBuildCommand(agent.getActiveBuildID(), this);

      //
      // pre-create local recivers of stdout and stderr
      //
      IoUtils.deleteFileHard(stderrFile);
      IoUtils.deleteFileHard(stdoutFile);
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out");
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err");
      if (mergedOutput) {
        if (mergedFile == null) {
          mergedFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".mrg");
        }
      }

      //
      // Create wrapper script for the command.
      //
      ThreadUtils.checkIfInterrupted();
      final WrapperScriptGenerator wrapperGenerator = WrapperScriptGeneratorFactory.makeScriptGenerator(agent);
      wrapperGenerator.setExecutionDirectory(remoteCurrentDir);
      wrapperGenerator.addVariables(env);
      if (remoteTempInputFile == null) {
        wrapperScriptFile = wrapperGenerator.generateScript(command);
      } else {
        final String commandWithInputStream = this.command + " < " + StringUtils.putIntoDoubleQuotes(remoteTempInputFile);
        wrapperScriptFile = wrapperGenerator.generateScript(commandWithInputStream);
      }
      signatureRegistry.register(wrapperScriptFile);
      final String commandLineToExecute = makeCommandLineToExecute(wrapperScriptFile);

      //
      // Start watchdog
      //
      ThreadUtils.checkIfInterrupted();
      if (timeoutSecs != Integer.MAX_VALUE) {
        // REVIEWME: simeshev@parabuilci.org -> make getting agent environment from agent instead of using factory.
        // Otherwise we can miss build host updates.
        final AgentEnvironment agentEnvironment = AgentManager.getInstance().getAgentEnvironment(getAgentHost());
        watchdog = new TimeoutWatchdog(agentEnvironment);
        watchdog.setTimeoutCallback(timeoutCallback);
        watchdog.setTimeoutSeconds(timeoutSecs);
        watchdog.addTimeoutMatches(signatureRegistry.signtatures());
        watchdog.start();
      }

      //
      // Launch command. If hung, it will be stuck forever in the COMMAND_POOL
      //
//      if (LOG.isDebugEnabled()) {
//        LOG.debug("Requesting command " + command + " to execute in the current dir: " + remoteCurrentDir);
//      }
      ThreadUtils.checkIfInterrupted();
      final RemoteCommandRunnable asyncCommand = new RemoteCommandRunnable(handle, agent,
              commandLineToExecute, tailBufferSize, stdoutFile, stderrFile, mergedFile);
      COMMAND_POOL.execute(asyncCommand);

      //
      // Wait for exit
      //
      final double deadlineTimeoutMillis = timeoutSecs == Integer.MAX_VALUE ? 60L * 60L * 1000L : 1.3 * timeoutSecs * 1000L;
      final long deadlineMillis = System.currentTimeMillis() + (long) deadlineTimeoutMillis;
      if (LOG.isDebugEnabled()) {
        LOG.debug("Deadline timeout, ms: " + deadlineTimeoutMillis);
      }
      boolean deadlineReached = false;
      while (!(asyncCommand.isResultAvailable() || watchdog != null && watchdog.isHung() || deadlineReached)) {
        Thread.sleep(500L);
        deadlineReached = System.currentTimeMillis() >= deadlineMillis;
      }

      // Process result
      if (asyncCommand.isResultAvailable()) {
        stop(watchdog);
        final int resultCode = asyncCommand.getResultCode();

        // Post execute
        ThreadUtils.checkIfInterrupted();
        postExecute(resultCode);


        return resultCode;
      } else {
        if (watchdog != null) {
          if (watchdog.isHung()) {
            throw new IOException("Command hung. It is possible that it is still running and affect subsequent commands: " + command);
          } else if (watchdog.isTimedOut()) {
            throw new IOException("Command timed out: " + command);
          } else {
            throw new IOException("Unknown command state on return from execute. Deadline " + deadlineTimeoutMillis + " ms reached: " + deadlineReached + ". It is possible that it is still running and may affect subsequent commands: " + command);
          }
        } else {
          throw new IOException("Unknown command state on return from execute. Deadline " + deadlineTimeoutMillis + " ms reached: " + deadlineReached + ". It is possible that it is still running and may affect subsequent commands: " + command);
        }
      }
    } catch (final InterruptedException e) {
      throw new CommandStoppedException(e);
    } finally {
      remoteCommandManager.unregisterBuildCommand(agent.getActiveBuildID(), this);
      stop(watchdog);
      IoUtils.closeHard(is);
      deleteTempInputFileHard();
      deleteRemoteFileHard(wrapperScriptFile);
      is = null;
    }
  }


  private static void stop(final TimeoutWatchdog watchdog) {
    if (watchdog != null) {
      watchdog.stop();
    }
  }


  public AgentHost getAgentHost() {
    return agent.getHost();
  }


  /**
   * Makes a command line string that will be passed to agent's
   * execute method.
   *
   * @param shellScriptFile a String path to a shell file script
   *                        to be executed.
   */
  private String makeCommandLineToExecute(final String shellScriptFile) throws IOException, AgentFailureException {
    final String quotedWrapperScriptFile = StringUtils.putIntoDoubleQuotes(shellScriptFile);
    final String commandToExecute;
    switch (agent.systemType()) {
      case AgentEnvironment.SYSTEM_TYPE_WIN95:
        commandToExecute = STR_COMMAND_C + quotedWrapperScriptFile;
        break;
      case AgentEnvironment.SYSTEM_TYPE_WINNT:
        commandToExecute = STR_CMD_C + quotedWrapperScriptFile;
        break;
      case AgentEnvironment.SYSTEM_TYPE_CYGWIN:
        commandToExecute = "sh " + quotedWrapperScriptFile;
        break;
      default:
        commandToExecute = "sh " + quotedWrapperScriptFile;
        break;
    }
    return commandToExecute;
  }


  /**
   * Adds timeout matches
   *
   * @param list of string to match when looking for timed out
   *             processes
   */
  public final void addSignatures(final List list) {
    this.signatureRegistry.register(list);
  }


  /**
   * Sets command to execute
   *
   * @param command to execute
   */
  public final void setCommand(final String command) {
    this.command = command;
  }


  /**
   * @return command
   */
  public final String getCommand() {
    return command;
  }


  public final void setInputStream(final InputStream is) {
    this.is = is;
  }


  /**
   * Sets current directory to execute in
   *
   * @param remoteCurrentDir directory to execute command in
   */
  public final void setCurrentDirectory(final String remoteCurrentDir) {
    this.remoteCurrentDir = remoteCurrentDir;
  }


  /**
   * Sets command timeout in seconds
   *
   * @param timeOut
   */
  public final void setTimeoutSecs(final int timeOut) {
    this.timeoutSecs = timeOut;
    if (this.timeoutSecs <= 1) {
      throw new IllegalStateException("Timeout \"" + this.timeoutSecs + "\" seconds is too short.");
    }
  }


  /**
   * Returns command timeout in seconds.
   */
  public final int getTimeoutSecs() {
    return timeoutSecs;
  }


  /**
   * @return merged stdout and stderr file
   */
  public final File getMergedFile() {
    return mergedFile;
  }


  /**
   * Sets merged stdout and stderr file
   */
  public final void setMergedFile(final File mergedFile) {
    this.mergedFile = mergedFile;
  }


  /**
   * @return stderr file
   */
  public final File getStderrFile() {
    return stderrFile;
  }


  /**
   * @return stdout file
   */
  public final File getStdoutFile() {
    return stdoutFile;
  }


  /**
   * Sets timeout handler
   *
   * @param timeoutCallback
   */
  public final void setTimeoutCallback(final TimeoutCallback timeoutCallback) {
    this.timeoutCallback = timeoutCallback;
  }


  /**
   * Removes all output and temporary files
   */
  public void cleanup() throws AgentFailureException {
    // NOTE: vimeshev@viewitier.com - 11/14/204 - we don't clean up
    // merged file because usually it's handled externally.
    IoUtils.deleteFileHard(stderrFile);
    IoUtils.deleteFileHard(stdoutFile);
    deleteTempInputFileHard();
  }


  /**
   * @return signatures
   */
  public final List getSignatures() {
    return signatureRegistry.signtatures();
  }


  public final void setMergedOutput(final boolean enable) {
    mergedOutput = enable;
  }


  /**
   * Sets log tail definition.
   *
   * @param tailBufferSize
   */
  public void setLogTailSize(final TailBufferSize tailBufferSize) {
    this.tailBufferSize = tailBufferSize;
  }


  private void deleteRemoteFileHard(final String remoteFile) {
    if (!StringUtils.isBlank(remoteFile)) {
      agent.deleteTempFileHard(remoteFile);
    }
  }


  private void deleteTempInputFileHard() {
    deleteRemoteFileHard(remoteTempInputFile);
    remoteTempInputFile = null;
  }


  private static int validRetryInterval(final int retryItervalSecs) {
    if (retryItervalSecs <= 0) {
      throw new IllegalArgumentException("Retry inteval should be a positive integer: " + retryItervalSecs);
    }
    return retryItervalSecs;
  }


  private static int validRetryTimes(final int retryTimes) {
    if (retryTimes < 0) {
      throw new IllegalArgumentException("Retry times should be a non-negative integer: " + retryTimes);
    }
    return retryTimes;
  }


  private boolean patternPresent(final Exception e) {

    // Check if there are any patterns
    if (ignoreErrorPatterns.isEmpty()) {
      return false;
    }

    // Match
    final String text = e.toString();
    for (int i = 0; i < ignoreErrorPatterns.size(); i++) {
      final Pattern pattern = Pattern.compile((String) ignoreErrorPatterns.get(i));
      if (LOG.isDebugEnabled()) LOG.debug("text: " + text);
      if (pattern.matcher(text).find()) {
        if (LOG.isDebugEnabled()) LOG.debug("pattern found");
        return true;
      }
    }

    // Nothing found
    if (LOG.isDebugEnabled()) LOG.debug("pattern not found");
    return false;
  }


  private void delayRandomly(final Random random) throws CommandStoppedException {
    final int delay = random.nextInt() % (retryItervalSecs * 1000);
    try {
      final int delayMillis = delay >= 0 ? delay : -delay;
      if (LOG.isDebugEnabled()) LOG.debug("delaying for, millis: " + delayMillis);
      Thread.sleep(delayMillis);
    } catch (final InterruptedException e) {
      throw new CommandStoppedException();
    }
  }


  public String toString() {
    return "RemoteCommand{" +
            "mergedOutput=" + mergedOutput +
            ", env=" + env +
            ", timeoutSecs=" + timeoutSecs +
            ", command='" + command + '\'' +
            ", remoteTempInputFile='" + remoteTempInputFile + '\'' +
            ", timeoutCallback=" + timeoutCallback +
            ", mergedFile=" + mergedFile +
            ", stderrFile=" + stderrFile +
            ", stdoutFile=" + stdoutFile +
            ", agent=" + agent +
            ", signatureRegistry=" + signatureRegistry +
            ", is=" + is +
            ", remoteCurrentDir='" + remoteCurrentDir + '\'' +
            ", handle=" + handle +
            ", retryTimes=" + retryTimes +
            ", retryItervalSecs=" + retryItervalSecs +
            ", ignoreErrorPatterns=" + ignoreErrorPatterns +
            ", tailBufferSize=" + tailBufferSize +
            '}';
  }
}
