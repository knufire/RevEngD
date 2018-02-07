package odyssey.renderers;

import soot.SootMethod;

public class ReturnMessageRenderer extends MessageRenderer {

  @Override
  public String renderMessage(SootMethod method) {
    return parseReturnType(method);
  }
}
