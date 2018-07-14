package net.agileframes.forces;

import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Sign;

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

}
