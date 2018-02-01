package odyssey.methodresolution;

import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public interface AggregationStrategy {
  
  public Set<SootMethod> resolve(List<Algorithm> algorithms, Unit u, SootMethod m, Scene scene);;

}
