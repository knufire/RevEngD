package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;
import odyssey.filters.Filter;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.tagkit.Tag;
import soot.util.Chain;

public class AssociationAnalyzer extends Analyzer {
  AnalyzerBundle bundle;

  public AssociationAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    List<Relationship> relationships = bundle.getList("relationships", Relationship.class);

    for (SootClass c : bundle.getList("classes", SootClass.class)) {
      if (passesFilters(c)) {
        generateAssociationRelationships(c, relationships);
      }
    }

    bundle.put("relationships", relationships);
    return bundle;
  }

  private void generateAssociationRelationships(SootClass c, List<Relationship> relationships) {
    Chain<SootField> fields = c.getFields();
    Scene scene = bundle.get("scene", Scene.class);
    for (SootField f : fields) {
      if (!passesFilters(f))
        continue;
      Tag signatureTag = f.getTag("SignatureTag");
      if (signatureTag != null) {
        String signature = signatureTag.toString();
        FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
        try {
          GenericType fieldType = fieldEvaluator.getType();

          SootClass containerClass;

          Set<String> containerTypes = fieldType.getAllContainerTypes();
          for (String s : containerTypes) {
            containerClass = scene.getSootClass(s);
            if (passesFilters(containerClass)) {
              Relationship rel = new Relationship(c, Relation.ASSOCIATION, containerClass, 0);
              addRelationship(rel);
            }
          }

          Set<String> genericTypes = fieldType.getAllElementTypes();
          SootClass genericClass;
          for (String s : genericTypes) {
            genericClass = scene.getSootClass(s);
            if (passesFilters(genericClass)) {
              Relationship rel = new Relationship(c, Relation.ASSOCIATION, genericClass, -1);
              addRelationship(rel);
            }
          }
        } catch (Exception e) {
          continue;
        }
      } else {
        boolean isArray = f.getType().toString().contains("[");
        Type baseType = f.getType().makeArrayType().baseType;
        SootClass fieldClass = scene.getSootClass(baseType.toString());
        if (passesFilters(fieldClass) && !c.equals(fieldClass)) {
//        if (passesFilters(fieldClass)) {
          addRelationship(new Relationship(c, Relation.ASSOCIATION, fieldClass, isArray ? -1 : 0));
        }
      }
    }
  }

  private void addRelationship(Relationship newRelationship) {
    List<Relationship> bundleRels = bundle.getList("relationships", Relationship.class);
    List<Relationship> toRemove = new ArrayList<>();
    for (Relationship r : bundleRels) {
      if (r.getToClass().equals(newRelationship.getToClass())) {
        if (r.getFromClass().equals(newRelationship.getFromClass())) {
          if (r.getRelation() == Relation.DEPENDENCY) {
            toRemove.add(r);
          }
        }
      }
    }
    bundleRels.add(newRelationship);
    bundleRels.removeAll(toRemove);
  }
}
