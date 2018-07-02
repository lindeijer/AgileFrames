package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.Spin;

import net.agileframes.forces.space.Position;
import net.agileframes.forces.space.POS;
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
 * The move that simulates the turning on the turntable. This move should be created dynamically as
 * the turntable can change position. This move is made for temporary use only and should be
 * deleted as soon as a turntable-machine is introduced.
 *
 * @author Wierenga
 * @version 0.0.1
 */
public class SpinAtCurrentPosition extends MoveImplBase{

   /**
   * @param angle      a double indicating the total spin-angle
   * @param position   a POS-object containing the position where should be spinned.
   *
   * @throws RemoteException
   */
  public SpinAtCurrentPosition(double angle, POS position) throws java.rmi.RemoteException {
    // Create transform to determine starting location
    Transform transform = new POSTransform(new Position(position.x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           position.y*CrossoverScene.scale+CrossoverScene.yTrans), position.alpha,
                                           new Position(CrossoverScene.scale, CrossoverScene.scale));
    // Create composedTrajectory
    Trajectory[] composedTrajectory = new Trajectory[1];
    composedTrajectory[0] = new Spin(angle);
    composedTrajectory[0].setTransform(new POSTransform(new Position(0,0),0,new Position(1,1)));

    this.trajectory = new Trajectory(composedTrajectory);
    this.trajectory.setTransform(transform);
    this.trajectory.obstacleAtEnd = false;
  }

  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory, trajectory.domain-0.01f, 0)
    };
  }

   /**
   * Executes the actions belonging to the rules that are defined on this object. Events are triggered when
   * the evolution reaches a certain value.
   *
   * @param eventID   number that identifies the kind of event; EVOLUTION_EVENT = 1, TERMINATING_EVENT = 2
   * @param seqNum    sequence number telling which event to execute:
   *                  0 = notify
   */
  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case 0:
        notified = true;
        synchronized(this) {this.notify();}
        break;
    }
  }


}