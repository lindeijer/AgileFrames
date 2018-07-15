package com.agileways.ui.scene;

import java.awt.*;
import javax.swing.*;

import net.agileframes.core.traces.Scene;
import java.awt.event.*;

public class SemaphoreWatcher_SelectScene extends JDialog {
  JPanel panel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JComboBox jComboBox1 = new JComboBox();
  JButton jButton1 = new JButton();
  JPanel jPanel2 = new JPanel();
  SemaphoreWatcherFrame parent = null;
  Scene[] scenes = null;
  JPanel jPanel3 = new JPanel();

  public SemaphoreWatcher_SelectScene(SemaphoreWatcherFrame parent, boolean modal, Scene[] scenes) {
    super(parent, "Select Scene", modal);
    this.scenes = scenes;
    this.parent = parent;
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
//    this.setTitle(title);
    this.setSize(250,50);
    this.getContentPane().setLayout(flowLayout2);
    panel1.setLayout(flowLayout1);
    jPanel1.setPreferredSize(new Dimension(20, 10));
    jLabel1.setText("Select Scene:");
    panel1.setMinimumSize(new Dimension(500, 100));
    panel1.setPreferredSize(new Dimension(500, 150));
    jButton1.setText("Select");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel2.setPreferredSize(new Dimension(500, 20));
    jPanel3.setPreferredSize(new Dimension(300, 10));
    this.getContentPane().add(panel1, null);

    if (scenes != null) {
      for (int i = 0; i < scenes.length; i++) {
        try {
          if (scenes[i] != null) { jComboBox1.addItem(scenes[i].getName());  }
        }
        catch (Exception e) { e.printStackTrace(); }
      }
    }

    panel1.add(jPanel1, null);
    panel1.add(jLabel1, null);
    panel1.add(jPanel3, null);
    panel1.add(jComboBox1, null);
    panel1.add(jPanel2, null);
    panel1.add(jButton1, null);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    int index = jComboBox1.getSelectedIndex();
    if (index < 0) { dispose(); return; }
    parent.replySelectScene(scenes[index]);
    dispose();
  }


}