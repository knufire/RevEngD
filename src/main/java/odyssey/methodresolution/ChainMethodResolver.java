package odyssey.methodresolution;

import java.util.List;

import soot.SootMethod;

public class ChainMethodResolver implements AggregationStrategy {

  boolean runNext = true;
  @Override
  public List<SootMethod> aggregateResults(List<SootMethod> previousResult, List<SootMethod> newResult) {
    runNext = newResult.isEmpty();
    return newResult;
  }

  @Override
  public boolean runNext() {
    return runNext;
  }

}
