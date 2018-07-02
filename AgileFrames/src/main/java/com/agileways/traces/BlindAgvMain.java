package com.agileways.traces;
import java.io.IOException;

import net.agileframes.server.AgileSystem;
import com.agileways.forces.machine.agv.avatar.AvatarAGV;
import net.agileframes.forces.space.*;
import com.agileways.forces.machine.agv.sim.*;
import net.agileframes.forces.trajectory.Start;

import net.agileframes.core.forces.*;
import net.agileframes.core.forces.Machine;

//import com.agileways.traces.scene.jumboterminal.JumboTerminal;
import com.agileways.traces.scene.AgvTrackerFrame;

import javax.media.j3d.*;
//import com.agileways.traces.scene.jumboterminal.JumboScene;
import net.agileframes.forces.mfd.ActorProxy;
import com.agileways.traces.scene.SceneViewer;
import com.agileways.forces.infrastructure.jumbo.avatar.Pillar;
import net.agileframes.vr.Color3D;

import com.agileways.forces.machine.crane.avatar.AvatarQC;

public class BlindAgvMain {  /*
  public static final int numberOfAgvs = 5;

  public static JumboScene jumboScene;
  public static SimAGV[] agv = new SimAGV[numberOfAgvs];
  public static ActorProxy[] actor = new ActorProxy[numberOfAgvs];

  public static void main(String[] args) {
    System.getProperties().put("agilesystem.mute","FALSE");

    for (int agvNr=0;agvNr<numberOfAgvs;agvNr++) {
      try {
        agv[agvNr] = new SimAGV("AGV"+(agvNr+10));
        System.out.println(agv[agvNr].toString()+" was created");
      }
      catch (Exception e) {
        System.out.println("Error in Main while creating AGVs: "+e);
        e.printStackTrace();
      }

      actor[agvNr] = new ActorProxy(agv[agvNr],null,null,null);
    }

  }

  */

}