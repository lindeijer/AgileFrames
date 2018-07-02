package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendLeft;
import com.agileways.forces.maneuver.CircularBendRight;
import com.agileways.forces.maneuver.SCurveRight;
import com.agileways.forces.maneuver.SCurveLeft;

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
 * The move that leads from the center westwards to the parkingplace.
 * This move is proceeded by LeaveStackForCenterWest or GoWestAtCenter and
 * followed by EnterTurntable.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class ParkWest extends MoveImplBase{
  private float ticket0aFree=0;
  private float ticket0Free=0;
  private float ticket1Free=0;

  /**
   * @param parkNr     destination park-place: 2 <= parkNr < numberOfParks-3
   * @param parkLane   destination park-lane:  0 <= parkLane < lanesPerPark
   *
   * @throws RemoteException
   */
  public ParkWest(int parkNr, int parkLane) throws java.rmi.RemoteException {
    if (parkNr <= CrossoverScene.cT.numberOfStacks-4) {
      // Create transform to determine starting location
      float x = CrossoverScene.cT.FREE_TERMINAL_LEFT + (parkNr + 2) * CrossoverScene.cT.STACK_WIDTH_INCL + CrossoverScene.cT.FREE_SPACE_AT_SIDES + CrossoverScene.cT.WIDTH_DRIVING_LANE;
      float y = CrossoverScene.cT.getStackLaneY() - CrossoverScene.cT.FREE_STACK_EXIT - CrossoverScene.cT.WIDTH_CENTER_LANE;
      float alpha = (float)Math.PI;
      Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);

      // Create composedMove
      float maxLength =3*CrossoverScene.cT.WIDTH_CENTER_LANE + CrossoverScene.cT.FREE_PARK_ENTRANCE - CrossoverScene.cT.TURN_RADIUS;
      float width = x - CrossoverScene.cT.STACK_WIDTH_INCL/2 - CrossoverScene.cT.getParkLaneX(parkNr,parkLane);
      float length = (float) Math.sqrt(4*width*(maxLength-CrossoverScene.cT.FREE_PARK_ENTRANCE) - width*width);
      //////
      // To get radius of an SCurve: Radius=0.25*(Width+Length^2/Width)
      // in this case width and radius are known and length is being calculated above
      // if length> width => an SCurve will be driven, otherwise two CircularBends
      //////

      Trajectory[] composedTrajectory;
      if (length>width) {composedTrajectory = new Trajectory[4];}else{composedTrajectory = new Trajectory[6];}
      composedTrajectory[0] = new GoStraight(CrossoverScene.cT.STACK_WIDTH_INCL/2-CrossoverScene.cT.TURN_RADIUS);
      composedTrajectory[0].setTransform(new POSTransform(0,0,0));
      composedTrajectory[1] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS,CrossoverScene.cT.AGV_TURNING_POINT);
      composedTrajectory[1].append(composedTrajectory[0],null);
      ticket0aFree = composedTrajectory[0].domain+composedTrajectory[1].domain+CrossoverScene.cT.AGV_LENGTH/2;
      if (length>width) {
        composedTrajectory[2] = new SCurveRight(length,width);
        composedTrajectory[2].append(composedTrajectory[1],null);
        ticket0Free = composedTrajectory[0].domain+composedTrajectory[1].domain+composedTrajectory[2].domain;
        ticket1Free = composedTrajectory[0].domain+composedTrajectory[1].domain+composedTrajectory[2].domain;
        composedTrajectory[3] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE + maxLength - length);
        composedTrajectory[3].append(composedTrajectory[2],null);
      } else {
        composedTrajectory[2] = new CircularBendRight((maxLength-CrossoverScene.cT.FREE_PARK_ENTRANCE),CrossoverScene.cT.AGV_TURNING_POINT);
        composedTrajectory[2].append(composedTrajectory[1],null);
        ticket0Free = composedTrajectory[0].domain+composedTrajectory[1].domain+composedTrajectory[2].domain;
        composedTrajectory[3] = new GoStraight(width - maxLength);
        composedTrajectory[3].append(composedTrajectory[2],null);
        composedTrajectory[4] = new CircularBendLeft(CrossoverScene.cT.FREE_PARK_ENTRANCE,CrossoverScene.cT.AGV_TURNING_POINT);
        composedTrajectory[4].append(composedTrajectory[3],null);
        ticket1Free = ticket0Free+composedTrajectory[3].domain+composedTrajectory[4].domain;
        composedTrajectory[5] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
        composedTrajectory[5].append(composedTrajectory[4],null);
      }

      this.trajectory = new Trajectory(composedTrajectory);
      this.trajectory.setTransform(transform);
    }// if
    else {
      this.trajectory = null;
      }
  }

  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    if (this.trajectory != null) {
      Move.EvolutionRule finishing =
        new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH  , 0);//notify
      Move.EvolutionRule ticket0aFreeRule =
        new Move.EvolutionRule(this, trajectory, ticket0aFree                                  , 1); //center1
      Move.EvolutionRule ticket0FreeRule =
        new Move.EvolutionRule(this, trajectory, ticket0Free                                   , 2); //center2
      Move.EvolutionRule beyond =
        new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2, 3); //parklane & totalPark
      Move.EvolutionRule ticket1FreeRule =
        new Move.EvolutionRule(this, trajectory, ticket1Free                                   , 4); //parkEntrance
      this.rules = new Rule[] {
        finishing,
        ticket0aFreeRule,
        ticket0FreeRule,
        beyond,
        ticket1FreeRule
      };
    }
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] (centerNorth)
   *                  2 = free entryTickets[1] (centerSouth)
   *                  3 = free entryTickets[2] & entryTickets[3] (parkLane and totalPark)
   *                  4 = free entryTickets[4]...entryTickets[3+lanesPerPark] (parkEntrance)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0://notify
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1:// center1
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in ParkWest:"+e.getMessage());}
        break;
      case 2:// center2
        try{this.entryTickets[1].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in ParkWest:"+e.getMessage());}
        break;
      case 3://parklane and totalpark
        try{
          entryTickets[2].free();//parkLane
          // entryTickets[3].free();//total park, is freed after selection in super action
        }
        catch(Exception e) {System.out.println("Exception while entryTickets[3].free() and entryTickets[4].free() in ParkWest:"+e.getMessage());}
        break;
      case 4:// parkEntrance
        try{
         for (int lane=0; lane<CrossoverScene.cT.lanesPerPark; lane++) {
            entryTickets[4+lane].free();
        } }
        catch(Exception e) {System.out.println("Exception while entryTickets[2].free() in ParkWest:"+e.getMessage());}
        break;
    }
  }

}