package com.agileways.forces.machine.crane.avatar;

import net.agileframes.vr.AvatarImplBase;
import com.agileways.forces.machine.crane.jumboqc.avatar.SimpleCrane;
import net.agileframes.core.forces.State;
import net.agileframes.forces.space.POS;
import net.agileframes.vr.Color3D;

public class AvatarQC extends AvatarImplBase{

  public AvatarQC(float x, float y, boolean complete) {
    SimpleCrane simplecrane = new SimpleCrane(100.0f, 100.0f, complete);
    //setColors(Color3f Leg, Color3f Rail, Color3f Spar, Color3f Slider, Color3f Cat, Color3f TurnTable, Color3f TurnCircle
    simplecrane.setColors(Color3D.red, Color3D.yellow, Color3D.gray, Color3D.orange, Color3D.green, Color3D.blue, Color3D.gray);
    this.addGeometry(simplecrane);
    this.addAppearance(Color3D.red); // er moet minstens een appearance maar is in dit geval nv belang...buggy !!
    POS state = new POS(x, y, 0.0);
    this.setState(state);
    this.setGeometryAndAppearanceID(0, 0);
  }

  /**
   * Overrides setState from AvatarImplBase
   */
  public void setState(State state) {
    POS newState = (POS)state;
    this.vectorTranslation.set(newState.x, newState.y, 0.0f);
    this.t3dTranslation.set(this.vectorTranslation);
    this.translateTG.setTransform(this.t3dTranslation);


//    this.vectorRotation.set(newState.gamma, newState.beta, newState.alpha);
//    this.t3dRotation.setEuler(this.vectorRotation);
//    this.rotateTG.setTransform(this.t3dRotation);

  }

} 
