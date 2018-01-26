package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.util.Chain;

public class InheritanceAnalyzer extends Analyzer {

  public InheritanceAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {

    List<Relationship> relationships = bundle.getList("relationships", Relationship.class);

    for (SootClass c : bundle.getList("classes", SootClass.class)) {
      if (passesFilters(c)) {
        generateExtendsRelationships(c, relationships);
        generateImplementsRelationships(c, relationships);
      }
    }

    bundle.put("relationships", relationships);
    return bundle;
  }

  private void generateExtendsRelationships(SootClass c, List<Relationship> relationships) {
    if (c.getName().equals("java.lang.Object")) {
      return;
    }
    SootClass superClass = c.getSuperclass();
    try {
      if (this.passesFilters(superClass)) {
        Relationship rel = new Relationship(c, Relation.EXTENDS, superClass, 0);
        relationships.add(rel);
      }
    } catch (Exception e) {
      System.out.println(c);
    }
  }

  private void generateImplementsRelationships(SootClass c, List<Relationship> relationships) {
    Chain<SootClass> interfaces = c.getInterfaces();
    for (SootClass i : interfaces) {
      if (passesFilters(i)) {
        relationships.add(new Relationship(c, Relation.IMPLEMENTS, i, 0));
      }
    }
  }

}
