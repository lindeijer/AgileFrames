package net.agileframes.forces.flag;

/*
import net.agileframes.core.Rule;
import net.agileframes.core.State;
import net.agileframes.forces.core.Machine;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.forces.rule.SafetyRule;
import net.agileframes.forces.core.Prediction;

/**
 * Predictor.java
 * Created: Wed Jan 12 12:59:43 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

computes (u+,t+) for every (interresting) flag.
The nearest (u+,t+) is the prediction
*/


public interface Predictor { } /*  // part of or actually is simulation propose-action

  public void setMachine(Machine machine);

  public Prediction predict(Rule rule,State state);

}

//// below is agileways code.  

  /**
  Make predictions for all the machines rules
  @param rules to be predicted
  @param state wrt which the predictions are made
  @return predictions, index 0 is earliest
  *
  public Prediction[] predict(State f) {
    // return predict(machine.rules,f);
    return null;
  }

  /**
  Make predictions for all rules
  @param rules to be predicted
  @param state wrt which the predictions are made
  @return predictions, index 0 is earliest
  /
  public Prediction[]  predict(Rule[] rules,State f) {
    Prediction[] predictions = new Prediction[rules.length];
    for (int i=0;i<rules.length;i++) {
      if (rules[i].active == true) {
        // predictions[i] = predict(rules[i],f);
      }
      else {
        predictions[i] = null;
      }
    }
    sort(predictions);
    this.predictions = predictions;
    return predictions;
  }


  /////////////////////////////////////////////////////////////////////

  /**
  public void sort(Prediction[] predictions) { // bubble sort
    boolean sorted = false;
    while (!sorted) {
      sorted = true;
      for (int i=0;i<predictions.length-1;i++) {
        if (predictions[i].t > predictions[i+1].t) {
          Prediction p = predictions[i+1];
          predictions[i+1] = predictions[i];
          predictions[i] = p;
          sorted = false;
        }
      }
    }
    // would be efficient if the rules were now arranged in the same order as the predictions  
  }

  public Prediction getEarliestPrediction() { return null; }

  public Prediction getFirstPrediction() { return null; }

  //////////////////////////////////////////////////////////////////

  public Prediction predict(EvolutionRule rule,State state) { // when will u > x ?
    float u = machine.u;
    float U; // = rule.move.uMax + rule.move.u0;
    float v; // = machine.velocity;
    float a; // = machine.accelleration;
    // solve dt: 0.5*a*dt^2 + v*dt + u = U ; dt > 0
    long du; // = U-u;
    // solve dt: 0.5*a*dt^2 + v*dt = du ; dt > 0
    float dt; // = ( -v + sqrt( v^2 - 4*a*du) ) / 2*a;
    return null; // new Prediction(dt,du,rule);
  }

  public Prediction predict(SpeedRule rule,State state) { // when will F'(u) > x
    float u = machine.u;
    float v; // = machine.velocity;
    float a; // = machine.accelleration;
    // solve dt: a*dt + v = rule.v  and
    float dt; // = ( rule.v - v ) / a;
    // slove du: v*dt + 0.5*a*dt^2
    float du; // = v*dt + 0.5*a*dt^2;
    return null; // new Prediction(dt,du,rule);
  }

  /**
  Overload this method for a more complex computation using predictor or stuff like that.
  This method assumes that G'(t)=v and G''(t)=a are applicable to u(t).

  public Prediction predict(ExpectedSpeedRule rule,State state) { // when will F'(h) > x
    // assume that F has no effect on accelleration.
    // v(u) = b(H-u)+vH;
    // u(t) = 0.5*a*t^2+v*t+u0;
    // v(t) = u'(t) = a*t+v;
    // solve t: a*t+v = b(H-u(t))+vH

    //          a*t+v = b(H-(0.5*a*t^2+v*t+u0))+vH
    //          a*t+v = b*H-b*(0.5*a*t^2+v*t+u0)+vH
    //          a*t+v = b*H - b*0.5*a*t^2 - b*v*t - b*u0 + vH
    //          b*0.5*a*t^2 + b*v*t + a*t = -v + b*H - b*u0 + vH
    //          (0.5*a*b)*t^2 + (b*v+a)*t + (b*(H-u0)-v-vH) = 0
    float a; // = machine.accelleration;
    float b; // = machine.decelleration;
    float v; // = machine.velocity;
    long  H; // = rule.horizon;
    float  u0 = machine.u;
    //
    float A; // = (0.5*a*b);
    float B; // = (b*v+a);
    float C; // = (b*(H-u0)-v-vH);
    //
    long dt; // = (long) (-B + sqrt(B^2 - 4*A*C))/2*A;
    long du; // = (long) 0.5*a*dt^2+v*dt+u0;
    return null; // new Prediction(dt,du,rule);
  }


  public Prediction predict(SafetyRule rule,State state) { // when will F'(H) > 0
    // assume that F has no effect on accelleration.
    // v(u) = b(H-u);
    // u(t) = 0.5*a*t^2+v*t+u0;
    // v(t) = u'(t) = a*t+v;
    // solve t: a*t+v = b(H-u(t))
    //          a*t+v = b(H-(0.5*a*t^2+v*t+u0))
    //          a*t+v = b*H-b*(0.5*a*t^2+v*t+u0)
    //          a*t+v = b*H - b*0.5*a*t^2 - b*v*t - b*u0
    //          b*0.5*a*t^2 + b*v*t + a*t = -v + b*H - b*u0
    //          (0.5*a*b)*t^2 + (b*v+a)*t + (b*(H-u0)-v) = 0
    float a; // = machine.accelleration;
    float b; // = machine.decelleration;
    float v; // = machine.velocity;
    float  H; // = rule.horizon;
    float  u0 = machine.u;
    //
    float A; // = (0.5*a*b);
    float B; // = (b*v+a);
    float C; // = (b*(H-u0)-v);

    long dt; // = (long) (-B + sqrt(B^2 - 4*A*C))/2*A;
    long du; // = (long) 0.5*a*dt^2+v*dt+u0;
    return null; // new Prediction(dt,du,rule);
  }

  /////////////////////////////////////////////////////////////

  public static Prediction[] predictions;

  ////////////////////////////////////////////////////////////////


  */
