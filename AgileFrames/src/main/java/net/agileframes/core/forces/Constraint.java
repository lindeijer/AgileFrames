package net.agileframes.core.forces;

/**
A constraint upon the dynamic interpretation of a machines activities
A constraint defines a flag that must be true,
the machine must attept to satisfy constraints.
At any one time a machine must satisfy a set of (mutually coherent) constraints.
<p>
A constraint is a flag the machine must force to be true if at all possible.
*/

public interface Constraint extends Flag {

  // public int getPriority();

}

