
/**
 * Title:        sceneDrawer<p>
 * Description:  Draws the scene of a high-performance container terminal<p>
 * Copyright:    Copyright (c) Herman Wierenga<p>
 * Company:      TU Delft<p>
 * @author Herman Wierenga
 * @version 1.0
 */
package com.agileways.traces;

public class VirtualitySemMain { /*

  public VirtualitySemMain() {
  }

  public static final int numberOfAgvs = 100;

  public static JumboScene jumboScene;
  public static AGV[] agv = new AGV[numberOfAgvs];
  public static AvatarAGV[] avatar = new AvatarAGV[numberOfAgvs];

  public static void main(String[] args) {
    System.getProperties().put("agilesystem.mute","FALSE");
    Virtuality virtuality = new Virtuality();

    ////////////// AGVS from JLS ///////////////////////////////////////////
    for (int agvNr=10;agvNr<numberOfAgvs;agvNr++) {
      try {
        String agvName = "AGV" + agvNr;
        ServiceID agvServiceID = null;
        Class[] agvClass = { net.agileframes.core.forces.Machine.class };
        Entry[] agvAttributes = new Entry[] {
          new Name(agvName)
        };
        ServiceTemplate agvServiceTemplate = new ServiceTemplate(
          agvServiceID,
          agvClass,
          agvAttributes
        );
        ServiceItem[] serviceItems = AgileSystem.lookup(agvServiceTemplate,1);
        ServiceItem agvServiceItem = null;
        if (serviceItems.length!=0) {
          agvServiceItem = serviceItems[0];
          agv[agvNr] = (AGV) agvServiceItem.service;
          AvatarFactory avatarFactory = (AvatarFactory)agvServiceItem.attributeSets[1];
          avatar[agvNr] = (AvatarAGV)avatarFactory.getAvatar((Body)agv[agvNr]);
        }
        if ( agv[agvNr] != null) {
          System.out.println("agv found:"+agv[agvNr].toString() + " and avatar="  + avatar[agvNr].toString());
        }
      }
      catch (Exception e) {
        System.out.println("Error in Main while creating AGVs: "+e);
        e.printStackTrace();
      }
      if ( agv[agvNr] != null) {
        avatar[agvNr] = new AvatarAGV((Body)agv[agvNr],1);
        virtuality.add(avatar[agvNr].getBG());
        avatar[agvNr].setGeometryAndAppearanceID(1,(agvNr%7));
      }
      // avatar[agvNr].setGeometryID((agvNr%7));
    }


    SurroundingsJumboScene.getSurroundings(virtuality);

    ////////Jumbo Scene/////////////////////////////////
    try {jumboScene = new JumboScene("JUMBO",new int[]{},new int[]{},new float[]{},new float[]{});}
    catch (Exception e) {System.out.println("Exception in SemMain.main while starting jumboScene= " + e.getMessage());e.printStackTrace();}

    JumboTerminal.jumboAvatar.setState(JumboTerminal.jumbo.getState());
    virtuality.add(JumboTerminal.jumboAvatar.getLayout());
    virtuality.end();
  }

   */
}