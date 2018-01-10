package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;

import odyssey.app.Configuration;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.SootClass;

public class AssociationAnalyzer extends Analyzer {

  public AssociationAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
    
    List<Relationship> relationships = bundle.relationships;
    
    for (SootClass c : bundle.classes) {
      if (passesFilters(c)) {
        // System.out.println(c.getName());
        generateAssociationRelationships(c, relationships);
      }
    }
    
    bundle.relationships = relationships;
    return bundle;
	}

  private void generateAssociationRelationships(SootClass c, List<Relationship> relationships) {
    //TODO: Actually generate relationships.
  }

}
