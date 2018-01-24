package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class ClassNameFilter implements Filter {

  public ClassNameFilter() { 
  }

  @Override
  public boolean shouldProcess(SootClass clazz) {
    String[] classes = System.getProperty("-c").split(" ");
    for(int i = 0; i< classes.length; i++) {
      if(classes[i].equals(clazz.getName().replaceAll("\\[\\]", ""))) {
        return true;
      }
    }
    return false;
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
