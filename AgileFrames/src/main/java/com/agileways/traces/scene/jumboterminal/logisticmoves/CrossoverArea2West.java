package com.agileways.traces.scene.jumboterminal.logisticmoves;

import com.agileways.forces.maneuver.GoStraight;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Transform;
import net.agileframes.traces.MoveImplBase;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;
import net.agileframes.core.forces.Rule;
import net.agileframes.traces.Move;
import com.agileways.traces.scene.jumboterminal.sceneactions.CrossoverAreaToWest;


/**
 * Move governed by CrossoverAreaToWest
 * @author Wierenga,Lindeijer,Evers
 */
public class CrossoverArea2West extends MoveImplBase {

   /**
   * @param stackAreaID  stack-number in front of which this move is situated:  2 <= stackAreaID < numberOfStacks-2
   */
  public CrossoverArea2West(int stackAreaID) {
    if ((stackAreaID>1) && (stackAreaID<CrossoverScene.cT.numberOfStacks-2)){
      Transform transform = computeGlobalLocale(stackAreaID);
      this.trajectory = createCrossover$West(transform);
    }// if
    else { this.trajectory = null;}
  }

  private Transform computeGlobalLocale(int stackAreaID) {
    CrossoverTerminal cT = CrossoverScene.cT;
    // compute relative locale.
    float relativeX = (stackAreaID + 1) * cT.STACK_WIDTH_INCL    +
                                          cT.FREE_TERMINAL_LEFT  +
                                          cT.FREE_SPACE_AT_SIDES +
                                          cT.WIDTH_DRIVING_LANE  ;
    float relativeY = cT.getStackLaneY()   -
                      cT.FREE_STACK_EXIT   -
                      cT.WIDTH_CENTER_LANE ;
    float alpha = (float)Math.PI;
    // compute global locale.
    float globalX = relativeX*CrossoverScene.scale+CrossoverScene.xTrans;
    float globalY = relativeY*CrossoverScene.scale+CrossoverScene.yTrans;
    Transform globalLocale =
      new POSTransform(globalX,globalY,alpha,CrossoverScene.scale);
    return globalLocale;
  }


  private Trajectory createCrossover$West(Transform transform) {
    Trajectory[] composedTrajectory = new Trajectory[1];
    composedTrajectory[0] = new GoStraight(CrossoverScene.cT.STACK_WIDTH_INCL);
    composedTrajectory[0].setTransform(new POSTransform(0,0,0));
    Trajectory crossover$West = new Trajectory(composedTrajectory);
    crossover$West.setTransform(transform);
    crossover$West.obstacleAtEnd = false;
    return crossover$West;
  }


  private final int FINISHING=0;
  private final int FINISHED =1;
  private final int BEYOND   =2;

  /**
   * Creates the (evolution) rules belonging to this object.
   */
  public void createRules(){
    float agvLength = CrossoverScene.cT.AGV_LENGTH;
    float tEnd = trajectory.domain;
    Move.EvolutionRule finishing,finished,beyond;
    finishing = new Move.EvolutionRule(this,trajectory,tEnd-agvLength/2  ,FINISHING);
    finished  = new Move.EvolutionRule(this,trajectory,tEnd,FINISHED );
    beyond    = new Move.EvolutionRule(this,trajectory,tEnd+agvLength ,BEYOND   );
    this.rules = new Rule[] { finishing,finished,beyond };
  }

  CrossoverAreaToWest crossoverAreaToWest;

  public Object finishing = new Object();

  public synchronized void event(int eventID,int seqNum) {
    switch (seqNum) {
      case FINISHING: {
        notified = true;
        synchronized(this) {this.notify();}
        synchronized(this.finishing) { this.finishing.notify(); }
        break;
      }
      case FINISHED: {
        CrossoverAreaToWest crossoverAreaToWest =
          (CrossoverAreaToWest)this.superSceneAction;
        crossoverAreaToWest.finished();
        break;
      }
      case BEYOND: {
        CrossoverAreaToWest crossoverAreaToWest =
          (CrossoverAreaToWest)this.superSceneAction;
        crossoverAreaToWest.beyond();
        break;
      }
    }
  }


}