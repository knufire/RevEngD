package odyssey.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import odyssey.app.Configuration;
import odyssey.app.Relation;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class DependencyAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;

  public DependencyAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    Set<Relationship> foundRelationships = new HashSet<>();
    for (SootClass c : bundle.classes) {
      if (passesFilters(c)) {
        generateDependencyRelationships(c, foundRelationships);
      }
    }

    addRelations(foundRelationships, bundle.relationships);
    return bundle;
  }

  private void generateDependencyRelationships(SootClass clazz, Set<Relationship> relationships) {
    List<SootMethod> methods = clazz.getMethods();
    methods.forEach(m -> {
      if (m.hasActiveBody()) {
        checkBody(clazz, m.getActiveBody(), relationships);
      }
    });
  }

  private void checkBody(SootClass clazz, Body body, Set<Relationship> relationships) {
    UnitGraph graph = new ExceptionalUnitGraph(body);
    graph.forEach(u -> {
      if (u instanceof InvokeStmt) {
        //TODO: DO SOMETHING
        InvokeStmt stmt = (InvokeStmt) u;
        InvokeExpr exp = stmt.getInvokeExpr();
        //System.err.println(exp.getMethod().getDeclaringClass().getName());
        SootClass declaringClass = exp.getMethod().getDeclaringClass();
        addRelationship(clazz, declaringClass, relationships);
      } else if (u instanceof AssignStmt) {
        Value rightOp = ((AssignStmt) u).getRightOp();
        if(rightOp instanceof InvokeExpr) {
          InvokeExpr invkExpr = (InvokeExpr)rightOp;
          SootMethod method = invkExpr.getMethod();
          //TODO: Generics and arrays
          SootClass returnType = bundle.scene.getSootClass(method.getReturnType().toString());
          addRelationship(clazz, returnType, relationships);
        }
        if(rightOp instanceof NewExpr) {
          NewExpr newExpr = (NewExpr)rightOp; 
          //TODO: Generics and arrays
          SootClass returnType = bundle.scene.getSootClass(newExpr.getType().toString());
          addRelationship(clazz, returnType, relationships);
        }
      }
    });
  }
  
  private void addRelationship(SootClass from, SootClass to, Set<Relationship> relationships) {
    if (passesFilters(to)) {
      if (!from.equals(to)) {        
        relationships.add(new Relationship(from, Relation.DEPENDENCY, to, 0));
      }
    }
  }

  /*
   * Do not add dependency if classes are already related by association
   */
  private void addRelations(Set<Relationship> foundRelationships, List<Relationship> relationships) {
    Set<Relationship> toBeAdded = new HashSet<>();
    SootClass toFoundClass;
    SootClass fromFoundClass;
    boolean add;
    for (Relationship found : foundRelationships) {
      toFoundClass = found.getToClass();
      fromFoundClass = found.getFromClass();
      add = true;
      for (Relationship bundleRelation : relationships) {
        if (toFoundClass.equals(bundleRelation.getToClass())) {
          if (fromFoundClass.equals(bundleRelation.getFromClass())) {
            if (bundleRelation.getRelation().equals(Relation.ASSOCIATION)) {
              add = false;
            }
          }
        }
      }
      if (add) {
        toBeAdded.add(found);
      }
    }
    relationships.addAll(toBeAdded);
  }
}
