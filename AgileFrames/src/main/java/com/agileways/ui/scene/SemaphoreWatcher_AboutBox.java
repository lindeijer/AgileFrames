package com.agileways.ui.scene;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class SemaphoreWatcher_AboutBox extends JDialog implements ActionListener {

  JPanel panel1 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JButton button1 = new JButton();
  ImageIcon imageIcon;
  FlowLayout flowLayout2 = new FlowLayout();
  String product = "Your Product Name";
  String version = "";
  String copyright = "Copyright (c) 1999";
  String comments = "Implementation of generic Forces in Vehicle-Forces.";
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  public SemaphoreWatcher_AboutBox(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try  {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    //imageControl1.setIcon(imageIcon);
    pack();
  }

  private void jbInit() throws Exception  {
    //imageIcon = new ImageIcon(getClass().getResource("your image name goes here"));
    this.setTitle("About");
    setResizable(false);
    panel1.setLayout(flowLayout1);
    button1.setPreferredSize(new Dimension(51, 20));
    button1.setText("OK");
    button1.addActionListener(this);
    panel1.setMinimumSize(new Dimension(300, 150));
    panel1.setPreferredSize(new Dimension(320, 150));
    insetsPanel1.setPreferredSize(new Dimension(300, 30));
    jPanel1.setPreferredSize(new Dimension(300, 70));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel1.setText("AgileFrames - Semaphore Watcher");
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel2.setText("TU Delft, 2001");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel3.setText("Views status of semaphores in a Scene");
    this.getContentPane().add(panel1, null);
    panel1.add(jPanel1, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jLabel3, null);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, null);

    this.setSize(50,10);
  }

  protected void processWindowEvent(WindowEvent e) {
    if(e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  void cancel() {
    dispose();
  }

  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == button1) {
      cancel();
    }
  }
}