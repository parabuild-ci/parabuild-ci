package ise.antelope.tasks;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

public class Call extends Task {
   
   private String target = null;
   
   public void setTarget(String target) {
      this.target = target;  
   }
   
   public void execute() {
      if (target == null)
         throw new BuildException("target is required");
      getProject().executeTarget(target);
   }
}
