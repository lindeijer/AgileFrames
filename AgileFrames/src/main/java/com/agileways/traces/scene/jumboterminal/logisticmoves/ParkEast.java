package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.SCurveRight;
import com.agileways.forces.maneuver.CircularBendRight;
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
 * The move that leads from the center eastwards to the parkingplace.
 * This move is proceeded by LeaveStackForCenterEast or GoEastAtCenter and
 * followed by EnterTurntable.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class ParkEast extends MoveImplBase{
  private float ticket0Free, ticket1Free;
  /**
   * @param parkNr     destination park-place: 2 <= parkNr < numberOfParks
   * @param parkLane   destination park-lane:  0 <= parkLane < lanesPerPark
   *
   * @throws RemoteException
   */
  public ParkEast(int parkNr, int parkLane) throws java.rmi.RemoteException {
    if (parkNr > 1) {
      // Create transform to determine starting location
      float x = CrossoverScene.cT.FREE_TERMINAL_LEFT + parkNr * CrossoverScene.cT.STACK_WIDTH_INCL + CrossoverScene.cT.FREE_SPACE_AT_SIDES+ CrossoverScene.cT.WIDTH_DRIVING_LANE;
      float y = CrossoverScene.cT.DIST_TOP_QUAY - CrossoverScene.cT.DIST_TOP_STACK - CrossoverScene.cT.LENGTH_STACKLANE -
                CrossoverScene.cT.DIST_STACK_CENTER - CrossoverScene.cT.WIDTH_CENTER_LANE;
      float alpha = 0;
      Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);

      // Create composedMove
      Trajectory[] composedTrajectory;
      float length = (CrossoverScene.cT.getParkLaneX(parkNr,parkLane) - CrossoverScene.cT.FREE_PARK_ENTRANCE)
                      -(x + 3.3168f*CrossoverScene.cT.WIDTH_CENTER_LANE);
      ////////////
      /// if length>0 then there is enough space left to make a crab-movement before turning into the
      /// parklane. Else a wide circularbendright should be performed directly.
      ////////////

      if (length>0) {
        composedTrajectory = new Trajectory[4];
        composedTrajectory[0] = new CrabRight(CrossoverScene.cT.WIDTH_CENTER_LANE);
        composedTrajectory[0].setTransform(new POSTransform(0,0,0));
        composedTrajectory[1] = new GoStraight(length);
        composedTrajectory[1].append(composedTrajectory[0],null);
        composedTrajectory[2] = new CircularBendRight(CrossoverScene.cT.FREE_PARK_ENTRANCE, CrossoverScene.cT.AGV_TURNING_POINT);
        composedTrajectory[2].append(composedTrajectory[1],null);
        ticket1Free = composedTrajectory[0].domain +composedTrajectory[1].domain+composedTrajectory[2].domain;
        composedTrajectory[3] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
        composedTrajectory[3].append(composedTrajectory[2],null);
      } else {
        composedTrajectory = new Trajectory[3];
        composedTrajectory[0] = new GoStraight(CrossoverScene.cT.getParkLaneX(parkNr,parkLane)-x - (CrossoverScene.cT.WIDTH_CENTER_LANE+CrossoverScene.cT.FREE_PARK_ENTRANCE));
        composedTrajectory[0].setTransform(new POSTransform(0,0,0));
        composedTrajectory[1] = new CircularBendRight(CrossoverScene.cT.WIDTH_CENTER_LANE+CrossoverScene.cT.FREE_PARK_ENTRANCE ,CrossoverScene.cT.AGV_TURNING_POINT);
        composedTrajectory[1].append(composedTrajectory[0],null);
        ticket1Free = composedTrajectory[0].domain +composedTrajectory[1].domain;
        composedTrajectory[2] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
        composedTrajectory[2].append(composedTrajectory[1],null);
      }

      this.trajectory = new Trajectory(composedTrajectory);
      this.trajectory.setTransform(transform);
    }// if
    else { this.trajectory = null;}
  }
  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0),
      new Move.EvolutionRule(this, trajectory, ticket1Free, 1),
      new Move.EvolutionRule(this, trajectory, ticket1Free, 3),
      new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2, 2)
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
   *                  2 = free entryTickets[1] & entryTickets[2] (parkLane and totalPark)
   *                  3 = free entryTickets[3]...entryTickets[2+lanesPerPark] (parkEntrance)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1://Center
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in ParkEast:"+e.getMessage());}
        break;
      case 2://park
        try{
          entryTickets[1].free();//parkLane
          // entryTickets[2].free();//total park
        }
        catch(Exception e) {System.out.println("Exception while entryTickets[2].free() and entryTickets[3].free() in ParkEast:"+e.getMessage());}
        break;
      case 3://parkEntrance
        try{
          for (int lane=0; lane<CrossoverScene.cT.lanesPerPark; lane++) {
            entryTickets[3+lane].free();
        } }
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in ParkEast:"+e.getMessage());}
        break;
    }
  }

}