package ise.antelope.tasks;

import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * Modeled after the TestSuite provided by jUnit, this class is an Ant task that
 * looks through the build file that contains this task, calls a 'setUp' target
 * (if it exists), then all targets whose names start with 'test', and last calls
 * a target named 'tearDown' (if it exists).  Both 'setUp' and 'tearDown' are
 * optional targets in the build file. <p>
 * Ant stores targets in a hashtable, so there is no guaranteed order in which
 * the 'test*' classes will be called.  If order is important, use the 'depends'
 * attribue of a target to enforce order, and do not name dependent targets with
 * a name starting with 'test'. <p>
 * Test targets may also be imported with the import task.  Imported files should
 * not have a "suite" task in the implicit target as such a task would rerun
 * all test targets in all files.
 *
 * @author Dale Anson
 */
public class SuiteOriginal extends Task {

    private Target setUp = null;
    private Target tearDown = null;
    private Vector testTargets = new Vector();
    private Vector failures = new Vector();
    private boolean showSummary = true;
    private boolean showOutput = true;

    private int tests_passed = 0;
    private int tests_failed = 0;

    public int getTestCaseCount() {
        return testTargets.size();
    }

    public int getRanCount() {
        return tests_passed + tests_failed;
    }

    public int getFailedCount() {
        return tests_failed;
    }

    public int getPassedCount() {
        return tests_passed;
    }

    public Enumeration getFailures() {
        return failures.elements();
    }

    public void setShowoutput( boolean b ) {
        showOutput = b;
    }
    
    public void setShowsummary(boolean b ) {
        showSummary = b;   
    }

    /** Run tests. */
    public void execute() {

        // get the setUp, tearDown, and test targets
        Hashtable targets = getProject().getTargets();
        Enumeration en = targets.keys();
        while ( en.hasMoreElements() ) {
            String target = ( String ) en.nextElement();
            if ( target.equals( "setUp" ) )
                setUp = ( Target ) targets.get( target );
            else if ( target.equals( "tearDown" ) )
                tearDown = ( Target ) targets.get( target );
            else if ( target.startsWith( "test" ) )
                testTargets.addElement( targets.get( target ) );
            // also check for imported targets.  Imported targets are named like
            // projectname.targetname, so check for whatever follows the last dot.
            // This is somewhat naive, as there is no restriction using dots in
            // target names, so a target named "build.testimonials" would be treated
            // as a test target.
            else if ( target.lastIndexOf( "." ) > 0 && target.substring( target.lastIndexOf( "." ) + 1 ).startsWith( "test" ) ) {
                testTargets.addElement( targets.get( target ) );
            }
        }

        // run the setUp target
        if ( setUp != null )
            setUp.execute();
        en = testTargets.elements();

        // run the test targets
        StringBuffer messages = new StringBuffer();
        while ( en.hasMoreElements() ) {
            Target target = ( Target ) en.nextElement();
            try {
                executeDependencies( target );
                target.performTasks();
                if (showOutput)
                    log( target.getName() + " passed." );
                ++tests_passed;
            }
            catch ( Exception e ) {
                ++tests_failed;
                if (showOutput)
                    log( target.getName() + " failed: " + e.getMessage() );
                failures.addElement( target.getName() + " failed: " + e.getMessage() );
            }
        }

        // run the tearDown target
        if ( tearDown != null )
            tearDown.execute();

        // print any error messages
        if ( showSummary ) {
            // log the failures
            if ( failures.size() > 0 ) {
                log( "" );
                log( "---- Errors ---------------------------------" );
                en = failures.elements();
                while(en.hasMoreElements()) {
                    String msg = (String)en.nextElement();
                    log(msg);
                }
            }

            // log the test count info
            log( "" );
            log( "---- Results --------------------------------" );
            log( "Ran " + getRanCount() + " out of " + getTestCaseCount() + " tests." );
            log( "Passed: " + getPassedCount() );
            log( "Failed: " + getFailedCount() );
        }
    }

    /**
     * Execute a target's dependencies followed by the target itself.
     * @param target the target to execute
     */
    private void executeDependencies( Target target ) {
        if ( target == null )
            return ;
        Enumeration en = target.getDependencies();
        if ( en == null )
            return ;
        while ( en.hasMoreElements() ) {
            String name = ( String ) en.nextElement();
            Target t = ( Target ) getProject().getTargets().get( name );
            executeDependencies( t );
            t.performTasks();
        }
    }
}

