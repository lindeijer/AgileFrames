package net.agileframes.traces;
import net.agileframes.core.traces.Navigator;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.forces.Actor;
import net.agileframes.core.server.Service;
import net.agileframes.core.server.Server;
import net.agileframes.core.server.Service.UnknownClientException;
import net.jini.core.lookup.ServiceID;

public class NavigatorProxy implements Navigator { // serializable !!

  public Scene scene = null; // remote interface

  public NavigatorProxy(Scene scene) {
    this.scene = scene;
  }

  ///////////////// implementation of Navigator ////////////////

   public Action getSceneAction(String name) { return null; }

  /**
  @param destination a Local somewhere in the infrastructure.
  @return the scene that controls the infrastructure at the destination.
  */
  public Scene getDestinationScene(Object dst) { return null; }


  /**
  @param destination a scene somewhere in the infrastructure.
  @return the lowest upper bound scene in the hierarchy wrt this scene and the destination scene.
  */
  public Scene getTopScene(Scene dst){ return null; }

  /**
  Called by an actor upon a top-scene after it gets a new job.
  @param actor the actor with the new job
  @param source the scene the starting point A is in
  @param A the starting point of the job, the actor is there now I suppose.
  @param destination the scene the finishing point B is in
  @param B the finishing point of the job.
  */
  public Action getSceneAction(Actor actor,
    Scene srcScene,Object src,
    Scene dstScene,Object dst
  ){ return null; }

  /**
  Called by an scene-action upon a sub-scene.
  @param superSceneAction in the context of a job
  @param source the scene the starting point A is in
  @param A the starting point of the job, the actor is there now I suppose.
  @param B the exit point of the job in the sub-scene
  */
  public Action getSceneAction(Action superSceneAction,
    Scene srcScene,Object src,Object dst
  ){ return null; }

  /**
  called by a sub-scene-action to get a superscene action.
  @param subSceneAction in the context of a job
  @param A the exit point of the job in the subscene, the actor is there now I suppose.
  @param destination the scene the finishing point A is in
  @param B the finishing point of the job in the destination scene.
  */
  public Action getSceneAction(Action subSceneAction,
    Object A,Scene destination,Object B
  ){ return null; }

  //////////////// implementation of service /////////////////////

  public Server getServer(){ return null; }
  public void setClient(Object client,ServiceID clientID) throws UnknownClientException {}
  public void setClient(Object client) throws UnknownClientException {}

} 