package odyssey.methodresolution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class UnionMethodResolver implements AggregationStrategy {

  @Override
  public Set<SootMethod> resolve(List<Algorithm> algorithms, Unit u, SootMethod m, Scene scene) {
    Set<SootMethod> result = new HashSet<>();
    algorithms.forEach(a -> {
      result.addAll(a.resolve(u, m, scene));
    });
    return result; 
  }


}
