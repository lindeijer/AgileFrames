package net.agileframes.core.brief;
import java.lang.reflect.Method;
import net.jini.core.lookup.ServiceID;

/**
Brief is the base-class for sending signals asynchronously to objects within
the same agileframes-system.
The destination-object is addressed with its unique ServiceID.
@see SignalOffice

*/

public class Signal extends Brief {

  String method;
  Object[] parameters;

  public Signal(ServiceID src,ServiceID dst,String method,Object[] parameters) {
    super(src,dst);
    this.method = method;
    this.parameters = parameters;
  }

  public Signal(ServiceID src,ServiceID dst,String method) {
    this(src,dst,method,new Object[]{});
  }

  public Signal(ServiceID src,ServiceID dst,String method,Object parameter) {
    this(src,dst,method,new Object[]{parameter});
  }

  public Signal(ServiceID src,ServiceID dst,String method,Object p1,Object p2) {
    this(src,dst,method,new Object[]{p1,p2});
  }

  public Signal(ServiceID src,ServiceID dst,String method,Object p1,Object p2,Object p3) {
    this(src,dst,method,new Object[]{p1,p2,p3});
  }

  public Signal(ServiceID src,ServiceID dst) {
    super(src,dst);
  }

  /////////////////////////////////////////////////////////////////////////

  /**
  Upon arrival the destination process is found, then signal(process) is called
  */
  public void signal(Object object) {
    try {
      Class processClass = object.getClass();
      Class[] parameterTypes = new Class[parameters.length];
      for (int i=0;i<parameters.length;i++) {
        parameterTypes[i] = parameters[i].getClass();
      }
      Method target = processClass.getDeclaredMethod(method,parameterTypes);
      target.invoke(object,parameters);
    }
    catch (Exception e) {System.out.println("Exception in signal():"+e);}
  }

}