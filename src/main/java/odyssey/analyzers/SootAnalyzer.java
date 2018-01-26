package odyssey.analyzers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import odyssey.filters.Filter;
import soot.Scene;
import soot.SootClass;
import soot.util.Chain;

public class SootAnalyzer extends Analyzer {

  private Scene scene;

  public SootAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    scene = bundle.get("scene", Scene.class);
    Chain<SootClass> classes = scene.getClasses();
    List<SootClass> filteredClasses = new ArrayList<>();
    
    for (SootClass c : classes) {
      if (passesFilters(c)) {
        System.out.println("PASSED THE DAMN FILTERS::::  " + c);
        filteredClasses.add(c);
      }
    }
    System.out.println(Arrays.deepToString(filteredClasses.toArray()));
    
    bundle.put("classes", filteredClasses);
    return bundle;
  }

}
