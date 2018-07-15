package net.agileframes.core.traces;

/**
 * <b>Interface to be implemented by all (serializable) actions. <i>[To be written]</i></b>
 * <p>
 * This interface should be implemented by all actions (in the context of a
 * Scene). At the time of writing, this interface is empty, but it should contain
 * all methods that are being used by both Move and SceneAction.<br>
 * It is advised to extend
 * {@link net.agileframes.traces.ActionIB Action Implementation Base} (<code>ActionIB</code>)
 * if you want to create a new action.
 * <p>
 * <b>Future contents:</b><br><ul>
 * <li>protected void execute(Ticket[])
 * <li>protected void run(Ticket[])
 * <li>protected void script()
 * <li>protected void setActor(Actor actor)
 * <li>protected void watch(interface Watchable)<br>
 * (Interface Watchable should be created in net.agileframes.forces
 * and implemented by Signs and Flags, probably the only method of Watchable
 * should be addListener())
 * </ul>
 * @see SceneAction
 * @see net.agileframes.core.forces.Move
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */

public interface Action {
	
	public Object clone(Actor actor) throws CloneNotSupportedException;
	
	public void setActor(Actor actor);
	
	public void execute();
	
	public Ticket getExitTicket();
	
	public void setBeginPosition(LogisticPosition beginPosition);
	
	public Scene getScene();
	
	public void execute(Ticket[] externalTickets);
}
