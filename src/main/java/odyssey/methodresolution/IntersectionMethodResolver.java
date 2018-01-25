package odyssey.methodresolution;

import java.util.ArrayList;
import java.util.List;

import soot.SootMethod;

public class IntersectionMethodResolver implements AggregationStrategy {

  
  @Override
  public List<SootMethod> aggregateResults(List<SootMethod> previousResult, List<SootMethod> newResult) {
    List<SootMethod> result = new ArrayList<>();
    newResult.forEach(n ->{
      previousResult.forEach(m -> {
        if (n.equals(m)) result.add(n);
      });
    });
    return result;
  }

  @Override
  public boolean runNext() {
    return true;
  }

}

