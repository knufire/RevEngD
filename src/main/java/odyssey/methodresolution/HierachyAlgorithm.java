package odyssey.methodresolution;

import java.util.List;

import soot.Hierarchy;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class HierachyAlgorithm implements Algorithm {

  @Override
  public List<SootMethod> resolve(Unit u, SootMethod m, Scene scene) {
    Hierarchy hierarchy = scene.getActiveHierarchy();
    List<SootMethod> possibleMethods = hierarchy.resolveAbstractDispatch(m.getDeclaringClass(), m);
    return possibleMethods;
  }

}
