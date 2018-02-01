package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;

public class InheritanceOverCompositionAnalyzer extends Analyzer {

  protected InheritanceOverCompositionAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    List<Pattern> patterns = bundle.getList("patterns", Pattern.class);
    List<Relationship> relationships = bundle.getList("relationships", Relationship.class);
    checkRelationships(patterns, relationships);
    return bundle;
  }
  
  private void checkRelationships(List<Pattern> patterns, List<Relationship> relationships) {
    Pattern p = new Pattern("IoverC");
    for (Relationship r: relationships) {
      if (r.getRelation().equals(Relation.EXTENDS)) {
        p.put("IoverC", r);
        p.put("IoverC", r.getFromClass());
        p.put("IoverC", r.getToClass());
      }
    }
    patterns.add(p);
  }

}
