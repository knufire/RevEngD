package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Scene;
import soot.SootClass;
import soot.Type;

public class DIPAnalyzer extends Analyzer {

  protected DIPAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    List<Pattern> patterns = bundle.getList("patterns", Pattern.class);
    List<Relationship> relationships = bundle.getList("relationships", Relationship.class);
    Pattern p = new Pattern("dip");
    relationships.forEach(r -> {
      SootClass toClass = r.getToClass();
      if (r.getRelation() == Relation.ASSOCIATION || r.getRelation() == Relation.DEPENDENCY) {
        if (!(toClass.isAbstract() || toClass.isInterface() || toClass.isFinal())) {
          createDIPPattern(r, p);
        }
      }
    });
    patterns.add(p);
    return bundle;
  }

  private void createDIPPattern(Relationship r, Pattern p) {   
    p.put("relationship", r);
    p.put("from", r.getFromClass());
    p.put("to", r.getToClass());
  }

}
