package odyssey.filters;

import odyssey.analyzers.AnalyzerBundle;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class RelationshipFilter implements Filter {

  AnalyzerBundle bundle;

  public RelationshipFilter(AnalyzerBundle bundle) {
    this.bundle = bundle;
  }

  @Override
  public boolean shouldProcess(SootClass clazz) {
    if (clazz.getName().contains("[")) {
      return bundle.classes.contains(bundle.scene.getSootClass(clazz.getName().replaceAll("\\[\\]", "")));
    }
    return bundle.classes.contains(clazz);
  }

  @Override
  public boolean shouldProcess(SootMethod method) {
    return true;
  }

  @Override
  public boolean shouldProcess(SootField field) {
    return true;
  }

}
