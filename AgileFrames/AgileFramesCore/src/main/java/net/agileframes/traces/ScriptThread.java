package net.agileframes.traces;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
/**
 * This class is needed to be able to destroy a remote-script.
 * Will be created and called in SceneAction which is Serializable.
 */
public class ScriptThread extends Thread implements Remote {
  public ScriptThread(String name) {
    super(name);
    try { UnicastRemoteObject.exportObject(this); }
    catch (Exception e) { e.printStackTrace(); }
  }
}