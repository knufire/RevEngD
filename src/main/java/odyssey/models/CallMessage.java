package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public class CallMessage extends Message {

  private String parameters;

  public CallMessage(SootClass caller, SootMethod method, String parameters) {
    super(caller, method);
    this.parameters = parameters;
  }

  public String getParameters() {
    return parameters;
  }

  public String getPlantUMLString() {
    if (getMethodName().equals("<init>")) {
      if (getMethodCallingClass().getSuperclass().equals(getMethod().getDeclaringClass())) {
        return getMethodCallingClassName() + " -> " + getReceivingClassName() + " : " + "super()";
      }
      return getMethodCallingClassName() + " -> " + getReceivingClassName() + " : " + "new " + getReceivingClassName()
          + getParameters();
    }
    return getMethodCallingClassName() + " -> " + getReceivingClassName() + " : " + getMethodName() + getParameters();
  }

}
