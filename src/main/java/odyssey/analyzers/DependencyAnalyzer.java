package odyssey.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import odyssey.filters.Filter;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Body;
import soot.Scene;
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
  private Set<Relationship> relationships;
  private Scene scene;

  public DependencyAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    this.scene = this.bundle.get("scene", Scene.class);
    relationships = new HashSet<>();
    for (SootClass c : bundle.getList("classes", SootClass.class)) {
      if (passesFilters(c)) {
        generateDependencyRelationships(c);
      }
    }

    addRelations(bundle.getList("relationships", Relationship.class));
    return bundle;
  }

  private void generateDependencyRelationships(SootClass clazz) {
    List<SootMethod> methods = clazz.getMethods();
    methods.forEach(m -> {
      if (!passesFilters(m)) return;
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
      toClass = scene.getSootClass(s);
      if (passesFilters(toClass)) {
        addRelationship(fromClass, toClass, 1);
      }
    }

    Set<String> genericTypes = type.getAllElementTypes();
    for (String s : genericTypes) {
      toClass = scene.getSootClass(s);
      if (passesFilters(toClass)) {
        addRelationship(fromClass, toClass, -1);
      }
    }
  }

  private void evaluateSootParameters(SootClass fromClass, List<Type> parameterTypes) {
    for (Type t : parameterTypes) {
      Type baseType = t.makeArrayType().baseType;
      SootClass toClass = scene.getSootClass(baseType.toString());
      if (passesFilters(toClass) && !fromClass.equals(toClass)) {
        addRelationship(fromClass, toClass, isArray(t) ? -1 : 0);
      }
    }
  }

  private void checkBody(SootClass clazz, Body body) {
    UnitGraph graph = new ExceptionalUnitGraph(body);
    graph.forEach(u -> {
      //System.err.println(u);
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
    SootMethod method = exp.getMethod();
    if (!passesFilters(method)) return;
    SootClass declaringClass = method.getDeclaringClass();
    addRelationship(clazz, declaringClass, 0);
  }

  private void processInvokeExpr(SootClass clazz, InvokeExpr invkExpr) {
    SootMethod method = invkExpr.getMethod();
    if (!passesFilters(method)) return;
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
        processReturnType(clazz, method);
      }
    } else {
      processReturnType(clazz, method);
    }
  }

  private boolean isArray(Type type) {
    return type.toString().contains("[");
  }

  private void processReturnType(SootClass clazz, SootMethod method) {
    Type returnType = method.getReturnType();
    SootClass returnClass = scene.getSootClass(returnType.toString());
    addRelationship(clazz, returnClass, isArray(returnType) ? -1 : 0);
  }

  private void processNewExpr(SootClass clazz, NewExpr newExpr) {
    SootClass returnType = scene.getSootClass(newExpr.getType().toString());
    addRelationship(clazz, returnType, 0);
  }

  private void addRelationship(SootClass from, SootClass to, int cardinality) {
    SootClass toClass = to;
    if (from.hasSuperclass()) {
      if (from.getSuperclass().equals(toClass)) {
        return;
      }
      if (from.getInterfaces().contains(toClass)) {
        return;
      }
    }
    if (passesFilters(toClass)) {
      if (!from.equals(toClass)) {
        boolean isArray = toClass.getType().toString().contains("[");
        if (isArray) {
          Type baseType = toClass.getType().makeArrayType().baseType;
          toClass = bundle.get("scene", Scene.class).getSootClass(baseType.toString().replaceAll("\\[\\]", ""));
        }
        relationships.add(new Relationship(from, Relation.DEPENDENCY, toClass, cardinality));
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
