
/**
 * Title:        sceneDrawer<p>
 * Description:  Draws the scene of a high-performance container terminal<p>
 * Copyright:    Copyright (c) Herman Wierenga<p>
 * Company:      TU Delft<p>
 * @author Herman Wierenga
 * @version 1.0
 */
package com.agileways.traces;

public class BlindSceneMain {  /*

  public BlindSceneMain() {
  }

  public static final int numberOfAgvs = 100;

  public static JumboScene jumboScene;
  //public static SimAGV[] agv = new SimAGV[numberOfAgvs];

  public static JumboSceneActions[] jumboActions = new JumboSceneActions[numberOfAgvs];
  public static ActorProxy[] actor = new ActorProxy[numberOfAgvs];

  public static int activeAgvs = 0;
  public static void main(String[] args) {
    System.getProperties().put("agilesystem.mute","FALSE");
    for (int agvNr=10;agvNr<numberOfAgvs;agvNr++) {
      try {
        String actorName = "AGV"+ agvNr;
        ServiceID actorServiceID = null;
        Class[] actorClass = { net.agileframes.core.forces.Actor.class };
        Entry[] actorAttributes = new Entry[] {
          new Name(actorName)
        };
        ServiceTemplate actorServiceTemplate = new ServiceTemplate(
          actorServiceID,
          actorClass,
          actorAttributes
        );
        actor[agvNr] = (ActorProxy)AgileSystem.lookup(actorServiceTemplate);
        if (actor[agvNr]!=null) {System.out.println("actor: "+actor[agvNr].toString());activeAgvs++;}
      }
      catch (Exception e) {
        System.out.println("Error in Main while creating AGVs: "+e);
        e.printStackTrace();
      }
    }

    System.out.println(activeAgvs+"  active AGVs were found");

    ////////Jumbo Scene/////////////////////////////////
    try {jumboScene = new JumboScene("JUMBO",new int[]{},new int[]{},new float[]{},new float[]{});}
    catch (Exception e) {System.out.println("Exception in SemMain.main while starting jumboScene= " + e.getMessage());e.printStackTrace();}

    System.out.println("Initializing sceneActions...");
    int nr = 0;
    for (int agvNr=10;agvNr<numberOfAgvs;agvNr++) {
      if (actor[agvNr]!=null) {
        nr++;
        jumboActions[agvNr] = new JumboSceneActions(jumboScene, actor[agvNr]);
        System.out.println((int)(100.0f*((float)nr)/activeAgvs)+"% done.");
      }
    }

    SceneViewer sceneViewer = new SceneViewer(jumboScene);
    JumboTerminal.jumboAvatar.setState(JumboTerminal.jumbo.getState());
    for (int agvNr=10;agvNr<numberOfAgvs;agvNr++) {
      if (actor[agvNr]!=null) {
        JumboSceneActionProvider jumboSceneActionProvider =
          new JumboSceneActionProvider(actor[agvNr],jumboScene,jumboActions[agvNr]);// starts up automatically
      }
    }
  }

  */

}