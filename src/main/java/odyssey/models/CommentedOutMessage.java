package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public class CommentedOutMessage extends Message {
  
  private String parameters;

  public CommentedOutMessage(SootClass methodCallingClass, SootMethod method, String parameters) {
    super(methodCallingClass, method);
    this.parameters = parameters;
  }

  public String getParameters() {
    return parameters;
  }
  
  @Override
  public String getPlantUMLString() {
    if (getMethodName().equals("<init>")) {
      if (getMethodCallingClass().hasSuperclass() && getMethodCallingClass().getSuperclass().equals(getMethod().getDeclaringClass())) {
        return "'" + getMethodCallingClassName() + " -> " + getReceivingClassName() + " : " + "super()";
      }
      return "'" + getMethodCallingClassName() + " -> " + getReceivingClassName() + " : " + "new " + getReceivingClassName()
          + getParameters();
    }
    return "'" + getMethodCallingClassName() + " -> " + getReceivingClassName() + " : " + getMethodName() + getParameters();
  }

}
