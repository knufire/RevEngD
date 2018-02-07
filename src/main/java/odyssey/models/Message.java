package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public class Message {
  private SootClass methodCaller;
  private SootClass methodReceiver;
  private SootMethod method;
  private String type;
  
  public Message(SootClass methodCaller, SootMethod method, SootClass methodReceiver, String type) {
    super();
    this.methodCaller = methodCaller;
    this.method = method;
    this.methodReceiver = methodReceiver;
    this.type = type;
  }
  
  public SootClass getMethodCallerClass() {
    return methodCaller;
  }
  public SootMethod getMethod() {
    return method;
  }
  public SootClass getReceiverClass() {
    return methodReceiver;
  }
  
  public String getMethodCallerClassName() {
    return methodCaller.getShortName().replaceAll("\\$", "");
  }
  
  public String getReceiverClassName() {
    return methodReceiver.getShortName().replaceAll("\\$", "");
  }
  
  public String getMethodName() {
    return method.getName();
  }
  
  public String getType() {
    return type;
  }
  
  public String toString() {
    return getMethodCallerClassName() + " calls " + getReceiverClassName() + "." + getMethodName();
  }
}
