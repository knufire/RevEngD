package odyssey.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Body;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

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
      if (!delagatesCall(m)) {
//        System.out.println("Violation: " + subClass + "\t Method: " + m);
        return true;
      }
      
    }
//    System.out.println("No violation: " + subClass);
    return false;
  }

  private boolean delagatesCall(SootMethod m) {
    if (m.getSubSignature().contains("<init>")) return true;
    if (!m.hasActiveBody()) return true;
    Body body = m.getActiveBody();
    UnitGraph graph = new ExceptionalUnitGraph(body);
    for (Unit u : graph) {
      if (u instanceof InvokeStmt) {
        InvokeStmt stmt = (InvokeStmt) u;
        InvokeExpr expr = stmt.getInvokeExpr();
        SootClass target = expr.getMethod().getDeclaringClass();
        Chain<SootField> fields = m.getDeclaringClass().getFields();
        for (SootField f : fields) {
          if (f.getType().equals(target.getType())) {
//            System.out.println("Types equal: " + f.getType());
            return true;
          }
        }
      }
    }
    return false;
  }

  private List<SootMethod> getOverriddenMethods(SootClass superClass, SootClass subClass) {
    List<SootMethod> result = new ArrayList<>();
//    System.out.print("Subclass Methods: ");
//    subClass.getMethods().forEach(m -> System.out.print(m.getSubSignature() + " "));
//    System.out.println();
//    System.out.print("Superclass Methods: ");
//    superClass.getMethods().forEach(m -> System.out.print(m.getSubSignature() + " "));
//    System.out.println();
    Map<String, SootMethod> methods = new HashMap<>();
    subClass.getMethods().forEach(m -> methods.put(m.getSubSignature(), m));
    for (SootMethod m : superClass.getMethods()) {
      if (methods.containsKey(m.getSubSignature())) 
        result.add(methods.get(m.getSubSignature()));
    }
    return result;
  }

  private void addPattern(Relationship r, Pattern p) {
    p.put("IoverC", r);
    p.put("from", r.getFromClass());
    p.put("to", r.getToClass());
  }

}
