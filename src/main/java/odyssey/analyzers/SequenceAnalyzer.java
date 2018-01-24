package odyssey.analyzers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import odyssey.filters.Filter;
import odyssey.models.CallMessage;
import odyssey.models.Message;
import odyssey.models.ReturnMessage;
import soot.Hierarchy;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class SequenceAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  int maxCallDepth;
  boolean showSuper;
  Path seqImageLocation;
  public SequenceAnalyzer(List<Filter> filters) {
    super(filters);
    maxCallDepth = Integer.parseInt(System.getProperty("-max-depth"));
    showSuper = Boolean.parseBoolean(System.getProperty("--include-super"));
    seqImageLocation = Paths.get(System.getProperty("-s"));
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    String entryMethodName = System.getProperty("-e");
    System.out.println("Processesing Entry Method:\t" + entryMethodName);
    SootMethod entryMethod = this.bundle.scene.getMethod(entryMethodName);
    processMethod(entryMethod, 0);
    String parseString = parseCalls();
    generateSeqImage(parseString);
    return this.bundle;
  }

  private void processMethod(SootMethod method, int depth) {
    if (depth >= maxCallDepth)
      return;
    if (method.hasActiveBody()) {
      UnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());
      for (Unit u : graph) {
        SootMethod targetMethod = resolveUsingCallGraph(u);
        
        if (targetMethod == null) {
          targetMethod = resolveUsingHierarchy(u);
        }
        
        if (targetMethod != null && passesFilters(targetMethod)) {
          if (showSuper || !isSuperCall(method, targetMethod)) {
            CallMessage newCall = new CallMessage(method.getDeclaringClass(), targetMethod,
                UMLParser.parseMethodParameters(targetMethod));
            bundle.messages.add(newCall);
          }
          
          processMethod(targetMethod, depth + 1);
          
          if (!targetMethod.getReturnType().toString().contains("void")) {
            bundle.messages
                .add(new ReturnMessage(method.getDeclaringClass(), targetMethod, UMLParser.parseReturnType(targetMethod)));
          }
        } else {
          //System.err.println("\nNot useful Statement: " + u + "\n");
        }
      }
    } else {
      System.err.println("Unalbe to load method body: " + method.getName());
    }
  }

  private SootMethod resolveUsingHierarchy(Unit u) {
    SootMethod method = null;
    if (u instanceof InvokeStmt) {
      method = ((InvokeStmt)u).getInvokeExpr().getMethod(); 
    } else if (u instanceof AssignStmt) {
      Value rightOp = ((AssignStmt) u).getRightOp();
      if (rightOp instanceof InvokeExpr) {
        method = ((InvokeExpr)rightOp).getMethod(); 
      }
    }
    if (method == null) return null;
    Hierarchy hierarchy = bundle.scene.getActiveHierarchy();
    List<SootMethod> possibleMethods = hierarchy.resolveAbstractDispatch(method.getDeclaringClass(), method);
    return possibleMethods.isEmpty() ? null : possibleMethods.get(0);
  }

  private boolean isSuperCall(SootMethod method, SootMethod targetMethod) {
    if (targetMethod.toString().contains("<init>")) {
      if (method.getDeclaringClass().getSuperclass().equals(targetMethod.getDeclaringClass())) {
        return true;
      }
    }
    return false;
  }

  private SootMethod resolveUsingCallGraph(Unit stmt) {
    CallGraph callGraph = this.bundle.scene.getCallGraph();
    Iterator<Edge> itr = callGraph.edgesOutOf(stmt);
    while (itr.hasNext()) {
      MethodOrMethodContext methodOrCntxt = itr.next().getTgt();
      SootMethod targetMethod = methodOrCntxt.method();
      if (targetMethod != null) {
        return targetMethod;
      }
    }
    return null;

  }

  private String parseCalls() {
    StringBuilder builder = new StringBuilder();
    builder.append("@startuml\n");
    for (Message c : bundle.messages) {
      builder.append(c.getPlantUMLString());
      builder.append("\n");
    }
    builder.append("@enduml\n");
    System.out.println("Generated Sequence Diagram\n" + builder.toString());
    return builder.toString();
  }

  private void generateSeqImage(String umlString) {
    SourceStringReader reader = new SourceStringReader(umlString);
    try {
      Files.createDirectories(seqImageLocation.getParent());

      OutputStream outStream = new FileOutputStream(seqImageLocation.toFile());
      FileFormatOption option = new FileFormatOption(FileFormat.SVG, false);
      reader.outputImage(outStream, option);
    } catch (Exception e) {
      System.err.println("Cannot create file to store the UML diagram.\n" + e);
    }

  }
}
