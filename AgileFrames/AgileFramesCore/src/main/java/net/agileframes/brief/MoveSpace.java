package net.agileframes.brief;
import net.jini.space.JavaSpace;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.brief.Brief;
import net.jini.core.entry.Entry;
import java.rmi.RemoteException;
/**
 * <b>The implementation of MoveSpace.</b>
 * <p>
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class MoveSpace extends BriefSpace {
  /** Empty Constructor. Not used. */
  public MoveSpace() {}
  /** The JavaSpace MoveSpace */
  protected static JavaSpace moveSpace = null;
  /** The name of this MoveSpace */
  protected static String moveSpaceName = null;
  /**
   * Returns a reference to the MoveSpace.<p>
   * A MoveSpace will be created when it doesnot exist already.
   * @see     net.agileframes.server.AgileSystem#getSpace(String)
   * @return  the MoveSpace
   */
  public static JavaSpace getMoveSpace()  {
    if (moveSpace == null) { moveSpace = AgileSystem.getSpace(moveSpaceName); }
    return moveSpace;
  }

  ///////////////////////////////////////////////////////////////
  /**
   * Calls method in BriefSpace.
   * @see BriefSpace#write(Brief)
   */
  public static void write(Brief brief) {
    BriefSpace.write(brief);
  }

  //////////////////////////////////////////////////////////////////////////
  /**
   * Calls method in BriefSpace.
   * @see BriefSpace#isBrief(Brief)
   */
  public static boolean isBrief(Brief template) {
    return BriefSpace.isBrief(moveSpace,template);
  }

  //////////////////////////////////////////////////////////////////////////

  /**
   * Calls method in BriefSpace.
   * @see BriefSpace#read(Brief)
   */
  public static Brief read(Brief template) {
    return BriefSpace.read(moveSpace,template);
  }

  ///////////////////////////////////////////////////////////////////////////
  /**
   * Calls method in BriefSpace.
   * @see BriefSpace#take(Brief)
   */
  public static Brief take(Brief template) {
     return BriefSpace.take(moveSpace,template);
  }

  ///////////////////////////////////////////////////////////////
  /**
   * Static Initializer.<p>
   * Reads system-property agileframes.movespace.name.
   * Gets MoveSpace.
   * @see #getMoveSpace()
   */
  static {
    moveSpaceName = System.getProperty("agileframes.movespace.name");
    if (moveSpaceName == null) { moveSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.movespace.name=" + moveSpaceName);
    if (getMoveSpace() == null) { System.out.println("moveSpace not found"); }
  }


}