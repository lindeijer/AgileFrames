package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendLeft;
import com.agileways.forces.maneuver.CircularBendRight;

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
 * The move that makes a turn in one of the four corners.
 * This move is proceeded by LeaveTurntable or GoNorth and
 * followed by GoNorth, GoEastAtStack or GoWestAtStack.
 *
 * @author Wierenga
 * @version 0.0.1
 */
public class Turn extends MoveImplBase{
/**
 The move that makes a turn in one of the four corners of the terminal.
 This static move is a 3-dimensional array:
 @param corner:  0 = North-West; 1 = NE; 2 = SE; 3 = SW
 @param lanePosition1: vertical lane-position: 0 = inner-lane; 1 = outer-lane
 @param lanePosition2: horizontal lane-position: 0 = inner-lane; 1 = outer-lane
 */

  /**
   * @param corner          the corner on the terminal:  0 = North-West; 1 = NE; 2 = SE; 3 = SW
   * @param lanePosition1   vertical lane-position: 0 = inner-lane; 1 = outer-lane
   * @param lanePosition2   horizontal lane-position: 0 = inner-lane; 1 = outer-lane
   *
   * @throws RemoteException
   */
  public Turn(int corner, int lanePosition1, int lanePosition2) throws java.rmi.RemoteException {
    // Create transform to determine starting location
    float x,y;
    float alpha;
    if (corner == 0) {
      x = (1-lanePosition1) * CrossoverScene.cT.WIDTH_DRIVING_LANE + CrossoverScene.cT.FREE_TERMINAL_LEFT;
      alpha = (float)(Math.PI/2);
    } else {
      if (corner == 1) {
        x = CrossoverScene.cT.TOTAL_WIDTH - CrossoverScene.cT.FREE_TERMINAL_RIGHT - (1-lanePosition1) * CrossoverScene.cT.WIDTH_DRIVING_LANE;
        alpha = (float)(Math.PI/2);
      } else {
        if (corner == 2) {
          x = CrossoverScene.cT.TOTAL_WIDTH - CrossoverScene.cT.FREE_TERMINAL_RIGHT - CrossoverScene.cT.WIDTH_DRIVING_LANE - CrossoverScene.cT.TURN_RADIUS;
          alpha = 0;
        } else {// corner=3
          x = CrossoverScene.cT.WIDTH_DRIVING_LANE + CrossoverScene.cT.TURN_RADIUS + CrossoverScene.cT.FREE_TERMINAL_LEFT;
          alpha = (float)(Math.PI);
        }
      }
    }
    if (corner<2) {y = CrossoverScene.cT.DIST_TOP_QUAY - 2 * CrossoverScene.cT.WIDTH_DRIVING_LANE - CrossoverScene.cT.TURN_RADIUS;}
    else{y = CrossoverScene.cT.FREE_QUAY + (2 - lanePosition2) * CrossoverScene.cT.WIDTH_DRIVING_LANE;}

    Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
    // Create composedMove
    Trajectory[] composedTrajectory;
    if (lanePosition1 == lanePosition2) {
      composedTrajectory = new Trajectory[1];
      if ((corner == 0) || (corner == 3)) {
        composedTrajectory[0] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS + lanePosition1 * CrossoverScene.cT.WIDTH_DRIVING_LANE, CrossoverScene.cT.AGV_TURNING_POINT);
      } else {
        composedTrajectory[0] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS + lanePosition1 * CrossoverScene.cT.WIDTH_DRIVING_LANE, CrossoverScene.cT.AGV_TURNING_POINT);
      }
      composedTrajectory[0].setTransform(new POSTransform(0,0,0));
    } else {
      composedTrajectory = new Trajectory[2];
      if (((lanePosition1 == 0) && (corner < 2)) || ((lanePosition1 == 1) && (corner >= 2))) {
        composedTrajectory[0] = new GoStraight(CrossoverScene.cT.WIDTH_DRIVING_LANE);
        composedTrajectory[0].setTransform(new POSTransform(0,0,0));
        if ((corner == 0) || (corner == 3)) {composedTrajectory[1] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);}
        else {composedTrajectory[1] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);}
        composedTrajectory[1].append(composedTrajectory[0],null);
      } else {
        if ((corner == 0) || (corner ==3)) {composedTrajectory[0] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);}
        else {composedTrajectory[0] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT);}
        composedTrajectory[0].setTransform(new POSTransform(0,0,0));
        composedTrajectory[1] = new GoStraight(CrossoverScene.cT.WIDTH_DRIVING_LANE);
        composedTrajectory[1].append(composedTrajectory[0],null);
      }
    }
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
      new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2-0.01f, 1),
      new Move.EvolutionRule(this, trajectory, trajectory.domain + CrossoverScene.cT.AGV_LENGTH/2-0.01f, 2)
    };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] (turn1)
   *                  2 = free entryTickets[1] (turn2) (if existent)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1:
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in Turn:"+e.getMessage());}
        break;
      case 2:
        try{if ((entryTickets.length>1) && (entryTickets[1]!=null)) {this.entryTickets[1].free();}}
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in Turn:"+e.getMessage());}
        break;
    }
  }

}