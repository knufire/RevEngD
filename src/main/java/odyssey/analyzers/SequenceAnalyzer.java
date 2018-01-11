package odyssey.analyzers;

import java.util.Iterator;
import java.util.List;

import odyssey.app.Call;
import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class SequenceAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  

  public SequenceAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    System.out.println("Entry Method:\t" + config.entryMethodName);
    SootMethod entryMethod = this.bundle.scene.getMethod(config.entryMethodName);
    processMethod(entryMethod, 0);
    parseCalls();
    return this.bundle;
  }
  
  private void processMethod(SootMethod method, int depth) {
    if (depth >= config.maxCallDepth) return;
    if (method.hasActiveBody()) {
      UnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());
      for (Unit u : graph) {
        SootMethod targetMethod = resolveUsingCallGraph(u);
        if (targetMethod != null && passesFilters(targetMethod)) {
          Call newCall = new Call(method.getDeclaringClass(), targetMethod);
          bundle.calls.add(newCall);
          processMethod(targetMethod, depth+1);
        }
      }
    } else {//Method has no active body
      //TODO: Do something when we don't have the active body
    }
  }
  
  private SootMethod resolveUsingCallGraph(Unit stmt) {
    CallGraph callGraph = this.bundle.scene.getCallGraph();
    Iterator<Edge> itr = callGraph.edgesOutOf(stmt);
    while (itr.hasNext()) {
      MethodOrMethodContext methodOrCntxt = itr.next().getTgt();
      SootMethod targetMethod = methodOrCntxt.method();
      if(targetMethod != null) {
        return targetMethod;
      }
    }
    return null;
   
  }
  
  private void parseCalls() {
    /*for (Call c: bundle.calls) {
      System.err.println(c);
    }*/
    
    for (Call c: bundle.calls) {
      System.out.println(c.getPlantUMLString());
    }
  }
}
