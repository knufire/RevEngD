package odyssey.renderers;

import odyssey.models.Pattern;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class InheritanceOverCompositionRenderer implements PatternRenderer {

  @Override
  public String getStyle() {
    return "";
  }

  @Override
  public String render(SootClass c, Pattern pattern) {
    return "#orange";
  }

  @Override
  public String render(SootMethod m, Pattern pattern) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String render(SootField f, Pattern pattern) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String render(Relationship r, Pattern pattern) {
    return "[#orange]";
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

}
