package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.ContextSensitiveCallGraph;

public class SequenceAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  

  public SequenceAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    ContextSensitiveCallGraph g = this.bundle.scene.getContextSensitiveCallGraph();
    
    //Find the entry method
    String entryMethodSignature = config.entryMethodName;
    
    
    return this.bundle;
  }
  
  private void generateCalls(SootMethod entryMethod, ContextSensitiveCallGraph g) {
    
  }
  
  private void parseCalls() {
    
  }

}
