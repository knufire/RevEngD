package odyssey.analyzers;

import java.util.Arrays;
import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

public class RelationshipAnalyzer extends Analyzer {

  public RelationshipAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    // TODO: Get the classes, look through the classes, build relationships
    System.out.println("Bundle! " + Arrays.deepToString(bundle.classes.toArray()));
    for (SootClass c : bundle.classes) {
      System.out.println(c.getName());
      Chain<SootField> fields = c.getFields();
      for (SootField f : fields) {
        // TODO: Figure out type the field is.
        System.out.println(f.getName());
      }
    }

    return bundle;
  }

}
