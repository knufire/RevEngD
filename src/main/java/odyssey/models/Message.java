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
  
  public String getMethodCallingClassName() {
    return methodCallingClass.getShortName();
  }
  
  public String getRecievingClass() {
    return method.getDeclaringClass().getShortName();
  }
  
  public String getMethodName() {
    return method.getName();
  }
  
  public String toString() {
    return getMethodCallingClassName() + " calls " + getRecievingClass() + "." + getMethodName();
  }
  
  public abstract String getPlantUMLString();
  
}
