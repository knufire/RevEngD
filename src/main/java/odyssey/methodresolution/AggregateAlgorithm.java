package odyssey.methodresolution;

import java.util.ArrayList;
import java.util.List;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class AggregateAlgorithm implements Algorithm {
  
  List<Algorithm> algorithms; 
  AggregationStrategy strat; 

  @Override
  public List<SootMethod> resolve(Unit u, SootMethod m, Scene scene) {
    List<SootMethod> finalResult = new ArrayList<>();
    for (Algorithm a : algorithms) {
      List<SootMethod> newResult = a.resolve(u, m, scene);
      finalResult = strat.aggregateResults(finalResult, newResult);
      if (!strat.runNext()) {
        break;
      }
    }
    return finalResult; 
  }

}
