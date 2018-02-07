package odyssey.renderers;

import soot.SootClass;

public class CommentMessageRenderer extends MessageRenderer {

  @Override
  public String renderFromClass(SootClass clazz) {
    return "'" + super.renderFromClass(clazz);
  }
  
}
