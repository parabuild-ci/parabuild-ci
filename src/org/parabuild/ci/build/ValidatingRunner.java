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

/**
 * Runs or skips delegate's method depending on validation
 * status.
 *
 * @noinspection ClassHasNoToStringMethod
 */
public final class ValidatingRunner {

  private Runnable runner = null;


  public ValidatingRunner(final Runnable delegate) {
    initRunner(delegate);
  }


  /**
   * Constructor
   */
  public ValidatingRunner(final BuildRunner buildRunner) {
    this(new Runnable() {
      private final BuildRunner wrappedBuildRunner = buildRunner;


      public void run() {
        wrappedBuildRunner.runBuild();
      }
    });
  }


  /**
   * Reinitilizes runner
   */
  public final void initRunner(final Runnable delegate) {

    runner = new ActiveRunner(delegate, this);
  }


  /**
   * Delegates run to a runner
   */
  public void runBuild() {
    runner.run();
  }


  /**
   * @return wrapped Runner
   */
  public Runnable getRunner() {
    return runner;
  }


  /**
   * Abstract runner
   *
   * @noinspection ClassHasNoToStringMethod
   */
  private abstract static class AbstractRunner implements Runnable {

    private final Runnable delegate;
    private final ValidatingRunner validatingRunner;
    private int counter = 0;


    protected AbstractRunner(final Runnable delegate, final ValidatingRunner validatingRunner) {
      this.delegate = delegate;
      this.validatingRunner = validatingRunner;
    }


    protected final boolean counterIsOver() {
      counter += 1;
      return counter > 10;
    }


    /**
     * Resets a counter to zero
     */
    protected final void resetCounter() {
      counter = 0;
    }


    protected final Runnable delegate() {
      return delegate;
    }


    protected final ValidatingRunner validatingRunner() {
      return validatingRunner;
    }


    /**
     * When an object implementing interface <code>Runnable</code>
     * is used to create a thread, starting the thread causes the
     * object's <code>run</code> method to be called in that
     * separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is
     * that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public abstract void run();
  }

  /**
   * Runner that accesses build runner
   */
  public static final class ActiveRunner extends AbstractRunner {

    /**
     * Constructor
     */
    public ActiveRunner(final Runnable delegate, final ValidatingRunner validatingRunner) {
      super(delegate, validatingRunner);
    }


    public void run() {
      delegate().run();
      if (counterIsOver()) {
        validatingRunner().initRunner(delegate());
        resetCounter();
      }
    }
  }
}

