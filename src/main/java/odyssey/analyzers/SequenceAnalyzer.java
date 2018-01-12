package odyssey.analyzers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import odyssey.app.Configuration;
import odyssey.filters.Filter;
import odyssey.models.CallMessage;
import odyssey.models.Message;
import odyssey.models.ReturnMessage;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class SequenceAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  private UMLParser parser;

  public SequenceAnalyzer(Configuration configuration, List<Filter> filters, UMLParser parser) {
    super(configuration, filters);
    this.parser = parser;
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    System.out.println("Processesing Entry Method:\t" + config.entryMethodName);
    SootMethod entryMethod = this.bundle.scene.getMethod(config.entryMethodName);
    processMethod(entryMethod, 0);
    String parseString = parseCalls();
    generateSeqImage(parseString);
    return this.bundle;
  }

  private void processMethod(SootMethod method, int depth) {
    if (depth >= config.maxCallDepth)
      return;
    if (method.hasActiveBody()) {
      UnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());
      for (Unit u : graph) {
        SootMethod targetMethod = resolveUsingCallGraph(u);

        if (targetMethod != null && passesFilters(targetMethod)) {
          if (config.showSuper || !isSuperCall(method, targetMethod)) {
            CallMessage newCall = new CallMessage(method.getDeclaringClass(), targetMethod,
                parser.parseMethodParameters(targetMethod));
            bundle.calls.add(newCall);
          }
          if (passesFilters(targetMethod.getDeclaringClass())) {
            processMethod(targetMethod, depth + 1);
          }
          if (!targetMethod.getReturnType().toString().contains("void")) {
            bundle.calls
                .add(new ReturnMessage(method.getDeclaringClass(), targetMethod, parser.parseReturnType(targetMethod)));
          }
        }
      }
    } else {
      System.err.println("Unalbe to load method body: " + method.getName());
    }
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
    for (Message c : bundle.calls) {
      builder.append(c.getPlantUMLString());
      builder.append("\n");
    }
    builder.append("@enduml\n");
    config.logger.log(Level.INFO, "Generated Sequence Diagram\n" + builder.toString());
    return builder.toString();
  }

  private void generateSeqImage(String umlString) {
    SourceStringReader reader = new SourceStringReader(umlString);
    try {
      Files.createDirectories(config.seqImageLocation.getParent());

      OutputStream outStream = new FileOutputStream(config.seqImageLocation.toFile());
      FileFormatOption option = new FileFormatOption(FileFormat.SVG, false);
      reader.outputImage(outStream, option);
    } catch (Exception e) {
      config.logger.error("Cannot create file to store the UML diagram.\n" + e);
    }

  }
}
