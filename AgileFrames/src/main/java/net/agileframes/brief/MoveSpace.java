package net.agileframes.brief;
import net.jini.space.JavaSpace;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.brief.Brief;
import net.jini.core.entry.Entry;

import java.rmi.RemoteException;


public class MoveSpace extends BriefSpace {

  public MoveSpace() {
  }

  protected static JavaSpace moveSpace = null;
  protected static String moveSpaceName = null;
  public static JavaSpace getMoveSpace()  {
    if (moveSpace == null) { moveSpace = AgileSystem.getSpace(moveSpaceName); }
    return moveSpace;
  }

  ///////////////////////////////////////////////////////////////

  public static void write(Brief brief) {
    BriefSpace.write(brief);
  }

  //////////////////////////////////////////////////////////////////////////

  public static boolean isBrief(Brief template) {
    return BriefSpace.isBrief(moveSpace,template);
  }

  //////////////////////////////////////////////////////////////////////////

  public static Brief read(Brief template) {
    return BriefSpace.read(moveSpace,template);
  }

  ///////////////////////////////////////////////////////////////////////////

  public static Brief take(Brief template) {
     return BriefSpace.take(moveSpace,template);
  }

  ///////////////////////////////////////////////////////////////

  static {
    moveSpaceName = System.getProperty("agileframes.movespace.name");
    if (moveSpaceName == null) { moveSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.movespace.name=" + moveSpaceName);
    if (getMoveSpace() == null) { System.out.println("moveSpace not found"); }
  }


} 