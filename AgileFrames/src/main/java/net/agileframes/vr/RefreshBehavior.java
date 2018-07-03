package net.agileframes.vr;
import net.agileframes.core.vr.Avatar;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;
import java.util.Enumeration;
/**
 * <b>RefreshBehavior is a behavior class that is used to update the state of an avatar.</b><p>
 * Every time it is triggered it invokes avatar.refresh()
 * @see     net.agileframes.core.vr.Avatar#refresh()
 * @author  F.A. van Dijk, D.G. Lindeijer
 * @version 0.1
 */
public class RefreshBehavior extends Behavior {
  private WakeupOnElapsedFrames wakeup;
  private Avatar avatar;
  private int frames;
  /**
   * Constructor - creates a new RefreshBehavior.<p>
   * @param avatar the avatar that this behavior is to be associated with
   * @param frames the number of frames between each 'trigger'
   */
  public RefreshBehavior(Avatar avatar, int frames) {
    this.avatar = avatar;
    this.wakeup = new WakeupOnElapsedFrames(frames);
    this.frames = frames;
  }
  /** Initializes this RefreshBehavior. */
  public void initialize() {
    wakeupOn(this.wakeup);
  }
  /**
   * Refreshes the Avatar.<p>
   * Inherited from Behavior.
   * @see     net.agileframes.core.vr.Avatar#refresh()
   * @param   criteria not used
   */
  public void processStimulus( Enumeration criteria ) {
    try {
      this.avatar.refresh();
      wakeupOn(this.wakeup);
    } catch (Exception e) { e.printStackTrace(); }
  }
}