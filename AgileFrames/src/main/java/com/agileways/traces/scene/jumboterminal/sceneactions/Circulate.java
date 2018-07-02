package com.agileways.traces.scene.jumboterminal.sceneactions;


import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;
import net.agileframes.core.forces.Actor;
import net.agileframes.traces.SceneAction;
import com.agileways.traces.scene.jumboterminal.CrossoverAgv;


/**
 * Action for circulating within the Crossover transport system
 * @author Wierenga,Lindeijer,Evers
 */

public class Circulate extends SceneAction {

  private CrossoverScene cS;
  private CrossoverTerminal cT;
  private CrossoverAgv crossoverAgv;

  // declared Actions
  public TopToStack[][][] topToStack;
  public StackAreaToWestToQuayArea [][][] stackAreaToWestToQuayArea;
  public StackAreaToSouthToQuayArea[][][] stackAreaToSouthToQuayArea;
  public StackAreaToEastToQuayArea [][][] stackAreaToEastToQuayArea;
  public TurnAction[][][] turnAction;
  public QuayToTop[][] quayToTop;

  public Circulate(CrossoverScene crossoverScene) {
    super(crossoverScene);
    this.cS = crossoverScene;
    this.cT = crossoverScene.cT;
    createSubActions();

  }

  public void createSubActions() {
    topToStack        = new TopToStack[cT.numberOfStacks][2][2];
    stackAreaToWestToQuayArea   = new StackAreaToWestToQuayArea[cT.numberOfStacks][cT.lanesPerStack][cT.numberOfStacks-1];
    stackAreaToSouthToQuayArea = new StackAreaToSouthToQuayArea[cT.numberOfStacks][cT.lanesPerStack][cT.numberOfStacks-1];
    stackAreaToEastToQuayArea   = new StackAreaToEastToQuayArea[cT.numberOfStacks][cT.lanesPerStack][cT.numberOfStacks-1];
    turnAction        = new TurnAction[4][2][2];
    quayToTop         = new QuayToTop[2][2];
    try {
      for (int stackAreaID=0;stackAreaID<cT.numberOfStacks;stackAreaID++) {
        for(int stackParkAreaID=0;stackParkAreaID<cT.lanesPerStack;stackParkAreaID++) {
          for (int quayAreaID=0; quayAreaID<cT.numberOfStacks-1;quayAreaID++) {
            if (quayAreaID<=stackAreaID-3) {
              stackAreaToWestToQuayArea[stackAreaID][stackParkAreaID][quayAreaID] =
                new StackAreaToWestToQuayArea(cS,stackAreaID, stackParkAreaID, quayAreaID);
            } else { if (quayAreaID>=stackAreaID+2) {
              stackAreaToEastToQuayArea[stackAreaID][stackParkAreaID][quayAreaID] =
                new StackAreaToEastToQuayArea(cS,stackAreaID, stackParkAreaID, quayAreaID);
            } else {
              stackAreaToSouthToQuayArea[stackAreaID][stackParkAreaID][quayAreaID] =
                new StackAreaToSouthToQuayArea(cS,stackAreaID, stackParkAreaID, quayAreaID);
            }}
          }
          for(int turnDirection=0;turnDirection<2;turnDirection++) {
            for(int northernLane=0;northernLane<2;northernLane++) {
              topToStack[stackAreaID][northernLane][turnDirection] =
                new TopToStack(cS,stackAreaID, northernLane, turnDirection);
      } } } }
      for(int northernLane=0;northernLane<2;northernLane++) {
        for(int turnDirection=0;turnDirection<2;turnDirection++) {
          quayToTop[northernLane][turnDirection] =
            new QuayToTop(cS,northernLane,turnDirection);
          for (int corner=0;corner<4;corner++) {
            turnAction[corner][northernLane][turnDirection] =
              new TurnAction(cS,corner,northernLane,turnDirection);
            for (int quayAreaID=0; quayAreaID<cT.numberOfStacks-1;quayAreaID++) {
              for(int parkLane=0;parkLane<cT.lanesPerPark;parkLane++) {
      } } } } }
    } catch(Exception e){
      System.out.println("Error in Circulate while initializing sceneActions:"+e.getMessage());
      e.printStackTrace();
    }
  }  // end of createSubActions

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    // preceded by implicit initialize(null,actor,null)
    this.crossoverAgv = (CrossoverAgv)actor.getService();
    try {
      for (int stackAreaID=0;stackAreaID<cT.numberOfStacks;stackAreaID++) {
        for(int stackParkAreaID=0;stackParkAreaID<cT.lanesPerStack;stackParkAreaID++) {
          for (int quayAreaID=0; quayAreaID<cT.numberOfStacks-1;quayAreaID++) {
            if (quayAreaID<=stackAreaID-3) {
              stackAreaToWestToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].assimilate(actor);
            } else { if (quayAreaID>=stackAreaID+2) {
              stackAreaToEastToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].assimilate(actor);
            } else {
              stackAreaToSouthToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].assimilate(actor);
            }}
          }
          for(int turnDirection=0;turnDirection<2;turnDirection++) {
            for(int northernLane=0;northernLane<2;northernLane++) {
              topToStack[stackAreaID][northernLane][turnDirection].assimilate(actor);
      } } } }
      for(int northernLane=0;northernLane<2;northernLane++) {
        for(int turnDirection=0;turnDirection<2;turnDirection++) {
          quayToTop[northernLane][turnDirection].assimilate(actor);
          for (int corner=0;corner<4;corner++) {
            turnAction[corner][northernLane][turnDirection].assimilate(actor);
            for (int quayAreaID=0; quayAreaID<cT.numberOfStacks-1;quayAreaID++) {
              for(int parkLane=0;parkLane<cT.lanesPerPark;parkLane++) {
      } } } } }
    } catch(Exception e){
      System.out.println("Error in Circulate while initializing sceneActions:"+e.getMessage());
      e.printStackTrace();
    }
  }  // end of initializeActions


  /////////////////////////////////////////////////////////

  private int stackAreaID;
  private int stackParkAreaID;
  private int quayCraneID;
  private int turnDirection;
  private int sideLane;

  protected void script() {
    try {
      enterToStackArea();
      while ( toCirculate() ) {
        stackAreaToQuayCrane();
        exchangeWithQuayCrane();
        quayCraneToStackArea();
      }
      stackAreaToExit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean toCirculate() { return crossoverAgv.toCirculate(); }

  /////////////////////////////////////////////////////////////////////

  public void enterToStackArea() throws Exception {
    sideLane = (int)
      Math.floor(2 * Math.random()); // 0 or 1
    this.turnDirection = (int)
      Math.floor(2 * Math.random()); // 0 or 1
    // this.turnDirection = 0;  // 0=EAST
    // this.turnDirection = 1;  // 1=WEST
    crossoverAgv.hello(this.turnDirection);
    sideTopToStackArea();
  }

  public void stackAreaToExit() throws Exception {
    System.out.println("In must stackAreaToExit, but I wil not");
  }

  ////////////////////////////////////////////////////////////

  public void stackAreaToQuayCrane() throws Exception {
    // stack area to quay area
    int quayAreaID = crossoverAgv.getQuayAreaID();
    int quayParkAreaID = 0;
    if ( quayAreaID>=stackAreaID+2 ) {
      stackAreaToEastToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].
        execute();
      quayParkAreaID =
        stackAreaToEastToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].
          getParkLane();
    } else {
      if (quayAreaID<=stackAreaID-3) {
        stackAreaToWestToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].
          execute();
        quayParkAreaID =
          stackAreaToWestToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].
            getQuayParkAreaID();
      } else {
        stackAreaToSouthToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].
          execute();
        quayParkAreaID =
          stackAreaToSouthToQuayArea[stackAreaID][stackParkAreaID][quayAreaID].
            getParkLane();
      }
    }
    // AgvTrackerFrame.setAgvInfo(actor.machine, stackAreaID, stackParkAreaID, quayAreaID, parkLane, turnDirection);
    // quay area to quay crane
    QuayAreaToQuayCrane quayAreaToQuayCrane =
      new QuayAreaToQuayCrane(cS,quayAreaID,quayParkAreaID);
    quayAreaToQuayCrane.assimilate(actor);
    quayAreaToQuayCrane.execute();
    angle = quayAreaToQuayCrane.getEndAngle();
  }

  double angle;

  public void exchangeWithQuayCrane() throws Exception {
    angle = Math.PI/2 - angle;
    double turnAngle;
    if (angle > Math.PI/2) { turnAngle = - angle + Math.PI ; }
    else {                   turnAngle = - angle;            }
    //
    this.quayCraneID = ((CrossoverAgv)actor.getService()).getQuayCraneID();
    // Thread.sleep((long) (500 * Math.random())+500);
    AtTurntable spinBeforeExchange =
      new AtTurntable(cS,this.quayCraneID, angle, turnAngle);
    spinBeforeExchange.assimilate(actor);  // ?
    spinBeforeExchange.execute();
    Thread.sleep((long) (1000 * Math.random())+1000);
    //((Body)((ActorProxy)actor).machine).removeChild(null);
    double turnAngle2 = Math.PI/2;
    if (turnAngle > 0) { turnAngle2= -turnAngle2; }
    // Thread.sleep((long) (500 * Math.random())+500);
    SceneAction spinAfterExchange =
      new AtTurntable(cS,this.quayCraneID, 0, turnAngle2);
    spinAfterExchange.assimilate(actor);
    spinAfterExchange.execute();
    Thread.sleep((long) (500 * Math.random())+500);
  }

  public void quayCraneToStackArea() throws Exception {
    quayCraneToSideTop();
    sideTopToStackArea();
  }

  //////////////////////////////////////////////////////////////

  public void sideTopToStackArea() throws Exception {
    stackAreaID = crossoverAgv.getStackAreaID();
    // AgvTrackerFrame.setAgvInfo(actor.machine, stackAreaID, 99, quayAreaID, 99, turnDirection);
    topToStack[stackAreaID][sideLane][turnDirection].execute();
    // topToStack selects its own stackParkArea !
    stackParkAreaID = topToStack[stackAreaID][sideLane][turnDirection].getStackLane();
    // AgvTrackerFrame.setAgvInfo(actor.machine, stackAreaID, stackParkAreaID, quayAreaID, 99, turnDirection);
  }
  public void quayCraneToSideTop() throws Exception {
    turnDirection = crossoverAgv.getTurnDirection();
    int northernLane = (int) Math.floor(2 * Math.random());  // 0 or 1
    int southernLane = (int) Math.floor(2 * Math.random());  // 0 or 1
        sideLane = (int) Math.floor(2 * Math.random());  // 0 or 1
    TableToQuay tableToQuay = new TableToQuay(cS,this.quayCraneID,turnDirection,northernLane);
    tableToQuay.assimilate(actor);
    tableToQuay.execute();
    turnAction[2+turnDirection][southernLane][northernLane].execute();
    quayToTop[southernLane][turnDirection].execute();
    turnAction[1-turnDirection][southernLane][sideLane].execute();
    // wait for next round...
  }





  //////////////////////////////////////////////////////////////////

    public void run() {
    System.out.println(Thread.currentThread().getName()+" is running..");
    try {
      this.script();
    } catch (Exception e){
      System.out.println("Error in SemMain.script1():"+e.getMessage());
      e.printStackTrace();
    }
  }

}

