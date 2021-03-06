package com.agileways.ui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Graph extends JPanel {
  //--- Attributes ---
  public int MAXPOINTS = 5000;
  public int MAXCURVES = 10;
  public int TIMERESOLUTION = 800;// milliseconds per pixel
  private Curve[] curves = new Curve[MAXCURVES];
  private int curveCounter = 0;
  //--- Constructor ---
  public Graph() {
    super();
  }
  //--- Methods ---
  public int addCurve(long beginTime, Color color, double maxValue, double minValue, String name) {
    System.out.println("added curve "+ curveCounter);
    curves[curveCounter] = new Curve(beginTime, color, maxValue, minValue, name);
    curveCounter++;
    return (curveCounter - 1);
  }
  public void setNewValue(int curveID, double value, long time) {
    if ( (curveID >= 0) && (curveID < curveCounter) && (curves[curveID] != null) ) {
      curves[curveID].setNewValue(value, time);
    }
    if (curveID == curveCounter - 1 ) { this.repaint(); }
  }
  public int getGraphHeight() { return (getHeight()-20); }
  public void clear() {
    for (int i = 0; i < curveCounter; i++ ) { curves[i] = null; }
    curveCounter = 0;
    this.repaint();
  }
  //--- Overloaded ---
  public void paint(Graphics g) {
    super.paint(g);
    for (int i = 0; i < curveCounter; i++ ) {
      if (curves[i] != null) { curves[i].paint(g); }
    }
  }

  //--- Inner class: CURVE ---
  class Curve extends JComponent {
    //--- CURVE: Attributes ---
    public long beginTime;
    private Point[] points = new Point[MAXPOINTS];
    private int counter = 0;
    private double scale, offset;
    private Color color;
    private String name;
    //--- CURVE: Constructor ---
    public Curve (long beginTime, Color color, double maxValue, double minValue, String name) {
      this.beginTime = beginTime;
      this.color = color;
      this.scale = getGraphHeight() / (maxValue - minValue);
      this.offset = minValue * scale;
      this.name = name;
    }
    //--- CURVE: Methods ---
    public void setNewValue(double value, long time) {
      if (counter >= MAXPOINTS) { return; }
      time = time - beginTime;
      int x = (int)(time / TIMERESOLUTION);
      int y = getGraphHeight() - (int)(value * scale - offset);
      points[counter] = new Point(x,y);
      counter++;
    }
    public void paint(Graphics g) {
      if (g == null) { return; }
      g.setColor(color);
      // print name of curve
      if (points[0] != null) { g.drawString(name, 30, points[0].y); } else { g.drawString(name, 30, getGraphHeight() - (int)offset); }
      // draw curve
      for (int i = 1; i < counter; i++){
        if ((points[i]!=null) && (points[i-1]!=null)) { g.drawLine(points[i-1].x, points[i-1].y, points[i].x, points[i].y); }
      }
    }
    //--- Inner class POINT
    class Point {
      public int x,y;
      public Point (int x, int y) { this.x = x; this.y = y; }
    }
  }
}
