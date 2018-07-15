package net.agileframes.traces;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.traces.SemaphoreRemote;

// data-object
public class LogisticPosition implements java.io.Serializable {
  //-- Attributes --
  public String name;
  public FuSpace location;
  public Scene scene;
  public SemaphoreRemote semaphore;
  public int[] params;

  //-- Constructor --
  public LogisticPosition(String name, Scene scene, int[] params) { this(name, null, scene, null, params); }
  public LogisticPosition(String name, FuSpace location, Scene scene, SemaphoreRemote semaphore, int[] params) {
    this.name = name;
    this.location = location;
    this.scene = scene;
    this.semaphore = semaphore;
    if (params != null) {
      this.params = new int[params.length];
      for (int i = 0; i < params.length; i++) {  this.params[i] = params[i]; }
    }
  }

  public String toString() {
    String s = "LogisticPosition: "+name;
    try {
      if (scene != null) s += " in "+scene.getName();
      if (semaphore != null) s += " (semaphore="+semaphore.getName()+")";
      if (location != null) s += "\nLocation = "+location.toString();
    } catch (Exception e) { e.printStackTrace(); }
    return s;
  }

  public String getName() {
    String s = this.name;
    if (params != null) {
      for (int i = 0; i < params.length; i++) { s += "."+params[i]; }
    }
    return s;
  }

  public boolean equals(Object obj) {
    if (obj == null) { return false; }
    LogisticPosition lp = (LogisticPosition) obj;
    if (!name.equals(lp.name)) { return false; }
    if (params != null) {
      if (lp.params == null) { return false; }
      if (lp.params.length != params.length) { return false; }
      for (int i = 0; i < params.length; i++) { if (params[i] != lp.params[i]) { return false; } }
    }
    if (lp.params != null) { return false; }
    return true;
  }
}