package odyssey.renderers;

import soot.SootClass;
import soot.SootMethod;

public class CommentMessageRenderer extends MessageRenderer {

  @Override
  public String renderFromClass(SootClass clazz) {
    return "'" + super.renderFromClass(clazz);
  }

  @Override
  public String renderMessage(SootMethod method) {
    return method.getName() + parseMethodParameters(method);
  }
  
}
