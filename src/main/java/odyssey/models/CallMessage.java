package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public class CallMessage extends Message {

  private String parameters;

  public CallMessage(SootClass caller, SootMethod method, String parameters) {
    super(caller, method, caller, parameters);
    this.parameters = parameters;
  }

  public String getParameters() {
    return parameters;
  }

  public String getPlantUMLString() {
    if (getMethodName().equals("<init>")) {
      if (getMethodCallerClass().hasSuperclass() && getMethodCallerClass().getSuperclass().equals(getMethod().getDeclaringClass())) {
        return getMethodCallerClassName() + " -> " + getReceiverClassName() + " : " + "super()";
      }
      return getMethodCallerClassName() + " -> " + getReceiverClassName() + " : " + "new " + getReceiverClassName()
          + getParameters();
    }
    return getMethodCallerClassName() + " -> " + getReceiverClassName() + " : " + getMethodName() + getParameters();
  }

}
