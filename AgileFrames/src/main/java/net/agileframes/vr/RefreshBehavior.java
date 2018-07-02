package net.agileframes.vr;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;

/**
  @author van Dijk, Lindeijer, Evers
  @version 0.0.1
  @since Wed Jan 12 12:59:43 2000

  Requests the avatar to update its state because it is visible within
  virtuality at this time.
*/

public class RefreshBehavior extends Behavior implements Runnable{
  private WakeupOnElapsedFrames wakeup;
  private AvatarImplBase avatar;


  /**
   * Constructor - creates a new RefreshBehavior
   * @param avatar the avatar that this behavior is to be asociated with
   * @param frames the number of frames between each 'trigger'
   * hshshshshshshshshshshhshshshshshshshshshshshhshshshshshshsh
   */
  public RefreshBehavior(AvatarImplBase avatar, int frames) {
    this.avatar = avatar;
    this.wakeup = new WakeupOnElapsedFrames(frames);
    this.frameRate = frames;
  }

  public void initialize() {
    Thread refreshThread = new Thread(this);
    refreshThread.setName("avatarRefreshThread");
    refreshThread.start();
    //this.wakeupOn(new WakeupOnElapsedFrames(frameRate));
    wakeupOn(this.wakeup);
  }

  public synchronized void run() {
    int check = 0;
    for(;;){
      try{
        check = 1;
        synchronized(this){this.wait();}
        check = 2;
        //System.out.println("refreshBehavior notified");
        if (avatar!=null) {avatar.refresh();} else {System.out.println("avatar=null!");}
        check = 3;
      }
      catch(Exception e){System.out.println("Exception in RefreshBehavior.run(), check="+check+"  error:"+e);
      e.printStackTrace();
      }
    }
  }

  public int frameRate = 1;
  public void processStimulus(java.util.Enumeration criteria) {
    synchronized(this){this.notify();}
    //System.out.println("Refreshing....");
//    this.wakeupOn(new WakeupOnElapsedFrames(frameRate));
     wakeupOn(this.wakeup);
  }
}