package odyssey.methodresolution;

import java.util.ArrayList;
import java.util.List;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public class AggregateAlgorithm implements Algorithm {
  
  private List<Algorithm> algorithms; 
  private AggregationStrategy strat; 
  
  
  
  public AggregateAlgorithm(AggregationStrategy strat) {
    super();
    this.algorithms = new ArrayList<>();
    this.strat = strat;
  }



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
  
  public void addAlgorithm(Algorithm a) {
    algorithms.add(a);
  }
  
  

}
