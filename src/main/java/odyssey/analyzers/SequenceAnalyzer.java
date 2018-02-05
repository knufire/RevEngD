package odyssey.analyzers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import odyssey.filters.Filter;
import odyssey.methodresolution.Algorithm;
import odyssey.models.CallMessage;
import odyssey.models.CommentedOutMessage;
import odyssey.models.Message;
import odyssey.models.ReturnMessage;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class SequenceAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  int maxCallDepth;
  boolean showSuper;
  Path seqImageLocation;
  Algorithm resolver;
  public SequenceAnalyzer(List<Filter> filters, Algorithm resolver) {
    super(filters);
    maxCallDepth = Integer.parseInt(System.getProperty("-max-depth"));
    showSuper = Boolean.parseBoolean(System.getProperty("--include-super"));
    seqImageLocation = Paths.get(System.getProperty("-s"));
    this.resolver = resolver;
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    String entryMethodName = System.getProperty("-e");
    System.out.println("Processesing Entry Method:\t" + entryMethodName);
    SootMethod entryMethod = this.bundle.get("scene", Scene.class).getMethod(entryMethodName);
    processMethod(entryMethod, 0);
    String parseString = parseCalls();
    generateSeqImage(parseString);
    bundle.put("seqString", parseString);
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
                .add(new ReturnMessage(unresolvedMethod.getDeclaringClass(), targetMethod, UMLParser.parseReturnType(targetMethod)));
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
    CommentedOutMessage newMessage = new CommentedOutMessage(callingMethod.getDeclaringClass(), targetMethod,
        UMLParser.parseMethodParameters(targetMethod));
    bundle.getList("messages", Message.class).add(newMessage);
  }
  
  private void createCallMessage(SootMethod callingMethod, SootMethod targetMethod) {
    CallMessage newCall = new CallMessage(callingMethod.getDeclaringClass(), targetMethod,
        UMLParser.parseMethodParameters(targetMethod));
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

  private String parseCalls() {
    StringBuilder builder = new StringBuilder();
    builder.append("@startuml\n");
    for (Message c : bundle.getList("messages", Message.class)) {
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
