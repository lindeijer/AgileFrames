package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendRight;
import com.agileways.forces.maneuver.CrabLeft;

import net.agileframes.forces.space.POSTransform;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Transform;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.traces.Move;
import net.agileframes.traces.SceneImplBase;

import com.agileways.traces.scene.jumboterminal.CrossoverScene;

import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;

import com.agileways.traces.scene.jumboterminal.sceneactions.StackAreaToWest;


/**
 * The move that leads from the stack westwards to the center.
 * This move is preceeded by EnterStackEast or EnterStackWest and
 * followed by ParkWest or GoWestAtCenter.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class LeaveStackForCenterWest extends MoveImplBase {
  private float ticket0Free = 0;
  private float ticket1Free =1;

   /**
   * @param stackNr    original stack-number:  0 <= stackNr < numberOfStacks
   * @param stackLane  original stack-lane:    0 <= stackLane < lanesPerStack
   *
   * @throws RemoteException
   */
  public LeaveStackForCenterWest(int stackNr, int stackLane) throws java.rmi.RemoteException {
    if ((stackNr > 2) && (stackNr<CrossoverScene.cT.numberOfStacks) && (stackLane>=0) && (stackLane<CrossoverScene.cT.lanesPerStack)) {
      // Create transform to determine starting location
      float x = CrossoverScene.cT.getStacklaneX(stackNr, stackLane);
      float y = CrossoverScene.cT.getStackLaneY();
      float alpha = (float)(-Math.PI/2);
      Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
      // Create composedMove
      Trajectory[] composedTrajectory = new Trajectory[4];
      composedTrajectory[0] = new CircularBendRight(CrossoverScene.cT.FREE_STACK_EXIT, CrossoverScene.cT.AGV_TURNING_POINT);
      composedTrajectory[0].setTransform(new POSTransform(0,0,0));

      composedTrajectory[1] = new GoStraight(stackLane * CrossoverScene.cT.WIDTH_STACKLANE+
      CrossoverScene.cT.STACK_WIDTH_INCL -
            CrossoverScene.cT.FREE_STACK_EXIT - 3.3168f * CrossoverScene.cT.WIDTH_CENTER_LANE);

      composedTrajectory[1].append(composedTrajectory[0],null);
      composedTrajectory[2] = new CrabLeft(CrossoverScene.cT.WIDTH_CENTER_LANE);
      composedTrajectory[2].append(composedTrajectory[1],null);
      ticket0Free = composedTrajectory[0].domain +
                    composedTrajectory[1].domain +
                    composedTrajectory[2].domain;

      composedTrajectory[3] = new GoStraight(CrossoverScene.cT.FREE_SPACE_BETWEEN_STACKS);
      composedTrajectory[3].append(composedTrajectory[2],null);
      ticket1Free = ticket0Free +
                    composedTrajectory[3].domain -
                    CrossoverScene.cT.AGV_LENGTH/2;

      this.trajectory = new Trajectory(composedTrajectory);
      this.trajectory.setTransform(transform);
      this.trajectory.obstacleAtEnd = false;
    }
    else { this.trajectory=null;}
  }

  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // Add Rules
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - CrossoverScene.cT.AGV_LENGTH, 0),//notify
      new Move.EvolutionRule(this, trajectory, ticket1Free              , 1),//stayNorth
      new Move.EvolutionRule(this, trajectory, trajectory.domain - 0.1f , 2),//centerNorth
      new Move.EvolutionRule(this, trajectory, ticket0Free              , 3) //stackExit
    };
  }

  public Object finishing = new Object();

  /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] (stayCenterNorth)
   *                  2 = free entryTickets[1] (centerNorth)
   *                  3 = free entryTickets[2]...entryTickets[length-1] (stackExit)
   */
  public synchronized void event(int eventID,int seqNum) {
    StackAreaToWest stackAreaToWest = (StackAreaToWest)this.superSceneAction;
    switch (seqNum) {
      case 0://notify
        notified = true;
        synchronized(this) {this.notify();}
        synchronized(this.finishing) { this.finishing.notify(); }
        break;
      case 1://free stayNorth
        //try{
        //  this.entryTickets[0].free();
        //} catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in LeaveStackForCenterWest:"+e.getMessage());}
        stackAreaToWest.beyondStackArea();
        break;
      case 2://free centerNorth
        //try{this.entryTickets[1].free();}
        //catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in LeaveStackForCenterWest:"+e.getMessage());}
        stackAreaToWest.beyond();
        break;
      case 3://free stackExit
        //try{
        //  for (int index=2; index<entryTickets.length; index++) {
        //    if (entryTickets[index]!=null) {entryTickets[index].free();}
        //  }
        //}
        //catch(Exception e) {System.out.println("Exception while entryTickets[2].free() in LeaveStackForCenterWest:"+e.getMessage());}
        stackAreaToWest.finished();
        break;
    }
  }


}