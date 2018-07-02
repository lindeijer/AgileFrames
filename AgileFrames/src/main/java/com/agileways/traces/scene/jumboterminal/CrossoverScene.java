package com.agileways.traces.scene.jumboterminal;
import net.agileframes.traces.SceneImplBase;
import net.agileframes.traces.Semaphore;
import net.agileframes.traces.MoveImplBase;
import com.agileways.traces.scene.jumboterminal.logisticmoves.*;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.POS;
import net.jini.core.lookup.ServiceID;
import com.agileways.traces.scene.jumboscene.SceneBody;
import com.agileways.traces.scene.jumboscene.SceneAvatar;
import net.agileframes.vr.Color3D;

/**
 * The Jumbo Container Terminal Scene
 *
 * @author Wierenga,Lindeijer,Evers
 * @version 0.0.1
 */
public class CrossoverScene extends SceneImplBase {

  // declared moves, not relevant for thesis, only for visualization.
  // are cloned when actions are created for an actor.
  public MoveImplBase[][][][] leaveStackForParking    = null;
  public MoveImplBase[][]     leaveStackForCenterEast = null;
  public MoveImplBase[][]     leaveStackForCenterWest = null;
  public MoveImplBase[][]     parkEast                = null;
  public MoveImplBase[][]     parkWest                = null;
  public MoveImplBase[]       goEastAtCenter          = null;
  public MoveImplBase[]       crossoverArea2West     = null;
  public MoveImplBase[][][]   enterStackEast          = null;
  public MoveImplBase[][][]   enterStackWest          = null;
  public MoveImplBase[][][]   goEastAtStack           = null;
  public MoveImplBase[][][]   goWestAtStack           = null;
  public MoveImplBase[][]     goNorth                 = null;
  public MoveImplBase[][][]   turn                    = null;

  private void createActions() {
    leaveStackForParking = new LeaveStackForParking [cT.numberOfStacks][cT.lanesPerStack][cT.numberOfStacks-1][cT.lanesPerPark];
    leaveStackForCenterEast = new LeaveStackForCenterEast[cT.numberOfStacks][cT.lanesPerStack];
    leaveStackForCenterWest = new LeaveStackForCenterWest[cT.numberOfStacks][cT.lanesPerStack];
    parkEast = new ParkEast[cT.numberOfStacks-1][cT.lanesPerPark];
    parkWest = new ParkWest[cT.numberOfStacks-1][cT.lanesPerPark];
    goEastAtCenter = new GoEastAtCenter[cT.numberOfStacks];
    crossoverArea2West = new CrossoverArea2West[cT.numberOfStacks];
    enterStackEast = new EnterStackEast[cT.numberOfStacks][cT.lanesPerStack][2];
    enterStackWest = new EnterStackWest[cT.numberOfStacks][cT.lanesPerStack][2];
    goEastAtStack = new GoEastAtStack[cT.numberOfStacks][cT.lanesPerStack][2];
    goWestAtStack = new GoWestAtStack[cT.numberOfStacks][cT.lanesPerStack][2];
    goNorth = new GoNorth[4][cT.NUMBER_OF_AGVS_AT_SIDES];
    turn = new Turn[4][2][2];

    // create all LogisticMoves
    System.out.println("creating logisticMoves...");
try {
    for (int stackNr=0;stackNr<cT.numberOfStacks;stackNr++) {
      for (int stackLane=0;stackLane<cT.lanesPerStack;stackLane++) {
        leaveStackForCenterWest[stackNr][stackLane] = new LeaveStackForCenterWest(stackNr, stackLane);
        leaveStackForCenterEast[stackNr][stackLane] = new LeaveStackForCenterEast(stackNr, stackLane);
        if (leaveStackForCenterWest[stackNr][stackLane].trajectory!=null) {
          jumboBody.addTrajectory(leaveStackForCenterWest[stackNr][stackLane].trajectory);}
        if (leaveStackForCenterEast[stackNr][stackLane].trajectory!=null) {
          jumboBody.addTrajectory(leaveStackForCenterEast[stackNr][stackLane].trajectory);}
    } }
    for (int parkNr=0;parkNr<cT.numberOfStacks-1;parkNr++) {
      for (int parkLane=0;parkLane<cT.lanesPerPark;parkLane++) {
        parkEast[parkNr][parkLane] = new ParkEast(parkNr, parkLane);
        parkWest[parkNr][parkLane] = new ParkWest(parkNr, parkLane);
        if (parkEast[parkNr][parkLane].trajectory!=null) {
          jumboBody.addTrajectory(parkEast[parkNr][parkLane].trajectory);}
        if (parkWest[parkNr][parkLane].trajectory!=null) {
          jumboBody.addTrajectory(parkWest[parkNr][parkLane].trajectory);}
    } }
    for (int stackNr=0;stackNr<cT.numberOfStacks;stackNr++) {
      for (int stackLane=0;stackLane<cT.lanesPerStack;stackLane++) {
        for (int parkNr=0;parkNr<cT.numberOfStacks-1;parkNr++) {
          for (int parkLane=0;parkLane<cT.lanesPerPark;parkLane++) {
            leaveStackForParking[stackNr][stackLane][parkNr][parkLane] = new LeaveStackForParking(stackNr,stackLane,parkNr,parkLane);
            if (leaveStackForParking[stackNr][stackLane][parkNr][parkLane].trajectory!=null) {
              jumboBody.addTrajectory(leaveStackForParking[stackNr][stackLane][parkNr][parkLane].trajectory);}
    } } } }
    for (int stackNr=0;stackNr<cT.numberOfStacks;stackNr++) {
      for (int stackLane=0;stackLane<cT.lanesPerStack;stackLane++) {
        for (int lanePosition=0;lanePosition<2;lanePosition++) {
          enterStackEast[stackNr][stackLane][lanePosition] = new EnterStackEast(stackNr, stackLane,lanePosition);
          enterStackWest[stackNr][stackLane][lanePosition] = new EnterStackWest(stackNr, stackLane,lanePosition);
          goEastAtStack[stackNr][stackLane][lanePosition] = new GoEastAtStack(stackNr, stackLane,lanePosition);
          goWestAtStack[stackNr][stackLane][lanePosition] = new GoWestAtStack(stackNr, stackLane,lanePosition);
          if (enterStackWest[stackNr][stackLane][lanePosition].trajectory!=null) {
            jumboBody.addTrajectory(enterStackWest[stackNr][stackLane][lanePosition].trajectory);}
          if (enterStackEast[stackNr][stackLane][lanePosition].trajectory!=null) {
            jumboBody.addTrajectory(enterStackEast[stackNr][stackLane][lanePosition].trajectory);}
          if (goEastAtStack[stackNr][stackLane][lanePosition].trajectory!=null) {
            jumboBody.addTrajectory(goEastAtStack[stackNr][stackLane][lanePosition].trajectory);}
          if (goWestAtStack[stackNr][stackLane][lanePosition].trajectory!=null) {
            jumboBody.addTrajectory(goWestAtStack[stackNr][stackLane][lanePosition].trajectory);}
    } } }
    for (int lanePosition=0;lanePosition<4;lanePosition++) {
      for (int index=0; index<cT.NUMBER_OF_AGVS_AT_SIDES; index++) {
        goNorth[lanePosition][index] = new GoNorth(lanePosition, index);
        if (goNorth[lanePosition][index].trajectory!=null) {
          jumboBody.addTrajectory(goNorth[lanePosition][index].trajectory);}
    } }
    for (int stackNr=0;stackNr<cT.numberOfStacks;stackNr++) {
      goEastAtCenter[stackNr] = new GoEastAtCenter(stackNr);
      crossoverArea2West[stackNr] = new CrossoverArea2West(stackNr);
      if (goEastAtCenter[stackNr].trajectory!=null) {
        jumboBody.addTrajectory(goEastAtCenter[stackNr].trajectory);}
      if (crossoverArea2West[stackNr].trajectory!=null) {
        jumboBody.addTrajectory(crossoverArea2West[stackNr].trajectory);}
    }
    for (int corner=0;corner<4;corner++) {
      for (int lanePosition1=0;lanePosition1<2;lanePosition1++) {
        for (int lanePosition2=0;lanePosition2<2;lanePosition2++) {
          turn[corner][lanePosition1][lanePosition2] = new Turn(corner,lanePosition1,lanePosition2);
          if (turn[corner][lanePosition1][lanePosition2].trajectory!=null) {
            jumboBody.addTrajectory(turn[corner][lanePosition1][lanePosition2].trajectory);}
    } } }
}
catch(Exception e) {
  System.out.println("Error in JumboScene.initialize while creating actions:"+e);e.printStackTrace();
}

  } // end of createActions



  // declared semaphores, add thesis name as comment!
  // new actions area PrimeTickets during initialize!
  public Semaphore[]   semStackEntrance;
  public Semaphore[][] semStack;            // sSPA
  public Semaphore[]   semTotalStack;
  public Semaphore[][] semStackExit;        // sSPAX
  //
  public Semaphore[]   semCenterStayNorth;  // sPCN
  public Semaphore[]   semCenterNorth;      // sTCN
  public Semaphore[]   semCenterSouth;      // sTCS
  public Semaphore[]   semCenterStaySouth;  // sPCS
  //
  public Semaphore[][] semParkEntrance;     // sQPAX
  public Semaphore[][] semPark;             // sQPA
  public Semaphore[]   semTotalPark;
  public Semaphore[]   semParkExit;
  //
  public Semaphore[][] semSideLane;
  public Semaphore[][] semTurn;
  public Semaphore[]   semTurnCollect;
  public Semaphore[][] semStackLane;
  public Semaphore[]   semTtable;           // sQC
  public Semaphore[][] semQuayLane;


  private void createSemaphores() {
    semStackEntrance   = new Semaphore[cT.numberOfStacks];
    semStack           = new Semaphore[cT.numberOfStacks][cT.lanesPerStack];
    semTotalStack      = new Semaphore[cT.numberOfStacks];
    semStackExit       = new Semaphore[cT.numberOfStacks][cT.lanesPerStack+1];
    semCenterStayNorth = new Semaphore[cT.numberOfStacks-2];
    semCenterNorth     = new Semaphore[cT.numberOfStacks];
    semCenterSouth     = new Semaphore[cT.numberOfStacks];
    semCenterStaySouth = new Semaphore[cT.numberOfStacks-2];
    semParkEntrance    = new Semaphore[cT.numberOfStacks-1][cT.lanesPerPark];
    semPark            = new Semaphore[cT.numberOfStacks-1][cT.lanesPerPark];
    semTotalPark       = new Semaphore[cT.numberOfStacks-1];
    semParkExit        = new Semaphore[cT.numberOfStacks-1];
    semSideLane        = new Semaphore[4][cT.NUMBER_OF_AGVS_AT_SIDES];
    semTurn            = new Semaphore[4][2];
    semTurnCollect     = new Semaphore[4];//this is instead of collect-ticket
    semStackLane       = new Semaphore[2][cT.NUMBER_OF_AGVS_AT_STACKLANE];
    semTtable          = new Semaphore[cT.NUMBER_OF_TURNTABLES];
    semQuayLane        = new Semaphore[2][cT.NUMBER_OF_AGVS_AT_STACKLANE];
    // create all semaphores
    System.out.println("creating semaphores...");
    try {
      for (int stackNr=0;stackNr<cT.numberOfStacks;stackNr++) {
        semStackEntrance[stackNr] = new Semaphore("semStackEntrance-"+stackNr);
        semCenterNorth[stackNr] = new Semaphore("semCenterNorth-"+stackNr);
        semCenterSouth[stackNr] = new Semaphore("semCenterSouth-"+stackNr);
        semTotalStack[stackNr] = new Semaphore("semTotalStack-"+stackNr,cT.lanesPerStack);
        if ((stackNr<cT.numberOfStacks-2) && (stackNr>0)){
          semCenterStayNorth[stackNr] = new Semaphore("semCenterStayNorth-"+stackNr);
          semCenterStaySouth[stackNr] = new Semaphore("semCenterStaySouth-"+stackNr);
        }
        if (stackNr<cT.numberOfStacks-1) {
          semParkExit[stackNr] = new Semaphore("semParkExit-"+stackNr);
          semTotalPark[stackNr] = new Semaphore("semTotalPark-"+stackNr, cT.lanesPerPark);
        }
        for(int stackLane=0;stackLane<=cT.lanesPerStack;stackLane++) {
          if (stackLane<cT.lanesPerStack) {
            semStack[stackNr][stackLane] = new Semaphore("semStack-"+stackNr+"."+stackLane);
          }
          semStackExit[stackNr][stackLane] = new Semaphore("semStackExit-"+stackNr+"."+stackLane);
        } //for stackLane
        for(int parkLane=0;parkLane<cT.lanesPerPark;parkLane++) {
          if (stackNr<cT.numberOfStacks-1) {
            semParkEntrance[stackNr][parkLane] = new Semaphore("semParkEntrance-"+stackNr+"."+parkLane);
            semPark[stackNr][parkLane] = new Semaphore("semPark-"+stackNr+"."+parkLane);
          }
        } // for parkLane
      } // for stackNr
      for (int lanePosition1 = 0; lanePosition1<4; lanePosition1++) {
        semTurnCollect[lanePosition1] =
          new Semaphore("semTurnCollect-"+lanePosition1);
        for (int lanePosition2 = 0; lanePosition2<2; lanePosition2++) {
          semTurn[lanePosition1][lanePosition2] =
            new Semaphore("semTurn-"+lanePosition1+"."+lanePosition2);
        }
        for (int index = 0; index<cT.NUMBER_OF_AGVS_AT_SIDES; index++) {
          semSideLane[lanePosition1][index] =
            new Semaphore("semSideLane-"+lanePosition1+"."+index);
        }
      }
      for (int lanePosition = 0; lanePosition<2; lanePosition++) {
        for (int index = 0; index<cT.NUMBER_OF_AGVS_AT_STACKLANE; index++) {
          semStackLane[lanePosition][index] =
            new Semaphore("semStackLane-"+lanePosition+"."+index);
          semQuayLane[lanePosition][index] =
            new Semaphore("semQuayLane-"+lanePosition+"."+index);
        }
      }
      for (int ttableNr=0; ttableNr<cT.NUMBER_OF_TURNTABLES; ttableNr++) {
        semTtable[ttableNr] = new Semaphore("semTtable-"+ttableNr);
      }
    }
    catch(Exception e) {
      System.out.println("Error in JumboScene.initialize while creating semaphores:"+e);e.printStackTrace();
    }
  } // end of createSemaphores

  ////////////////////////////////////////////////////////////////////

  public static CrossoverTerminal cT;
  public static POSTransform locale;
  public int numberOfStacks;

  public CrossoverScene(String name,POSTransform locale,int numberOfStacks,ServiceID serviceID) throws java.rmi.RemoteException {
    super(name + "@JumboScene");
    this.numberOfStacks = numberOfStacks;
    this.cT = new CrossoverTerminal(numberOfStacks);
    this.serviceID = serviceID;
    this.locale = locale;
    //
    POS pos = (POS)locale.transform(new POS(0,0,0));
    xTrans = pos.x;
    yTrans = pos.y;
    //
    createSemaphores();

    this.size = computeNumberOfMoves();
    jumboBody = new SceneBody(size);
    createActions();
    huh();
  }


  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////

  /** Scales all indices */
  public static float scale = 1f;
  /** Translation of entire terminal in x direction */
  public static float xTrans = -60 * scale;
  /** Translation of entire terminal in y direction */
  public static float yTrans = -15 * scale;

  /** SceneBody containing all trajectories to be viusualized*/
  public SceneBody jumboBody = null;

  /** SceneAvatar for visualizing trajectories */
  public SceneAvatar jumboAvatar = null;


  public int size;
  private int computeNumberOfMoves() {
    // determine total size of Moves-array:
    int numberOfMoves = 0;
    // size of GoEastAtStack,GoWestAtStack, EnterStackEast, EnterStackWest:
    numberOfMoves += 4 * cT.numberOfStacks * cT.lanesPerStack * 2;
    // size of ParkEast, ParkWest:
    if (cT.numberOfStacks > 2) {numberOfMoves += 2 * (cT.numberOfStacks - 3) * cT.lanesPerPark;}
    // size of LeaveStackForCenterEast, LeaveStackForCenterWest:
    if (cT.numberOfStacks > 2) {numberOfMoves += 2 * (cT.numberOfStacks - 3) * cT.lanesPerStack;}
    // size of LeaveStackForParking:
    numberOfMoves += 1 * (cT.numberOfStacks - 1) * cT.lanesPerStack * cT.lanesPerPark * 2;
    if (cT.numberOfStacks > 2) {numberOfMoves += 2 * (cT.numberOfStacks - 2) * cT.lanesPerStack * cT.lanesPerPark;}
    // size of Turn:
    numberOfMoves += 16;
    // size of GoNorth:
    numberOfMoves += 4*cT.NUMBER_OF_AGVS_AT_SIDES;
    // size of GoEastAtCenter, GoWestAtCenter:
    if (cT.numberOfStacks > 4) { numberOfMoves += 2 * (cT.numberOfStacks - 4); }
    return numberOfMoves;
  }

  /** Creates objects for visualization and determines number of trajectories to be visualized */
  private void huh() {
    // jumboBody = new SceneBody(this);
    jumboBody.setResolution(2);
    jumboBody.setColor(Color3D.yellow);
    jumboAvatar = new SceneAvatar(this);
  }

  /////////////////////////////////////////////////////////////////////

}

