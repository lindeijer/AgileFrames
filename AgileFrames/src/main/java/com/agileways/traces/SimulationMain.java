package com.agileways.traces;

import com.agileways.forces.machine.agv.avatar.AvatarAGV;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.POS;
import com.agileways.forces.machine.agv.sim.SimAGV;
import net.agileframes.forces.trajectory.Start;
import com.agileways.traces.scene.jumboterminal.sceneactions.Circulate;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.vr.Virtuality;
import net.agileframes.forces.mfd.ActorProxy;
import com.agileways.traces.scene.jumboterminal.CrossoverAgv;
import com.agileways.traces.scene.SceneViewer;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;




/**
 * Main. File that should be runned in order to start up a Jumboterminal with
 * a number of simulated AGVs.
 *
 * @author Wierenga
 * @version 0.0.1
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SimulationMain extends JFrame {

  /** Number of simulated AGVs that will be driving in the terminal */
  private static int numberOfAgvs = 1;

  private static CrossoverScene crossoverScene;
  private static SimAGV[] agv;
  private static AvatarAGV[] avatar;
  private static Circulate[] jumboActions;
  private static ActorProxy[] actor;

  JLabel jLabel1 = new JLabel();
  JTextField numberOgSimAgvsTextField = new JTextField();
  JButton startExtSimButton = new JButton();

  /** Main. */
  public static void main(String[] args) {
	  
	  
    // set properties
    System.getProperties().put("agilesystem.mute","TRUE");

    // get the number of simulated agvs to create
    System.out.println("SimulationMain.main: getting the number is agvs to simulate");
    SimulationMain simulationMain = new SimulationMain();
    simulationMain.setSize(321,200);
    simulationMain.setVisible(true);

    try {
      synchronized(simulationMain) {
        simulationMain.wait();
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("SimulationMain.main: quitting");
    }
    // virtuality is used to create the 3d environment
    Virtuality virtuality = new Virtuality();

    // create simulated AGVs
    agv = new SimAGV[numberOfAgvs];
    avatar = new AvatarAGV[numberOfAgvs];
    jumboActions = new Circulate[numberOfAgvs];
    actor = new ActorProxy[numberOfAgvs];
    //
    for (int agvNr=0;agvNr<numberOfAgvs;agvNr++) {
      try {
        agv[agvNr] = new SimAGV("AGV"+agvNr);
        System.out.println("agv"+agvNr+".toString()="+agv[agvNr].toString());
      }
      catch (Exception e) {
        System.out.println("Error in Main while creating AGVs: "+e);
        e.printStackTrace();
      }
      // create avatar for every AGV and put it in the 3d world
      avatar[agvNr] = new AvatarAGV(agv[agvNr],1);
      virtuality.add(avatar[agvNr].getBG());
      avatar[agvNr].setGeometryAndAppearanceID(1,(agvNr%7));

      // create actor for every AGV
      actor[agvNr] = new ActorProxy(agv[agvNr],null,null,null);
    }

    //
    // create the JumboScene with all its semaphores and moves
    try {
      float scale = 1f;
      /** Translation of entire terminal in x direction */
      float xTrans = -60 * scale;
      /** Translation of entire terminal in y direction */
      float yTrans = -15 * scale;
      crossoverScene = new CrossoverScene("JUMBO",
                             new POSTransform(xTrans,yTrans,0,scale),
                             // 5,null);
                             // 6,null);
                             7,null);
    }
    catch (Exception e) {
      System.out.println("Exception in SemMain.main while starting jumboScene= " + e.getMessage());
      e.printStackTrace();
    }

    // add visualization of the trajectories to the 3d world
    crossoverScene.jumboAvatar.setState(crossoverScene.jumboBody.getState());
    virtuality.add(crossoverScene.jumboAvatar.getLayout());
    // display surroundings (QuayCranes, Pillars, etc.) in 3d world
    crossoverScene.jumboAvatar.display(virtuality);

    // create the scene's semaphore viewer
    if (true) { SceneViewer sceneViewer = new SceneViewer(crossoverScene); }

    // create all Scene Actions for every AGV
    System.out.println("Starting AGVs...");
    for (int agvNr=0;agvNr<numberOfAgvs;agvNr++) {
      CrossoverAgv crossoverAgv = new CrossoverAgv(crossoverScene);
      Circulate circulate = new Circulate(crossoverScene);
      actor[agvNr].acceptJob(circulate,crossoverAgv);
    }

    // 3d world is finished
    virtuality.end();
  }

  public SimulationMain() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    jLabel1.setText("number of simulated agvs:");
    jLabel1.setBounds(new Rectangle(21, 39, 154, 22));
    this.getContentPane().setLayout(null);
    this.setTitle("CrossoverTerminal Peripheral Simulation");
    numberOgSimAgvsTextField.setText("5");
    numberOgSimAgvsTextField.setBounds(new Rectangle(176, 40, 117, 21));
    startExtSimButton.setText("Start Peripheral Simulation");
    startExtSimButton.setBounds(new Rectangle(75, 82, 224, 27));
    startExtSimButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        startExtSimButton_mouseClicked(e);
      }
    });
    this.getContentPane().add(jLabel1, null);
    this.getContentPane().add(numberOgSimAgvsTextField, null);
    this.getContentPane().add(startExtSimButton, null);
  }

  private synchronized void startExtSimButton_mouseClicked(MouseEvent e) {
    String numberOgSimAgvsText = this.numberOgSimAgvsTextField.getText();
    try {
      numberOfAgvs = Integer.parseInt(numberOgSimAgvsText);
    } catch (Exception ex) {
      ex.printStackTrace();
      numberOgSimAgvsTextField.setText("enter an integer");
      System.out.println("SimulationMain.startExtSimButton_mouseClicked: enter an integer");
      return;
    }
    this.notify();
  }
    /*
    if (false) { AgvTrackerFrame.setAgv(actor.machine);  }
    if (false) {
      try{
        actor.claimFrame.setTitle("ClaimFrame For "+agv.getName()+"  (actor="+actor.toString()+")\n");
        actor.claimFrame.setVisible(true);
        actor.claimFrame.setEnabled(true);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
    */
}
