package odyssey.renderers;

import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class SingletonRenderer implements PatternRenderer {
  @Override
  public String getStyle() {
    return "skinparam class {\n borderColor<<Singleton>> blue\n}";
  }

  @Override
  public String render(SootClass c, Pattern pattern) {
    return "<<Singleton>>";
  }

  @Override
  public String render(SootMethod m, Pattern pattern) {
    return "";
  }

  @Override
  public String render(SootField f, Pattern pattern) {
    return "";
  }

  @Override
  public String render(Relationship r, Pattern pattern) {
    if (r.getRelation() == Relation.ASSOCIATION) {
      return "[#blue]";
    } else {
      return "";
    }
  }

}
