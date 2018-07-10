package com.agileways.ui.scene;

import java.awt.*;
import javax.swing.*;

import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.SemaphoreRemote;
import java.awt.event.*;
import net.agileframes.traces.viewer.SemaphoreProperties;

public class SemaphoreWatcher_SetProps extends JDialog {
  FlowLayout flowLayout2 = new FlowLayout();
  Scene scene = null;
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel9 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JComboBox semCombo = new JComboBox();
  JTextField xText = new JTextField();
  JTextField yText = new JTextField();
  JComboBox shapeCombo = new JComboBox();
  JLabel jLabel7 = new JLabel();
  JPanel jPanel10 = new JPanel();
  JButton okBtn = new JButton();
  JPanel jPanel11 = new JPanel();
  JButton applyBtn = new JButton();
  private SemaphoreWatcherFrame parent = null;
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JTextField frameWidthText = new JTextField();
  JTextField frameHeightText = new JTextField();
  JTextField widthText = new JTextField();
  JTextField heightText = new JTextField();

  public SemaphoreWatcher_SetProps(SemaphoreWatcherFrame parent, boolean modal, Scene scene) {
    super(parent, "Set Parameters", modal);
    this.scene = scene;
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
    this.setSize(350,300);
    this.getContentPane().setLayout(flowLayout2);

    if ( (scene != null) && (scene.getSemaphores() != null) ) {
      SemaphoreRemote[] semaphores = scene.getSemaphores();
      for (int i = 0; i < semaphores.length; i++) {
        try { semCombo.addItem(semaphores[i].getName()); }
        catch (Exception e) { e.printStackTrace(); }
      }
    }
    shapeCombo.addItem("Rectangle");
    shapeCombo.addItem("Oval");
    fillIn(0);

    jPanel1.setMinimumSize(new Dimension(340, 170));
    jPanel1.setPreferredSize(new Dimension(340, 170));
    this.setResizable(false);
    jPanel4.setMinimumSize(new Dimension(85, 170));
    jPanel4.setPreferredSize(new Dimension(85, 170));
    jPanel3.setMinimumSize(new Dimension(160, 170));
    jPanel3.setPreferredSize(new Dimension(160, 170));
    jPanel2.setMinimumSize(new Dimension(80, 170));
    jPanel2.setPreferredSize(new Dimension(80, 170));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setMaximumSize(new Dimension(80, 21));
    jLabel1.setMinimumSize(new Dimension(80, 21));
    jLabel1.setPreferredSize(new Dimension(80, 21));
    jLabel1.setText("Semaphore:");
    jLabel2.setMaximumSize(new Dimension(80, 21));
    jLabel2.setMinimumSize(new Dimension(80, 21));
    jLabel2.setPreferredSize(new Dimension(80, 21));
    jLabel2.setText("X-position:");
    jLabel3.setMaximumSize(new Dimension(80, 21));
    jLabel3.setMinimumSize(new Dimension(80, 21));
    jLabel3.setPreferredSize(new Dimension(80, 21));
    jLabel3.setText("Y-position:");
    jLabel4.setMaximumSize(new Dimension(80, 21));
    jLabel4.setMinimumSize(new Dimension(80, 21));
    jLabel4.setPreferredSize(new Dimension(80, 21));
    jLabel4.setText("Shape:");
    jPanel8.setMinimumSize(new Dimension(85, 100));
    jPanel8.setPreferredSize(new Dimension(85, 100));
    jPanel5.setMinimumSize(new Dimension(340, 100));
    jPanel5.setPreferredSize(new Dimension(340, 100));
    jPanel7.setMinimumSize(new Dimension(160, 100));
    jPanel7.setPreferredSize(new Dimension(160, 100));
    jPanel6.setMinimumSize(new Dimension(80, 100));
    jPanel6.setPreferredSize(new Dimension(80, 100));
    jLabel5.setMaximumSize(new Dimension(80, 21));
    jLabel5.setMinimumSize(new Dimension(80, 21));
    jLabel5.setPreferredSize(new Dimension(80, 21));
    jLabel5.setText("Frame-Width:");
    jLabel6.setMaximumSize(new Dimension(80, 21));
    jLabel6.setMinimumSize(new Dimension(80, 21));
    jLabel6.setPreferredSize(new Dimension(80, 21));
    jLabel6.setText("Frame-Height:");
    xText.setMinimumSize(new Dimension(125, 21));
    xText.setPreferredSize(new Dimension(125, 21));
    yText.setMinimumSize(new Dimension(125, 21));
    yText.setPreferredSize(new Dimension(125, 21));
    semCombo.setMaximumSize(new Dimension(125, 21));
    semCombo.setMinimumSize(new Dimension(125, 21));
    semCombo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        semCombo_actionPerformed(e);
      }
    });
    shapeCombo.setMaximumSize(new Dimension(125, 21));
    shapeCombo.setMinimumSize(new Dimension(125, 21));
    jLabel7.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel7.setMaximumSize(new Dimension(80, 21));
    jLabel7.setMinimumSize(new Dimension(80, 21));
    jLabel7.setPreferredSize(new Dimension(80, 21));
    jLabel7.setText("Frame");
    jPanel10.setMinimumSize(new Dimension(125, 21));
    jPanel10.setPreferredSize(new Dimension(125, 21));
    jPanel9.setMinimumSize(new Dimension(340, 50));
    jPanel9.setPreferredSize(new Dimension(340, 50));
    okBtn.setText("OK");
    okBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okBtn_actionPerformed(e);
      }
    });
    jPanel11.setMinimumSize(new Dimension(240, 27));
    jPanel11.setPreferredSize(new Dimension(240, 27));
    applyBtn.setText("Apply");
    applyBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyBtn_actionPerformed(e);
      }
    });
    jLabel8.setText("Height:");
    jLabel8.setPreferredSize(new Dimension(80, 21));
    jLabel8.setMinimumSize(new Dimension(80, 21));
    jLabel8.setMaximumSize(new Dimension(80, 21));
    jLabel9.setText("Width:");
    jLabel9.setPreferredSize(new Dimension(80, 21));
    jLabel9.setMinimumSize(new Dimension(80, 21));
    jLabel9.setMaximumSize(new Dimension(80, 21));
    frameWidthText.setMinimumSize(new Dimension(125, 21));
    frameWidthText.setPreferredSize(new Dimension(125, 21));
    frameHeightText.setMinimumSize(new Dimension(125, 21));
    frameHeightText.setPreferredSize(new Dimension(125, 21));
    widthText.setMinimumSize(new Dimension(125, 21));
    widthText.setPreferredSize(new Dimension(125, 21));
    heightText.setMinimumSize(new Dimension(125, 21));
    heightText.setPreferredSize(new Dimension(125, 21));
    this.getContentPane().add(jPanel1, null);
    jPanel1.add(jPanel4, null);
    jPanel4.add(jLabel1, null);
    jPanel4.add(jLabel2, null);
    jPanel4.add(jLabel3, null);
    jPanel4.add(jLabel9, null);
    jPanel4.add(jLabel8, null);
    jPanel4.add(jLabel4, null);
    jPanel1.add(jPanel3, null);
    jPanel3.add(semCombo, null);
    jPanel3.add(xText, null);
    jPanel3.add(yText, null);
    jPanel3.add(widthText, null);
    jPanel3.add(heightText, null);
    jPanel3.add(shapeCombo, null);
    jPanel1.add(jPanel2, null);
    jPanel2.add(applyBtn, null);
    this.getContentPane().add(jPanel5, null);
    jPanel5.add(jPanel8, null);
    jPanel8.add(jLabel7, null);
    jPanel8.add(jLabel5, null);
    jPanel8.add(jLabel6, null);
    jPanel5.add(jPanel7, null);
    jPanel7.add(jPanel10, null);
    jPanel7.add(frameWidthText, null);
    jPanel7.add(frameHeightText, null);
    jPanel5.add(jPanel6, null);
    this.getContentPane().add(jPanel9, null);
    jPanel9.add(okBtn, null);
    jPanel9.add(jPanel11, null);

  }

  void okBtn_actionPerformed(ActionEvent e) {
/*    int index = semCombo.getSelectedIndex();
    if (index < 0) { dispose(); return; }
    parent.replySelectScene(scenes[index]);*/
    dispose();
  }


  void applyBtn_actionPerformed(ActionEvent e) {
    // check input:
    int sem = semCombo.getSelectedIndex();
    if (sem < 0) { return; }
    double x = Double.parseDouble(xText.getText());
    double y = Double.parseDouble(yText.getText());
    double w = Double.parseDouble(widthText.getText());
    double h = Double.parseDouble(heightText.getText());
    double fw = Double.parseDouble(frameWidthText.getText());
    double fh = Double.parseDouble(frameHeightText.getText());
    if ( x < 0) { x = 0; xText.setText("0.0");}
    if ( x > parent.w) { x = parent.w; xText.setText(String.valueOf(parent.w));}
    if ( y < 0) { y = 0; yText.setText("0.0");}
    if ( y > parent.h) { y = parent.h; yText.setText(String.valueOf(parent.h));}
    if ( w < 0) { w = 0; widthText.setText("0.0");}
    if ( w > parent.getWidth()) { w = parent.getWidth(); widthText.setText(String.valueOf(parent.getWidth()));}
    if ( h < 0) { h = 0; heightText.setText("0.0");}
    if ( h > parent.getHeight()) { h = parent.getHeight(); heightText.setText(String.valueOf(parent.getHeight()));}
    if (fw < 0) { fw = 0; frameWidthText.setText("0.0");}
    if (fw > 1000) { fw = 1000; frameWidthText.setText("1000.0");}
    if (fh < 0) { fh = 0; frameHeightText.setText("0.0"); }
    if (fh > 1000) { fh = 1000; frameHeightText.setText("1000.0");}

    int shape = shapeCombo.getSelectedIndex();
    if (shape < 0) { shape = 0; }
    SemaphoreProperties props = new SemaphoreProperties(x, y, w, h, shape);
    SemaphoreProperties frameProps = new SemaphoreProperties(parent.getX(), parent.getY(), fw, fh, 0);

    parent.parent.setSemaphoreProps(sem, props);
    parent.parent.setSemaphoreProps(semCombo.getItemCount(), frameProps);
    parent.repaint();
  }

  void semCombo_actionPerformed(ActionEvent e) {
    int sem = semCombo.getSelectedIndex();
    if (sem < 0) { return; }
    fillIn(sem);
  }

  private void fillIn(int semNr) {
    SemaphoreProperties props = parent.parent.getProps(semNr);
    xText.setText(String.valueOf(props.x));
    yText.setText(String.valueOf(props.y));
    shapeCombo.setSelectedIndex(props.shape);
    widthText.setText(String.valueOf(props.width));
    heightText.setText(String.valueOf(props.height));

    props = parent.parent.getProps(semCombo.getItemCount());
    frameWidthText.setText(String.valueOf(props.width));
    frameHeightText.setText(String.valueOf(props.height));
  }

}