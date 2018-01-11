package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class JDKFilter implements Filter {

  @Override
  public boolean shouldProcess(SootClass clazz) {
    return !clazz.getJavaPackageName().startsWith("java.");
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
