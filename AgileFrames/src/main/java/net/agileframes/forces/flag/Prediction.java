package net.agileframes.forces.flag;
import net.agileframes.core.forces.Flag;

/**
 * Created: Wed Jan 12 12:59:43 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

Prediction of where and when a flag will be lowered or raised.
*/

public class Prediction {

  public long dt;
  public float du;
  public Flag flag;

  public Prediction(long dt,float du,Flag flag) {
    // this.dt=dt; this.du=du;this.rule=rule;
  }

  public Prediction(long dt,float du) { this(dt,du,null); }

}


