package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendRight;
import com.agileways.forces.maneuver.CircularBendLeft;
import com.agileways.forces.maneuver.Spin;

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
 * The move that starts at the parkplace and goes to the selected turntable. This move should be created
 * dynamically as turntable can change from position. This move is proceeded by LeaveStackForParking,
 * ParkEast or ParkWest and followed by LeaveTurntable.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class EnterTurntable extends MoveImplBase{
  public float bestAlpha = (float)(Math.PI/2);
   /**
   * @param parkNr         origin park-number :             0 <= parkNr < numberOfParks
   * @param parkLane       origin park-lane:                0 <= parkLane < lanesPerPark
   * @param ttableNr       destination turntable-number:    0 <= ttableNr < numberOfTurntables
   *
   * @throws RemoteException
   */
  public EnterTurntable(int parkNr, int parkLane, int ttableNr) throws java.rmi.RemoteException {
    // Create transform to determine starting location
    float x = CrossoverScene.cT.getParkLaneX(parkNr, parkLane);
    float y = CrossoverScene.cT.getParkLaneY();
    float alpha = (float)(-Math.PI/2);
    Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
    // Create composedMove
    Trajectory[] composedTrajectory = new Trajectory[2];
    // calculate angle needed for turn  the problem is solved numeric
    float lastDiff = 999;

    float length = 0;
    float ttableX = CrossoverScene.cT.TTABLE_X[ttableNr]+
                    CrossoverScene.cT.FREE_TERMINAL_LEFT+
                    CrossoverScene.cT.WIDTH_DRIVING_LANE+
                    CrossoverScene.cT.FREE_SPACE_AT_SIDES+
                    CrossoverScene.cT.TTABLE_WIDTH/2+
                    CrossoverScene.cT.STACK_WIDTH_INCL/2;
    for (float alph=0;alph<(float)(Math.PI/2);alph+=0.001f) {
      float dy=(float)(CrossoverScene.cT.FREE_PARK_EXIT+
                       0.5f*CrossoverScene.cT.TTABLE_HEIGHT-
                       CrossoverScene.cT.TURN_RADIUS*Math.sin(alph)
      );
      float dx=(float)(ttableX-x-CrossoverScene.cT.TURN_RADIUS*(1-Math.cos(alph)));
      if (ttableX<x) {
        dx=(float)(-ttableX+x-CrossoverScene.cT.TURN_RADIUS*(1-Math.cos(alph)));
      }
      float diff = (float)( Math.atan(dy/dx) - (Math.PI/2 - alph) );
      if (Math.abs(diff)>lastDiff) {break;}
      lastDiff = Math.abs(diff);
      bestAlpha = alph;
      length = (float)Math.sqrt(dx*dx+dy*dy);
    }

    if (ttableX>x) {
      composedTrajectory[0] = new CircularBendLeft(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT, bestAlpha);
    } else {
      composedTrajectory[0] = new CircularBendRight(CrossoverScene.cT.TURN_RADIUS, CrossoverScene.cT.AGV_TURNING_POINT, bestAlpha);
    }
    composedTrajectory[0].setTransform(new POSTransform(0,0,0));
    composedTrajectory[1] = new GoStraight(length);
    composedTrajectory[1].append(composedTrajectory[0],null);

    this.trajectory = new Trajectory(composedTrajectory);
    this.trajectory.setTransform(transform);
    this.trajectory.obstacleAtEnd = false;
  }

  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // Add Rules
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain - 0.01f, 0),
      new Move.EvolutionRule(this, trajectory, CrossoverScene.cT.FREE_PARK_EXIT+ CrossoverScene.cT.AGV_LENGTH/2, 1),
      // next evolution will be different when SpinAtCurrentPosition is no longer used...
      new Move.EvolutionRule(this, trajectory, trajectory.domain+CrossoverScene.cT.AGV_LENGTH/2+(float)(Math.PI-Math.abs(bestAlpha))*10, 2)
     };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   *                  1 = free entryTickets[0] (parkExit)
   *                  2 = free entryTickets[1] (turntable)
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
      case 1:// parkexit
        try{this.entryTickets[0].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[0].free() in EnterTurntable:"+e.getMessage());}
        break;
      case 2:// tunrtable
        try{this.entryTickets[1].free();}
        catch(Exception e) {System.out.println("Exception while entryTickets[1].free() in EnterTurntable:"+e.getMessage());}
        break;
    }
  }

}