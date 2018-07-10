package com.agileways.ui;

import java.rmi.RemoteException;

import com.agileways.forces.LookUpServices;
import net.agileframes.forces.MachineProxy;
import net.agileframes.forces.MachineIB;//for props
import net.agileframes.traces.ActorIB;  //for props
import net.agileframes.traces.ActorProxy;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class LookUpFrame extends JFrame {
  JMenuBar menuBar1 = new JMenuBar();
  JMenu menuFile = new JMenu();
  JMenuItem menuFileExit = new JMenuItem();
  JMenu menuHelp = new JMenu();
  JMenuItem menuHelpAbout = new JMenuItem();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel machinesPanel = new JPanel();
  JPanel actorspanel = new JPanel();
  JPanel histPanel = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JList machineList = new JList();
  JLabel jLabel4 = new JLabel();
  JLabel nameLabel = new JLabel();
  JCheckBox historyCheck = new JCheckBox();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JLabel jLabel14 = new JLabel();
  JList actorList = new JList();
  JTextField busyField = new JTextField();
  JTextField destField = new JTextField();
  JLabel nameLabel1 = new JLabel();
  JTextField nextDestField = new JTextField();
  JTextField capField = new JTextField();
  JCheckBox historyCheck1 = new JCheckBox();
  JTextField idleField = new JTextField();
  JLabel speedLabel1 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextField origField = new JTextField();
  JTextField actorNameField = new JTextField();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel9 = new JPanel();
  JPanel jPanel10 = new JPanel();
  JPanel jPanel11 = new JPanel();
  JPanel jPanel12 = new JPanel();
  JPanel jPanel13 = new JPanel();
  Graph graph = new Graph();

  //Construct the frame
  public LookUpFrame(LookUpServices lookupServices) {
    this.lookupServices = lookupServices;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try  {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception  {
    this.getContentPane().setLayout(flowLayout1);
    this.setSize(new Dimension(410, 370));
    this.setTitle("-- AgileFrames LookUp Actors & Machines --");
    menuFile.setText("File");
    menuFileExit.setText("Exit");
    menuFileExit.addActionListener(new ActionListener()  {

      public void actionPerformed(ActionEvent e) {
        fileExit_actionPerformed(e);
      }
    });
    menuHelp.setText("Help");
    menuHelpAbout.setText("About");
    menuHelpAbout.addActionListener(new ActionListener()  {

      public void actionPerformed(ActionEvent e) {
        helpAbout_actionPerformed(e);
      }
    });
    jTabbedPane1.setPreferredSize(new Dimension(400, 315));
    jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentShown(ComponentEvent e) {
        jTabbedPane1_componentShown(e);
      }
    });
    actorspanel.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentShown(ComponentEvent e) {
        actorspanel_componentShown(e);
      }
    });
    machinesPanel.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentShown(ComponentEvent e) {
        machinesPanel_componentShown(e);
      }
    });
    histPanel.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentShown(ComponentEvent e) {
        histPanel_componentShown(e);
      }
    });
    jPanel1.setPreferredSize(new Dimension(390, 10));
    jPanel3.setPreferredSize(new Dimension(100, 250));
    jPanel2.setPreferredSize(new Dimension(280, 250));
    jPanel4.setPreferredSize(new Dimension(95, 20));
    jScrollPane1.setPreferredSize(new Dimension(95, 215));
    jLabel1.setPreferredSize(new Dimension(95, 17));
    jLabel1.setText("Machines:");
    jLabel4.setText("Active Time:");
    jLabel4.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel4.setForeground(SystemColor.textText);
    jLabel4.setAlignmentY((float) 0.0);
    jLabel4.setPreferredSize(new Dimension(150, 15));
    nameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    nameLabel.setForeground(SystemColor.textText);
    nameLabel.setPreferredSize(new Dimension(150, 15));
    nameLabel.setText("Name:");
    historyCheck.setHorizontalTextPosition(SwingConstants.LEFT);
    historyCheck.setPreferredSize(new Dimension(230, 15));
    historyCheck.setText("Keep History");
    historyCheck.setForeground(SystemColor.textText);
    historyCheck.setFont(new java.awt.Font("Dialog", 0, 10));
    historyCheck.addChangeListener(new javax.swing.event.ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        historyCheck_stateChanged(e);
      }
    });
    jLabel8.setText("Actors:");
    jLabel8.setPreferredSize(new Dimension(95, 17));
    jLabel9.setText("Properties:");
    jLabel9.setPreferredSize(new Dimension(265, 17));
    jLabel10.setText("Idle Time:");
    jLabel10.setPreferredSize(new Dimension(150, 17));
    jLabel11.setPreferredSize(new Dimension(150, 17));
    jLabel11.setText("Origin:");
    jLabel12.setPreferredSize(new Dimension(150, 17));
    jLabel12.setText("Busy Time:");
    jLabel13.setPreferredSize(new Dimension(150, 17));
    jLabel13.setText("Next Destination:");
    jLabel14.setPreferredSize(new Dimension(150, 17));
    jLabel14.setText("Capacity:");
    busyField.setBorder(null);
    busyField.setPreferredSize(new Dimension(75, 21));
    busyField.setEditable(false);
    busyField.setHorizontalAlignment(SwingConstants.RIGHT);
    destField.setBorder(null);
    destField.setPreferredSize(new Dimension(75, 21));
    destField.setEditable(false);
    destField.setHorizontalAlignment(SwingConstants.RIGHT);
    nameLabel1.setText("Name:");
    nameLabel1.setPreferredSize(new Dimension(150, 17));
    nextDestField.setBorder(null);
    nextDestField.setPreferredSize(new Dimension(75, 21));
    nextDestField.setEditable(false);
    nextDestField.setHorizontalAlignment(SwingConstants.RIGHT);
    capField.setBorder(null);
    capField.setPreferredSize(new Dimension(75, 21));
    capField.setEditable(false);
    capField.setHorizontalAlignment(SwingConstants.RIGHT);
    historyCheck1.setText("Keep History");
    historyCheck1.setPreferredSize(new Dimension(230, 20));
    historyCheck1.setHorizontalTextPosition(SwingConstants.LEFT);
    idleField.setHorizontalAlignment(SwingConstants.RIGHT);
    idleField.setEditable(false);
    idleField.setPreferredSize(new Dimension(75, 21));
    idleField.setBorder(null);
    speedLabel1.setPreferredSize(new Dimension(150, 17));
    speedLabel1.setText("Destination:");
    jScrollPane2.setPreferredSize(new Dimension(95, 215));
    origField.setHorizontalAlignment(SwingConstants.RIGHT);
    origField.setEditable(false);
    origField.setPreferredSize(new Dimension(75, 21));
    origField.setBorder(null);
    actorNameField.setBorder(null);
    actorNameField.setPreferredSize(new Dimension(75, 21));
    actorNameField.setEditable(false);
    actorNameField.setHorizontalAlignment(SwingConstants.RIGHT);
    jPanel7.setPreferredSize(new Dimension(390, 10));
    jPanel8.setPreferredSize(new Dimension(280, 250));
    jPanel9.setPreferredSize(new Dimension(100, 250));
    jPanel10.setPreferredSize(new Dimension(95, 20));
    jPanel11.setPreferredSize(new Dimension(265, 20));
    jPanel12.setBorder(BorderFactory.createEtchedBorder());
    jPanel12.setPreferredSize(new Dimension(265, 215));
    jPanel13.setPreferredSize(new Dimension(390, 10));
    machineList.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        machineList_mouseClicked(e);
      }
    });
    actorList.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        actorList_mouseClicked(e);
      }
    });
    jPanel14.setPreferredSize(new Dimension(390, 50));
    jPanel14.setLayout(flowLayout2);
    jButton1.setHorizontalAlignment(SwingConstants.LEFT);
    jButton1.setHorizontalTextPosition(SwingConstants.LEFT);
    jButton1.setText("Clear");
    jButton1.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    machineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    machineScrollPane.setBorder(null);
    machineScrollPane.setPreferredSize(new Dimension(270, 215));
    jLabel2.setPreferredSize(new Dimension(265, 17));
    jLabel2.setText("Properties:");
    jPanel5.setPreferredSize(new Dimension(265, 20));
    jLabel3.setText("Properties:");
    jLabel3.setPreferredSize(new Dimension(265, 17));
    jPanel6.setPreferredSize(new Dimension(265, 20));
    jLabel5.setText("Properties:");
    jLabel5.setPreferredSize(new Dimension(265, 17));
    jPanel15.setPreferredSize(new Dimension(265, 20));
    jLabel15.setPreferredSize(new Dimension(265, 17));
    jLabel15.setText("Properties:");
    jPanel16.setPreferredSize(new Dimension(265, 20));
    machinePropPanel.setMaximumSize(new Dimension(230, 32767));
    machinePropPanel.setMinimumSize(new Dimension(230, 400));
    machinePropPanel.setPreferredSize(new Dimension(230, 400));
    nameValue.setFont(new java.awt.Font("Dialog", 1, 10));
    nameValue.setForeground(SystemColor.textText);
    nameValue.setPreferredSize(new Dimension(75, 15));
    nameValue.setHorizontalAlignment(SwingConstants.RIGHT);
    nameValue.setText("N/A");
    timeValue.setText("N/A");
    timeValue.setHorizontalAlignment(SwingConstants.RIGHT);
    timeValue.setPreferredSize(new Dimension(75, 15));
    timeValue.setFont(new java.awt.Font("Dialog", 1, 10));
    timeValue.setForeground(SystemColor.textText);
    machineList.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        machineList_keyTyped(e);
      }
    });
    menuFile.add(menuFileExit);
    menuHelp.add(menuHelpAbout);
    menuBar1.add(menuFile);
    menuBar1.add(menuHelp);
    this.setJMenuBar(menuBar1);
    this.getContentPane().add(jTabbedPane1, null);
    jTabbedPane1.add(machinesPanel, "Machines");
    machinesPanel.add(jPanel1, null);
    machinesPanel.add(jPanel3, null);
    jPanel3.add(jPanel4, null);
    jPanel4.add(jLabel1, null);
    jPanel3.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(machineList, null);
    machinesPanel.add(jPanel2, null);
    jPanel2.add(jPanel16, null);
    jPanel16.add(jLabel15, null);
    jPanel2.add(machineScrollPane, null);
    machineScrollPane.getViewport().add(machinePropPanel, null);
    machinePropPanel.add(nameLabel, null);
    machinePropPanel.add(nameValue, null);
    machinePropPanel.add(jLabel4, null);
    machinePropPanel.add(timeValue, null);
    machinePropPanel.add(historyCheck, null);
    jTabbedPane1.add(actorspanel, "Actors");
    actorspanel.add(jPanel7, null);
    actorspanel.add(jPanel8, null);
    jPanel8.add(jPanel11, null);
    jPanel11.add(jLabel9, null);
    jPanel8.add(jPanel12, null);
    jPanel12.add(nameLabel1, null);
    jPanel12.add(actorNameField, null);
    jPanel12.add(jLabel11, null);
    jPanel12.add(origField, null);
    jPanel12.add(speedLabel1, null);
    jPanel12.add(destField, null);
    jPanel12.add(jLabel13, null);
    jPanel12.add(nextDestField, null);
    jPanel12.add(jLabel14, null);
    jPanel12.add(capField, null);
    jPanel12.add(jLabel12, null);
    jPanel12.add(busyField, null);
    jPanel12.add(jLabel10, null);
    jPanel12.add(idleField, null);
    jPanel12.add(historyCheck1, null);
    actorspanel.add(jPanel9, null);
    jPanel9.add(jPanel10, null);
    jPanel10.add(jLabel8, null);
    jPanel9.add(jScrollPane2, null);
    jScrollPane2.getViewport().add(actorList, null);
    jTabbedPane1.add(histPanel, "History");
    histPanel.add(jPanel13, null);
    graph.setPreferredSize(new Dimension(390, 200));
    graph.setBackground(Color.darkGray);
    histPanel.add(graph, null);
    histPanel.add(jPanel14, null);
    jPanel14.add(jButton1, null);

    this.setVisible(true);
    this.setEnabled(true);
    this.invalidate();
    jPanel5.add(jLabel2, null);
    jPanel6.add(jLabel3, null);
    jPanel15.add(jLabel5, null);
  }

  //File | Exit action performed
  public void fileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  //Help | About action performed
  public void helpAbout_actionPerformed(ActionEvent e) {
    LookUpFrame_AboutBox dlg = new LookUpFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }

  //Overridden so we can exit on System Close
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if(e.getID() == WindowEvent.WINDOW_CLOSING) {
      fileExit_actionPerformed(null);
    }
  }

  void jTabbedPane1_componentShown(ComponentEvent e) {

  }

  void actorspanel_componentShown(ComponentEvent e) {

  }

  void machinesPanel_componentShown(ComponentEvent e) {

  }

  void histPanel_componentShown(ComponentEvent e) {

  }

  void machineList_mouseClicked(MouseEvent e) {
    this.setMachineProperties();
  }

  void actorList_mouseClicked(MouseEvent e) {
    this.setActorProperties();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  private ActorProxy[] actors;
  private String[] actorNames;
  private MachineProxy[] machines;
  private String[] machineNames;
  private final int MAXDATA = 5000;
  private final long REFRESHTIME = 300;
  private MachineIB.MachineProperties[] machineData = new MachineIB.MachineProperties[MAXDATA];
  private int machineDataCounter = 0;
  private boolean emptyMachines = false;
  private JLabel[] propLabel = new JLabel[20];
  private JLabel[] propValue = new JLabel[20];
  private int propCounter = 0;
  private LookUpServices lookupServices = null;

  public void setActors(ActorProxy[] actors) {
    this.actors = actors;
    if (actors != null) {
      actorNames = new String[actors.length];
      for (int i = 0; i < actors.length; i++) {
        if (actors[i] != null) {
          actorNames[i] = actors[i].getName();
        }
      }
      actorList.setListData(actorNames);
    } else {
      actorList.setListData(new Object[] {});
    }
    setActorProperties();
  }
  public void setMachines(MachineProxy[] machines) {
    this.machines = machines;
    if (machines != null) {
      machineNames = new String[machines.length];
      for (int i = 0; i < machines.length; i++) {
        if (machines[i] != null) {
          machineNames[i] = machines[i].getName();  }
        }
      machineList.setListData(machineNames);
    } else {
      machineList.setListData(new Object[] {});
    }
    setMachineProperties();
  }
  private boolean started = false;
  JPanel jPanel14 = new JPanel();
  JButton jButton1 = new JButton();
  FlowLayout flowLayout2 = new FlowLayout();
  JScrollPane machineScrollPane = new JScrollPane();
  JPanel machinePropPanel = new JPanel();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel5 = new JPanel();
  JLabel jLabel3 = new JLabel();
  JPanel jPanel6 = new JPanel();
  JLabel jLabel5 = new JLabel();
  JPanel jPanel15 = new JPanel();
  JLabel jLabel15 = new JLabel();
  JPanel jPanel16 = new JPanel();
  JLabel nameValue = new JLabel();
  JLabel timeValue = new JLabel();
  private synchronized void collectMachineProperties() {
    if ( (machineDataCounter >= MAXDATA) || (machineList == null) || (machineList.getSelectedIndex() < 0) ) { return; }
    MachineProxy selMachine = machines[machineList.getSelectedIndex()];
    if (selMachine == null) { return; }
    MachineIB.MachineProperties props = (MachineIB.MachineProperties)selMachine.getProperties();
    if (props != null) {
      machineData[machineDataCounter] = props;
      machineDataCounter++;
    }
  }
  private void setMachineProperties(){
    if (jTabbedPane1.getSelectedIndex() != 0) { return; }
    if ( (machineDataCounter == 0) || (machineList == null) || (machineList.getSelectedIndex() < 0) ) { emptyMachineProperties(); return; }
    MachineIB.MachineProperties props = machineData[machineDataCounter - 1];

    // standard props:
    nameValue.setText(props.name);
    int time = (int)props.activeTime/1000;
    int hours = time / 3600;
    int minutes = (time - hours * 3600) / 60;
    int seconds = (time - hours * 3600 - minutes * 60);
    String secStr = String.valueOf(seconds);
    if (seconds < 10) { secStr = "0"+secStr; }
    String minStr = String.valueOf(minutes);
    if (minutes < 10) { minStr = "0"+minStr; }
    timeValue.setText(hours+":"+minStr+":"+secStr);

    // specific props:
    // add/remove labels iff necessary
    if (props.specProps.length < propCounter) { // remove prop-labels
      for (int i = props.specProps.length; i < propCounter; i++ ) {
        machinePropPanel.remove(propLabel[i]);
        machinePropPanel.remove(propValue[i]);
        propLabel[i] = null; propValue[i] = null;
      }
      propCounter = props.specProps.length;
      this.repaint();
    } else if (props.specProps.length > propCounter) { // add prop-labels
      for (int i = propCounter; i < props.specProps.length; i++ ) {
        propLabel[i] = new JLabel();
        propLabel[i].setFont(new java.awt.Font("Dialog", 1, 10));
        propLabel[i].setPreferredSize(new Dimension(150, 15));
        propLabel[i].setForeground(SystemColor.textText);
        propValue[i] = new JLabel();
        propValue[i].setFont(new java.awt.Font("Dialog", 1, 10));
        propValue[i].setPreferredSize(new Dimension(75, 15));
        propValue[i].setHorizontalAlignment(SwingConstants.RIGHT);
        propValue[i].setForeground(SystemColor.textText);
        machinePropPanel.add(propLabel[i]);
        machinePropPanel.add(propValue[i]);
      }
      propCounter = props.specProps.length;
      this.repaint();
    }
    // write spec props:
    for (int i = 0; i < props.specProps.length; i++ ) {
      propLabel[i].setText(props.specProps[i].description);
      switch(props.specProps[i].type) {
        case MachineIB.MachineProperties.DOUBLE:
          propValue[i].setText(doubleToString(props.specProps[i].value, 2));
          break;
        case MachineIB.MachineProperties.BOOLEAN:
          if (props.specProps[i].value > 0) { propValue[i].setText("TRUE"); } else { propValue[i].setText("FALSE"); }
          break;
        case MachineIB.MachineProperties.INTEGER:
          propValue[i].setText(String.valueOf((int)props.specProps[i].value));
          break;
        case MachineIB.MachineProperties.PERCENT:
          propValue[i].setText(String.valueOf((int)(100 * props.specProps[i].value))+" %");
          break;
        default:
          propValue[i].setText("I/O");
          break;
      }
    }
    emptyMachines = false;
  }
  private String doubleToString(double nr, int decimals) {
    int intNr = (int) Math.abs(nr);
    int decNr = (int) Math.abs(((nr - intNr) * Math.pow(10, decimals)));
    String sign = ""; if (nr < 0) { sign = "-"; }
    return sign + intNr+"."+decNr;
  }
  private void setGraphProperties(){
    if (machineDataCounter == 0) { return; }
    MachineIB.MachineProperties props = null;
    if (!started) {
      props = machineData[machineDataCounter - 1];
      graph.addCurve(props.activeTime, Color.red, 14.5125, -14.5125, props.specProps[0].description);
      graph.addCurve(props.activeTime, Color.yellow, 1.5, -1.5, props.specProps[1].description);
      graph.addCurve(props.activeTime, Color.green, 36, -12, props.specProps[2].description);
      graph.addCurve(props.activeTime, Color.blue, 1, -1, props.specProps[6].description);
      started = true;
    }
    props = machineData[machineDataCounter - 1];
    graph.setNewValue(0, props.specProps[0].value, props.activeTime);
    graph.setNewValue(1, props.specProps[1].value, props.activeTime);
    graph.setNewValue(2, props.specProps[2].value, props.activeTime);
    graph.setNewValue(3, props.specProps[6].value, props.activeTime);
  }


  private void emptyMachineProperties() {
    if (emptyMachines) { return; }
    nameValue.setText("N/A");
    timeValue.setText("N/A");
    for (int i = 0; i < propCounter; i++ ) {
      machinePropPanel.remove(propLabel[i]);
      machinePropPanel.remove(propValue[i]);
      propLabel[i] = null; propValue[i] = null;
    }
    propCounter = 0;
    this.repaint();
    emptyMachines = true;
  }
  private void emptyActorProperties() {
    busyField.setText("N/A");
    destField.setText("N/A");
    nextDestField.setText("N/A");
    capField.setText("N/A");
    idleField.setText("N/A");
    origField.setText("N/A");
    actorNameField.setText("N/A");
  }
  private void setActorProperties(){
    if (jTabbedPane1.getSelectedIndex() != 1) { return; }
    if ( (actorList == null) || (actorList.getSelectedIndex() < 0) ) { emptyActorProperties(); return; }
    ActorProxy selActor = actors[actorList.getSelectedIndex()];
    if (selActor == null) { emptyActorProperties(); return; }
    ActorIB.ActorProperties props = null;
    try { props = (ActorIB.ActorProperties)selActor.getProperties(); }
    catch (RemoteException e) {
      System.out.println("RemoteException while reading Actor-properties in LookUpFrame: "+e.getMessage());
      emptyActorProperties();
      actorList.remove(actorList.getSelectedIndex());
      actorList.repaint();
      return;
    }
    busyField.setText(props.busy +" %");
    destField.setText(props.destination);
    nextDestField.setText(props.nextDestination);
    //capField.setText(props.capacity);
    try {capField.setText(selActor.getMachineProxy().getName());}  catch (Exception e) {}
    idleField.setText(props.idle +" %");
    origField.setText(props.origin);
    actorNameField.setText(props.name);
  }

  private void runForRefreshData() {
    while (historyCheck.isSelected()) {
      collectMachineProperties();
      setMachineProperties();
      setGraphProperties();

      try { synchronized(this) { wait(REFRESHTIME); } }
      catch (Exception e) { e.printStackTrace(); }
    }
  }

  void historyCheck_stateChanged(ChangeEvent e) {
    if (historyCheck.isSelected()) {
      Thread dataRefreshThread = new Thread("DataRefreshThread") {
        public void run() { runForRefreshData(); }
      };
      dataRefreshThread.start();
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {// Clear-Button
    graph.clear();
    for (int i = 0; i < machineDataCounter; i++) {
      machineData[i] = null;
    }
    machineDataCounter = 0;
    started = false;
  }

  void machineList_keyTyped(KeyEvent e) {
    if (Character.getNumericValue(e.getKeyChar()) != 13) {
      System.out.println("Press d to remove service");
    } else {
      lookupServices.removeServiceManually(machineList.getSelectedIndex());
    }
  }
}

