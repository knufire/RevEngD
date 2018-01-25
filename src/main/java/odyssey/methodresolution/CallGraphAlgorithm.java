package odyssey.methodresolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class CallGraphAlgorithm implements Algorithm {

  @Override
  public List<SootMethod> resolve(Unit u, SootMethod m, Scene scene) {
    CallGraph callGraph = scene.getCallGraph();
    Iterator<Edge> itr = callGraph.edgesOutOf(u);
    List<SootMethod> targetMethods = new ArrayList<>();
    while (itr.hasNext()) {
      MethodOrMethodContext methodOrCntxt = itr.next().getTgt();
      SootMethod targetMethod = methodOrCntxt.method();
      if (targetMethod != null) {
        targetMethods.add(targetMethod);
      }
    }
    return targetMethods;
  }

}
