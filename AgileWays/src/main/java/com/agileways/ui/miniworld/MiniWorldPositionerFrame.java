package com.agileways.ui.miniworld;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;
import net.agileframes.server.AgileSystem;
import net.agileframes.forces.xyaspace.XYATransform;
import net.jini.core.lookup.*;
import com.agileways.miniworld.lps.RemoteDistributor;

public class MiniWorldPositionerFrame extends JFrame {
  private int x,y,rotation,scale;
  private File file = null;

  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel5 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel10 = new JPanel();
  JPanel jPanel11 = new JPanel();
  JTextField scaleField = new JTextField();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JTextField rotField = new JTextField();
  JTextField yField = new JTextField();
  JTextField xField = new JTextField();
  JTextField fileNameField = new JTextField();
  JButton scalePlusBtn = new JButton();
  JButton scaleMinBtn = new JButton();
  JButton rotPlusBtn = new JButton();
  JButton rotMinBtn = new JButton();
  JButton yPlusBtn = new JButton();
  JButton yMinBtn = new JButton();
  JButton xPlusBtn = new JButton();
  JButton xMinBtn = new JButton();
  JButton cancelBtn = new JButton();
  JButton saveBtn = new JButton();

  public MiniWorldPositionerFrame() {
    try {
      if (!lookupDistributor()) {
        System.out.println("WE WILL QUIT");;
        System.exit(1);
      } else {System.out.println("We found the distributor!");}
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    MiniWorldPositionerFrame miniWorldPositionerFrame = new MiniWorldPositionerFrame();
  }
  private void jbInit() throws Exception {
    this.setResizable(false);
    this.setTitle("-- MiniWorldPositioner --");
    this.setSize(310,220);
    this.getContentPane().setLayout(flowLayout1);
    jPanel1.setPreferredSize(new Dimension(300, 30));
    jPanel2.setPreferredSize(new Dimension(300, 120));
    jPanel3.setPreferredSize(new Dimension(300, 30));
    jPanel4.setPreferredSize(new Dimension(100, 28));
    jLabel1.setText("FileName");
    jPanel5.setPreferredSize(new Dimension(100, 115));
    jLabel2.setPreferredSize(new Dimension(54, 21));
    jLabel2.setText("Scale");
    jPanel7.setPreferredSize(new Dimension(190, 28));
    jPanel8.setPreferredSize(new Dimension(50, 115));
    jPanel10.setPreferredSize(new Dimension(10, 21));
    jPanel11.setPreferredSize(new Dimension(135, 115));
    scaleField.setMaximumSize(new Dimension(45, 21));
    scaleField.setMinimumSize(new Dimension(45, 21));
    scaleField.setPreferredSize(new Dimension(45, 21));
    scaleField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        scaleField_focusLost(e);
      }
    });
    jLabel5.setText("Rotation");
    jLabel5.setPreferredSize(new Dimension(54, 21));
    jLabel6.setText("Y");
    jLabel6.setPreferredSize(new Dimension(54, 21));
    jLabel7.setText("X-trans");
    jLabel7.setPreferredSize(new Dimension(54, 21));
    rotField.setPreferredSize(new Dimension(45, 21));
    rotField.setMinimumSize(new Dimension(45, 21));
    rotField.setMaximumSize(new Dimension(45, 21));
    rotField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        rotField_focusLost(e);
      }
    });
    yField.setPreferredSize(new Dimension(45, 21));
    yField.setMinimumSize(new Dimension(45, 21));
    yField.setMaximumSize(new Dimension(45, 21));
    yField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        yField_focusLost(e);
      }
    });
    xField.setPreferredSize(new Dimension(45, 21));
    xField.setMinimumSize(new Dimension(45, 21));
    xField.setMaximumSize(new Dimension(45, 21));
    xField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        xField_focusLost(e);
      }
    });
    fileNameField.setPreferredSize(new Dimension(90, 21));
    fileNameField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fileNameField_actionPerformed(e);
      }
    });
    fileNameField.setMinimumSize(new Dimension(45, 21));
    fileNameField.setMaximumSize(new Dimension(45, 21));
    scalePlusBtn.setPreferredSize(new Dimension(45, 21));
    scalePlusBtn.setText("+");
    scalePlusBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        scalePlusBtn_actionPerformed(e);
      }
    });
    scaleMinBtn.setText("-");
    scaleMinBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        scaleMinBtn_actionPerformed(e);
      }
    });
    scaleMinBtn.setPreferredSize(new Dimension(45, 21));
    rotPlusBtn.setText("+");
    rotPlusBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rotPlusBtn_actionPerformed(e);
      }
    });
    rotPlusBtn.setPreferredSize(new Dimension(45, 21));
    rotMinBtn.setText("-");
    rotMinBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rotMinBtn_actionPerformed(e);
      }
    });
    rotMinBtn.setPreferredSize(new Dimension(45, 21));
    yPlusBtn.setText("+");
    yPlusBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        yPlusBtn_actionPerformed(e);
      }
    });
    yPlusBtn.setPreferredSize(new Dimension(45, 21));
    yMinBtn.setText("-");
    yMinBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        yMinBtn_actionPerformed(e);
      }
    });
    yMinBtn.setPreferredSize(new Dimension(45, 21));
    xPlusBtn.setText("+");
    xPlusBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        xPlusBtn_actionPerformed(e);
      }
    });
    xPlusBtn.setPreferredSize(new Dimension(45, 21));
    xMinBtn.setText("-");
    xMinBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        xMinBtn_actionPerformed(e);
      }
    });
    xMinBtn.setPreferredSize(new Dimension(45, 21));
    cancelBtn.setText("Cancel");
    cancelBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelBtn_actionPerformed(e);
      }
    });
    cancelBtn.setPreferredSize(new Dimension(120, 21));
    saveBtn.setPreferredSize(new Dimension(120, 21));
    saveBtn.setText("Save&Close");
    saveBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveBtn_actionPerformed(e);
      }
    });
    setBtn.setMinimumSize(new Dimension(60, 21));
    setBtn.setPreferredSize(new Dimension(73, 21));
    setBtn.setText("Set");
    setBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setBtn_actionPerformed(e);
      }
    });
    jPanel6.setPreferredSize(new Dimension(12, 10));
    this.getContentPane().add(jPanel1, null);
    jPanel1.add(jPanel4, null);
    jPanel4.add(jLabel1, null);
    jPanel1.add(jPanel7, null);
    jPanel7.add(fileNameField, null);
    jPanel7.add(setBtn, null);
    jPanel7.add(jPanel6, null);
    this.getContentPane().add(jPanel2, null);
    jPanel2.add(jPanel5, null);
    jPanel5.add(jLabel7, null);
    jPanel5.add(jLabel6, null);
    jPanel5.add(jLabel5, null);
    jPanel5.add(jLabel2, null);
    jPanel2.add(jPanel8, null);
    jPanel8.add(xField, null);
    jPanel8.add(yField, null);
    jPanel8.add(rotField, null);
    jPanel8.add(scaleField, null);
    jPanel2.add(jPanel11, null);
    jPanel11.add(xMinBtn, null);
    jPanel11.add(xPlusBtn, null);
    jPanel11.add(yMinBtn, null);
    jPanel11.add(yPlusBtn, null);
    jPanel11.add(rotMinBtn, null);
    jPanel11.add(rotPlusBtn, null);
    jPanel11.add(scaleMinBtn, null);
    jPanel11.add(scalePlusBtn, null);
    this.getContentPane().add(jPanel3, null);
    jPanel3.add(saveBtn, null);
    jPanel3.add(jPanel10, null);
    jPanel3.add(cancelBtn, null);

    this.setVisible(true);
    this.invalidate();
  }

  void xMinBtn_actionPerformed(ActionEvent e) {
    int x = Integer.parseInt(xField.getText());
    x--;
    xField.setText(String.valueOf(x));
    xField_focusLost(null);
  }

  void xPlusBtn_actionPerformed(ActionEvent e) {
    int x = Integer.parseInt(xField.getText());
    x++;
    xField.setText(String.valueOf(x));
    xField_focusLost(null);
  }

  void yMinBtn_actionPerformed(ActionEvent e) {
    int y = Integer.parseInt(yField.getText());
    y--;
    yField.setText(String.valueOf(y));
    yField_focusLost(null);
  }

  void yPlusBtn_actionPerformed(ActionEvent e) {
    int y = Integer.parseInt(yField.getText());
    y++;
    yField.setText(String.valueOf(y));
    yField_focusLost(null);
  }

  void rotMinBtn_actionPerformed(ActionEvent e) {
    int rotation = Integer.parseInt(rotField.getText());
    rotation--;
    rotField.setText(String.valueOf(rotation));
    rotField_focusLost(null);
  }

  void rotPlusBtn_actionPerformed(ActionEvent e) {
    int rotation = Integer.parseInt(rotField.getText());
    rotation++;
    rotField.setText(String.valueOf(rotation));
    rotField_focusLost(null);
  }

  void scaleMinBtn_actionPerformed(ActionEvent e) {
    int scale = Integer.parseInt(scaleField.getText());
    scale--;
    scaleField.setText(String.valueOf(scale));
    scaleField_focusLost(null);
  }

  void scalePlusBtn_actionPerformed(ActionEvent e) {
    int scale = Integer.parseInt(scaleField.getText());
    scale++;
    scaleField.setText(String.valueOf(scale));
    scaleField_focusLost(null);
  }

  void saveBtn_actionPerformed(ActionEvent e) {
    if (file == null ) { return; }
    try {
      FileOutputStream fos = new FileOutputStream(file);
      DataOutputStream dos = new DataOutputStream(fos);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(rotation);
      dos.writeInt(scale);
    } catch (Exception er) { er.printStackTrace(); }
    cancelBtn_actionPerformed(null);
  }

  void cancelBtn_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  void fileNameField_actionPerformed(ActionEvent e) {
    String fileName = fileNameField.getText();
    if (fileName.equals("")) { file = null; return; }
    file = new File(AgileSystem.agileframesDataPath + "MiniWorldPosition_"+fileName);
    try {
      if (file.exists()) {
        //open it!
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        x = dis.readInt();
        y = dis.readInt();
        rotation = dis.readInt();
        scale = dis.readInt();
      } else {
        //create it!
        file.createNewFile();
        x = 0;
        y = 0;
        rotation = 0;
        scale = 100;
      }
    } catch (Exception er) { er.printStackTrace(); }

    xField.setText(String.valueOf(x));
    yField.setText(String.valueOf(y));
    rotField.setText(String.valueOf(rotation));
    scaleField.setText(String.valueOf(scale));
  }


  void xField_focusLost(FocusEvent e) {
    if (xField.getText().equals("")) {return;}
    x = Integer.parseInt(xField.getText());
    transformDistributor();
  }

  void yField_focusLost(FocusEvent e) {
    if (yField.getText().equals("")) {return;}
    y = Integer.parseInt(yField.getText());
    transformDistributor();
  }

  void rotField_focusLost(FocusEvent e) {
    if (rotField.getText().equals("")) {return;}
    int rotation = Integer.parseInt(rotField.getText());
    while ( (rotation < 0) || (rotation > 360) ) {
      if (rotation < 0) { rotation += 360; }
      if (rotation > 360) { rotation -= 360; }
    }
    this.rotation = rotation;
    rotField.setText(String.valueOf(rotation));
    transformDistributor();
  }

  private int lastScale = 100;
  void scaleField_focusLost(FocusEvent e) {
    if (scaleField.getText().equals("")) {return;}
    int scale = Integer.parseInt(scaleField.getText());
    if (scale <= 0) { scale = lastScale;}
    this.scale = scale;
    lastScale = this.scale;
    scaleField.setText(String.valueOf(scale));
    transformDistributor();
  }

  private RemoteDistributor distributor = null;
  JButton setBtn = new JButton();
  JPanel jPanel6 = new JPanel();
  private boolean lookupDistributor() {
    ServiceTemplate st = new ServiceTemplate(null, new Class[] {com.agileways.miniworld.lps.RemoteDistributor.class} , null);
    Object obj = AgileSystem.lookup(st);
    if (obj == null) {
      System.out.println("Didnot find a RemoteDistributor");
      return false;
    }
    distributor = (RemoteDistributor)obj;
    if (distributor==null) {
      System.out.println("Found a distributor, but didnt find distributor (??)");
      return false;
    }
    return true;
  }
  private void transformDistributor() {
    double rad = Math.PI * ((double)rotation) / 180;
    XYATransform t = new XYATransform((double)x,(double)y,rad);
    double s = ((double)scale) / 100;
    if (distributor == null) {
      if (!lookupDistributor()) {
        System.out.println("NO DISTRIBUTOR FOUND!!");
        return;
      }
    }
    try { distributor.setTransformAndScale(t,s); }
    catch (Exception e) {e.printStackTrace(); }
  }

  void setBtn_actionPerformed(ActionEvent e) {
    File defFile = new File(AgileSystem.agileframesDataPath + "MiniWorldPositionData");
    try {
      if (!defFile.exists()) { file.createNewFile(); }
      FileOutputStream fos = new FileOutputStream(defFile);
      DataOutputStream dos = new DataOutputStream(fos);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(rotation);
      dos.writeInt(scale);
    } catch (Exception er) { er.printStackTrace(); }
  }
}