package net.agileframes.forces;
import net.agileframes.forces.flag.AbstractFlag;
import net.agileframes.core.forces.Flag;

public class FlagManager {

  /**
  neither are null, flags does not contain any holes
  */
  public static void add(AbstractFlag[] flags,AbstractFlag flag) {
    // System.out.println("to insert flag=" + flag.toString() + " into flags=" + flags.toString());
    try{
    AbstractFlag helper;
    for (int i=0;i<flags.length;i++) {
      if (flags[i] != null) {
        if (flags[i].getEvolution()>flag.getEvolution()) {
          //System.out.println("flag to be inserted before another flag");
          helper = flags[i];
          flags[i] = flag;
          flag = helper;    // this is bubble-sort
        }
        else {
          //System.out.println("flag not inserted yet at i=" + i);
        }
      }
      else {
        flags[i] = flag;
        flag = null;
        //System.out.println("flag inserted at i="+i);
        break;
      }
    }
    if (flag != null) { throw new RuntimeException("add flag to a full flags"); }
    //
    for (int i=0;i<flags.length;i++) {
      if (flags[i] != null) {
        // System.out.println("flags["+i+"]=" + flags[i].toString());
      }
    }
    }catch (Exception e) {
      System.out.println("Exception in FlagManager.add():"+e.getMessage());
      System.out.println("flags.length="+flags.length);
      System.out.println("flag="+flag.toString());
      e.printStackTrace();
    }
  }

  /**
  remove and close the hole
  */
  public static void remove(Flag[] flags,Flag flag) {
    int i;
    for (i=0;i<flags.length;i++) {
      if (flags[i] == flag) {
        flags[i] = null;
        // flag.finalize();
        break;
      }
    }
    int j;
    for (j=i;j<flags.length-1;j++) {
      flags[j] = flags[j+1];
      if (flags[j] == null) { break; }
    }
  }





}