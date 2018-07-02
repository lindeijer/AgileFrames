package com.agileways.traces.scene.jumboscene;

import net.agileframes.vr.AvatarImplBase;
import com.agileways.traces.scene.jumboscene.SceneState;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.vr.Color3D;

import javax.media.j3d.*;
import javax.vecmath.*;

import net.agileframes.vr.Virtuality;
import com.agileways.forces.machine.crane.avatar.AvatarQC;
import com.agileways.forces.infrastructure.jumbo.avatar.Pillar;

import com.agileways.traces.scene.jumboterminal.CrossoverScene;

/**
 * SceneAvatar is the Avatar belonging to SceneBody.
 *
 * Use setState to input the state of SceneBody.
 * Use getLayout to add the visualization of trajectories to the 3d environment.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class SceneAvatar extends AvatarImplBase{

  private SceneState scene;
  private float domain = 0;

  /** 3d-Pillar at center of the terminal */
  public Pillar[][] centerPillar;
  /** 3d-Pillar at parks on the terminal */
  public Pillar[][] parkPillar;

  CrossoverScene crossoverScene;

  public SceneAvatar(CrossoverScene crossoverScene) {
    this.crossoverScene = crossoverScene;

    // declare pillars
    centerPillar = new Pillar[crossoverScene.cT.numberOfStacks][2];
    parkPillar   = new Pillar[crossoverScene.cT.numberOfStacks][crossoverScene.cT.lanesPerPark];

    // create the pillars
    System.out.println("creating pillars");
    for (int i=0; i<crossoverScene.cT.numberOfStacks; i++) {
      if (i>0) {
        for (int j=0; j<2; j++) {
          float x = (crossoverScene.cT.STACK_WIDTH_INCL*i + crossoverScene.cT.FREE_SPACE_AT_SIDES + crossoverScene.cT.FREE_TERMINAL_LEFT +
                   crossoverScene.cT.WIDTH_DRIVING_LANE ) * crossoverScene.scale + crossoverScene.xTrans;
          float y = (crossoverScene.cT.DIST_CENTER_PARK + crossoverScene.cT.DIST_PARK_QUAY + crossoverScene.cT.LENGTH_PARKLANE +
                    (j-0.5f)*crossoverScene.cT.WIDTH_CENTER_LANE) * crossoverScene.scale + crossoverScene.yTrans;
          centerPillar[i][j] = new Pillar(x,y,(float)Math.PI/2,Color3D.darkgreen,1);
        }
      }
      for (int j=0;j<crossoverScene.cT.lanesPerPark; j++) {
        float x = (crossoverScene.cT.getParkLaneX(i,j) - crossoverScene.cT.WIDTH_PARKLANE/2)* crossoverScene.scale + crossoverScene.xTrans;
        float y = (crossoverScene.cT.getParkLaneY() + crossoverScene.cT.LENGTH_PARKLANE/2)* crossoverScene.scale + crossoverScene.yTrans;
        if (i < crossoverScene.cT.numberOfStacks - 1) {
          parkPillar[i][j] = new Pillar(x,y,0,Color3D.darkgreen,1);
        } else {
          if (j==0) {          parkPillar[i][j] = new Pillar(x,y,0,Color3D.darkgreen,1);}
        }
      }
    }
  }

  /**
   * Inputs scene-state with all the trajectories to be visualized.
   * Use SceneBody.getState as input.
   * @param scene   SceneState
   */
  public void setState(SceneState scene) {this.scene = scene;}

  /**
   * Returns the visualization of all the trajectories in scene-state.
   * Add the result of this method to the virtuality to show on screen.
   *
   * @return  Shape3D containing all pixels that visualize trajectories.
   *          Use Virtuality.add to add to 3d environment and do not forget
   *          to Virtuality.end to show 3d environment on screen.
   */
  public Shape3D getLayout(){
    // This method will make a shape with all the moves inside
    System.out.println("Calculating Model Layout...");

    // Determine and set size of Array
    domain = 0;
    for (int i=0;i<scene.trajectories.length;i++) {
      if (scene.trajectories[i] != null) {domain+=scene.trajectories[i].domain;}
    }
    int totalSize = (int)(domain * scene.resolution + 1);// total number of dots on canvas3D
    int offset = 0;
    PointArray pArray = new PointArray(totalSize, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

    // Calculate one composedMove at a time
    // i = the index of the composedMove to draw
    for (int i=0;i<scene.trajectories.length;i++) {
      if (scene.trajectories[i] != null) {
        int composedTrajSize = (int)(scene.trajectories[i].domain * scene.resolution + 1);// number of dots of this move
        // Compute one point at a time
        // j = the index of the dot to draw
        for (int j=0;j<composedTrajSize;j++) {
          float u = (float)(j * scene.trajectories[i].domain / composedTrajSize);
          State state = scene.trajectories[i].compute(u);
          POS pos = (POS) scene.trajectories[i].initialTransform.transform(state);
          pArray.setCoordinate(offset + j,new Point3f(pos.x,pos.y,0));
          if (j==0) {pArray.setColor(offset + j,Color3D.red);}// make first dot of trajectory red
          else {pArray.setColor(offset + j,scene.color);}
        }
        offset += composedTrajSize - 1;
      }
    }
    System.out.println("Finished Calculating Model Layout");
    return new Shape3D(pArray);
  }

  ///////////////////////////////////////////////////////////////////////////




  public void display(Virtuality virtuality){

    // qcs
    AvatarQC[] avatarQC = new AvatarQC[crossoverScene.cT.NUMBER_OF_TURNTABLES];
    for (int i=0; i<crossoverScene.cT.NUMBER_OF_TURNTABLES; i++){
      float x = crossoverScene.cT.TTABLE_X[i] +
                crossoverScene.cT.FREE_TERMINAL_LEFT +
                crossoverScene.cT.WIDTH_DRIVING_LANE +
                crossoverScene.cT.FREE_SPACE_AT_SIDES +
                crossoverScene.cT.TTABLE_WIDTH/2 +
                crossoverScene.cT.STACK_WIDTH_INCL/2;
      x = x*crossoverScene.scale + crossoverScene.xTrans;
      float y = (crossoverScene.cT.getParkLaneY() +
                 crossoverScene.cT.LENGTH_PARKLANE/2)* crossoverScene.scale +
                 crossoverScene.yTrans;
      avatarQC[i] = new AvatarQC(x,y, false);
      virtuality.add(avatarQC[i].getBG());
    }

    //pillars
    System.out.println("displaying pillars");
    for (int i=0; i<crossoverScene.cT.numberOfStacks; i++) {
      if (i>0) {
        for (int j=0; j<2; j++) {
          virtuality.add(centerPillar[i][j].getBG());
        }
      }
      for (int j=0;j<crossoverScene.cT.lanesPerPark; j++) {
        if (i<crossoverScene.cT.numberOfStacks-1) {
          if (parkPillar[i][j] != null) {
            virtuality.add(parkPillar[i][j].getBG());
          } else { System.out.println("not displaying a pillar"); }
        } else {
          if (j==0) {
            virtuality.add(parkPillar[i][j].getBG());
          }
        }
      }
    }
  }






}