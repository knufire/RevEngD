package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class NullSuperFilter implements Filter{

  @Override
  public boolean shouldProcess(SootClass clazz) {

    return clazz.getName().equals("java.lang.Object") || clazz.getSuperclass() != null;
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
