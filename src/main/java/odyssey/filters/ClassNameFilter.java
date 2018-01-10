package odyssey.filters;

import odyssey.app.Configuration;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class ClassNameFilter implements Filter {

  Configuration config;

  public ClassNameFilter(Configuration config) {
    this.config = config;
  }

  @Override
  public boolean shouldProcess(SootClass clazz) {
    return config.classNames.contains(clazz.getName().replaceAll("\\[\\]", ""));
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
