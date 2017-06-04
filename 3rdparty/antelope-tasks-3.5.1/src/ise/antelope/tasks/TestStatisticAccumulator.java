package ise.antelope.tasks;

import java.util.Enumeration;

interface TestStatisticAccumulator {
    public int getFailedCount();
    public Enumeration getFailures();
    public int getWarningCount();
    public int getPassedCount();
    public int getRanCount();
    public String getSummary();
    public int getTestCaseCount();
}
