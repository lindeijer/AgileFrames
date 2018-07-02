package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendLeft;
import com.agileways.forces.maneuver.CircularBendRight;
import com.agileways.forces.maneuver.SCurveLeft;
import com.agileways.forces.maneuver.SCurveRight;
import com.agileways.forces.maneuver.CrabLeft;
import com.agileways.forces.maneuver.CrabRight;

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
 * The move that starts at the toplane and enters the stack from the west. This move is proceeded
 * by GoEastAtStack and followed by LeaveStackForParking, LeaveStackForCenterEast or LeaveStackForCenterWest.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class EnterStackWest extends MoveImplBase{
  private float freeTicket=0;
   /**
   * @param stackNr         destination stack-number :  0 <= stackNr < numberOfStacks
   * @param stackLane       destination stack-lane:     0 <= stackLane < lanesPerStack
   * @param lanePosition    lane-position at toplane (innerlane = 0; outerlane =1)
   *
   * @throws RemoteException
   */
  public EnterStackWest(int stackNr, int stackLane, int lanePosition) throws java.rmi.RemoteException {
    // Create transform to determine starting location
    float x = CrossoverScene.cT.getStacklaneX(stackNr, stackLane) - CrossoverScene.cT.TURN_RADIUS;
    float y = CrossoverScene.cT.DIST_TOP_QUAY - (2 - lanePosition) * CrossoverScene.cT.WIDTH_DRIVING_LANE;
    float alpha = 0;
    Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
    // Create composedMove
    Trajectory[] composedTrajectory = new Trajectory[2];
    composedTrajectory[0] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);
    composedTrajectory[0].setTransform(new POSTransform(0,0,0));
    freeTicket = composedTrajectory[0].domain;
    composedTrajectory[1] = new GoStraight(CrossoverScene.cT.LENGTH_STACKLANE + CrossoverScene.cT.FREE_STACK_ENTRANCE -
        CrossoverScene.cT.TURN_RADIUS + lanePosition * CrossoverScene.cT.WIDTH_DRIVING_LANE);
    composedTrajectory[1].append(composedTrajectory[0],null);

    this.trajectory = new Trajectory(composedTrajectory);
    this.trajectory.setTransform(transform);
  }
  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // watch out!! these rules are overloaded in TopToStack!!
    this.rules = new Rule[] {
      //new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0),
      //new Move.EvolutionRule(this, trajectory, trajectory.domain-0.1f, 2),
      //new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2, 1)
    };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] & entryTickets[1] (totalStack and stack)
   *                  2 = free entryTickets[2] & entryTickets[3] (entrance and crossing-stackLane)
   *                  default = free entryTickets[seqNum+1]      (all stackLanes)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1:// free stack
        try{
          entryTickets[1].free();//stackLane
          entryTickets[0].free();//totalstack
        }
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in EnterStackWest:"+e.getMessage());}
        break;
      case 2:// free entrance (and crossinglane)
        try{this.entryTickets[2].free();
        if (this.entryTickets[3]!=null) {entryTickets[3].free();}
        }
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in EnterStackWest:"+e.getMessage());}
        break;
      default:// free all stackLanes
        try{this.entryTickets[seqNum+1].free();
        }
        catch(Exception e) {System.out.println("Exception while entryTickets["+(seqNum+1)+"].free() in EnterStackWest:"+e.getMessage());}
        break;
    }
  }

}