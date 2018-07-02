package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import com.agileways.forces.maneuver.CircularBendLeft;
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
import com.agileways.traces.scene.jumboterminal.sceneactions.StackAreaToEast;

/**
 * The move that leads from the stack eastwards to the center.
 * This move is proceeded by EnterStackEast or EnterStackWest and
 * followed by ParkEast or GoEastAtCenter.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class LeaveStackForCenterEast extends MoveImplBase{
  private float ticket0Free, beyondStackArea, ticket2Free, beyondCenterNorth;
   /**
   * @param stackNr    original stack-number:  0 <= stackNr < numberOfStacks
   * @param stackLane  original stack-lane:    0 <= stackLane < lanesPerStack
   *
   * @throws RemoteException
   */
  public LeaveStackForCenterEast(int stackNr, int stackLane) throws java.rmi.RemoteException {
    if (stackNr <= CrossoverScene.cT.numberOfStacks-4) {
      // Create transform to determine starting location
      float x = CrossoverScene.cT.getStacklaneX(stackNr, stackLane);
      float y = CrossoverScene.cT.getStackLaneY();
      float alpha = (float)(-Math.PI/2);
      Transform transform = new POSTransform(x*CrossoverScene.scale+CrossoverScene.xTrans,
                                           y*CrossoverScene.scale+CrossoverScene.yTrans, alpha,
                                           CrossoverScene.scale);
      // Create composedMove
      Trajectory[] composedTrajectory = new Trajectory[4];
      composedTrajectory[0] = new CircularBendLeft(CrossoverScene.cT.FREE_STACK_EXIT, CrossoverScene.cT.AGV_TURNING_POINT);
      composedTrajectory[0].setTransform(new POSTransform(0,0,0));
      composedTrajectory[1] = new GoStraight((CrossoverScene.cT.lanesPerStack - stackLane - 1) * CrossoverScene.cT.WIDTH_STACKLANE);
      composedTrajectory[1].append(composedTrajectory[0],null);
      ticket0Free = composedTrajectory[0].domain +
                    composedTrajectory[1].domain;
      composedTrajectory[2] = new SCurveRight((CrossoverScene.cT.STACK_WIDTH_INCL+CrossoverScene.cT.FREE_SPACE_BETWEEN_STACKS
                                              -CrossoverScene.cT.FREE_STACK_EXIT - CrossoverScene.cT.WIDTH_STACKLANE)
                                              ,(3 * CrossoverScene.cT.WIDTH_CENTER_LANE));
      composedTrajectory[2].append(composedTrajectory[1],null);
      beyondStackArea = ticket0Free +
                        composedTrajectory[2].domain/3;
      beyondCenterNorth = ticket0Free +
                          2*composedTrajectory[2].domain/3;
      composedTrajectory[3] = new GoStraight(CrossoverScene.cT.WIDTH_STACKLANE);
      composedTrajectory[3].append(composedTrajectory[2],null);
      ticket2Free = ticket0Free +
                    composedTrajectory[2].domain;

      this.trajectory = new Trajectory(composedTrajectory);
      this.trajectory.setTransform(transform);
      this.trajectory.obstacleAtEnd = false;
    }// if
    else { this.trajectory = null;}
  }
  /**
   * Creates the (evolution) rules belonging to this object, needed for freeing entryTickets
   * and notifying the scene-action.
   */
  public void createRules(){
    // Add Rules
    this.rules = new Rule[] {
      new Move.EvolutionRule(this, trajectory,
            beyondStackArea                                    , 0),
      new Move.EvolutionRule(this, trajectory,
            beyondCenterNorth                                  , 1),
      new Move.EvolutionRule(this, trajectory,
            trajectory.domain - CrossoverScene.cT.AGV_LENGTH/2 , 2),
      new Move.EvolutionRule(this, trajectory,
            trajectory.domain                                  , 3),
      new Move.EvolutionRule(this, trajectory,
            trajectory.domain+CrossoverScene.cT.AGV_LENGTH     , 4)
    };
  }

  public Object finishing = new Object();

  public synchronized void event(int eventID,int seqNum) {
    StackAreaToEast stackAreaToEast = (StackAreaToEast)this.superSceneAction;
    switch (seqNum) {
      case 0: stackAreaToEast.beyondStackArea();   break;
      case 1: stackAreaToEast.beyondCenterNorth(); break;
      case 2: {
        notified = true;
        synchronized(this) {this.notify();}
        synchronized(this.finishing) { this.finishing.notify(); }
        break;
      }
      case 3: stackAreaToEast.finished();          break;
      case 4: stackAreaToEast.beyond();            break;

    }
  }

}
