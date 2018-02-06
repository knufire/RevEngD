package odyssey.renderers;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public interface IClassRenderer extends Renderer<SootClass> {
  public String renderMethod(SootMethod method);
  public String renderField(SootField field);
}
