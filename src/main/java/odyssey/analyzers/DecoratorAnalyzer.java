package odyssey.analyzers;

import java.util.List;
import java.util.stream.Collectors;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.util.Chain;

public class DecoratorAnalyzer extends Analyzer {

  private List<Relationship> relationships;

  protected DecoratorAnalyzer(List<Filter> filters) {
    super(filters);
  }

  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    List<Pattern> patterns = bundle.getList("patterns", Pattern.class);
    List<SootClass> classes = bundle.getList("classes", SootClass.class);
    relationships = bundle.getList("relationships", Relationship.class);
    addClassesToPatterns(classes, patterns);
    return bundle;
  }

  private void addClassesToPatterns(List<SootClass> classes, List<Pattern> patterns) {
    classes.forEach(c -> {
      addClassToPattern(c, patterns);
    });
  }

  private void addClassToPattern(SootClass c, List<Pattern> patterns) {
    if (!passesFilters(c)) {
      return;
    }
    if (c.getSuperclass().getName().equals("java.lang.Object")) {
      return;
    }
    SootClass decorated = typeOfDecoratedObject(c);
    if (decorated == null) {
      return;
    }
    // Decorator
    List<SootMethod> missingMethods = findMissingMethods(c, decorated);
    if (!missingMethods.isEmpty()) {
      patterns.add(createBadDecoratorPattern(c, decorated, missingMethods));
    } else {
      patterns.add(createRegularDecoratorPattern(c, decorated));
    }
  }

  private SootClass typeOfDecoratedObject(SootClass c) {
    Chain<SootField> fields = c.getFields();
    Type t = c.getSuperclass().getType();
    for (SootField f : fields) {
      if (f.getType().equals(t)) {
        return c.getSuperclass();
      }
    }
    return null;
  }

  private List<SootMethod> findMissingMethods(SootClass c, SootClass decorated) {
    List<SootMethod> methods = getMissingMethods(getDeclaredMethods(c), decorated);
    return methods;
  }

  private List<SootMethod> getDeclaredMethods(SootClass c) {
    return c.getMethods().stream().filter(m -> c.declaresMethodByName(m.getName())).collect(Collectors.toList());
  }

  private List<SootMethod> getMissingMethods(List<SootMethod> decoratorMethods, SootClass decorated) {
    return decorated.getMethods().stream().filter(m -> m.isPublic()).filter(m -> !methodMatches(m, decoratorMethods))
        .collect(Collectors.toList());
  }

  private boolean methodMatches(SootMethod m, List<SootMethod> decoratorMethods) {
    String toMatch = m.getSignature().split(" ")[1];
    for (SootMethod decoratorMethod : decoratorMethods) {
      if (toMatch.equals(decoratorMethod.getSignature().split(" ")[1])) {
        return true;
      }
    }
    return false;
  }

  private Pattern createBadDecoratorPattern(SootClass c, SootClass decorated, List<SootMethod> missingMethods) {
    Pattern p = new Pattern("decorator");
    p.put("component", decorated);
    p.put("badDecorator", c);
    p.put("decorates", findDecoratorRelationship(c, decorated));
    String methodKey = c.getName() + " badMethod";
    missingMethods.forEach(m -> p.put(methodKey, m));
    return p;
  }

  private Pattern createRegularDecoratorPattern(SootClass c, SootClass decorated) {
    Pattern p = new Pattern("decorator");
    p.put("component", decorated);
    p.put("decorator", c);
    p.put("decorates", findDecoratorRelationship(c, decorated));
    return p;
  }

  private Relationship findDecoratorRelationship(SootClass c, SootClass decorated) {
    for (Relationship r : relationships) {
      // System.out.println(r);
      // System.out.println("\t " + c);
      // System.out.println("\t" + decorated.getShortName());
      // System.out.println();
      if (r.getFromClass().equals(c) && r.getToClass().equals(decorated)) {
        if (r.getRelation().equals(Relation.ASSOCIATION)) {
          return r;
        }
      }
    }
    throw new RuntimeException("Could not find a relationship for this Decorator " + c.getName());
  }
}
