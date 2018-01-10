package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootMethod;

public class DependencyAnalyzer extends Analyzer {

  public DependencyAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {

    List<Relationship> relationships = bundle.relationships;

    for (SootClass c : bundle.classes) {
      if (passesFilters(c)) {
        // System.out.println(c.getName());
        generateDependencyRelationships(c, relationships);
      }
    }

    bundle.relationships = relationships;
    return bundle;
  }

  private void generateDependencyRelationships(SootClass clazz, List<Relationship> relationships) {
//    List<SootMethod> methods = clazz.getMethods();
//    methods.
  }

}
