package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public abstract class Message {
  private SootClass methodCallingClass;
  private SootMethod method;
  
  public Message(SootClass methodCallingClass, SootMethod method) {
    super();
    this.methodCallingClass = methodCallingClass;
    this.method = method;
  }
  
  public SootClass getMethodCallingClass() {
    return methodCallingClass;
  }
  public SootMethod getMethod() {
    return method;
  }
  public SootClass getReceivingClass() {
    return method.getDeclaringClass();
  }
  
  protected String getMethodCallingClassName() {
    return methodCallingClass.getShortName();
  }
  
  protected String getReceivingClassName() {
    return method.getDeclaringClass().getShortName();
  }
  
  protected String getMethodName() {
    return method.getName();
  }
  
  public String toString() {
    return getMethodCallingClassName() + " calls " + getReceivingClassName() + "." + getMethodName();
  }
  
  public abstract String getPlantUMLString();
  
}
