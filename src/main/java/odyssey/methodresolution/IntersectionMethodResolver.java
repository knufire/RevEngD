package odyssey.methodresolution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class IntersectionMethodResolver implements AggregationStrategy {

  @Override
  public Set<SootMethod> resolve(List<Algorithm> algorithms, Unit u, SootMethod m, Scene scene) {
    Set<SootMethod> result = new HashSet<>();
    algorithms.forEach(a -> {
      List<SootMethod> algorithmResult = a.resolve(u, m, scene);
      if (result.isEmpty()) {
        result.addAll(algorithmResult);
      } else {
        result.retainAll(algorithmResult);
      }
    });
    return result;
  }


}

