package com.agileways.traces.scene;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import net.agileframes.core.forces.Machine;

/**
 * Frame that contains information about the AGVs in the Scene.
 *
 * The Frame will be created in its static method.
 * AGV needs to be added by using method setAgv and after that
 * being updated by setAgvInfo every time when needed.
 *
 * @author Wierenga
 * @version 0.0.1
 */
public class AgvTrackerFrame extends JFrame{
  private static Machine[] agvList;
  private static int index = 0;
  private static int[] stackNr;
  private static int[] stackLane;
  private static int[] parkNr;
  private static int[] parkLane;
  private static int[] turnDirection;
  private static int agvNr =0;

  TitledBorder titledBorder1;
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  JLabel jLabel1 = new JLabel();
  static JComboBox jComboBox1 = new JComboBox();
  JPanel jPanel13 = new JPanel();
  JLabel jLabel2 = new JLabel();
  static JTextField jTextField1 = new JTextField();
  static JPanel jPanel7 = new JPanel();
  JLabel jLabel3 = new JLabel();
  static JTextField jTextField2 = new JTextField();
  JPanel jPanel14 = new JPanel();
  static JTextField jTextField3 = new JTextField();
  static JTextField jTextField4 = new JTextField();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  static JRadioButton jRadioButton1 = new JRadioButton();
  static JRadioButton jRadioButton2 = new JRadioButton();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel16 = new JPanel();
  JPanel jPanel9 = new JPanel();
  static JTextField jTextField6 = new JTextField();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  static JTextField jTextField7 = new JTextField();
  JPanel jPanel17 = new JPanel();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel7 = new JLabel();

  /** Constructor that will initialize */
  public AgvTrackerFrame() {
    try  {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /** Initialize this Frame */
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    this.setSize(300,330);
    this.setLocation(850,310);
    this.setVisible(true);
    this.getContentPane().setLayout(flowLayout1);
    this.setEnabled(true);
    this.setTitle("AgvTracer");

    jPanel1.setAlignmentX((float) 0.0);
    jPanel1.setAlignmentY((float) 0.0);
    jPanel1.setPreferredSize(new Dimension(300, 40));
    jPanel2.setAlignmentX((float) 0.0);
    jPanel2.setAlignmentY((float) 0.0);
    jPanel2.setPreferredSize(new Dimension(300, 20));
    jPanel4.setAlignmentX((float) 0.0);
    jPanel4.setAlignmentY((float) 0.0);
    jPanel4.setPreferredSize(new Dimension(300, 40));
    jPanel3.setAlignmentX((float) 0.0);
    jPanel3.setAlignmentY((float) 0.0);
    jPanel3.setPreferredSize(new Dimension(300, 40));
    jPanel5.setAlignmentX((float) 0.0);
    jPanel5.setAlignmentY((float) 0.0);
    jPanel5.setPreferredSize(new Dimension(300, 40));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel1.setAlignmentY((float) 0.0);
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
    jLabel1.setText("AGV:");
    jLabel1.setVerticalAlignment(SwingConstants.BOTTOM);
    jLabel1.setVerticalTextPosition(SwingConstants.BOTTOM);
    jComboBox1.setAlignmentY((float) 0.0);
    jComboBox1.setToolTipText("Select an AGV");
    jComboBox1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jComboBox1_actionPerformed(e);
      }
    });
    jPanel13.setAlignmentX((float) 0.0);
    jPanel13.setAlignmentY((float) 0.0);
    jPanel13.setPreferredSize(new Dimension(70, 30));
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel2.setText("From Stack: ");
    jTextField1.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField1.setPreferredSize(new Dimension(30, 25));
    jTextField1.setToolTipText("");
    jTextField1.setEditable(false);
    jPanel7.setBackground(Color.lightGray);
    jPanel7.setAlignmentX((float) 0.0);
    jPanel7.setAlignmentY((float) 0.0);
    jPanel7.setBorder(BorderFactory.createLineBorder(Color.black));
    jPanel7.setPreferredSize(new Dimension(26, 26));
    jLabel3.setText("Lane: ");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
    jTextField2.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField2.setPreferredSize(new Dimension(30, 25));
    jTextField2.setEditable(false);
    jPanel14.setPreferredSize(new Dimension(70, 30));
    jPanel14.setAlignmentY((float) 0.0);
    jPanel14.setAlignmentX((float) 0.0);
    jTextField3.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField3.setPreferredSize(new Dimension(30, 25));
    jTextField3.setEditable(false);
    jTextField4.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField4.setPreferredSize(new Dimension(30, 25));
    jTextField4.setEditable(false);
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel4.setText("Lane: ");
    jLabel5.setText("    To Stack: ");
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel6.setText("Driving:");
    jLabel6.setFont(new java.awt.Font("Dialog", 1, 14));
    jRadioButton1.setText("clockwise");
    jRadioButton1.setForeground(new Color(102, 102, 153));
    jRadioButton2.setText("counterclockwise");
    jRadioButton2.setForeground(new Color(102, 102, 153));
    jPanel6.setPreferredSize(new Dimension(300, 20));
    jPanel6.setAlignmentY((float) 0.0);
    jPanel6.setAlignmentX((float) 0.0);
    jPanel8.setPreferredSize(new Dimension(300, 10));
    jPanel8.setAlignmentY((float) 0.0);
    jPanel8.setAlignmentX((float) 0.0);
    jPanel16.setPreferredSize(new Dimension(150, 15));
    jPanel16.setAlignmentY((float) 0.0);
    jPanel16.setAlignmentX((float) 0.0);
    jPanel9.setPreferredSize(new Dimension(300, 40));
    jPanel9.setAlignmentY((float) 0.0);
    jPanel9.setAlignmentX((float) 0.0);
    jTextField6.setEnabled(false);
    jTextField6.setFont(new java.awt.Font("Dialog", 0, 10));
    jTextField6.setPreferredSize(new Dimension(30, 15));
    jTextField6.setEditable(false);
    jLabel8.setFont(new java.awt.Font("Dialog", 1, 10));
    jLabel8.setText("This AGV has driven in total:");
    jLabel9.setText("This is round nr.:");
    jLabel9.setEnabled(false);
    jLabel9.setFont(new java.awt.Font("Dialog", 1, 10));
    jTextField7.setPreferredSize(new Dimension(60, 15));
    jTextField7.setEditable(false);
    jTextField7.setFont(new java.awt.Font("Dialog", 0, 10));
    jPanel17.setAlignmentX((float) 0.0);
    jPanel17.setAlignmentY((float) 0.0);
    jPanel17.setPreferredSize(new Dimension(20, 15));
    jLabel10.setText("meters.");
    jLabel10.setFont(new java.awt.Font("Dialog", 1, 10));
    jLabel7.setToolTipText("");
    jLabel7.setText("            Clr:");
    this.getContentPane().add(jPanel8, null);
    this.getContentPane().add(jPanel1, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jComboBox1, null);
    jPanel1.add(jLabel7, null);
    jPanel1.add(jPanel7, null);
    this.getContentPane().add(jPanel2, null);
    this.getContentPane().add(jPanel3, null);
    jPanel3.add(jLabel2, null);
    jPanel3.add(jTextField1, null);
    jPanel3.add(jLabel3, null);
    jPanel3.add(jTextField2, null);
    jPanel3.add(jPanel13, null);
    this.getContentPane().add(jPanel4, null);
    jPanel4.add(jLabel5, null);
    jPanel4.add(jTextField3, null);
    jPanel4.add(jLabel4, null);
    jPanel4.add(jTextField4, null);
    jPanel4.add(jPanel14, null);
    this.getContentPane().add(jPanel5, null);
    jPanel5.add(jLabel6, null);
    jPanel5.add(jRadioButton1, null);
    jPanel5.add(jRadioButton2, null);
    this.getContentPane().add(jPanel6, null);
    this.getContentPane().add(jPanel9, null);
    jPanel9.add(jLabel9, null);
    jPanel9.add(jTextField6, null);
    jPanel9.add(jPanel16, null);
    jPanel9.add(jLabel8, null);
    jPanel9.add(jTextField7, null);
    jPanel9.add(jLabel10, null);
    jPanel9.add(jPanel17, null);
  }

  private static AgvTrackerFrame frame;

  /** Static. Creates Frame */
  static {
    frame = new AgvTrackerFrame();
    int number = 50;
    frame.agvList = new Machine[number];
    frame.stackNr = new int[number];
    frame.stackLane = new int[number];
    frame.parkNr = new int[number];
    frame.parkLane = new int[number];
    frame.turnDirection = new int[number];
  }


  /** Adds an AGV to this Frame */
  public static void setAgv(Machine agv){
    frame.agvList[index] = agv;
    frame.jComboBox1.addItem("AGV"+index);
    frame.index++;
  }

  /** Updates the Frame with new information */
  public static void setAgvInfo(Machine agv,
                                int stNr,
                                int stLane,
                                int pNr,
                                int pLane,
                                int tDirection){
    int nr = 0;
    for (int i=0; i<agvList.length;i++){
      if ((frame.agvList[i] != null) && (agv == frame.agvList[i])) {nr=i;}
    }
    frame.stackNr[nr] = stNr;
    frame.stackLane[nr] = stLane;
    frame.parkNr[nr] = pNr;
    frame.parkLane[nr] = pLane;
    frame.turnDirection[nr] = tDirection;
    if (agvNr == nr) {frame.setFrame();}
  }

  /** Displays information in the viewed Frame */
  public static void setFrame(){
    try{
      frame.jTextField7.setText(agvList[agvNr].getState(null).u+"");
      int r,g,b;
      if ((frame.agvNr%7)>=3) {r = 1;} else {r = 0;}
      if (((frame.agvNr%7)-r*4)>=1) {g = 1;} else {g = 0;}
      if (((frame.agvNr%7)-r*4-g*2)>=0) {b = 1;} else {b = 0;}
      frame.jPanel7.setBackground(new Color(r*255,g*255,b*255));
      frame.jTextField1.setText(frame.stackNr[frame.agvNr]+"");
      if (frame.stackLane[frame.agvNr] != 99) {
        frame.jTextField2.setText(frame.stackLane[frame.agvNr]+"");
      } else {
        frame.jTextField2.setText("n/a");
      }
      frame.jTextField3.setText(frame.parkNr[frame.agvNr]+"");
      if (frame.parkLane[frame.agvNr] != 99) {
        frame.jTextField4.setText(frame.parkLane[frame.agvNr]+"");
      } else {
        frame.jTextField4.setText("n/a");
      }
      if (frame.turnDirection[agvNr]==0) {
        frame.jRadioButton2.setSelected(true);
        frame.jRadioButton1.setSelected(false);
      } else {
        frame.jRadioButton1.setSelected(true);
        frame.jRadioButton2.setSelected(false);

      }
    } catch (Exception e){}
  }

  /** Is being called by the system if the frame needs to be repainted */
  public void repaint(){
    setFrame();
  }

  /** Change AGV of which information is displayed */
  void jComboBox1_actionPerformed(ActionEvent e) {
    //combobox changed...
    frame.jComboBox1.setPopupVisible(false);
    frame.agvNr = frame.jComboBox1.getSelectedIndex();
    frame.setFrame();
  }

}