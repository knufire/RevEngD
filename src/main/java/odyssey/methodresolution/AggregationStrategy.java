package odyssey.methodresolution;

import java.util.List;

import soot.SootMethod;

public interface AggregationStrategy {
  
  public List<SootMethod> aggregateResults(List<SootMethod> previousResult, List<SootMethod> newResult);
  public boolean runNext();

}
