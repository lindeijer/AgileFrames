package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendRight;
import com.agileways.forces.maneuver.CircularBendLeft;

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
 * The move that starts at the turntable and leads via the quaylane to the south-east or the
 * south-west corner. This move should be created dynamically as turntable can change from position.
 * This move is proceeded by EnterTurnTable and followed by Turn.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class LeaveTurntable extends MoveImplBase{
   /**
   * @param ttableNr        origin turntable-number:    0 <= ttableNr < numberOfTurntables
   * @param turnDirection   direction of the move: 0 = counterclockwise; 1 = clockwise
   * @param lanePosition    lane-position on the quay-lane: 0 = inner-lane; 1 = outerlane
   *
   * @throws RemoteException
   */
  public LeaveTurntable(int ttableNr, int turnDirection, int lanePosition) throws java.rmi.RemoteException {
    // Create transform to determine starting location
    float x = CrossoverScene.cT.TTABLE_X[ttableNr]+CrossoverScene.cT.FREE_TERMINAL_LEFT+CrossoverScene.cT.WIDTH_DRIVING_LANE+CrossoverScene.cT.FREE_SPACE_AT_SIDES+CrossoverScene.cT.TTABLE_WIDTH/2+CrossoverScene.cT.STACK_WIDTH_INCL/2;
    float y = CrossoverScene.cT.getParkLaneY()-CrossoverScene.cT.FREE_PARK_EXIT-CrossoverScene.cT.TTABLE_HEIGHT/2;
    float alpha = (float)(-Math.PI/2);
    Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
    // Create composedMove
    Trajectory[] composedTrajectory = new Trajectory[3];
    composedTrajectory[0] = new GoStraight(y - CrossoverScene.cT.FREE_QUAY - (2-lanePosition) * CrossoverScene.cT.WIDTH_DRIVING_LANE -
                                               CrossoverScene.cT.TURN_RADIUS);
    composedTrajectory[0].setTransform(new POSTransform(0,0,0));
    if (turnDirection == 0) {
      composedTrajectory[1] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);
      composedTrajectory[1].append(composedTrajectory[0],null);
      composedTrajectory[2] = new GoStraight(CrossoverScene.cT.TOTAL_WIDTH - CrossoverScene.cT.FREE_TERMINAL_RIGHT -
                                             CrossoverScene.cT.WIDTH_DRIVING_LANE - 2 * CrossoverScene.cT.TURN_RADIUS -
                                             x - CrossoverScene.cT.AGV_LENGTH/2);
      composedTrajectory[2].append(composedTrajectory[1],null);
    }
    else {
      composedTrajectory[1] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);
      composedTrajectory[1].append(composedTrajectory[0],null);
      composedTrajectory[2] = new GoStraight(x - CrossoverScene.cT.WIDTH_DRIVING_LANE - 2 * CrossoverScene.cT.TURN_RADIUS- CrossoverScene.cT.FREE_TERMINAL_LEFT);
      composedTrajectory[2].append(composedTrajectory[1],null);
    }

    this.trajectory = new Trajectory(composedTrajectory);
    this.trajectory.setTransform(transform);
  }

    /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // watch out!! these rules are overloaded in TableToQuay
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0)//,
     };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  default  {if seqNum = odd:} insist entryTickets[(seqNum-1)/2] (quayLane)
   *                           {if seqNum = even:} free  entryTickets[(seqNum-2)/2] (quayLane)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0://notify
        notified = true;
        synchronized(this) {this.notify();}
      break;
      default:
        if ((seqNum % 2)==1) {//odd seqNumbers: insist()
          try{
            entryTickets[(seqNum-1)/2].insist();
          } catch(Exception e) {System.out.println("Exception while entryTickets[].insist() in LeaveTurntable:"+e.getMessage());}
        } else {//even seqNumbers: free()
          try{
            entryTickets[(seqNum-2)/2].free();
          } catch(Exception e) {System.out.println("Exception while entryTickets[].free() in LeaveTurntable:"+e.getMessage());}
        }
      break;
    }
  }

}