package com.agileways.vr.agv;

import net.agileframes.vr.space3d.Avatar3D;
import net.agileframes.core.vr.Body;
import net.agileframes.vr.Colors;


/**
 * AvatarAGV is a simple Java3D representation of an AGV.
 */

public class AvatarAGV extends Avatar3D {

  /**
   * Create an avatar representing an AGV, connect it to a body and with a refreshbehavior
   * @param body body the body to which this avatar will be conected
   * @param frames the number of 'frames' bewteen each trigger
   */
  public AvatarAGV(Body agv, int frames) {
    super(agv,frames);
    addGeometry(new SimpleAgvEmpty());
    addGeometry(new SimpleAgvLoaded());

    addAppearance(Colors.green);
    addAppearance(Colors.blue);
    addAppearance(Colors.yellow);
    addAppearance(Colors.cyan);
    addAppearance(Colors.magenta);
    addAppearance(Colors.darkblue);
    addAppearance(Colors.darkgreen);
    addAppearance(Colors.brown);
    addAppearance(Colors.purple);
    addAppearance(Colors.gray);
    addAppearance(Colors.orange);
    addAppearance(Colors.pink);
    addAppearance(Colors.marine);
    addAppearance(Colors.uglygreen);

    this.setGeometryAndAppearanceID(0, 0); //default
  }

  /**
   * Create a avatar representing an AGV and connect it to a body
   * @param body the body to which this avatar will be connected
   */
  public AvatarAGV(Body body) {
    this(body,0);
  }


  /**
   * Create an avatar representing an AGV
   */
  public AvatarAGV() {
    this(null, -1);
  }



}

