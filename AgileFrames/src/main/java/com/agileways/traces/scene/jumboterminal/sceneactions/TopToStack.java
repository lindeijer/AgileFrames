package com.agileways.traces.scene.jumboterminal.sceneactions;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.BlockException;
import java.rmi.RemoteException;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.brief.Brief;
import net.agileframes.traces.SceneAction;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.traces.Move;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.ticket.SelectTicket;

/**
 * The SceneAction that drives from the top-lane into the stack.
 */
public class TopToStack extends SceneAction {
  private int stackNr, lanePosition, turnDirection;
  private int stackLane = -1;

  /**
   * @param stackNr         destination stackNr  0<=stackNr<numberOfStacks
   * @param lanePosition    position of quay-lane 0=innerlane; 1=outerlane
   * @param turnDirection   direction of agv 0=from east; 1=from west
   *
   * @throws RemoteException
   */
  public TopToStack(CrossoverScene cS,
      int stackNr, int lanePosition, int turnDirection) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    //
    this.stackNr = stackNr;
    this.lanePosition = lanePosition;
    this.turnDirection = turnDirection;
  }

  private MoveImplBase move1, move4;
  private MoveImplBase[][] move2;
  private MoveImplBase[]  move3;
  private PrimeTicket primTicTotalStack,primTicStackEntrance,primTicCrossingLane;
  private PrimeTicket[] primTicStackLane;
  private PrimeTicket[] primTicStack;
  private float distance, lastWidth;
  private int numbOfSems = 99;
  private int startLane;



  private CrossoverScene cS;
  private CrossoverTerminal cT;

  /**
   * Creates the necessary tickets for this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);
    //
    move2 = new MoveImplBase[cT.numberOfStacks][cT.lanesPerStack];
    move3 = new MoveImplBase[cT.lanesPerStack];
    primTicStack = new PrimeTicket[cT.lanesPerStack];
    startLane = cT.lanesPerStack-1;
    //create tickets
    primTicTotalStack = new PrimeTicket(this, cS.semTotalStack[stackNr]);
    primTicStackEntrance = new PrimeTicket(this, cS.semStackEntrance[stackNr]);
    primTicCrossingLane = null;
    for (int lane=0;lane<cT.lanesPerStack;lane++){
      primTicStack[lane] = new PrimeTicket(this, cS.semStack[stackNr][lane]);
    }
  }

  /**
   * Executes the appropriate logistic moves and claims semaphores by insisting on their
   * respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    primTicTotalStack.reserve();
    float drivenDistance =99;
    int index = 0;
    if (turnDirection == 0){// driving westwards on the top-lane
      // total distance driven in this action:
      distance = cT.FREE_SPACE_AT_SIDES + cT.FREE_SPACE_BETWEEN_STACKS +
                 cT.STACK_WIDTH_INCL * (cT.numberOfStacks-1-stackNr) +
                 cT.WIDTH_STACKLANE * (cT.lanesPerStack-1)
                 -2*cT.TURN_RADIUS;
      lastWidth = cT.LAST_WIDTH_STACKLANE;
      drivenDistance = cT.AGV_LENGTH - lastWidth;
      // number of semaphores passed by this action:
      numbOfSems = 1+(int)Math.ceil((distance-lastWidth+cT.AGV_LENGTH)/cT.AGV_LENGTH);// correction because last semaphore is larger
      if (distance<=lastWidth) {numbOfSems=1;}

      // first move gets all the tickets and rules, tickets are insisted later in this script.
      move1 = cS.goWestAtStack[cT.numberOfStacks-1][cT.lanesPerStack-1][lanePosition].clone(actor,null);
      drivenDistance+=move1.trajectory.domain;
      move1.reset();
      move1.rules = new Rule[numbOfSems+1];
      move1.rules[0] = new Move.EvolutionRule(move1, move1.trajectory, move1.trajectory.domain - cT.AGV_LENGTH, 0);
      primTicStackLane = new PrimeTicket[numbOfSems];
      for (int i=0; i<numbOfSems; i++) {
        move1.rules[i+1] = new Move.EvolutionRule(move1,move1.trajectory,(i+0.5f) * cT.AGV_LENGTH + lastWidth,i+1);
        primTicStackLane[i] = new PrimeTicket(this, cS.semStackLane[lanePosition][cT.NUMBER_OF_AGVS_AT_STACKLANE-1-i]);
      }
      primTicStackLane[index].insist();
      index++;
      move1.exec(null, primTicStackLane , null);

      // aditional goWestAtStacks
      for (int nr=cT.numberOfStacks-1; nr>=stackNr+1; nr--) {
        for (int lane=cT.lanesPerStack-1; lane>=0; lane--) {
          if (!((lane==cT.lanesPerStack-1) && (nr==cT.numberOfStacks-1))) {
            move2[nr][lane] = cS.goWestAtStack[nr][lane][lanePosition].clone(actor,null);
            drivenDistance+=move2[nr][lane].trajectory.domain;
            while (drivenDistance>=cT.AGV_LENGTH){
              primTicStackLane[index].insist();
              index++;
              drivenDistance-=cT.AGV_LENGTH;
            }
            move2[nr][lane].reset();
            move2[nr][lane].exec(null, new PrimeTicket[]{}, new Brief[]{});
          }
        }
      }

      // using SelectTicket:
      //selTicStack = new SelectTicket("selTicStack",this,primTicStack);
      //selTicStack.insist();
      //stackLane = selTicStack.snip()-1;

      // select stackLane:
      primTicTotalStack.insist();
      boolean gotOne=false;
      int stLane=0;
      while (!gotOne) {
        gotOne = primTicStack[stLane].attempt();
        stLane++;
      }
      stackLane = stLane-1;

      // last goWestAtStack:
      if (stackNr==cT.numberOfStacks-1) {startLane=cT.lanesPerStack-2;} else {startLane=cT.lanesPerStack-1;}
      for (int lane=startLane; lane>=stackLane; lane--) {
        move3[lane] = cS.goWestAtStack[stackNr][lane][lanePosition].clone(actor,null);
        drivenDistance+=move3[lane].trajectory.domain;
        while (drivenDistance>=cT.AGV_LENGTH){
          primTicStackLane[index].insist();
          index++;
          drivenDistance-=cT.AGV_LENGTH;
        }
        move3[lane].reset();
        move3[lane].exec(null, null, new Brief[]{});
      }

      // last move: all primTic's will be added to be sure they will
      // be freed in time
      move4 = cS.enterStackEast[stackNr][stackLane][lanePosition].clone(actor,null);
      move4.reset();
      move4.rules = new Rule[numbOfSems+3];
      move4.rules[0] = new Move.EvolutionRule(move4, move4.trajectory, move4.trajectory.domain - cT.AGV_LENGTH/2, 0);
      move4.rules[1] = new Move.EvolutionRule(move4, move4.trajectory, move4.trajectory.domain - cT.AGV_LENGTH/2 - 0.01f, 2);
      move4.rules[2] = new Move.EvolutionRule(move4, move4.trajectory, move4.trajectory.domain + cT.AGV_LENGTH/2, 1);
      for (int i=0; i<numbOfSems; i++) {
        move4.rules[i+3] = new Move.EvolutionRule(move4,move4.trajectory,cT.AGV_LENGTH/2,i+3);
      }
      // if the outer lane is being taken then the inner lane needs to be claimed as well
      if (lanePosition==1) {primTicCrossingLane = new PrimeTicket(this, cS.semStackLane[0][cT.NUMBER_OF_AGVS_AT_STACKLANE-index]);}


    } else {//clockwise: the agv is in the north-west-corner of the terminal

      drivenDistance = 0;
      distance = cT.FREE_SPACE_AT_SIDES + cT.STACK_WIDTH_INCL * stackNr +
                 cT.FREE_SPACE_BETWEEN_STACKS + cT.WIDTH_STACKLANE * (cT.lanesPerStack - 1)
                 -2*cT.TURN_RADIUS;
      // number of semaphores passed by this action:
      numbOfSems = 1+(int)Math.ceil(distance/cT.AGV_LENGTH);

      // first move gets all the tickets and rules, tickets are insisted later in this script.
      move1 = cS.goEastAtStack[0][0][lanePosition].clone(actor,null);
      drivenDistance+=move1.trajectory.domain;
      move1.reset();
      move1.rules = new Rule[numbOfSems+1];
      move1.rules[0] = new Move.EvolutionRule(move1, move1.trajectory, move1.trajectory.domain - cT.AGV_LENGTH, 0);
      primTicStackLane = new PrimeTicket[numbOfSems];
      for (int i=0; i<numbOfSems; i++) {
        move1.rules[i+1] = new Move.EvolutionRule(move1,move1.trajectory,(float)((i+1.5)*cT.AGV_LENGTH),i+1);
        primTicStackLane[i] = new PrimeTicket(this, cS.semStackLane[lanePosition][i]);
      }
      primTicStackLane[index].insist();
      index++;
      move1.exec(null, primTicStackLane, new Brief[]{});

      for (int nr=0; nr<=stackNr-1; nr++) {
        for (int lane=0; lane<=cT.lanesPerStack-1; lane++) {
          if (!((lane==0) && (nr==0))) {
            move2[nr][lane] = cS.goEastAtStack[nr][lane][lanePosition].clone(actor,null);
            drivenDistance+=move2[nr][lane].trajectory.domain;
            while (drivenDistance>=cT.AGV_LENGTH){
              primTicStackLane[index].insist();
              index++;
              drivenDistance-=cT.AGV_LENGTH;
            }
            move2[nr][lane].reset();
            move2[nr][lane].exec(null, new PrimeTicket[]{}, new Brief[]{});
          }
      } }

      //selTicStack = new SelectTicket("selTicStack",this,primTicStack);
      //selTicStack.insist();
      //stackLane = selTicStack.snip()-1;

      primTicTotalStack.insist();
      boolean gotOne=false;
      int stLane=0;
      while (!gotOne) {
        gotOne = primTicStack[stLane].attempt();
        stLane++;
      }
      stackLane = stLane-1;


      if (stackNr==0) {startLane=1;} else {startLane=0;}
      for (int lane=startLane; lane<=stackLane; lane++) {
        move3[lane] = cS.goEastAtStack[stackNr][lane][lanePosition].clone(actor,null);
        drivenDistance+=move3[lane].trajectory.domain;
        while (drivenDistance>=cT.AGV_LENGTH){
          primTicStackLane[index].insist();
          index++;
          drivenDistance-=cT.AGV_LENGTH;
        }
        move3[lane].reset();
        move3[lane].exec(null, new PrimeTicket[]{}, null);
      }

      // last move: all primTic's will be added to be sure they will
      // be freed in time
      move4 = cS.enterStackWest[stackNr][stackLane][lanePosition].clone(actor,null);
      move4.reset();
      move4.rules = new Rule[numbOfSems+3];
      move4.rules[0] = new Move.EvolutionRule(move4, move4.trajectory, move4.trajectory.domain - cT.AGV_LENGTH/2, 0);
      move4.rules[1] = new Move.EvolutionRule(move4, move4.trajectory, move4.trajectory.domain - cT.AGV_LENGTH/2 - 0.01f, 2);
      move4.rules[2] = new Move.EvolutionRule(move4, move4.trajectory, move4.trajectory.domain + cT.AGV_LENGTH/2, 1);
      for (int i=0; i<numbOfSems; i++) {
        move4.rules[i+3] = new Move.EvolutionRule(move4,move4.trajectory,cT.AGV_LENGTH/2,i+3);
      }
      // if the outer lane is being taken then the inner lane needs to be claimed as well
      if (lanePosition==1) {primTicCrossingLane = new PrimeTicket(this, cS.semStackLane[0][index-1]);}
    }

    if (primTicCrossingLane!=null) {primTicCrossingLane.insist();}
    primTicStackEntrance.insist();

    PrimeTicket[] allTickets = new PrimeTicket[primTicStackLane.length + 4];
    allTickets[0]=primTicTotalStack;
    allTickets[1]=primTicStack[stackLane];
    allTickets[2]=primTicStackEntrance;
    allTickets[3]=primTicCrossingLane;
    for (int i=0;i<primTicStackLane.length;i++){ allTickets[i+4] = primTicStackLane[i];  }

    move4.exec(null, allTickets, new Brief[]{});

  }

  /**
   * gives the chosen stacklane
   * @return the stacklane selected in this sceneaction
   */
  public int getStackLane() {
    return stackLane;
  }
}