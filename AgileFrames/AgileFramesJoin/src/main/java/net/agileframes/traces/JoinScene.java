package net.agileframes.traces;

import java.rmi.RemoteException;

import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.vr.AvatarFactory;
import net.agileframes.core.vr.SceneAvatarFactory;

public class JoinScene extends SceneIB {

	public JoinScene(String name) throws RemoteException {
		super(name);
	}
	
	@Override
	public AvatarFactory getAvatarFactory() {
		return new SceneAvatarFactory();
	}
	
	// inherited from scene
	public synchronized Action join(Actor actor, LogisticPosition lp) throws java.rmi.RemoteException {
		// return the sa to the requested lp
		System.out.println("SceneIB.join: actor="+actor+",lp="+lp);
		FuSpace p = actor.getMachine().getState();
		// create sa that claims right semaphore
		Action sa = new JoinSceneAction(lp);
		try {
			sa = (SceneAction) sa.clone(actor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		registerSA(actor, sa);
		sa.setActor(actor);// this doesnt help, because this actor will be serialized!
		return sa;
	}

}
