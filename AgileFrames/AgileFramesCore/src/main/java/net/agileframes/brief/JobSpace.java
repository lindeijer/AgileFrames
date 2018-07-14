package net.agileframes.brief;
import net.jini.space.JavaSpace;
import net.agileframes.server.AgileSystem;
/**
 * <b>The implementation of JobSpace.</b>
 * <p>
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class JobSpace {
  /** Empty Constructor. Not used. */
  public JobSpace() {}
  /** The JavaSpace JobSpace */
  protected static JavaSpace jobSpace = null;
  /** The name of this JobSpace */
  protected static String jobSpaceName = null;
  /**
   * Returns a reference to the JobSpace.<p>
   * A JobSpace will be created when it doesnot exist already.
   * @see     net.agileframes.server.AgileSystem#getSpace(String)
   * @return  the JobSpace
   */
  public static JavaSpace getJobSpace()   {
    if (jobSpace == null) { jobSpace = AgileSystem.getSpace(jobSpaceName); }
    return jobSpace;
  }
  /**
   * Static Initializer.<p>
   * Reads system-property agileframes.jobspace.name.
   * Gets JobSpace.
   * @see #getJobSpace()
   */
  static {
    jobSpaceName = System.getProperty("agileframes.jobspace.name");
    if (jobSpaceName == null) { jobSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.jobspace.name=" + jobSpaceName);
    if (getJobSpace() == null) { System.out.println("jobSpace not found"); }
  }

}