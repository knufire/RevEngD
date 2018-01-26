package odyssey.analyzers;

import java.util.Collection;
import java.util.List;

import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.Tag;

public class UMLParser {

  public static String parse(SootClass c) {
    StringBuilder builder = new StringBuilder();
    builder.append(getClassType(c.getModifiers()));
    builder.append(" ");
    builder.append(c.getShortName());
    builder.append(" {");
    return builder.toString();
  }

  public static String parse(SootField f) {
    StringBuilder builder = new StringBuilder();
    builder.append(getAccessModifier(f.getModifiers()));
    builder.append(" ");
    builder.append(getStaticAbstractModifier(f.getModifiers()));

    Tag signatureTag = f.getTag("SignatureTag");
    if (signatureTag != null) {
      try {
        String signature = signatureTag.toString();
        FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
        GenericType fieldType = fieldEvaluator.getType();
        builder.append(parse(fieldType));
      } catch (Exception e) {
        // DoNothing
      }
    } else {
      builder.append(parse(f.getType()));
    }

    builder.append(" ");
    builder.append(f.getName());
    return builder.toString();
  }

  public static String parse(SootMethod m) {
    StringBuilder builder = new StringBuilder();
    builder.append(getAccessModifier(m.getModifiers()));
    builder.append(" ");
    builder.append(getStaticAbstractModifier(m.getModifiers()));
    builder.append(parseReturnType(m));
    builder.append(" ");
    builder.append(parseMethodName(m));
    builder.append(parseMethodParameters(m));
    return builder.toString();
  }

  public static String parseReturnType(SootMethod m) {
    StringBuilder builder = new StringBuilder();
    Tag signatureTag = m.getTag("SignatureTag");
    if (signatureTag != null) {
      MethodEvaluator evaluator = new MethodEvaluator(signatureTag.toString());
      try {
        builder.append(parse(evaluator.getReturnType()));
      } catch (IllegalStateException e) {
        builder.append(parse(m.getReturnType()));
      }
    } else {
      builder.append(parse(m.getReturnType()));
    }
    return builder.toString();
  }

  public static String parseMethodName(SootMethod m) {
    String methodName = Scene.v().quotedNameOf(m.getName());
    if (methodName.contains("<init>")) {
      return trimQualifiedName(Scene.v().quotedNameOf(m.getDeclaringClass().getName()));
    } else {
      return methodName;
    }
  }

  public static String parseMethodParameters(SootMethod m) {
    StringBuilder builder = new StringBuilder();
    Tag signatureTag = m.getTag("SignatureTag");
    List<Type> params = m.getParameterTypes();
    if (signatureTag != null) {
      MethodEvaluator evaluator = new MethodEvaluator(signatureTag.toString());
      try {
        builder.append(parse(evaluator.getParameterTypes()));
      } catch (IllegalStateException e) {
        builder.append(parse(params));
      }

    } else {
      builder.append(parse(params));
    }
    return builder.toString();
  }

  public static String parse(Collection<GenericType> parameterTypes) {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    for (GenericType t : parameterTypes) {
      builder.append(parse(t));
      builder.append(",");
    }
    return builder.substring(0, builder.length() - 1) + ")";

  }

  public static String parse(List<Type> types) {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    for (int i = 0; i < types.size(); i++) {
      builder.append(parse(types.get(i)));
      if (i < types.size() - 1) {
        builder.append(",");
      }
    }
    builder.append(")");
    return builder.toString();
  }

  public static String parse(Type t) {
    return trimQualifiedName(t.toQuotedString());
  }

  public static String parse(GenericType type) {
    StringBuilder builder = new StringBuilder();
    builder.append(trimQualifiedName(type.getContainerType()));
    List<GenericType> elementTypes = type.getElementTypes();
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

    if (type.isArray()) {
      for (int i = 0; i < type.getDimension(); ++i) {
        builder.append("[]");
      }
    }

    return builder.toString();
  }

  public static String parse(Relationship r) {
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

  public static String parse(Relation r) {
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

  public static String parseCardinality(int cardinality) {
    if (cardinality == -1) {
      return "\"1..*\"";
    }
    if (cardinality == 0) {
      return "";
    }
    return "\"" + cardinality + "\"";
  }

  public static String trimQualifiedName(String s) {
    String[] parts = s.split("\\.");
    return parts[parts.length - 1];
  }

  public static String getAccessModifier(int m) {
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

  public static String getStaticAbstractModifier(int m) {
    if (soot.Modifier.isAbstract(m)) {
      return "{abstract} ";
    }
    if (soot.Modifier.isStatic(m)) {
      return "{static} ";
    }
    return "";
  }

  public static String getClassType(int m) {
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
