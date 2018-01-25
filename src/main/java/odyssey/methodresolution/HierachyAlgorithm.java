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
    if (m == null) return Collections.emptyList();
    Hierarchy hierarchy = scene.getActiveHierarchy();
    System.out.println(m);
    List<SootMethod> possibleMethods = hierarchy.resolveAbstractDispatch(m.getDeclaringClass(), m);
    return possibleMethods;
  }

}
