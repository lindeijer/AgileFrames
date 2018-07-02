package net.agileframes.brief;
import net.jini.space.JavaSpace;
import net.agileframes.server.AgileSystem;

public class JobSpace {

  public JobSpace() {
  }

  protected static JavaSpace jobSpace = null;
  protected static String jobSpaceName = null;
  public static JavaSpace getJobSpace()   {
    if (jobSpace == null) { jobSpace = AgileSystem.getSpace(jobSpaceName); }
    return jobSpace;
  }


  static {
    jobSpaceName = System.getProperty("agileframes.jobspace.name");
    if (jobSpaceName == null) { jobSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.jobspace.name=" + jobSpaceName);
    if (getJobSpace() == null) { System.out.println("jobSpace not found"); }
  }

} 