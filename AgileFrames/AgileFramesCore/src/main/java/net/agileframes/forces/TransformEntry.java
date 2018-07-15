package net.agileframes.forces;
import net.jini.entry.AbstractEntry;
import net.agileframes.core.forces.FuTransform;
/**
 * <b>Entry for the FuTransform-object.</b>
 * <p>
 * To be used with Jini Lookup Services.
 * An entry is a strongly-typed field in a service item that describes the
 * service or provides secondary interfaces to the service. In this case,
 * the Entry provides the Service with information about the Transform
 * belonging to that Service.
 * @see   net.agileframes.traces.SceneIB#uploadScene()
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class TransformEntry extends AbstractEntry {
  /** The transform for which this Entry is made. */
  public FuTransform transform;
  /** Empty Constructor, not used but needed by AbstractEntry. */
  public TransformEntry() {}
  /**
   * Basic Constructor.
   * @param transform the transform for which this Entry is made
   */
  public TransformEntry(FuTransform transform) {
    this.transform = transform;
  }
}