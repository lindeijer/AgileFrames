package com.agileways.traces.scene.jumboterminal;

import net.agileframes.forces.mfd.ActorProxy;

/**
 * This object makes sure AGVs keep running on the cT.
 *
 * StackNumbers, ParkNumbers and other indices are chosen randomly and
 * Scene Actions are being created (and sent) to the actors.
 * In the final program this object will not be needed anymore because Services
 * will take care of everything.
 *
 * @author Wierenga,Lindeijer,Evers
 * @version 0.0.1
 */

 public class CrossoverAgv {

  private CrossoverScene crossoverScene;
  private CrossoverTerminal cT;

  /**
   * Constructor. Program starts running automatically after calling this constructor.
   *
   * @param actor             ActorProxy for which SceneActions are created
   * @param crossoverScene        JumboScene where the actor drives its rounds
   * @param crossoverSceneActions JumboSceneActions that contain the prefab SceneActions. Dynamic
   *                          SceneActions are created during run-time
   *
   */
  public CrossoverAgv(CrossoverScene crossoverScene) {
    this.crossoverScene = crossoverScene;
    this.cT = CrossoverScene.cT;
  }

  private int turnDirection;
  private int quayAreaID;
  // private long sleepingTimeStack,sleepingTimePark;

  public void hello(int turnDirection) {
    this.turnDirection = turnDirection;
  }

  public boolean toCirculate() { return true; }


  public int getTurnDirection() {
    System.out.print("CrossoverAgv.getTurnDirection: ");
    if (quayAreaID<(cT.numberOfStacks-1)/2) {
      turnDirection=1; // go via west
      System.out.println("1=WEST");
    }
    else {
      turnDirection=0; // go via east
      System.out.println("0=EAST");
    }
    return turnDirection;
  }



  public int getStackAreaID() {
    int stackAreaID;
    if (turnDirection==0)
    { // from east side
      int max = cT.numberOfStacks - 1;
      int min = (int)Math.ceil(cT.numberOfStacks*0.5);
      stackAreaID =  min + (int)Math.round( (max-min) * Math.random() );
    }
    else
    { // from west side
      stackAreaID = (int) Math.floor(cT.numberOfStacks * Math.random()/2);
    }
    System.out.println("CrossoverAgv.getStackAreaID()=" + stackAreaID + " turnDirection=" + turnDirection);
    return stackAreaID;
  }

  public int getQuayAreaID()
  {
    this.quayAreaID = (int) Math.floor((cT.numberOfStacks-1) * Math.random());
    System.out.println("CrossoverAgv.getQuayAreaID()=" + quayAreaID);
    return quayAreaID;
  }


  public int getQuayCraneID(int parkLane)
  {
    quayCraneID = (int)
    (cT.NUMBER_OF_TURNTABLES  *
     (
      ((float)(quayAreaID*(cT.lanesPerPark)+parkLane+1))
      /
      (cT.lanesPerPark*(cT.numberOfStacks-1))
     )
    );
    if (quayCraneID<0) { quayCraneID=0; }
    if (quayCraneID>cT.NUMBER_OF_TURNTABLES-1) {
        quayCraneID=cT.NUMBER_OF_TURNTABLES-1; }
    System.out.println("CrossoverAgv.getQuayCraneID()=" + quayCraneID +
                       " quayArea=" + quayAreaID +
                       " quayParkArea=" + parkLane);

    return quayCraneID;
  }

  int quayCraneID;

  public int getQuayCraneID() {
    return quayCraneID;
  }




}














