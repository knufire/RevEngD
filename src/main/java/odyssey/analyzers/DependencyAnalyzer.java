package odyssey.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import odyssey.app.Configuration;
import odyssey.app.Relation;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.Body;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.util.Chain;

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
        // System.out.println(c.getName());
        generateDependencyRelationships(c, foundRelationships);
      }
    }

    addRelations(foundRelationships, bundle.relationships);
    return bundle;
  }

  private void generateDependencyRelationships(SootClass clazz, Set<Relationship> relationships) {
    List<SootMethod> methods = clazz.getMethods();
    methods.forEach(m -> {
      checkParameterTypes(clazz, m.getParameterTypes());
      if (m.hasActiveBody()) {
        checkBody(clazz, m.getActiveBody(), relationships);
      }
    });
  }

  private void checkParameterTypes(SootClass clazz, List<Type> parameterTypes) {

  }

  private void checkBody(SootClass clazz, Body body, Set<Relationship> relationships) {
    Chain<Local> locals = body.getLocals();
    SootClass localClass;
    for (Local l : locals) {
      localClass = findClassForLocal(l);
      if (localClass != null && !localClass.equals(clazz)) {
        relationships.add(new Relationship(clazz, Relation.DEPENDENCY, localClass, 0));
      }
    }
  }

  private SootClass findClassForLocal(Local l) {
    String classNameOflocal = l.getType().toString();
    for (SootClass c : bundle.classes) {
      if (c.getName().equals(classNameOflocal) && passesFilters(c)) {
        return c;
      }
    }
    return null;
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
