package odyssey.models;

import soot.SootClass;
import soot.SootMethod;

public class ReturnMessage extends Message {

  private String returnType;

  public ReturnMessage(SootClass reciever, SootMethod method, String returnType) {
    super(reciever, method);
    this.returnType = returnType;
  }

  public String getReturnType() {
    return returnType;
  }

  @Override
  public String getPlantUMLString() {
    return getReceivingClassName() + " --> " + getMethodCallingClassName() + " : " + getReturnType();
  }

}
