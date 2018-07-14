package net.agileframes.forces;

import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.Ticket;

/**
 * <b>Pre-defined Move to join a Scene.</b>
 * <p>
 * 
 * @author H.J. Wierenga
 * @version 0.1
 */
public class JoinMove extends Move {
	// for nr, see JoinManoeuvre
	public JoinMove(FuTransform t, FuSpace p, int nr) {
		super(t);

		this.manoeuvre = new JoinManoeuvre(transform, p, nr);
		this.signs = new Sign[1];
		signs[0] = new Sign();
	}

	/** The script. */
	public void moveScript() {
		try {
			System.out.println("JOINMOVE: about to start exec: manoeuvre="+manoeuvre);
			manoeuvre.startExecution();
			System.out.println("JOINMOVE: about to watch flag="+manoeuvre.getFlag(0));
			watch(manoeuvre.getFlag(0));
			System.out.println("JOINMOVE: about to broadcast");
			signs[0].broadcast();
			System.out.println("JOINMOVE: sign broadcasted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object clone(Actor actor) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public void execute() {
		// TODO Auto-generated method stub
	}

	public Ticket getExitTicket() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBeginPosition(LogisticPosition beginPosition) {
		// TODO Auto-generated method stub
	}

	public Scene getScene() {
		// TODO Auto-generated method stub
		return null;
	}

	public void execute(Ticket[] externalTickets) {
		// TODO Auto-generated method stub
	}

}
