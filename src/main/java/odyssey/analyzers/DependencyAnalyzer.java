package odyssey.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import odyssey.app.Configuration;
import odyssey.app.Relation;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class DependencyAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  Set<Relationship> relationships;

  public DependencyAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    relationships = new HashSet<>();
    for (SootClass c : bundle.classes) {
      if (passesFilters(c)) {
        generateDependencyRelationships(c);
      }
    }

    addRelations(bundle.relationships);
    return bundle;
  }

  private void generateDependencyRelationships(SootClass clazz) {
    List<SootMethod> methods = clazz.getMethods();
    methods.forEach(m -> {
      Tag signatureTag = m.getTag("SignatureTag");
      if (signatureTag != null) {
        try {
          evaluateMethodUsingTag(clazz, signatureTag);
        } catch (Exception e) {
          System.out.println("Failed to parse Generic --- Ignoring");
          evaluateSootParameters(clazz, m.getParameterTypes());
        }
      } else {
        evaluateSootParameters(clazz, m.getParameterTypes());
      }
      if (m.hasActiveBody()) {
        checkBody(clazz, m.getActiveBody());
      }
    });
  }

  /*
   * May throw illegal state exception when failing to parse generics
   */
  private void evaluateMethodUsingTag(SootClass clazz, Tag signatureTag) {
    String signature = signatureTag.toString();
    MethodEvaluator evaluator = new MethodEvaluator(signature);
    processGenericType(clazz, evaluator.getReturnType());
    List<GenericType> paramTypes = evaluator.getParameterTypes();
    for (GenericType type : paramTypes) {
      processGenericType(clazz, type);
    }
  }

  private void processGenericType(SootClass fromClass, GenericType type) {
    SootClass toClass;
    Set<String> containerTypes = type.getAllContainerTypes();
    for (String s : containerTypes) {
      toClass = bundle.scene.getSootClass(s);
      if (passesFilters(toClass)) {
        addRelationship(fromClass, toClass);
      }
    }

    Set<String> genericTypes = type.getAllElementTypes();
    for (String s : genericTypes) {
      toClass = bundle.scene.getSootClass(s);
      if (passesFilters(toClass)) {
        addRelationship(fromClass, toClass);
      }
    }
  }

  private void evaluateSootParameters(SootClass fromClass, List<Type> parameterTypes) {
    for (Type t : parameterTypes) {
      Type baseType = t.makeArrayType().baseType;
      SootClass toClass = bundle.scene.getSootClass(baseType.toString());
      if (passesFilters(toClass) && !fromClass.equals(toClass)) {
        addRelationship(fromClass, toClass);
      }
    }
  }

  private void checkBody(SootClass clazz, Body body) {
    UnitGraph graph = new ExceptionalUnitGraph(body);
    graph.forEach(u -> {
      if (u instanceof InvokeStmt) {
        processInvokeStmt(clazz, (InvokeStmt) u);
      } else if (u instanceof AssignStmt) {
        Value rightOp = ((AssignStmt) u).getRightOp();
        if (rightOp instanceof InvokeExpr) {
          processInvokeExpr(clazz, (InvokeExpr) rightOp);
        }
        if (rightOp instanceof NewExpr) {
          processNewExpr(clazz, (NewExpr) rightOp);
        }
      }
    });
  }

  private void processInvokeStmt(SootClass clazz, InvokeStmt stmt) {
    InvokeExpr exp = stmt.getInvokeExpr();
    SootClass declaringClass = exp.getMethod().getDeclaringClass();
    addRelationship(clazz, declaringClass);
  }

  private void processInvokeExpr(SootClass clazz, InvokeExpr invkExpr) {
    SootMethod method = invkExpr.getMethod(); 
    Tag signatureTag = method.getTag("SignatureTag");
    if (signatureTag != null) {
      MethodEvaluator evaluator = new MethodEvaluator(signatureTag.toString());
      try {
        processGenericType(clazz, evaluator.getReturnType());
        List<GenericType> paramTypes = evaluator.getParameterTypes();
        for (GenericType type : paramTypes) {
          processGenericType(clazz, type);
        }
      } catch (IllegalStateException e) {
        SootClass returnType = bundle.scene.getSootClass(method.getReturnType().toString());
        addRelationship(clazz, returnType);
      }
    } else {
      SootClass returnType = bundle.scene.getSootClass(method.getReturnType().toString());
      addRelationship(clazz, returnType);
    }
  }

  private void processNewExpr(SootClass clazz, NewExpr newExpr) {
    SootClass returnType = bundle.scene.getSootClass(newExpr.getType().toString());
    addRelationship(clazz, returnType);
  }

  private void addRelationship(SootClass from, SootClass to) {
    if (from.hasSuperclass()) {
      if (from.getSuperclass().equals(to)) {
        return;
      }
      if (from.getInterfaces().contains(to)) {
        return;
      }
    }
    if (passesFilters(to)) {
      if (!from.equals(to)) {
        relationships.add(new Relationship(from, Relation.DEPENDENCY, to, 0));
      }
    }
  }

  /*
   * Do not add dependency if classes are already related by association
   */
  private void addRelations(List<Relationship> bundleRelations) {
    Set<Relationship> toBeAdded = new HashSet<>();
    SootClass toFoundClass;
    SootClass fromFoundClass;
    boolean add;
    for (Relationship found : this.relationships) {
      toFoundClass = found.getToClass();
      fromFoundClass = found.getFromClass();
      add = true;
      for (Relationship bundleRelation : bundleRelations) {
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
    bundleRelations.addAll(toBeAdded);
  }
}
