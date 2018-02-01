package odyssey.methodresolution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class ChainMethodResolver implements AggregationStrategy {

  @Override
  public Set<SootMethod> resolve(List<Algorithm> algorithms, Unit u, SootMethod m, Scene scene) {
    Set<SootMethod> result = new HashSet<>();
    int i = 0;
    while (result.isEmpty() && i < algorithms.size()) {
      result.addAll(algorithms.get(i).resolve(u, m, scene));
      i++;
    }
    return result;
  }
  

}
