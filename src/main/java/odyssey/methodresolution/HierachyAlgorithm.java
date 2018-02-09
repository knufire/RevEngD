package odyssey.methodresolution;

import java.util.Collections;
import java.util.List;

import soot.Hierarchy;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class HierachyAlgorithm implements Algorithm {

  @Override
  public List<SootMethod> resolve(Unit u, SootMethod m, Scene scene) {
    if (m == null)
      return Collections.emptyList();
    Hierarchy hierarchy = scene.getActiveHierarchy();
    try {
      List<SootMethod> possibleMethods = hierarchy.resolveAbstractDispatch(m.getDeclaringClass(), m);
      return possibleMethods;
    } catch (RuntimeException e) {
      System.err.println("HiearchyAlgorithm: Unable to resolve concrete dispatch : " + m.getName());
    }
    return Collections.emptyList();
  }

}
