package odyssey.analyzers;

import java.util.List;
import java.util.Set;

import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;
import odyssey.app.Configuration;
import odyssey.app.Relation;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootField;
import soot.tagkit.Tag;
import soot.util.Chain;

public class AssociationAnalyzer extends Analyzer {
  AnalyzerBundle bundle;

  public AssociationAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    List<Relationship> relationships = bundle.relationships;

    for (SootClass c : bundle.classes) {
      if (passesFilters(c)) {
        generateAssociationRelationships(c, relationships);
      }
    }

    bundle.relationships = relationships;
    return bundle;
  }

  private void generateAssociationRelationships(SootClass c, List<Relationship> relationships) {
    Chain<SootField> fields = c.getFields();
    for (SootField f : fields) {
      Tag signatureTag = f.getTag("SignatureTag");
      if (signatureTag != null) {
        String signature = signatureTag.toString();
        FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
        GenericType fieldType = fieldEvaluator.getType();

        SootClass containerClass;

        Set<String> containerTypes = fieldType.getAllContainerTypes();
        for (String s : containerTypes) {
          containerClass = bundle.scene.getSootClass(s);
          if (passesFilters(containerClass)) {
            Relationship rel = new Relationship(c, Relation.ASSOCIATION, containerClass, 0);
            relationships.add(rel);
          }
        }

        Set<String> genericTypes = fieldType.getAllElementTypes();
        SootClass genericClass;
        for (String s : genericTypes) {
          genericClass = bundle.scene.getSootClass(s);
          if (passesFilters(genericClass)) {
            Relationship rel = new Relationship(c, Relation.ASSOCIATION, genericClass, -1);
            relationships.add(rel);
          }
        }
      } else {
        SootClass fieldClass = bundle.scene.getSootClass(f.getType().toString());
        if (passesFilters(fieldClass) && !baseClassSame(c, fieldClass)) {
          int cardinality = 0;
          if (fieldClass.getName().contains("[")) {
            cardinality = -1;
          }
          relationships.add(new Relationship(c, Relation.ASSOCIATION, fieldClass, cardinality));
        }
      }
    }
  }

  // Do not let something have arrays of itself
  private boolean baseClassSame(SootClass c1, SootClass c2) {
    return c1.getName().replaceAll("\\[\\]", "").equals(c2.getName().replaceAll("\\[\\]", ""));
  }

}
