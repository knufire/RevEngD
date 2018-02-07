package odyssey.renderers;

import odyssey.models.Message;
import soot.SootClass;
import soot.SootMethod;

public interface IMessageRenderer extends Renderer<Message> {
  public String renderFromClass(SootClass clazz);
  public String renderToClass(SootClass clazz);
  public String renderArrow();
  public String renderMessage(SootMethod method);
}
