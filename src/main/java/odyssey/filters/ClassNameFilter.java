package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class ClassNameFilter implements Filter {

  public ClassNameFilter() {
  }

  @Override
  public boolean shouldProcess(SootClass clazz) {
    String[] blackListedPackages = System.getProperty("-bl").split(" ");
    String[] whiteListedClasses = System.getProperty("-c").split(" ");
    for (int i = 0; i < whiteListedClasses.length; i++) {
      if (clazz.toString().contains(whiteListedClasses[i])) {
        return true;
      }
    }
    for (int i = 0; i < blackListedPackages.length; i++) {
      if (clazz.toString().contains(blackListedPackages[i].trim())) {
        return false;
      }
    }
    return true;
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
