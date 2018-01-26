package odyssey.renderers;

import odyssey.models.Pattern;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public interface PatternRenderer {
  String getStyle();
  
  String render(SootClass c, Pattern pattern);

  String render(SootMethod m, Pattern pattern);

  String render(SootField f, Pattern pattern);

  String render(Relationship r, Pattern pattern);

  String getName();
}
