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
 * The move that goes one position northwards on one of the sidelanes. This move is proceeded
 * by another GoNorth or Turn and followed by another GoNorth or Turn.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class GoNorth extends MoveImplBase{
   /**
   * @param lanePosition     lane-position  of sidelane (0 = far west; 1 = west; 2 = east; 3 = far east)
   * @param index            index of position on side-lanes  0 <= index < numberOfAgvsAtSidelane
   *
   * @throws RemoteException
   */
  public GoNorth(int lanePosition, int index) throws java.rmi.RemoteException {
    // Create transform to determine starting location
    float x;
    if (lanePosition < 2) {x = lanePosition * CrossoverScene.cT.WIDTH_DRIVING_LANE + CrossoverScene.cT.FREE_TERMINAL_LEFT;}
    else {x = CrossoverScene.cT.TOTAL_WIDTH - (3 - lanePosition) * CrossoverScene.cT.WIDTH_DRIVING_LANE - CrossoverScene.cT.FREE_TERMINAL_RIGHT;}
    float y = CrossoverScene.cT.FREE_QUAY + 2 * CrossoverScene.cT.WIDTH_DRIVING_LANE + CrossoverScene.cT.TURN_RADIUS + index*CrossoverScene.cT.AGV_LENGTH;
    float alpha = (float)(Math.PI/2);
    Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
    // Create composedMove
    Trajectory[] composedTrajectory = new Trajectory[1];
    if (index == CrossoverScene.cT.NUMBER_OF_AGVS_AT_SIDES-1) {
      composedTrajectory[0] = new GoStraight(CrossoverScene.cT.DIST_TOP_QUAY - 4*CrossoverScene.cT.WIDTH_DRIVING_LANE
                                            -2*CrossoverScene.cT.TURN_RADIUS - CrossoverScene.cT.FREE_QUAY
                                            -index*CrossoverScene.cT.AGV_LENGTH);
    } else {
      composedTrajectory[0] = new GoStraight(CrossoverScene.cT.AGV_LENGTH);
    }
    composedTrajectory[0].setTransform(new POSTransform(0,0,0));

    this.trajectory = new Trajectory(composedTrajectory);
    this.trajectory.setTransform(transform);
  }
  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0),
      new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2, 1)
    };
  }
   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] (sideLane)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1:
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in GoNorth:"+e.getMessage());}
        break;
     }
  }

}