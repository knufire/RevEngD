package odyssey.analyzers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import odyssey.app.Call;
import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.MethodOrMethodContext;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ContextSensitiveCallGraph;
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
    SootMethod entryMethod = this.bundle.scene.getMethod(config.entryMethodName);
    if (entryMethod.hasActiveBody()) {
      UnitGraph graph = new ExceptionalUnitGraph(entryMethod.getActiveBody());
      graph.forEach(u -> {
        if (u instanceof InvokeStmt) {
          resolveUsingCallGraph(u, ((InvokeStmt) u).getInvokeExpr().getMethod());
        } else if (u instanceof AssignStmt) {
          Value rightOp = ((AssignStmt) u).getRightOp();
          if (rightOp instanceof InvokeExpr) {
            resolveUsingCallGraph(u, ((InvokeExpr) rightOp).getMethod());
          }
        }
      });
    } else {
      
    }
    
    return this.bundle;
  }
  
  private SootMethod resolveUsingCallGraph(Unit stmt, SootMethod method) {
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

  

  

  
  private void processInvokeExpr(SootClass callingClass, InvokeExpr expr) {
    SootMethod method = expr.getMethod();
    SootClass recievingClass = method.getDeclaringClass();
    if (passesFilters(recievingClass) && passesFilters(method)) {
      this.bundle.calls.add(new Call(callingClass, method));
    }
  }
 

}
