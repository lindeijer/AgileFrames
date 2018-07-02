package com.agileways.forces.machine.agv;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import com.agileways.forces.machine.agv.avatar.AvatarAGV;
import net.agileframes.vr.Virtuality;
//import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;
//import com.agileways.traces.scene.jumboscene.SceneState;
import com.agileways.traces.scene.jumboscene.SceneAvatar;
import com.agileways.traces.scene.jumboscene.SceneBody;
import net.agileframes.vr.Color3D;
import  com.agileways.forces.maneuver.CircularBendLeft;
import  com.agileways.forces.maneuver.CircularBendRight;
//import  com.agileways.forces.maneuver.GoStraight;
//import com.agileways.forces.machine.agv.AgvRegistry;
import java.rmi.Naming;
//import java.rmi.RemoteException;
//import com.agileways.forces.machine.agv.AGV;
import net.agileframes.core.vr.Body;


public class AgvAvatarMain {

  public static void main(String[] args) {
    try {
      Virtuality virtuality = new Virtuality();
      //
      if (true) {  // put trajectories in virtuality

        Trajectory[] compTraj = new Trajectory[8];
        compTraj[0] = new CircularBendLeft(30.0f,0.0f);
        compTraj[0].initialTransform = new POSTransform(new Position(0,0),0,new Position(1,1));
        //
        compTraj[1] = new CircularBendRight(30.0f,0.0f);
        compTraj[1].append(compTraj[0],null);
        compTraj[2] = new CircularBendRight(30.0f,0.0f);
        compTraj[2].append(compTraj[1],null);
        compTraj[3] = new CircularBendRight(30.0f,0.0f);
        compTraj[3].append(compTraj[2],null);
        compTraj[4] = new CircularBendRight(30.0f,0.0f);
        compTraj[4].append(compTraj[3],null);
        //
        compTraj[5] = new CircularBendLeft(30.0f,0.0f);
        compTraj[5].append(compTraj[4],null);
        compTraj[6] = new CircularBendLeft(30.0f,0.0f);
        compTraj[6].append(compTraj[5],null);
        compTraj[7] = new CircularBendLeft(30.0f,0.0f);
        compTraj[7].append(compTraj[6],null);
        //
        Trajectory traj = new Trajectory(compTraj);
        traj.initialTransform = new POSTransform(new Position(0,0),0,new Position(1,1));
        //
        SceneBody davidSceneBody = new SceneBody(1);
        davidSceneBody.addTrajectory(traj);
        davidSceneBody.setResolution(100);
        davidSceneBody.setColor(Color3D.magenta);
        SceneAvatar davidSceneAvatar = new SceneAvatar(null);
        davidSceneAvatar.setState(davidSceneBody.getState());
        virtuality.add(davidSceneAvatar.getLayout());
      }
      AgvRegistry agvRegistry = (AgvRegistry)Naming.lookup("rmi://dutw1700.wbmt.tudelft.nl/agvRegistry");
      System.out.println("AgvRegistryImplBase found=" + agvRegistry.toString());
      AGV agv = agvRegistry.lookup("LpsTestAGV");
      System.out.println("found LpsTestAGV=" + agv.toString());
      AvatarAGV agvAvatar = new AvatarAGV((Body)agv,1);
      agvAvatar.setGeometryAndAppearanceID(1,1);
      virtuality.add(agvAvatar.getBG() );
      virtuality.end();
      //
      BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
      for(;;) {
        System.out.println("press return to connect to a new agv");
        console.readLine();
        agv = agvRegistry.lookup("LpsTestAGV");
        System.out.println("AGV 'LpsTestAGV' found again =" + agv.toString());
        boolean setRefreshbehavior = true;
        //agvAvatar.setBody((Body)agv,setRefreshbehavior);
      }
    } catch (Exception e) {
      System.out.println("Exception in main: " + e.getMessage());
      e.printStackTrace();
    }
  }


}