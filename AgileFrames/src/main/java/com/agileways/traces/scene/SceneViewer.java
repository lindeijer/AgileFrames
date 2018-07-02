package com.agileways.traces.scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.traces.Semaphore;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;

/**
 * Frame in which the SemaphoreViewers are displayed.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class SceneViewer extends JFrame {
  CrossoverScene crossoverScene = null;
  FlowLayout flowLayout = new FlowLayout();
  JPanel panel = new JPanel();
  SemaphoreViewer[] semViewer = new SemaphoreViewer[500];

  public SceneViewer(CrossoverScene crossoverScene) {
    this.crossoverScene = crossoverScene;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * View Frame and create all SemaphoreViewers.
   */
  private void jbInit() throws Exception {
    this.setTitle("SceneViewer");
    this.setVisible(true);
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        this_mouseClicked(e);
      }
    });
    this.setEnabled(true);
    this.setSize(300,310);
    this.setLocation(850,0);
    this.setResizable(true);
    this.getContentPane().setLayout(flowLayout);


    // create all semaphoreViewers
    int nr = 0;
    for (int stackNr=0;stackNr<crossoverScene.cT.numberOfStacks;stackNr++) {
      semViewer[nr] = new SemaphoreViewer(crossoverScene.semStackEntrance[stackNr],0,stackNr,0,this);
      crossoverScene.semStackEntrance[stackNr].setViewer(semViewer[nr]);
      nr++;
      semViewer[nr] = new SemaphoreViewer(crossoverScene.semCenterNorth[stackNr],1,stackNr,0,this);
      crossoverScene.semCenterNorth[stackNr].setViewer(semViewer[nr]);
      nr++;
      semViewer[nr] = new SemaphoreViewer(crossoverScene.semCenterSouth[stackNr],13,stackNr,0,this);
      crossoverScene.semCenterSouth[stackNr].setViewer(semViewer[nr]);
      nr++;
      if ((stackNr<crossoverScene.cT.numberOfStacks-2) && (stackNr>0)){
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semCenterStayNorth[stackNr],3,stackNr,0,this);
        crossoverScene.semCenterStayNorth[stackNr].setViewer(semViewer[nr]);
        nr++;
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semCenterStaySouth[stackNr],4,stackNr,0,this);
        crossoverScene.semCenterStaySouth[stackNr].setViewer(semViewer[nr]);
        nr++;
      }
      if (stackNr<crossoverScene.cT.numberOfStacks-1) {
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semParkExit[stackNr],6,stackNr,0,this);
        crossoverScene.semParkExit[stackNr].setViewer(semViewer[nr]);
        nr++;
      }
      for(int stackLane=0;stackLane<crossoverScene.cT.lanesPerStack+1;stackLane++) {
        if (stackLane<crossoverScene.cT.lanesPerStack) {
          semViewer[nr] = new SemaphoreViewer(crossoverScene.semStack[stackNr][stackLane],7,stackNr,stackLane,this);
          crossoverScene.semStack[stackNr][stackLane].setViewer(semViewer[nr]);
          nr++;
        }
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semStackExit[stackNr][stackLane],2,stackNr,stackLane,this);
        crossoverScene.semStackExit[stackNr][stackLane].setViewer(semViewer[nr]);
        nr++;
      }
      for(int stackLane=0;stackLane<crossoverScene.cT.lanesPerPark;stackLane++) {
        if (stackNr<crossoverScene.cT.numberOfStacks-1) {
          semViewer[nr] = new SemaphoreViewer(crossoverScene.semPark[stackNr][stackLane],9,stackNr,stackLane,this);
          crossoverScene.semPark[stackNr][stackLane].setViewer(semViewer[nr]);
          nr++;
          semViewer[nr] = new SemaphoreViewer(crossoverScene.semParkEntrance[stackNr][stackLane],5,stackNr,stackLane,this);
          crossoverScene.semParkEntrance[stackNr][stackLane].setViewer(semViewer[nr]);
          nr++;
        }
      }//for stackLane
    }//for stackNr
    for (int lanePosition1 = 0; lanePosition1<4; lanePosition1++) {
      for (int lanePosition2 = 0; lanePosition2<2; lanePosition2++) {
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semTurn[lanePosition1][lanePosition2],10,lanePosition1,lanePosition2,this);
        crossoverScene.semTurn[lanePosition1][lanePosition2].setViewer(semViewer[nr]);
        nr++;
      }
      for (int index = 0; index<crossoverScene.cT.NUMBER_OF_AGVS_AT_SIDES; index++) {
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semSideLane[lanePosition1][index],11,lanePosition1,index,this);
        crossoverScene.semSideLane[lanePosition1][index].setViewer(semViewer[nr]);
        nr++;
      }
    }
    for (int lanePosition = 0; lanePosition<2; lanePosition++) {
      for (int index = 0; index<crossoverScene.cT.NUMBER_OF_AGVS_AT_STACKLANE; index++) {
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semStackLane[lanePosition][index],8,lanePosition,index,this);
        crossoverScene.semStackLane[lanePosition][index].setViewer(semViewer[nr]);
        nr++;
        semViewer[nr] = new SemaphoreViewer(crossoverScene.semQuayLane[lanePosition][index],14,lanePosition,index,this);
        crossoverScene.semQuayLane[lanePosition][index].setViewer(semViewer[nr]);
        nr++;
      }
    }
    for (int ttableNr=0; ttableNr<crossoverScene.cT.NUMBER_OF_TURNTABLES; ttableNr++) {
      semViewer[nr] = new SemaphoreViewer(crossoverScene.semTtable[ttableNr],12,ttableNr,0,this);
      crossoverScene.semTtable[ttableNr].setViewer(semViewer[nr]);
      nr++;
    }

  }


  /**
   * Repaints this frame. Called by the system when needed.
   * The sizes of semaphores are made variable to the size of the frame.
   */
  public void repaint(){
    Graphics graphics = this.getContentPane().getGraphics();
    graphics.clearRect(0,0,getWidth(), getHeight());
    for (int nr=0; nr<semViewer.length; nr++) {
      if (semViewer[nr]!=null) {
        semViewer[nr].changed = true;
        semViewer[nr].xScale =  (this.getWidth()-10)/crossoverScene.cT.TOTAL_WIDTH;
        semViewer[nr].yScale =  this.getHeight()/crossoverScene.cT.DIST_TOP_QUAY;
        semViewer[nr].paint(graphics);
      }
    }
  }

  /**
   * Paints all SemaphoreViewers. Called when model is changed.
   */
  public void modelChanged(){
    Graphics graphics = this.getContentPane().getGraphics();
    for (int nr=0; nr<semViewer.length; nr++) {
      if (semViewer[nr]!=null) {semViewer[nr].paint(graphics);}
    }
  }

  /**
   * Calls repaint.
   */
  public void paint(Graphics g){
    repaint();
  }

  void this_mouseClicked(MouseEvent e) {
    this.modelChanged();
    this.repaint();
  }
}