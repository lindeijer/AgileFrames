package com.agileways.traces.scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.traces.Semaphore;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;

/**
 * Visualizes Semaphores with the colors indicating their capacities.
 * Semaphores will be placed in SceneViewer.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class SemaphoreViewer extends Component{
  FlowLayout flowLayout = new FlowLayout();
  JPanel panel = new JPanel();
  Semaphore semaphore = null;
  int semaphoreType, param1, param2;
  float x, y, width, height;
  int state = 5;
  public boolean changed = true;
  SceneViewer sceneViewer;
  private CrossoverScene crossoverScene;
  private CrossoverTerminal cT;

  /**
   * Constructor.
   */
  public SemaphoreViewer(
           Semaphore semaphore,
           int semaphoreType,
           int param1,
           int param2,
           SceneViewer sceneViewer)
  {
    this.sceneViewer = sceneViewer;
    this.crossoverScene = sceneViewer.crossoverScene;
    this.cT = sceneViewer.crossoverScene.cT;
    this.semaphore = semaphore;
    this.semaphoreType = semaphoreType;
    this.param1 = param1;
    this.param2 = param2;
    setSizeAndPosition();
  }

  /**
   * Gives each Semaphore x,y coordinate and height and width.
   * Visualisation does not mean semaphores are physical objects.
   */
  public void setSizeAndPosition() {
    switch (semaphoreType) {
      case 0:
        // semStackEntrance, param1=stackNr
        x = (float)(cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES);
        y = (float)(cT.WIDTH_DRIVING_LANE*2);
        height = (float)cT.FREE_STACK_ENTRANCE;
        width = (float) cT.STACK_WIDTH_INCL;
        break;
      case 7:
        // semStack, param1=stackNr, param2=stackLane
        x = (float)(cT.STACK_WIDTH_INCL*param1+cT.WIDTH_STACKLANE*param2+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES+ cT.FREE_SPACE_BETWEEN_STACKS/2);
        y = (float)(cT.DIST_TOP_STACK);
        width = (float)cT.WIDTH_STACKLANE;
        height = (float) cT.LENGTH_STACKLANE;
        break;
      case 2:
        // semStackExit, param1=stackNr, param2=stackLane
        x = (float)(cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES + param2*cT.STACK_WIDTH_INCL/(cT.lanesPerStack+1) + cT.FREE_SPACE_BETWEEN_STACKS/2);
        y = (float)(cT.LENGTH_STACKLANE+ cT.DIST_TOP_STACK);
        height = (float)cT.FREE_STACK_EXIT;
        width = (float) cT.STACK_WIDTH_INCL/(cT.lanesPerStack+1);
        break;
      case 3:
        // semCenterStayNorth, param1=stackNr
        x = (float)(cT.STACK_WIDTH_INCL*(param1+1)+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES - cT.AGV_LENGTH/2);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE + cT.DIST_STACK_CENTER - cT.WIDTH_CENTER_LANE*2);
        width = (float)cT.AGV_LENGTH;
        height = (float)cT.WIDTH_CENTER_LANE;
        break;
      case 1:
        // semCenterNorth, param1=stackNr
        x = (float)(cT.AGV_LENGTH/2+ cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE + cT.DIST_STACK_CENTER - cT.WIDTH_CENTER_LANE*2);
        width = (float) (cT.STACK_WIDTH_INCL-cT.AGV_LENGTH);
        height = (float)(cT.WIDTH_CENTER_LANE*2);
        break;
      case 4:
        // semCenterStaySouth, param1=stackNr
        x = (float)(cT.STACK_WIDTH_INCL*(param1+1)+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES - cT.AGV_LENGTH/2);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE + cT.DIST_STACK_CENTER + cT.WIDTH_CENTER_LANE);
        width = (float)cT.AGV_LENGTH;
        height = (float)cT.WIDTH_CENTER_LANE;
        break;
      case 5:
        // semParkEntrance, param1=parkNr param2=parkLane
        x = (float)(cT.PARK_WIDTH_INCL/2+cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES + param2*cT.STACK_WIDTH_INCL/cT.lanesPerPark);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE + cT.DIST_STACK_CENTER + cT.WIDTH_CENTER_LANE*2);
        height = (float)cT.FREE_PARK_ENTRANCE;
        width = (float) cT.STACK_WIDTH_INCL/cT.lanesPerPark;
        break;
      case 9:
        // semPark, param1=parkNr, param2=parkLane
        x = (float)(cT.PARK_WIDTH_INCL/2+cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES+param2*cT.WIDTH_PARKLANE);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE+ cT.DIST_STACK_CENTER + cT.DIST_CENTER_PARK);
        width = (float)cT.WIDTH_PARKLANE;
        height = (float) cT.LENGTH_PARKLANE;
        break;
      case 6:
        // semParkExit, param1=parkNr
        x = (float)(cT.PARK_WIDTH_INCL/2+cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE + cT.DIST_STACK_CENTER + cT.DIST_CENTER_PARK + cT.LENGTH_PARKLANE);
        height = (float)cT.FREE_PARK_EXIT;
        width = (float) cT.STACK_WIDTH_INCL;
        break;
      case 11:
        // semSideLane, param1=lanePosition1(0..3), param2=index
        if (param1<2) {x = (float) (param1*cT.WIDTH_DRIVING_LANE);}
        else {x=(float)(cT.TOTAL_WIDTH + (param1-4)*cT.WIDTH_DRIVING_LANE - cT.FREE_TERMINAL_RIGHT);}
        if (param2==cT.NUMBER_OF_AGVS_AT_SIDES-1) {y = (float)(2*cT.WIDTH_DRIVING_LANE+cT.TURN_RADIUS);}
        else {y = (float)(cT.DIST_TOP_QUAY - cT.WIDTH_DRIVING_LANE*2 - cT.TURN_RADIUS - (param2+1)*cT.AGV_LENGTH - cT.FREE_QUAY);}
        width = (float)cT.WIDTH_DRIVING_LANE;
        if (param2==cT.NUMBER_OF_AGVS_AT_SIDES-1) {height = (float)(cT.DIST_TOP_QUAY-cT.TURN_RADIUS*2 - cT.WIDTH_DRIVING_LANE*4 - param2*cT.AGV_LENGTH - cT.FREE_QUAY);}
        else {height = (float)cT.AGV_LENGTH;}
        break;
      case 10:
        // semTurn, param1=corner, param2=lanePosition
        width = (float)((cT.WIDTH_DRIVING_LANE*2 + cT.TURN_RADIUS)/2);
        height = (float)(cT.WIDTH_DRIVING_LANE*2 + cT.TURN_RADIUS);
        if ((param1==1) || (param1==2)) {
          x = (float)(cT.TOTAL_WIDTH - cT.FREE_TERMINAL_RIGHT
                     - (2-param2)*width);
        }
        else {
          x = (float)((1-param2)*width);
        }
        if (param1<2) {y = (float)(0);}
        else {
          y = (float)(cT.DIST_TOP_QUAY - cT.FREE_QUAY
                      - height);}
        break;
      case 8:
        // semStackLane, param1=lanePosition, param2=index
        x = (float)(cT.WIDTH_DRIVING_LANE*2+cT.TURN_RADIUS + param2*cT.AGV_LENGTH);
        y = (float)((1-param1)*cT.WIDTH_DRIVING_LANE);
        height = (float)(cT.WIDTH_DRIVING_LANE);
        if (param2==cT.NUMBER_OF_AGVS_AT_STACKLANE-1) {
          width = (float)(cT.STACK_WIDTH_INCL*cT.numberOfStacks +
                        cT.FREE_SPACE_AT_SIDES*2 - 2*cT.TURN_RADIUS -
                        (cT.NUMBER_OF_AGVS_AT_STACKLANE-1)*cT.AGV_LENGTH-1);
        } else {width = (float)(cT.AGV_LENGTH);}
        break;
      case 12:
        // semTtable, param1=ttableNr
        x = (float)(cT.TTABLE_X[param1]+cT.FREE_TERMINAL_LEFT+cT.WIDTH_DRIVING_LANE+cT.FREE_SPACE_AT_SIDES+cT.STACK_WIDTH_INCL/2);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE+ cT.DIST_STACK_CENTER + cT.DIST_CENTER_PARK + cT.LENGTH_PARKLANE + cT.FREE_PARK_EXIT);
        height= (float)(cT.TTABLE_HEIGHT);
        width = (float)(cT.TTABLE_WIDTH);
        break;
      case 13:
        // semCenterSouth, param1=stackNr
        x = (float)(cT.AGV_LENGTH/2+ cT.STACK_WIDTH_INCL*param1+cT.WIDTH_DRIVING_LANE*2+cT.FREE_SPACE_AT_SIDES);
        y = (float)(cT.DIST_TOP_STACK + cT.LENGTH_STACKLANE + cT.DIST_STACK_CENTER);
        width = (float) (cT.STACK_WIDTH_INCL-cT.AGV_LENGTH);
        height = (float)(cT.WIDTH_CENTER_LANE*2);
        break;
      case 14:
        // semQuayLane, param1=lanePosition, param2=index
        x = (float)(cT.WIDTH_DRIVING_LANE*2+cT.TURN_RADIUS + param2*cT.AGV_LENGTH);
        y = (float)((param1-2)*cT.WIDTH_DRIVING_LANE+cT.DIST_TOP_QUAY - cT.FREE_QUAY);
        height = (float)(cT.WIDTH_DRIVING_LANE);
        if (param2==cT.NUMBER_OF_AGVS_AT_STACKLANE-1) {
          width = (float)(cT.STACK_WIDTH_INCL*cT.numberOfStacks +
                        cT.FREE_SPACE_AT_SIDES*2 - 2*cT.TURN_RADIUS -
                        (cT.NUMBER_OF_AGVS_AT_STACKLANE-1)*cT.AGV_LENGTH-1);
        } else {width = (float)(cT.AGV_LENGTH);}
        break;
    }
  }
  public float xScale = 1;
  public float yScale = 1;

  /**
   * Paints sempaphore in right color on right position.
   * Red: capacity = 0; Green: capacity = 1; Yellow: capacity = 2
   * Orange: capacity = 3; Magenta: capacity = 4; Cyan: capacity = 5;
   * Blue: capacity > 5.
   */
  public void paint (Graphics g) {
    if (changed) {
      Graphics2D graph = (Graphics2D)g;
      graph.setBackground(Color.yellow);
      int capacity = semaphore.getCapacity();
      switch (capacity) {
        case 0:graph.setColor(Color.red);break;
        case 1:graph.setColor(Color.green);break;
        case 2:graph.setColor(Color.yellow);break;
        case 3:graph.setColor(Color.orange);break;
        case 4:graph.setColor(Color.magenta);break;
        case 5:graph.setColor(Color.cyan);break;
        default:graph.setColor(Color.blue);break;
      }
      graph.fillRect((int)(x*xScale),(int)(y*yScale),(int)(width*xScale),(int)(height*yScale));
      graph.setColor(Color.black);
      graph.drawRect((int)(x*xScale),(int)(y*yScale),(int)(width*xScale),(int)(height*yScale));
      changed = false;
    }
  }


  /**
   * Redraws Semaphore. Called when semaphore changed capacity (color).
   */
  public void modelChanged() {
    changed = true;
    sceneViewer.modelChanged();
  }

  /**
   * Repaints SceneViewer.
   */
  public void repaint(){
    changed = true;
    sceneViewer.repaint();
  }

}