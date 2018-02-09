package odyssey.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

public class AdapterAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  
  protected AdapterAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    List<Pattern> patterns = this.bundle.getList("patterns", Pattern.class);
    List<SootClass> clazzes = this.bundle.getList("classes", SootClass.class);
    List<Relationship> relationships = this.bundle.getList("relationships", Relationship.class);
    
    List<Relation> adaptsRelations = new ArrayList<>();
    List<Relation> targetRelations = new ArrayList<>();
    adaptsRelations.add(Relation.ASSOCIATION);
    targetRelations.add(Relation.EXTENDS);
    targetRelations.add(Relation.IMPLEMENTS);
    
    Double threshold = Double.parseDouble(System.getProperty("-adapterThreshold"));
    
    for (SootClass c: clazzes) {
      if (!c.isConcrete()) 
        continue;
      List<SootClass> targets = new ArrayList<>(c.getInterfaces());
      
      if (c.hasSuperclass() && !c.getSuperclass().getName().equals("java.lang.Object")) {
        targets.add(c.getSuperclass());
      }
      if (targets.size() == 1) {
        SootClass target = targets.get(0);
        System.out.println("Class: " + c); 
        System.out.println("Target: " + target);
        List<SootMethod> methods = getOverriddenMethods(target, c);
        System.out.println(methods);
        Map<SootField, Integer> scores = new HashMap<>();
        boolean hasBodies = false;
        for (SootMethod m : methods) {
          if (m.hasActiveBody()) {
            hasBodies = true;
          }
          SootField field = delagatesCall(m, c.getFields());
          if (field != null) {
            scores.put(field, scores.getOrDefault(field, 0)+1);
          }
        }
        
        System.out.println("Scores: " + scores);
        Entry<SootField, Integer> maxField = null;
        for (Entry<SootField, Integer> e : scores.entrySet()) {
          if (maxField == null || e.getValue() > maxField.getValue()) {
            maxField = e;
          }
        }
        if (maxField != null) {
          double percentage = (maxField.getValue() / (double)target.getMethodCount());
          System.out.println("Percent: " + percentage);
          if (percentage > threshold) {
            SootClass adaptee = this.bundle.get("scene", Scene.class).getSootClass(maxField.getKey().getType().toString());
            Relationship adapts = getRelationship(c, adaptee, adaptsRelations, relationships);
            Relationship isATarget = getRelationship(c, target, targetRelations, relationships);
            if (adapts != null && isATarget != null) {
              patterns.add(addAdapterPattern(target, c, adaptee, adapts, isATarget));
            }
          }
        } else if (!hasBodies) {
          SootClass adaptee = this.bundle.get("scene", Scene.class).getSootClass(c.getFields().getFirst().getType().toString());
          Relationship adapts = getRelationship(c, adaptee, adaptsRelations, relationships);
          Relationship isATarget = getRelationship(c, target, targetRelations, relationships);
          if (adapts != null && isATarget != null) {
            patterns.add(addAdapterPattern(target, c, adaptee, adapts, isATarget));
          }
        }
      }
      
    }
    
    return this.bundle;
  }

  private Relationship getRelationship(SootClass from, SootClass to, List<Relation> relations, List<Relationship> relationships) {
    for (Relationship r : relationships) {
      if (r.getFromClass().equals(from) && r.getToClass().equals(to) && relations.contains(r.getRelation())) {
        return r;
      }
    }
    return null;
  }

  private Pattern addAdapterPattern(SootClass target, SootClass adapter, SootClass adaptee, Relationship adapts, Relationship isATarget) {
    Pattern p = new Pattern("adapter");
    p.put("target", target);
    p.put("adapter", adapter);
    p.put("adaptee", adaptee);
    p.put("adapts", adapts);
    p.put("isATarget", isATarget);
    return p;
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
  
  private SootField delagatesCall(SootMethod m, Chain<SootField> fields) {
    if (m.getSubSignature().contains("<init>")) return null;
    if (!m.hasActiveBody()) {
     return null;
    }
    Body body = m.getActiveBody();
    UnitGraph graph = new ExceptionalUnitGraph(body);
    for (Unit u : graph) {
      System.out.println("Unit: " + u);
      if (u instanceof InvokeStmt) {
        InvokeStmt stmt = (InvokeStmt) u;
        InvokeExpr expr = stmt.getInvokeExpr();
        SootClass target = expr.getMethod().getDeclaringClass();
        for (SootField f : fields) {
          if (f.getType().equals(target.getType())) {
            return f;
          }
        }
      } else if (u instanceof AssignStmt) {
        AssignStmt stmt = (AssignStmt) u;
        if (stmt.getRightOp() instanceof InvokeExpr) {
          InvokeExpr expr = (InvokeExpr) stmt.getRightOp();
          SootClass target = expr.getMethod().getDeclaringClass();
          for (SootField f : fields) {
            if (f.getType().equals(target.getType())) {
              return f;
            }
          }
        }
      }
    }
    return null;
  }  
}
