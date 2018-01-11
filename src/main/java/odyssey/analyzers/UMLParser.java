package odyssey.analyzers;

import java.util.List;

import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import odyssey.app.Relation;
import odyssey.app.Relationship;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.Tag;

public class UMLParser {

  public UMLParser() {
  }

  public String parse(SootClass c) {
    StringBuilder builder = new StringBuilder();
    builder.append(getClassType(c.getModifiers()));
    builder.append(" ");
    builder.append(c.getShortName());
    builder.append(" {");
    return builder.toString();
  }

  public String parse(SootField f) {
    StringBuilder builder = new StringBuilder();
    builder.append(getAccessModifier(f.getModifiers()));
    builder.append(" ");
    builder.append(getStaticAbstractModifier(f.getModifiers()));
    
    Tag signatureTag = f.getTag("SignatureTag");
    if (signatureTag != null) {
      String signature = signatureTag.toString();
      FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
      GenericType fieldType = fieldEvaluator.getType();
      builder.append(parse(fieldType));
    } else {
      builder.append(parse(f.getType()));
    }
    
    builder.append(" ");
    builder.append(f.getName());
    return builder.toString();
  }

  public String parse(SootMethod m) {
    List<Type> params = m.getParameterTypes();
    StringBuilder builder = new StringBuilder();
    builder.append(getAccessModifier(m.getModifiers()));
    builder.append(" ");
    builder.append(getStaticAbstractModifier(m.getModifiers()));
    
    //Return Type
    Tag signatureTag = m.getTag("SignatureTag");
    if (signatureTag != null) {
      try {
        MethodEvaluator evaluator = new MethodEvaluator(signatureTag.toString());
        builder.append(parse(evaluator.getReturnType()));
      } catch (Exception e) {       
        builder.append(parse(m.getReturnType()));
      }
    } else {
      builder.append(parse(m.getReturnType()));
    }
    
    builder.append(" ");
    
    
    //Method Name
    String methodName = Scene.v().quotedNameOf(m.getName());
    if (methodName.contains("<init>")) {
      builder.append(trimQualifiedName(Scene.v().quotedNameOf(m.getDeclaringClass().getName())));
    } else {
      builder.append(methodName);
    }
    
    //Parameters
    builder.append("(");
    for (int i = 0; i < params.size(); i++) {
      builder.append(trimQualifiedName(params.get(i).toQuotedString()));
      if (i < params.size() - 1) {
        builder.append(",");
      }
    }
    builder.append(")");
    return builder.toString();
  }

  public String parse(Type t) {
    return trimQualifiedName(t.toQuotedString());
  }
  
  private String parse(GenericType fieldType) {
    StringBuilder builder = new StringBuilder();
    builder.append(trimQualifiedName(fieldType.getContainerType()));
    List<GenericType> elementTypes = fieldType.getElementTypes();
    if (!elementTypes.isEmpty()) {
      builder.append("<");
      for (int i = 0; i < elementTypes.size(); ++i) {
        builder.append(parse(elementTypes.get(i)));

        if (i != elementTypes.size() - 1) {
          builder.append(",");
        }
      }
      builder.append(">");
    }
    
    if(fieldType.isArray()) {
      for(int i = 0; i < fieldType.getDimension(); ++i) {
        builder.append("[]");
      }
    }

    return builder.toString();
  }

  public String parse(Relationship r) {
    StringBuilder builder = new StringBuilder();
    builder.append(r.getToClass().getShortName().replaceAll("\\[\\]", ""));
    builder.append(" ");
    builder.append(parseCardinality(r.getCardinality()));
    builder.append(" ");
    builder.append(parse(r.getRelation()));
    builder.append(" ");
    builder.append(r.getFromClass().getShortName());
    return builder.toString();
  }

  private String parse(Relation r) {
    switch (r) {
    case ASSOCIATION:
      return "<--";
    case DEPENDENCY:
      return "<..";
    case EXTENDS:
      return "<|--";
    case IMPLEMENTS:
      return "<|..";
    default:
      return "<--";
    }
  }

  private String parseCardinality(int cardinality) {
    if (cardinality == -1) {
      return "\"1..*\"";
    }
    if (cardinality == 0) {
      return "";
    }
    return "\"" + cardinality + "\"";
  }

  public String trimQualifiedName(String s) {
    String[] parts = s.split("\\.");
    return parts[parts.length - 1];
  }

  public String getAccessModifier(int m) {
    if (soot.Modifier.isPublic(m)) {
      return "+";
    }
    if (soot.Modifier.isProtected(m)) {
      return "#";
    }
    if (soot.Modifier.isPrivate(m)) {
      return "-";
    }
    return "~"; // Return package-level access by default
  }

  public String getStaticAbstractModifier(int m) {
    if (soot.Modifier.isAbstract(m)) {
      return "{abstract} ";
    }
    if (soot.Modifier.isStatic(m)) {
      return "{static} ";
    }
    return "";
  }

  public String getClassType(int m) {
    if (soot.Modifier.isEnum(m)) {
      return "enum";
    }
    if (soot.Modifier.isInterface(m)) {
      return "interface";
    }
    if (soot.Modifier.isAbstract(m)) {
      return "abstract";
    }
    return "class";
  }
}
