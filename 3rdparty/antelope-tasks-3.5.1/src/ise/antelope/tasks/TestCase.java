package ise.antelope.tasks;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;

import ise.library.ascii.MessageBox;

/**
 * Modeled after the TestCase provided by jUnit, this class is an Ant task that
 * looks through the build file set in the <code>setFile</code> method, calls a
 * 'setUp' target in that build file (if it exists), then all targets whose
 * names start with 'test', and last calls a target named 'tearDown' (if it
 * exists). Both 'setUp' and 'tearDown' are optional targets in the build file.
 * <p>
 *
 * Ant stores targets in a hashtable, so there is no guaranteed order in which
 * the 'test*' classes will be called. If order is important, use the 'depends'
 * attribue of a target to enforce order, and do not name dependent targets with
 * a name starting with 'test'. <p>
 *
 * Test targets may also be imported with the import task. Imported files should
 * not have a "suite" task in the implicit target as such a task would rerun all
 * test targets in all files.
 *
 * @author    Dale Anson
 * @version   $Revision: 138 $
 */
public class TestCase extends Task implements TestStatisticAccumulator {

    private boolean enabled = true;
    private boolean assertEnabled = true;
    private Target setUp = null;
    private Target tearDown = null;
    private Vector testTargets = new Vector();
    private Vector failures = new Vector();
    private boolean failOnError = false;
    private boolean showSummary = true;
    private boolean showOutput = true;
    private File testFile = null;
    private String test_name = "";

    private int tests_passed = 0;
    private int tests_failed = 0;
    private int tests_warning = 0;

    /** task initilization */
    public void init() {
        super.init();
        setTaskName("testcase");
    }

    /**
     * @return   the count of test targets.
     */
    public int getTestCaseCount() {
        return testTargets.size();
    }

    /**
     * @return   the number of tests targets actually executed.
     */
    public int getRanCount() {
        return tests_passed + tests_warning + tests_failed;
    }

    /**
     * @return   the number of tests that failed.
     */
    public int getFailedCount() {
        return tests_failed;
    }
    
    public int getWarningCount() {
        return tests_warning;   
    }

    /**
     * @return   the number of tests that passed.
     */
    public int getPassedCount() {
        return tests_passed;
    }

    /**
     * @return   an Enumeration of the failures. Individual elements are Strings
     *      containing the name of the failed target and the reason why it
     *      failed.
     */
    public Enumeration getFailures() {
        return failures.elements();
    }

    /**
     * @param b  if true, show the output of the tests as they run. Optional,
     *      default is true, do show output.
     */
    public void setShowoutput(boolean b) {
        showOutput = b;
    }

    /**
     * @param b  if true, show the summary output (number of tests, passed,
     *      failed) after the completion of all tests. Optional, default is
     *      true, do show summary.
     */
    public void setShowsummary(boolean b) {
        showSummary = b;
    }

    /**
     * @param f  the file containing the tests to execute. Required. The file
     *      itself is a standard Ant build file, but will be treated differently
     *      than if Ant itself ran it. If there is a target named "setUp", that
     *      target will be executed first, then all targets with names starting
     *      with "test" (not in any particular order), then if there is a target
     *      named "tearDown", that target will be executed last. All other
     *      targets are ignored.
     */
    public void setFile(File f) {
        testFile = f;
    }

    /**
     * Should Asserts be enabled? Many (most?) tests will use the Assert task,
     * which requires a property to be set to actually enable the asserts. By
     * default, Asserts are enabled for testcases.
     *
     * @param b  if false, do not enable asserts. Note that this sets an Ant
     *      property, and due to property immutability, this attribute may have
     *      no effect if it has been set already. Generally, asserts are enabled
     *      or disabled for an entire build.
     */
    public void setAssertsenabled(boolean b) {
        assertEnabled = b;
    }

    /**
     * Should the build fail if the test fails? By default, a failed test does
     * not cause the build to fail, so all tests may have the opportunity to
     * run.
     *
     * @param b  set to true to cause the build to fail if the test fails
     */
    public void setFailonerror(boolean b) {
        failOnError = b;
    }

    /**
     * @return   the name of the test as set by <code>setName</code>. If the
     *      name has not be explicitly set, then the test name is the project
     *      name if there is one, otherwise, the filename of the test file.
     */
    public String getName() {
        return test_name;
    }

    /**
     * Set the name for this testcase.
     *
     * @param n  the name for the testcase
     */
    public void setName(String n) {
        test_name = n;
    }

    /**
     * @param b  if true, execute the test. This is handy for enabling or
     *      disabling groups of tests in a 'suite' by setting a single property.
     *      Optional, default is true, the test should run.
     */
    public void setEnabled(boolean b) {
        enabled = b;
    }

    /** Run tests. */
    public void execute() {
        
        /*
        Hashtable parent_targets = getProject().getTargets();
        
        for (Iterator it = parent_targets.keySet().iterator(); it.hasNext(); ) {
            log(it.next());   
        }
        */
        
        if (!enabled)
            return;

        // check the test file
        if (testFile == null)
            throw ProjectHelper.addLocationToBuildException(new BuildException("missing file for testcase"), getLocation());
        if (!testFile.exists())
            throw ProjectHelper.addLocationToBuildException(new BuildException("file not found for testcase: " + testFile), getLocation());

        // make sure asserts are enabled
        String ae = assertEnabled ? "true" : "false";
        if (assertEnabled)
            getProject().setProperty("ant.enable.asserts", ae);

        // create a new project, similar to the "ant" task, but not nearly as involved.
        // All properties will be copied from the parent project to the subproject, and
        // any properties set during execution of the subproject will be copied back to
        // the parent project.
        Project myProject = new Project();
        initializeProject(myProject);
        try {
            ProjectHelper.configureProject(myProject, testFile);
        }
        catch (BuildException be) {
            throw ProjectHelper.addLocationToBuildException(be, getLocation());
        }

        // set the test name, use a name that was set as an attribute first,
        // otherwise, use the project name, and if all else fails, use the name
        // of the test file
        if (test_name == null || test_name.equals(""))
            test_name = myProject.getName();
        if (test_name == null || test_name.equals(""))
            test_name = testFile.getName();

        if (showOutput) {
            // output start of test
            log(MessageBox.box("Starting test: " + test_name));
        }

        // get the setUp, tearDown, and test targets
        Hashtable targets = myProject.getTargets();
        Enumeration en = targets.keys();
        while (en.hasMoreElements()) {
            String target = (String) en.nextElement();
            if (target.equals("setUp"))
                setUp = (Target) targets.get(target);
            else if (target.equals("tearDown"))
                tearDown = (Target) targets.get(target);
            else if (target.startsWith("test"))
                testTargets.addElement(targets.get(target));
            // also check for imported targets.  Imported targets are named like
            // projectname.targetname, so check for whatever follows the last dot.
            // This is somewhat naive, as there is no restriction using dots in
            // target names, so a target named "build.testimonials" would be treated
            // as a test target.
            else if (target.lastIndexOf(".") > 0 && target.substring(target.lastIndexOf(".") + 1).startsWith("test")) {
                testTargets.addElement(targets.get(target));
            }
        }

        // run the setUp target
        if (setUp != null)
            setUp.execute();

        // run the test targets
        StringBuffer messages = new StringBuffer();
        en = testTargets.elements();
        while (en.hasMoreElements()) {
            Target target = (Target) en.nextElement();
            try {
                myProject.executeTarget(target.getName());
                if (showOutput)
                    log(target.getName() + " passed.");
                ++tests_passed;
            }
            catch (Exception e) {
                String error = "ERROR: ";
                if (e instanceof AssertException) {
                    int level = ((AssertException)e).getLevel();
                    if (level == AssertException.ERROR)
                        ++ tests_failed;
                    else if (level == AssertException.WARN) {
                        ++ tests_warning;
                        error = "WARNING: ";
                    }
                    else {
                        // info or debug level
                        if (showOutput)
                            log(target.getName() + ": " + e.getMessage());
                        continue;
                    }
                }
                else
                    ++tests_failed;
                if (showOutput)
                    log(error + target.getName() + " failed: " + e.getMessage());
                failures.addElement(error + test_name + ": " + target.getName() + " failed: " + e.getMessage());
                if (failOnError)
                    throw new BuildException(e.getMessage());
            }
        }

        // run the tearDown target
        if (tearDown != null)
            tearDown.execute();

        // copy any new properties to parent project
        addAlmostAll(getProject(), myProject.getProperties());
        /*
        Hashtable props = myProject.getProperties();
        for (en = props.keys(); en.hasMoreElements(); ) {
            String key = (String)en.nextElement();
            String value = (String)props.get(key);
            if (value != null)
                getProject().setNewProperty(key, value);
    }
        */
        // print any error messages
        if (showSummary) {
            log(getSummary());
        }
    }

    /**
     * Gets the summary attribute of the TestCase object
     *
     * @return   The summary value
     */
    public String getSummary() {
        String title = (test_name == null ? "Test" : test_name) + " Results";
        StringBuffer msg = new StringBuffer();
        String ls = System.getProperty("line.separator");
        // log the failures
        if (failures.size() > 0) {
            String error_title = "Errors";
            StringBuffer error_msg = new StringBuffer();
            Enumeration en = failures.elements();
            while (en.hasMoreElements()) {
                error_msg.append((String) en.nextElement()).append(ls);
            }
            int box_width = MessageBox.getMaxWidth();
            MessageBox.setMaxWidth(box_width - 8);
            msg.append(MessageBox.box(error_title, error_msg));
            MessageBox.setMaxWidth(box_width);
            msg.append(ls);
        }

        // log the test count info
        msg.append("Ran:     ").append(getRanCount()).append(" out of ").append(getTestCaseCount()).append(" tests.").append(ls);
        msg.append("Passed:  ").append(getPassedCount()).append(ls);
        msg.append("Warning: ").append(getWarningCount()).append(ls);
        msg.append("Failed:  ").append(getFailedCount()).append(ls);
        return MessageBox.box(title, msg);
    }

    /**
     * Attaches the build listeners of the current project to the new project,
     * configures a possible logfile, transfers task and data-type definitions,
     * transfers properties (either all or just the ones specified as user
     * properties to the current project, depending on inheritall), transfers
     * the input handler.
     *
     * @param newProject
     */
    private void initializeProject(Project newProject) {
        newProject.setBaseDir(getProject().getBaseDir());
        newProject.setInputHandler(getProject().getInputHandler());

        Iterator iter = getProject().getBuildListeners().iterator();
        while (iter.hasNext()) {
            newProject.addBuildListener((BuildListener) iter.next());
        }

        getProject().initSubProject(newProject);

        // copy properties
        /// are these necessary?  Does addAlmostAll cover these properties?
        getProject().copyInheritedProperties(newProject);
        getProject().copyUserProperties(newProject);

        // set all properties from calling project
        addAlmostAll(newProject, getProject().getProperties());

    }

    /**
     * Copies all properties from the given table to the given project -
     * omitting those that have already been set in the project as well as
     * properties named basedir or ant.file.
     *
     * @param props    properties to copy to the project
     * @param project  The feature to be added to the AlmostAll attribute
     */
    private void addAlmostAll(Project project, Hashtable props) {
        Enumeration e = props.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            if ("basedir".equals(key) || "ant.file".equals(key)) {
                // basedir and ant.file get special treatment in execute()
                continue;
            }

            String value = props.get(key).toString();
            // don't re-set user properties, avoid the warning message
            if (project.getProperty(key) == null) {
                // no user property
                project.setNewProperty(key, value);
            }
        }
    }

}

