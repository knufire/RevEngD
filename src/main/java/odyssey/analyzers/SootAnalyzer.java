package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.Scene;
import soot.SootClass;
import soot.util.Chain;

public class SootAnalyzer extends Analyzer{
  
  private Scene scene;

  public SootAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    scene = bundle.scene;
    
    Chain<SootClass> chain = scene.getApplicationClasses();
    List<SootClass> classList = new ArrayList<SootClass>();
    chain.forEach(c -> 
    { 
      /////TOOOO be completed later
    });
    
    bundle.classes = classList;
    
    return bundle;
  }

}
