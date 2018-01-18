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
        return getMethodCallingClassName() + " -> " + getRecievingClass() + " : " + "super()";
      }
      return getMethodCallingClassName() + " -> " + getRecievingClass() + " : " + "new " + getRecievingClass()
          + getParameters();
    }
    return getMethodCallingClassName() + " -> " + getRecievingClass() + " : " + getMethodName() + getParameters();
  }

}
