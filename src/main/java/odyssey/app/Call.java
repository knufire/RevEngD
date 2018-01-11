package odyssey.app;

import soot.SootClass;
import soot.SootMethod;

public class Call {
  
  private SootClass caller;
  private SootMethod method;
  
  public Call(SootClass caller, SootMethod method) {
    super();
    this.caller = caller;
    this.method = method;
  }
  
  public SootClass getCaller() {
    return caller;
  }
  public SootMethod getMethod() {
    return method;
  }
  
  public String getCallingClass() {
    return caller.getShortName();
  }
  
  public String getRecievingClass() {
    return method.getDeclaringClass().getShortName();
  }
  
  public String getMethodName() {
    return method.getSignature();
  }
  
}
