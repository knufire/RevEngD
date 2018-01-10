package odyssey.analyzers;

import java.util.List;
import java.util.Set;

import odyssey.app.Configuration;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootField;
import soot.tagkit.Tag;
import soot.util.Chain;
import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;


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
      if(signatureTag != null) {
        // Use SignatureEvaluator API for parsing the field signature
        String signature = signatureTag.toString();
        FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
        GenericType fieldType = fieldEvaluator.getType();
//        System.out.println(fieldType);
        
        Set<String> containerTypes = fieldType.getAllContainerTypes();
        
        
        
        // TODO: Try playing with the following methods
//        fieldType.getContainerType();
//        fieldType.getElementTypes();
//        fieldType.getAllContainerTypes();
//        fieldType.getAllElementTypes();
//        fieldType.isArray();
//        fieldType.getDimension();
       
      }
      else {
        // Bytecode signature for this field is unavailable, so let's use soot API
        // System.out.println(f.getType().toString());
      }

      if (passesFilters(f.getDeclaringClass())) {
        //relationships.add(new Relationship(c, Relation.ASSOCIATION, , 0));
      }
    }
  }

}
