package net.agileframes.core.vr;
import net.jini.entry.AbstractEntry;
import java.lang.ClassCastException;


/**
 * <b>An entry-object necessary if Avatar is needed in a remote Virtuality. </b>
 * <p>
 * An implementation of the Entry interface that
 * knows to cast the service object to type Avatar
 * and instantiates and returns a new Avatar object
 * <p>
 * This class currently is only equiped with methods for obtaining space2d
 * and space3d avatars.
 * @see Avatar
 * @see Virtuality
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public abstract class AvatarFactory extends AbstractEntry {
  /** Empty Constructor. Needed for every Entry. */
  public AvatarFactory() {}
  /**
   * Retrieves the right Avatar-type coresponding to the uploaded Body
   * @param Body The machine-descendant object which has been uploaded
   * @return Avatar corresponding to the Body uploaded
   */
  public abstract Avatar getAvatar(Class virtualityType, Body body);
  
}
