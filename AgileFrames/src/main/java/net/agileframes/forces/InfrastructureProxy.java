package net.agileframes.forces;
import net.agileframes.core.forces.Infrastructure;
import net.agileframes.core.forces.Machine;

/**
Downloaded by a machine when it becomes a player on the infrastructure playground.
The proxy is tailored for use with specific types of machines, the machines
must therefore take care to download the right one.

<P>Usually the proxy is downloaded by the machine proprioceptor, this is the object
that knows iff it needs an infrastructure proxy.

<P>A downloaded proxy sits on the machine and is responsible for communication with
the INFRASTRUCTURE, and for informing the machine of its functional state within
the infrastructure whenever it can.

<P>the infrastructure will send data to the proxy which the proxy needs.
This may be the functional state of the machine as observed by a monitoring-system
implemented in the infrastructure, or it may be partial data such as the location
of blinkers/magnets within the infrastructure which the machine may observe with sensors.

<P>In the second case the machine will pass relevant observations to the proxy, together
with the data provided by the infrastructure the proxy can compute the functional state.
The machine will call special methods on a special proxy.

<P>When the proxy knows a new functional state for the machine it invokes setState
on the machine.

<P>This specific proxy assumes it simple gets state updates from the infrastructure
and that the INFRASTRUCTURE simply knows how to recognize machines of the type
that downloaded the proxy.
*/

public class InfrastructureProxy {} /*

implements Infrastructure {

  public InfrastructureProxy() { // re-serialization
  }

  /////////////// implementation of Infrastructure ///////////////////////

  Machine machine = null; // note that this is a remote-stub of machine

  public void addMachine(Machine machine) {
    // inform the INFRASTRUCTURE that the machine is in the game
    // now the INFRASTRUCTURE is aware of the machine it will pass any
    // relevant information it knows about/for it to the proxy,
    // such as observed functional state
    // possibly observed via an infra-red camera system...
    // or a map of the magnet-locations...
    //
    // the INFRASTRUCTURE may/will call machine.echo();
    // the proxy may/will call machine.setState(state,time);
  }

  public void removeMachine(Machine machine){
    // inform the INFRASTRUCTURE that the machine is in the game and
    // that it may stop looking out for it.
  }


}

*/