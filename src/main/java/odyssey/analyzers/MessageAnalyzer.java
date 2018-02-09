package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.methodresolution.Algorithm;
import odyssey.models.Message;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class MessageAnalyzer extends Analyzer {
  
  private AnalyzerBundle bundle;
  private int maxCallDepth;
  private boolean showSuper;
  private Algorithm resolver;

  public MessageAnalyzer(List<Filter> filters, Algorithm resolver) {
    super(filters);
    maxCallDepth = Integer.parseInt(System.getProperty("-max-depth"));
    showSuper = Boolean.parseBoolean(System.getProperty("--include-super"));
    this.resolver = resolver;
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    String entryMethodName = System.getProperty("-e");
    System.out.println("Processesing Entry Method:\t" + entryMethodName);
    SootMethod entryMethod = this.bundle.get("scene", Scene.class).getMethod(entryMethodName);
    processMethod(entryMethod, 0);
    return this.bundle;
  }
  
  private void processMethod(SootMethod method, int depth) {
    if (depth >= maxCallDepth)
      return;
    if (method.hasActiveBody()) {
      UnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());
      for (Unit u : graph) {
        
        SootMethod unresolvedMethod = null;
        if (u instanceof InvokeStmt) {
          unresolvedMethod = ((InvokeStmt)u).getInvokeExpr().getMethod(); 
        } else if (u instanceof AssignStmt) {
          Value rightOp = ((AssignStmt) u).getRightOp();
          if (rightOp instanceof InvokeExpr) {
            unresolvedMethod = ((InvokeExpr)rightOp).getMethod(); 
          }
        }
        
        List<SootMethod> possibleTargets = resolver.resolve(u, unresolvedMethod, bundle.get("scene", Scene.class));
        
        if (possibleTargets.isEmpty()) continue;
        SootMethod targetMethod = possibleTargets.get(0);
        if (passesFilters(targetMethod)) {
          if (showSuper || !isSuperCall(method, targetMethod)) {
            createCallMessage(method, targetMethod);
            processCommentedOutMessages(possibleTargets, method);
          }
          
          processMethod(targetMethod, depth + 1);
          
          if (!targetMethod.getReturnType().toString().contains("void")) {
            bundle.getList("messages", Message.class)
                  .add(new Message(targetMethod.getDeclaringClass(), targetMethod, method.getDeclaringClass(), "return"));
          }
        }
      }
    }
  }
  
  private void processCommentedOutMessages(List<SootMethod> methods, SootMethod methodCall) {
    for (int i = 1; i < methods.size(); i++) {
      createCommentedOutMessage(methodCall, methods.get(i));
    }
  }
  
  private void createCommentedOutMessage(SootMethod callingMethod, SootMethod targetMethod) {
    Message newMessage = new Message(callingMethod.getDeclaringClass(), targetMethod, targetMethod.getDeclaringClass(), "comment");
    bundle.getList("messages", Message.class).add(newMessage);
  }
  
  private void createCallMessage(SootMethod callingMethod, SootMethod targetMethod) {
    Message newCall;
    if (targetMethod.toString().contains("<init>")) {
      newCall = new Message(callingMethod.getDeclaringClass(), targetMethod, targetMethod.getDeclaringClass(), "init");
    } else {
      newCall = new Message(callingMethod.getDeclaringClass(), targetMethod, targetMethod.getDeclaringClass(), "default");
    }
    bundle.getList("messages", Message.class).add(newCall);
  }

  private boolean isSuperCall(SootMethod method, SootMethod targetMethod) {
    if (targetMethod.toString().contains("<init>")) {
      if (method.getDeclaringClass().getSuperclass().equals(targetMethod.getDeclaringClass())) {
        return true;
      }
    }
    return false;
  }

}
