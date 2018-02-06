package odyssey.renderers;

import odyssey.models.Message;
import soot.SootClass;

public interface MessageRenderer extends Renderer<Message> {
  public String renderFromClass(SootClass clazz);
}
