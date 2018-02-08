package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;

public class DIPAnalyzer extends Analyzer {

  protected DIPAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    List<Pattern> patterns = bundle.getList("patterns", Pattern.class);
    List<Relationship> relationships = bundle.getList("relationships", Relationship.class);
    relationships.forEach(r -> {
      if (r.getRelation() == Relation.ASSOCIATION || r.getRelation() == Relation.DEPENDENCY) {
        if (!(r.getToClass().isAbstract() || r.getToClass().isInterface() || r.getToClass().isFinal())) {
          patterns.add(createDIPPattern(r));
        }
      }
    });
    
    return bundle;
  }

  private Pattern createDIPPattern(Relationship r) {
    Pattern p = new Pattern("dip");
    p.put("relationship", r);
    p.put("violator", r.getFromClass());
    return p;
  }

}
