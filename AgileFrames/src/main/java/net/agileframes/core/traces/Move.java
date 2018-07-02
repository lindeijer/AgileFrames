package net.agileframes.core.traces;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Actor;
import net.jini.core.lookup.ServiceID;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;

/**
Executed by a SceneAction, it will call exec or run.
A sceneaction receives a "finished" and a "started" brief from the move,
(these may originate from the move or from the machine itself).
A move receives handler invocations, these may have been invoked by signals or rmi.
A move may retrieve briefs if it wants to.

These pre-defined moves work as follows:

    they are cloned from their original when the scene-action is created.
    they are passed the actor when they are cloned.
    upon exec() or run() the move calls acceptMove(Trajectory,Rule[])
      * Trajectory must be executed
      * Rules are added for signalling "started" and "finished"
      * handlers started() and finished() send the "started" and "finished" briefs to the super
*/

public class Move /* extends UnicastRemoteObject implements Action */ {

   public ServiceID serviceID; // assigned during clone.

   // trajectory
   // rules
   // handlers

}


