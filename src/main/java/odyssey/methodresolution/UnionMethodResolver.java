package odyssey.methodresolution;

import java.util.ArrayList;
import java.util.List;

import soot.SootMethod;

public class UnionMethodResolver implements AggregationStrategy {

  @Override
  public List<SootMethod> aggregateResults(List<SootMethod> previousResult, List<SootMethod> newResult) {
    List<SootMethod> result = new ArrayList<>();
    result.addAll(previousResult);
    result.addAll(newResult);
    return result;
  }

  @Override
  public boolean runNext() {
    return true;
  }

}
