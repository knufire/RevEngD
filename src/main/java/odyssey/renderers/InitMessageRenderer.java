package odyssey.renderers;

import soot.SootMethod;

public class InitMessageRenderer extends MessageRenderer {

  @Override
  public String renderMessage(SootMethod method) {
    if (message.getMethodCallerClass().hasSuperclass() && message.getMethodCallerClass().getSuperclass().equals(message.getMethod().getDeclaringClass())) {
      return "super()";
    }
    return "new " + message.getReceiverClassName();
  }

}
