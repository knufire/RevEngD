package odyssey.renderers;

import java.util.List;

import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public interface IClassRenderer extends Renderer<SootClass> {
  public String renderMethod(SootMethod method);
  public void setFilters(List<Filter> filters);
  public String renderField(SootField field);
  public String renderStyle();
}
