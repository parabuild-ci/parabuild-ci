
package ise.antelope.tasks;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class AssertLevel extends EnumeratedAttribute {
    private String[] values = new String[] {"error", "warning", "info", "debug"};
    public String[] getValues() {
        return values;
    }
}


