package net.agileframes.core.vr;
import java.rmi.Remote;
/**
 * <b>Interface for bodies that need to be visualized in a remote Virtuality.</b>
 * <p>
 * Created: Wed Jan 12 14:56:39 2000
 * @see     Body
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface BodyRemote extends Body, Remote {}
