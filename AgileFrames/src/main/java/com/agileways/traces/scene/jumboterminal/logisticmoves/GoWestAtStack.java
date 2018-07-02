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
 * The move that goes one position further westwards at the top-lane so that it ends
 * above the stack and lane specified in the constructor. This move is proceeded by another
 * GoWestAtStack or a Turn and followed by another GoWestAtStack or an EnterStackEast.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class GoWestAtStack extends MoveImplBase{
   /**
   *
   * @param stackNr         destination stack-number:  0 <= stackNr < numberOfStacks
   * @param stackLane       destination stack-lane:    0 <= stackLane < lanesPerStack
   * @param lanePosition    lane-position at toplane (innerlane = 0; outerlane = 1)
   *
   * @throws RemoteException
   */
  public GoWestAtStack(int stackNr, int stackLane, int lanePosition) throws java.rmi.RemoteException {
    float x;
    if ((stackNr==CrossoverScene.cT.numberOfStacks-1) && (stackLane==CrossoverScene.cT.lanesPerStack-1)) {
      x = CrossoverScene.cT.TOTAL_WIDTH - CrossoverScene.cT.FREE_TERMINAL_RIGHT -
          CrossoverScene.cT.TURN_RADIUS - CrossoverScene.cT.WIDTH_DRIVING_LANE;
    } else {
      if (stackLane==CrossoverScene.cT.lanesPerStack-1) {
        x = CrossoverScene.cT.getStacklaneX(stackNr+1,0)+ CrossoverScene.cT.TURN_RADIUS;
      } else {
        x = CrossoverScene.cT.getStacklaneX(stackNr,stackLane+1)+ CrossoverScene.cT.TURN_RADIUS;
    } }
    float y = CrossoverScene.cT.DIST_TOP_QUAY - (2 - lanePosition) * CrossoverScene.cT.WIDTH_DRIVING_LANE;
    float alpha = (float)Math.PI;
    Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
    // Create composedMove
    Trajectory[] composedTrajectory = new Trajectory[1];
    float length = -CrossoverScene.cT.getStacklaneX(stackNr,stackLane) - CrossoverScene.cT.TURN_RADIUS + x;
    composedTrajectory[0] = new GoStraight(length);
    composedTrajectory[0].setTransform(new POSTransform(0,0,0));

    this.trajectory = new Trajectory(composedTrajectory);
    this.trajectory.setTransform(transform);
  }

  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // watch out! will be overloaded by TopToStack!!
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0)
    };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  default = free entryTickets[seqNum-1]
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      default:
        try{this.entryTickets[seqNum-1].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets["+(seqNum-1)+"].free() in GoWestAtStack:"+e.getMessage());}
        break;
    }
  }

}