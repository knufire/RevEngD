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
    String parseString = parseCalls();
    generateUMLImage(parseString);
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
          if (passesFilters(targetMethod.getDeclaringClass())) {
            processMethod(targetMethod, depth+1);
          }
        }
      }
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
  
  private String parseCalls() {
    /*for (Call c: bundle.calls) {
      System.err.println(c);
    }*/
    StringBuilder builder = new StringBuilder();
    builder.append("@startuml\n");
    for (Call c: bundle.calls) {
      builder.append(c.getPlantUMLString());
      builder.append("\n");
    }
    builder.append("@enduml\n");
    config.logger.log(Level.INFO, "Generated Sequence Diagram\n" + builder.toString());
    return builder.toString();
  }
  
  private void generateUMLImage(String umlString) {
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
