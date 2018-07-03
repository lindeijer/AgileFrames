// Skeleton class generated by rmic, do not edit.
// Contents subject to change without notice.

package net.agileframes.traces;

public final class ActorIB_Skel
    implements java.rmi.server.Skeleton
{
    private static final java.rmi.server.Operation[] operations = {
	new java.rmi.server.Operation("boolean acceptJob(net.jini.core.lookup.ServiceID, net.agileframes.core.services.Job)"),
	new java.rmi.server.Operation("java.lang.String getLoginbaseName()"),
	new java.rmi.server.Operation("net.agileframes.core.forces.MachineRemote getMachine()"),
	new java.rmi.server.Operation("java.lang.String getName()"),
	new java.rmi.server.Operation("net.agileframes.core.traces.Actor.Properties getProperties()"),
	new java.rmi.server.Operation("net.jini.core.lookup.ServiceID getServiceID()"),
	new java.rmi.server.Operation("net.jini.core.lookup.ServiceID getServiceID(long)"),
	new java.rmi.server.Operation("void run()")
    };
    
    private static final long interfaceHash = 3059164585558893047L;
    
    public java.rmi.server.Operation[] getOperations() {
	return (java.rmi.server.Operation[]) operations.clone();
    }
    
    public void dispatch(java.rmi.Remote obj, java.rmi.server.RemoteCall call, int opnum, long hash)
	throws java.lang.Exception
    {
	if (opnum < 0) {
	    if (hash == -6086090398976811155L) {
		opnum = 0;
	    } else if (hash == -142064174142828223L) {
		opnum = 1;
	    } else if (hash == 4013240963088959209L) {
		opnum = 2;
	    } else if (hash == 6317137956467216454L) {
		opnum = 3;
	    } else if (hash == 646903834764046703L) {
		opnum = 4;
	    } else if (hash == 3551855729045843904L) {
		opnum = 5;
	    } else if (hash == 5001282240910113962L) {
		opnum = 6;
	    } else if (hash == -8003352271541955702L) {
		opnum = 7;
	    } else {
		throw new java.rmi.UnmarshalException("invalid method hash");
	    }
	} else {
	    if (hash != interfaceHash)
		throw new java.rmi.server.SkeletonMismatchException("interface hash mismatch");
	}
	
	net.agileframes.traces.ActorIB server = (net.agileframes.traces.ActorIB) obj;
	switch (opnum) {
	case 0: // acceptJob(ServiceID, Job)
	{
	    net.jini.core.lookup.ServiceID $param_ServiceID_1;
	    net.agileframes.core.services.Job $param_Job_2;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_ServiceID_1 = (net.jini.core.lookup.ServiceID) in.readObject();
		$param_Job_2 = (net.agileframes.core.services.Job) in.readObject();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } catch (java.lang.ClassNotFoundException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    boolean $result = server.acceptJob($param_ServiceID_1, $param_Job_2);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeBoolean($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 1: // getLoginbaseName()
	{
	    call.releaseInputStream();
	    java.lang.String $result = server.getLoginbaseName();
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 2: // getMachine()
	{
	    call.releaseInputStream();
	    net.agileframes.core.forces.MachineRemote $result = server.getMachine();
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 3: // getName()
	{
	    call.releaseInputStream();
	    java.lang.String $result = server.getName();
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 4: // getProperties()
	{
	    call.releaseInputStream();
	    net.agileframes.core.traces.Actor.Properties $result = server.getProperties();
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 5: // getServiceID()
	{
	    call.releaseInputStream();
	    net.jini.core.lookup.ServiceID $result = server.getServiceID();
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 6: // getServiceID(long)
	{
	    long $param_long_1;
	    try {
		java.io.ObjectInput in = call.getInputStream();
		$param_long_1 = in.readLong();
	    } catch (java.io.IOException e) {
		throw new java.rmi.UnmarshalException("error unmarshalling arguments", e);
	    } finally {
		call.releaseInputStream();
	    }
	    net.jini.core.lookup.ServiceID $result = server.getServiceID($param_long_1);
	    try {
		java.io.ObjectOutput out = call.getResultStream(true);
		out.writeObject($result);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	case 7: // run()
	{
	    call.releaseInputStream();
	    server.run();
	    try {
		call.getResultStream(true);
	    } catch (java.io.IOException e) {
		throw new java.rmi.MarshalException("error marshalling return", e);
	    }
	    break;
	}
	    
	default:
	    throw new java.rmi.UnmarshalException("invalid method number");
	}
    }
}
