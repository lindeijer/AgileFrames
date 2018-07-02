package com.agileways.traces.scene;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * Frame that should be created for each machine and in which the
 * claimed semaphores should be listed.
 *
 * @author Wierenga
 * @version 0.0.1
 */
public class ClaimFrame extends JFrame {
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea1 = new JTextArea();

  public ClaimFrame() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setSize(500,470);

    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTextArea1, null);
  }

  /**
   * Adds text to the claim frame
   */
  public void addText(String textToAdd) {
    jTextArea1.append(textToAdd);
  }
}