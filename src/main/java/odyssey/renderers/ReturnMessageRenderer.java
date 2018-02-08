package odyssey.renderers;

import soot.SootMethod;

public class ReturnMessageRenderer extends MessageRenderer {

  @Override
  public String renderMessage(SootMethod method) {
    if (message.getMethodCallerClass().equals(message.getReceiverClass())) {
      return super.renderMessage(method);
    }
    return parseReturnType(method) + System.lineSeparator() + "deactivate " + method.getDeclaringClass().getShortName();
  }

  @Override
  public String renderArrow() {
    return "-->";
  }
}
