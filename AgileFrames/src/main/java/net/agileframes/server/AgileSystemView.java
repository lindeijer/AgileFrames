package net.agileframes.server;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import javax.swing.*;
//import net.jini.core.entry.*;
//import java.io.*;

public class AgileSystemView extends Frame {

  AgileSystemRoot agileSystemRoot = null;

  public AgileSystemView(AgileSystemRoot agileSystemRoot,String title) {
    this(title);
    this.agileSystemRoot = agileSystemRoot;
  }
  
  public AgileSystemView(String title) {
    super(title);
    this.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          dispose();
          AgileSystem.dispose();
          System.exit(0);
        }
      }
    );
    this.setSize(200,20);
    this.setVisible(true);
    System.out.println("AgileSystemView() created");
  }

}

/**

jButton1.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            button1_mouseClicked();
          }
        }
      );
    }
    catch (Exception e) {
      e.printStackTrace();
    }

///////////////////////////////////////////////////////////////////

public class ActivationServiceJFrame extends JFrame implements Entry {
  JLabel jLabel1 = new JLabel();
  JList jList1 = new JList();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton jButton1 = new JButton("refresh");

  ActivationService activationProxy = null;
  ActivationServer activationServer = null;
  String[] registeredAgileGroupNames = null;
  String hostName = null;

  public ActivationServiceJFrame() { // reserialization
    try  {
      jbInit();
      this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            if (activationServer != null) {
              activationServer.cleanupAndDie();
            }
            if (activationProxy != null) {
              // activationProxy.cleanupAndDie();
            }
            dispose();
          }
        }
      );
      jButton1.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            button1_mouseClicked();
          }
        }
      );
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void button1_mouseClicked() {
    if (activationServer != null) {
      try {
        registeredAgileGroupNames = activationServer.getRegisteredAgileGroupNames();
      }
      catch (Exception e) {
        registeredAgileGroupNames = new String[] { "remote" , "exception" };
      }
    }
    if (activationProxy != null) {
      registeredAgileGroupNames = activationProxy.getRegisteredAgileGroupNames();
    }
    this.jList1.setListData(registeredAgileGroupNames);
  }

  //////////////////// local side ///////////////////////////////

  public void setActivationServer(ActivationServer server) {
    activationServer = server;
    try {
      registeredAgileGroupNames =
        server.getRegisteredAgileGroupNames();
      hostName = server.getHostName();
      //
      setTitle("ActivationServer on localhost (" + hostName + ")");
      this.jList1.setListData(registeredAgileGroupNames);
    } catch (Exception e) {
      setTitle("ActivationService " + hostName + ". Exception during setActivationServer: " + e.getMessage());
    }
    //
    this.setVisible(true);
  }

  //////////////////// remote side ///////////////////////////////

  public void setProxy(Object proxy) { this.setProxy((ActivationService)proxy); }

  private void setProxy(ActivationService proxy) {
    activationProxy = proxy;
    try {
      registeredAgileGroupNames =
        activationProxy.getRegisteredAgileGroupNames();
      hostName = activationProxy.getHostName();
      //
      setTitle("ActivationService for ActivationServer on " + hostName);
      this.jList1.setListData(registeredAgileGroupNames);
    } catch (Exception e) {
      setTitle("ActivationService " + hostName + ". Exception during setProxy: " + e.getMessage());
    }
    //
    this.setVisible(true);
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(gridBagLayout1);
    this.setSize(new Dimension(351, 300));
    this.setTitle("howdy");
    jLabel1.setText("registered AgileGroups");
    this.getContentPane().add(jLabel1,new GridBagConstraints2(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 25, 25));
    this.getContentPane().add(jList1, new GridBagConstraints2(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 25, 25));
    this.getContentPane().add(jButton1, new GridBagConstraints2(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
  }

}

class GridBagConstraints2 extends java.awt.GridBagConstraints {

  public GridBagConstraints2(
      int gx, int gy,
      int gh, int gw,
      double wx, double wy,  // percentage of left-over space for slot
      int a,                 // anchor within assigned slot
      int f,                 // how to fill within the slot.
      java.awt.Insets i,     // space between component and slot.
      int px, int py ) {     // extra size for component.
    this.gridx = gx;      this.gridy = gy;
    this.gridheight = gh; this.gridwidth = gw;
    this.weightx = wx;    this.weighty = wy;
    this.anchor = a;      this.fill = f;
    this.insets = i;
    this.ipadx = px;      this.ipady = py;
  }
}

*/