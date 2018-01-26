package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;

public class SingletonAnalyzer extends Analyzer {

  public SingletonAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    List<SootClass> classes = bundle.getList("classes", SootClass.class);
    List<Pattern> patterns = bundle.getList("patterns", Pattern.class);
    List<Relationship> relationships = bundle.getList("relationships", Relationship.class);

    for (SootClass clazz : classes) {
      if (privateConstructor(clazz) && hasGetInstance(clazz) && hasInstanceField(clazz)) {
        Pattern p = new Pattern("singleton");
        p.put("singleton", clazz);
        Relationship r = createRelationship(relationships, clazz);
        p.put("singleton", r);
        patterns.add(p);
      }
    }
    return bundle;
  }

  private boolean hasGetInstance(SootClass clazz) {
    List<SootMethod> methods = clazz.getMethods();
    for (SootMethod m : methods) {
      if (!m.isConstructor() && clazz.getType().equals(m.getReturnType())) {
        return true;
      }
    }
    return false;
  }

  private boolean privateConstructor(SootClass clazz) {
    List<SootMethod> methods = clazz.getMethods();
    for (SootMethod m : methods) {
      if (m.isConstructor()) {
        return soot.Modifier.isPrivate(m.getModifiers());
      }
    }
    return false;
  }

  private boolean hasInstanceField(SootClass clazz) {
    Chain<SootField> fields = clazz.getFields();
    for (SootField f : fields) {
      if (f.getType().equals(clazz.getType())) {
        return true;
      }
    }
    return false;
  }

  private Relationship createRelationship(List<Relationship> relationships, SootClass clazz) {
    Relationship r = new Relationship(clazz, Relation.ASSOCIATION, clazz, 0);
    relationships.add(r);
    return r;
  }

}
