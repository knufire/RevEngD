package odyssey.analyzers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import odyssey.app.Configuration;
import odyssey.app.Relation;
import odyssey.app.Relationship;
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
    
    List<Relationship> relationships = new ArrayList<Relationship>();
    
    for (SootClass c : bundle.classes) {
      System.out.println(c.getName());
      
      SootClass superClass = c.getSuperclass();
      if (this.passesFilters(superClass)) {
    	  Relationship rel = new Relationship(c, Relation.EXTENDS, superClass, 0);
    	  relationships.add(rel);
      }
    }
    
    bundle.relationships = relationships;
    System.out.println(Arrays.deepToString(bundle.relationships.toArray()));
    return bundle;
  }

}
