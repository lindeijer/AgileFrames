package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Transform;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.traces.SceneImplBase;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;
import net.agileframes.traces.Move;

/**
 * The move that goes one stack to the east on the lower centerlane. This move is proceeded
 * by another GoEastAtCenter or LeaveStackForCenterEast and followed by another GoEastAtCenter
 * or a ParkEast.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class GoEastAtCenter extends MoveImplBase  {
   /**
   * @param stackNr  stack-number in front of which this move is situated:  2 <= stackNr < numberOfStacks-2
   *
   * @throws RemoteException
   */
  public GoEastAtCenter(int stackNr) throws java.rmi.RemoteException {
    if ((stackNr>1) && (stackNr<CrossoverScene.cT.numberOfStacks-2)){
      // Create transform to determine starting location
      float x =  CrossoverScene.cT.FREE_TERMINAL_LEFT +
                 stackNr * CrossoverScene.cT.STACK_WIDTH_INCL +
                 CrossoverScene.cT.FREE_SPACE_AT_SIDES +
                 CrossoverScene.cT.WIDTH_DRIVING_LANE;
      float y =  CrossoverScene.cT.DIST_TOP_QUAY -
                 CrossoverScene.cT.DIST_TOP_STACK -
                 CrossoverScene.cT.LENGTH_STACKLANE -
                 CrossoverScene.cT.DIST_STACK_CENTER -
                 CrossoverScene.cT.WIDTH_CENTER_LANE;
      float alpha = 0;
      Transform transform = new POSTransform(
        x*CrossoverScene.scale+CrossoverScene.xTrans,
        y*CrossoverScene.scale+CrossoverScene.yTrans,
        alpha,CrossoverScene.scale
      );

      // this.trajectory = new CrossoverArea$East(transform);

      // Create composedMove
      Trajectory[] composedTrajectory = new Trajectory[1];
      composedTrajectory[0] =
        new GoStraight(CrossoverScene.cT.STACK_WIDTH_INCL);
      composedTrajectory[0].setTransform(new POSTransform(0,0,0));

      this.trajectory = new Trajectory(composedTrajectory);
      this.trajectory.setTransform(transform);
      this.trajectory.obstacleAtEnd = false;
    }// if
    else { this.trajectory = null;}
  }
  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0) ,  // finished
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH/2, 1) ,// finishing
      new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2, 2)  // beyond: incorrect, must be + AGV_LENGTH!
    };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] (centerSouth)
   *                  2 = free entryTickets[1] (stayCenterSouth)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1:// stackCenter
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in GoEastAtCenter:"+e.getMessage());}
        break;
      case 2:// staySouth
        try{this.entryTickets[1].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in GoEastAtCenter:"+e.getMessage());}
        break;

    }
  }

}


/*

class CrossoverArea$East(POSTransform transform) extends GoStraight {
  super(CrossoverScene.cT.STACK_WIDTH_INCL);
}

*/