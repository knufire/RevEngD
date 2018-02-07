package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public class CommentedOutMessage extends Message {
  
  private String parameters;

  public CommentedOutMessage(SootClass methodCallingClass, SootMethod method, String parameters) {
    super(methodCallingClass, method, methodCallingClass, parameters);
    this.parameters = parameters;
  }

  public String getParameters() {
    return parameters;
  }
  
  public String getPlantUMLString() {
    if (getMethodName().equals("<init>")) {
      if (getMethodCallerClass().hasSuperclass() && getMethodCallerClass().getSuperclass().equals(getMethod().getDeclaringClass())) {
        return "'" + getMethodCallerClassName() + " -> " + getReceiverClassName() + " : " + "super()";
      }
      return "'" + getMethodCallerClassName() + " -> " + getReceiverClassName() + " : " + "new " + getReceiverClassName()
          + getParameters();
    }
    return "'" + getMethodCallerClassName() + " -> " + getReceiverClassName() + " : " + getMethodName() + getParameters();
  }

}
