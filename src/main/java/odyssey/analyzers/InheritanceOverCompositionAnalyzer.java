package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

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
    for (Relationship r : relationships) {
      if (r.getRelation().equals(Relation.EXTENDS) && isViolation(r)) {
        addPattern(r, p);
      }
    }
    patterns.add(p);
  }

  private boolean isViolation(Relationship r) {
    SootClass superClass = r.getToClass();
    SootClass subClass = r.getFromClass();
    List<SootMethod> overriddenMethods = getOverriddenMethods(superClass, subClass);
    for (SootMethod m : overriddenMethods) {
      if (!delagatesCall(m)) return true;
    }
    return false;
  }

  private boolean delagatesCall(SootMethod m) {
    if (!m.hasActiveBody()) return true;
    Body body = m.getActiveBody();
    UnitGraph graph = new ExceptionalUnitGraph(body);
    for (Unit u : graph) {
      if (u instanceof InvokeStmt) {
        InvokeStmt stmt = (InvokeStmt) u;
        if (stmt.containsFieldRef()) return true;
      }
    }
    return false;
  }

  private List<SootMethod> getOverriddenMethods(SootClass superClass, SootClass subClass) {
    List<SootMethod> result = new ArrayList<>();
    result.addAll(subClass.getMethods());
    result.retainAll(superClass.getMethods());
    return result;
  }

  private void addPattern(Relationship r, Pattern p) {
    p.put("IoverC", r);
    p.put("IoverC", r.getFromClass());
    p.put("IoverC", r.getToClass());
  }

}
