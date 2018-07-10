package com.agileways.ui.scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.core.traces.Scene;

import net.agileframes.traces.viewer.SemaphoreProperties;
import net.agileframes.traces.viewer.SceneViewer;
import net.agileframes.traces.viewer.SemaphoreViewer;



/**
 * Frame in which the SemaphoreViewers are displayed.
 *
 * @author Wierenga
 * @version 0.1
 */

public class SemaphoreWatcherFrame extends JFrame implements SceneViewer {
  //-- Attributes --
  FlowLayout flowLayout = new FlowLayout();
  JPanel panel = new JPanel();

  private Scene[] scenes = null;
  private Scene scene = null;
  private SemaphoreViewer[] semViewers = new SemaphoreViewer[] {};
  private double xScale = 1;
  private double yScale = 1;
  public double w = 300;//width
  public double h = 300;//height
  private double x = 700;
  private double y = 0;

  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuScene = new JMenu();
  JMenuItem jMenuSceneSelect = new JMenuItem();
  JMenuItem jMenuSceneProps = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  SemaphoreWatcher parent = null;

  public SemaphoreWatcherFrame(SemaphoreWatcher parent) {
    this.parent = parent;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public void setSemaphoreViewers(SemaphoreViewer[] viewers) {
    if (semViewers != null) { for (int i=0; i < semViewers.length; i++) { remove(semViewers[i]); } }
    this.semViewers = viewers;
    this.connectionLost = false;
    if (viewers != null) {
      for (int i=0; i < viewers.length; i++) {
        viewers[i].setGraphics(this.getContentPane().getGraphics());
        getContentPane().add(viewers[i]);
      }
    }
    this.repaint();
  }
  public Scene getSelectedScene() { return scene; }

  /**
   * View Frame and create all SemaphoreViewers.
   */
  public void jbInit() throws Exception {
    this.getContentPane().setLayout(flowLayout);

    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        exit(e);
      }
    });
    jMenuScene.setText("Scene");
    jMenuSceneSelect.setText("Select Scene");
    jMenuSceneSelect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectScene(e);
      }
    });
    jMenuSceneProps.setText("Properties");
    jMenuSceneProps.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setProperties(e);
      }
    });
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        about(e);
      }
    });
    jMenuFile.add(jMenuFileExit);
    jMenuScene.add(jMenuSceneSelect);
    jMenuScene.add(jMenuSceneProps);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuScene);
    jMenuBar1.add(jMenuHelp);

    this.setJMenuBar(jMenuBar1);


    this.setSize((int)(w * xScale), (int)(h * yScale));
    this.setLocation((int)x, (int)y);
    setVisible(true);
    setEnabled(true);
    invalidate();

  }


  /**
   * Paints this frame. Called by the system when needed.
   * The sizes of semaphores are made variable to the size of the frame.
   */
  private boolean connectionLost = false;
  public void paint(Graphics graphics){
    graphics.clearRect(0, 0, getWidth(), getHeight());

    xScale = ((double)getWidth())/w;
    yScale = ((double)getHeight())/(h+30);

    for (int nr = 0; nr < semViewers.length; nr++) {
      if (semViewers[nr]!=null) {
        semViewers[nr].changed = true;
        semViewers[nr].paint(graphics);
        if ( (semViewers[nr].connectionLost) && (!connectionLost) ) {
          for (int i = 0; i < semViewers.length; i++) { semViewers[i].connectionLost = true; }
          connectionLost = true;
        }
      }
    }
    this.jMenuBar1.repaint();
  }


  /**
   * Paints all SemaphoreViewers. Called when model is changed.
   */
  public void modelChanged(){
    //System.out.println("SemaphoreWatcherFrame: modelChanged");
    Graphics graphics = this.getContentPane().getGraphics();
    for (int nr = 0; nr < semViewers.length; nr++) {
      if (semViewers[nr] != null) { semViewers[nr].paint(graphics); }
    }
  }

  // menu items
  void selectScene(ActionEvent e) {
    scenes = parent.getScenes();
    SemaphoreWatcher_SelectScene selectDialog = new SemaphoreWatcher_SelectScene(this, true, scenes);
    selectDialog.setLocation(100,100);
    selectDialog.show();
  }

  void setProperties(ActionEvent e) {
    if (scene == null) { return; }
    SemaphoreWatcher_SetProps propsDialog = new SemaphoreWatcher_SetProps(this, true, scene);
    propsDialog.setLocation(100,100);
    propsDialog.setSize(350,400);
    propsDialog.show();
  }

  void about(ActionEvent e) {
    SemaphoreWatcher_AboutBox about = new SemaphoreWatcher_AboutBox(this);
    Dimension aboutSize = about.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    about.setSize(320,150);
    about.setLocation((frmSize.width - aboutSize.width) / 2 + loc.x, (frmSize.height - aboutSize.height) / 2 + loc.y);
    about.setModal(true);
    about.show();
  }

  /**File | Exit action performed*/
  public void exit(ActionEvent e) {
    System.exit(0);
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      exit(null);
    }
  }

  public void replySelectScene(Scene scene) {
    this.scene = scene;
    parent.watchScene(scene);
  }

  public double getXScale() { return xScale; }
  public double getYScale() { return yScale; }

}
