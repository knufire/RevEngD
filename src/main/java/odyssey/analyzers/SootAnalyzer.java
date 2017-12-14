package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.Scene;
import soot.SootClass;
import soot.util.Chain;

public class SootAnalyzer extends Analyzer {

  private Scene scene;

  public SootAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    scene = bundle.scene;
    Chain<SootClass> classes = scene.getApplicationClasses();
    List<SootClass> filteredClasses = new ArrayList<>();
    for (SootClass c : classes) {
      //System.out.println(c.getName());
      if (passesFilters(c)) {
        filteredClasses.add(c);
      }
    }
    bundle.classes = filteredClasses;
    return bundle;
  }

}
