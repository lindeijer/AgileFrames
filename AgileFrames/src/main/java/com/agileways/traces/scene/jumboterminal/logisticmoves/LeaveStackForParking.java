package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendLeft;
import com.agileways.forces.maneuver.CircularBendRight;
import com.agileways.forces.maneuver.SCurveLeft;
import com.agileways.forces.maneuver.SCurveRight;

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
 * The move that leads from the stack through the center to the parkingplace.
 * This move is proceeded by EnterStackEast or EnterStackWest and
 * followed by EnterTurntable.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class LeaveStackForParking extends MoveImplBase{
  // evolution-values for freeing entryTickets
  private float stackExit1Free = 0;// ticket0Free
  private float stackExit2Free = 0;// ticket1Free
  private float centerNorthFree = 0;// ticket2aFree
  private float centerSouthFree = 0;// ticket2Free
  private float parkEntranceFree = 0;// ticket3Free

  /**
   * @param stackNr    original stack-number:  0 <= stackNr < numberOfStacks
   * @param stackLane  original stack-lane:    0 <= stackLane < lanesPerStack
   * @param parkNr     destination park-place: stackNr-2 <= parkNr <= stackNr+1
   * @param parkLane   destination park-lane:  0 <= parkLane < lanesPerPark
   *
   * @throws RemoteException
   */
  public LeaveStackForParking(int stackNr, int stackLane, int parkNr, int parkLane) throws java.rmi.RemoteException {
    if ((parkNr==stackNr) || (parkNr==stackNr-1) || (parkNr==stackNr-2) || (parkNr==stackNr+1)){
      // Create transform to determine starting location
      float x = CrossoverScene.cT.getStacklaneX(stackNr,stackLane);
      float y = CrossoverScene.cT.getStackLaneY();
      float alpha = (float)(-Math.PI/2);
      Trajectory[] composedTrajectory = null;
      Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
      //////// go straight through center:
      if ((parkNr==stackNr) || (parkNr==stackNr-1)) {
        // Create composedMove
        float width1 = (1 + (float)(CrossoverScene.cT.lanesPerStack - 1)/2 - (stackLane + 1)) * CrossoverScene.cT.WIDTH_STACKLANE;
        float width2;
        if (stackNr == parkNr) {width2 = CrossoverScene.cT.STACK_WIDTH_INCL/2 - CrossoverScene.cT.PARK_WIDTH_EXCL/2 +
                                        parkLane * CrossoverScene.cT.WIDTH_PARKLANE;}
        else  {width2 = - CrossoverScene.cT.STACK_WIDTH_INCL/2 - CrossoverScene.cT.PARK_WIDTH_EXCL/2 +
                         parkLane * CrossoverScene.cT.WIDTH_PARKLANE;}
        float maxLength2 =2*CrossoverScene.cT.WIDTH_CENTER_LANE + CrossoverScene.cT.FREE_PARK_ENTRANCE;
        float length2 = (float) Math.sqrt(4*Math.abs(width2)*(maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE) - width2*width2);
        //////
        // To get radius of an SCurve: Radius=0.25*(Width+Length^2/Width)
        // in this case width and radius are known and length is being calculated above
        // if length> width => an SCurve will be driven, otherwise two CircularBends
        //////

        if (length2 > Math.abs(width2)) {composedTrajectory = new Trajectory[3];}
        else {composedTrajectory = new Trajectory[5];}

        if (width1>0) {composedTrajectory[0] = new SCurveLeft(CrossoverScene.cT.DIST_STACK_CENTER,width1);}
        else {
          if (width1<0) {composedTrajectory[0] = new SCurveRight(CrossoverScene.cT.DIST_STACK_CENTER,-width1);}
          else {composedTrajectory[0] = new GoStraight(CrossoverScene.cT.DIST_STACK_CENTER);}
        }
        composedTrajectory[0].setTransform(new POSTransform(0,0,0));
        centerNorthFree = composedTrajectory[0].domain;
        stackExit2Free = composedTrajectory[0].domain/2;
        if (length2>Math.abs(width2)) {
          if (width2>0) {composedTrajectory[1] = new SCurveLeft(length2,width2);}
          else {composedTrajectory[1] = new SCurveRight(length2,-width2);}
          composedTrajectory[1].append(composedTrajectory[0],null);
          centerSouthFree = composedTrajectory[1].domain * (CrossoverScene.cT.WIDTH_CENTER_LANE*2/length2)  + composedTrajectory[0].domain;
          composedTrajectory[2] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE + maxLength2 - length2);
          composedTrajectory[2].append(composedTrajectory[1],null);
          parkEntranceFree = composedTrajectory[0].domain+composedTrajectory[1].domain+composedTrajectory[2].domain;
        } else {
          if (width2>0) {
             composedTrajectory[1] = new CircularBendLeft((maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE),CrossoverScene.cT.AGV_TURNING_POINT);
             centerSouthFree = composedTrajectory[1].domain + composedTrajectory[0].domain;
             composedTrajectory[1].append(composedTrajectory[0],null);
             composedTrajectory[2] = new GoStraight(width2 - maxLength2);
             composedTrajectory[2].append(composedTrajectory[1],null);
             composedTrajectory[3] = new CircularBendRight(CrossoverScene.cT.FREE_PARK_ENTRANCE,CrossoverScene.cT.AGV_TURNING_POINT);
             composedTrajectory[3].append(composedTrajectory[2],null);
             parkEntranceFree = centerSouthFree+composedTrajectory[3].domain+composedTrajectory[2].domain;
             composedTrajectory[4] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
             composedTrajectory[4].append(composedTrajectory[3],null);
           } else {
             composedTrajectory[1] = new CircularBendRight((maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE),CrossoverScene.cT.AGV_TURNING_POINT);
             centerSouthFree = composedTrajectory[1].domain + composedTrajectory[0].domain;
             composedTrajectory[1].append(composedTrajectory[0],null);
             composedTrajectory[2] = new GoStraight(-width2 - maxLength2);
             composedTrajectory[2].append(composedTrajectory[1],null);
             composedTrajectory[3] = new CircularBendLeft(CrossoverScene.cT.FREE_PARK_ENTRANCE,CrossoverScene.cT.AGV_TURNING_POINT);
             composedTrajectory[3].append(composedTrajectory[2],null);
             parkEntranceFree = centerSouthFree + composedTrajectory[3].domain + composedTrajectory[2].domain;
             composedTrajectory[4] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
             composedTrajectory[4].append(composedTrajectory[3],null);
           }
         }
       }// end if parkNr=stackNr or parkNr==stackNr-1
       else {
         if (parkNr==stackNr-2) {//// first go right, then cross center:
           float width2 = CrossoverScene.cT.STACK_WIDTH_INCL/2+ CrossoverScene.cT.PARK_WIDTH_EXCL/2- parkLane* CrossoverScene.cT.WIDTH_PARKLANE;
           float maxLength2 =2*CrossoverScene.cT.WIDTH_CENTER_LANE + CrossoverScene.cT.FREE_PARK_ENTRANCE;
           float length2 = (float) Math.sqrt(4*Math.abs(width2)*(maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE) - width2*width2);

           if (length2 > width2) {composedTrajectory = new Trajectory[6];}
           else {composedTrajectory = new Trajectory[8];}

           composedTrajectory[0] = new CircularBendRight(CrossoverScene.cT.FREE_STACK_EXIT,CrossoverScene.cT.AGV_TURNING_POINT);
           composedTrajectory[0].setTransform(new POSTransform(0,0,0));
           composedTrajectory[1] = new GoStraight(CrossoverScene.cT.STACK_WIDTH_INCL/2+
                    CrossoverScene.cT.FREE_SPACE_BETWEEN_STACKS + stackLane * CrossoverScene.cT.WIDTH_STACKLANE
                    - CrossoverScene.cT.FREE_STACK_EXIT - CrossoverScene.cT.TURN_RADIUS);
           composedTrajectory[1].append(composedTrajectory[0],null);
           stackExit1Free = composedTrajectory[0].domain + composedTrajectory[1].domain;
           composedTrajectory[2] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS,CrossoverScene.cT.AGV_TURNING_POINT);
           composedTrajectory[2].append(composedTrajectory[1],null);
           composedTrajectory[3] = new GoStraight(2*CrossoverScene.cT.WIDTH_CENTER_LANE - CrossoverScene.cT.TURN_RADIUS);
           composedTrajectory[3].append(composedTrajectory[2],null);
           stackExit2Free = stackExit1Free + composedTrajectory[3].domain + composedTrajectory[2].domain;
           centerNorthFree = stackExit2Free;
           if (length2>width2) {
             composedTrajectory[4] = new SCurveRight(length2,width2);
             composedTrajectory[4].append(composedTrajectory[3],null);
             centerSouthFree = stackExit2Free + composedTrajectory[4].domain* (2*CrossoverScene.cT.WIDTH_CENTER_LANE/length2);
             composedTrajectory[5] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE + maxLength2 - length2);
             parkEntranceFree = stackExit2Free + composedTrajectory[5].domain + composedTrajectory[4].domain;
             composedTrajectory[5].append(composedTrajectory[4],null);
           } else {
             composedTrajectory[4] = new CircularBendRight((maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE),CrossoverScene.cT.AGV_TURNING_POINT);
             composedTrajectory[4].append(composedTrajectory[3],null);
             centerSouthFree = stackExit2Free + composedTrajectory[4].domain;
             composedTrajectory[5] = new GoStraight(width2 - maxLength2);
             composedTrajectory[5].append(composedTrajectory[4],null);
             composedTrajectory[6] = new CircularBendLeft(CrossoverScene.cT.FREE_PARK_ENTRANCE,CrossoverScene.cT.AGV_TURNING_POINT);
             composedTrajectory[6].append(composedTrajectory[5],null);
             composedTrajectory[7] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
             parkEntranceFree = centerSouthFree + composedTrajectory[5].domain + composedTrajectory[6].domain+ composedTrajectory[7].domain;
             composedTrajectory[7].append(composedTrajectory[6],null);
           }
         }//end if parkNr==stackNr-2
         else {//// first go left, then cross center:
           float width2 = CrossoverScene.cT.STACK_WIDTH_INCL/2 - CrossoverScene.cT.PARK_WIDTH_EXCL/2 +parkLane * CrossoverScene.cT.WIDTH_PARKLANE;
           float maxLength2 =2*CrossoverScene.cT.WIDTH_CENTER_LANE + CrossoverScene.cT.FREE_PARK_ENTRANCE;
           float length2 = (float) Math.sqrt(4*Math.abs(width2)*(maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE) - width2*width2);

           if (length2 > width2) {composedTrajectory = new Trajectory[6];}
           else {composedTrajectory = new Trajectory[8];}

           composedTrajectory[0] = new CircularBendLeft(CrossoverScene.cT.FREE_STACK_EXIT,CrossoverScene.cT.AGV_TURNING_POINT);
           composedTrajectory[0].setTransform(new POSTransform(0,0,0));
           composedTrajectory[1] = new GoStraight(CrossoverScene.cT.STACK_WIDTH_INCL/2+
                    CrossoverScene.cT.FREE_SPACE_BETWEEN_STACKS +
                   (CrossoverScene.cT.lanesPerStack-stackLane-1) * CrossoverScene.cT.WIDTH_STACKLANE
                   - CrossoverScene.cT.FREE_STACK_EXIT - CrossoverScene.cT.TURN_RADIUS);
           composedTrajectory[1].append(composedTrajectory[0],null);
           stackExit1Free = composedTrajectory[0].domain + composedTrajectory[1].domain;
           composedTrajectory[2] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS,CrossoverScene.cT.AGV_TURNING_POINT);
           composedTrajectory[2].append(composedTrajectory[1],null);
           composedTrajectory[3] = new GoStraight(2*CrossoverScene.cT.WIDTH_CENTER_LANE - CrossoverScene.cT.TURN_RADIUS);
           composedTrajectory[3].append(composedTrajectory[2],null);
           stackExit2Free = stackExit1Free + composedTrajectory[2].domain + composedTrajectory[3].domain;
           centerNorthFree = stackExit2Free;
           if (length2>width2) {
             composedTrajectory[4] = new SCurveLeft(length2,width2);
             composedTrajectory[4].append(composedTrajectory[3],null);
             centerSouthFree = stackExit2Free + composedTrajectory[4].domain* (2*CrossoverScene.cT.WIDTH_CENTER_LANE/length2);
             composedTrajectory[5] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE + maxLength2 - length2);
             composedTrajectory[5].append(composedTrajectory[4],null);
             parkEntranceFree = stackExit2Free + composedTrajectory[4].domain + composedTrajectory[5].domain;
           } else {
             composedTrajectory[4] = new CircularBendLeft((maxLength2-CrossoverScene.cT.FREE_PARK_ENTRANCE),CrossoverScene.cT.AGV_TURNING_POINT);
             composedTrajectory[4].append(composedTrajectory[3],null);
             centerSouthFree = stackExit2Free + composedTrajectory[4].domain;
             composedTrajectory[5] = new GoStraight(width2 - maxLength2);
             composedTrajectory[5].append(composedTrajectory[4],null);
             composedTrajectory[6] = new CircularBendRight(CrossoverScene.cT.FREE_PARK_ENTRANCE,CrossoverScene.cT.AGV_TURNING_POINT);
             composedTrajectory[6].append(composedTrajectory[5],null);
             composedTrajectory[7] = new GoStraight(CrossoverScene.cT.LENGTH_PARKLANE);
             composedTrajectory[7].append(composedTrajectory[6],null);
             parkEntranceFree = centerSouthFree + composedTrajectory[5].domain + composedTrajectory[6].domain+ composedTrajectory[7].domain;
           }
         }//end else (parkNr==stackNr+1)
       }// end else
       this.trajectory = new Trajectory(composedTrajectory);
       this.trajectory.setTransform(transform);
     }// if
     else {this.trajectory = null;}
  }

  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // Add Rules
    if (trajectory==null) {System.out.println("trajectory=null");}
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH   , 0),//notify
      new Move.EvolutionRule(this, trajectory, centerNorthFree                                , 1),//free center1
      new Move.EvolutionRule(this, trajectory, centerSouthFree                                , 2),//free center2
      new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2 , 3),//parkLane and totalPark
      new Move.EvolutionRule(this, trajectory, parkEntranceFree - CrossoverScene.cT.AGV_LENGTH/2- 0.01f, 4),//free parkEntrance
      new Move.EvolutionRule(this, trajectory, stackExit2Free                                 , 5)//free stackExit
    };
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
   *                  5 = free entryTickets[4+lanesPerPark]...entryTickets[length-1] (stackExit)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0://notify
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1://center1
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in LeaveStackForParking:"+e.getMessage());}
        break;
      case 2://center2
        try{this.entryTickets[1].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[2].free() in LeaveStackForParking:"+e.getMessage());}
        break;
      case 3://parklane
        try{
          entryTickets[2].free();//parklane
          entryTickets[3].free();//total park
        }
        catch(Exception e) {System.out.println("Exception while entryTickets[2]&[3].free() in LeaveStackForParking:"+e.getMessage());}
        break;
      case 4://parkentrance
        try{
          for (int lane=0; lane<CrossoverScene.cT.lanesPerPark; lane++) {
            entryTickets[4+lane].free();}
          }
        catch(Exception e) {System.out.println("Exception while entryTickets[4].free() in LeaveStackForParking:"+e.getMessage());}
        break;
      case 5://stackExit
        try{
          for (int index=4+CrossoverScene.cT.lanesPerPark; index<entryTickets.length; index++) {
            if (entryTickets[index]!=null) {entryTickets[index].free();}
          }
        }
        catch(Exception e) {System.out.println("Exception while entryTickets[5].free() in LeaveStackForParking:"+e.getMessage());}
        break;
    }
  }

}