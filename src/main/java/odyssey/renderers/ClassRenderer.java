package odyssey.renderers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import odyssey.filters.Filter;
import odyssey.models.Pattern;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.Tag;

public class ClassRenderer implements IClassRenderer {

  protected Pattern pattern;
  protected SootClass clazz;
  protected List<Filter> filters = Collections.emptyList();

  @Override
  public final String render(SootClass clazz) {
    this.clazz = clazz;

    StringBuilder builder = new StringBuilder();
    builder.append(renderClassType());
    builder.append(" ");
    builder.append(renderClassName());
    builder.append(" {");
    builder.append(System.lineSeparator());
    builder.append(renderFields());
    builder.append(System.lineSeparator());
    builder.append(renderMethods());
    builder.append(System.lineSeparator());
    builder.append("}");
    builder.append(System.lineSeparator());
    return builder.toString();
  }
  
  @Override
  public String getName() {
    return "default";
  }

  @Override
  public final String render(SootClass clazz, Pattern pattern) {
    this.pattern = pattern;
    return render(clazz);
  }

  @Override
  public String renderMethod(SootMethod method) {
    StringBuilder builder = new StringBuilder();
    builder.append(getAccessModifier(method.getModifiers()));
    builder.append(" ");
    builder.append(getStaticAbstractModifier(method.getModifiers()));
    builder.append(parseReturnType(method));
    builder.append(" ");
    builder.append(parseMethodName(method));
    builder.append(parseMethodParameters(method));
    return builder.toString();
  }
  
  @Override
  public String renderField(SootField field) {
    StringBuilder builder = new StringBuilder();
    builder.append(getAccessModifier(field.getModifiers()));
    builder.append(" ");
    builder.append(getStaticAbstractModifier(field.getModifiers()));

    Tag signatureTag = field.getTag("SignatureTag");
    if (signatureTag != null) {
      try {
        String signature = signatureTag.toString();
        FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
        GenericType fieldType = fieldEvaluator.getType();
        builder.append(renderGenericType(fieldType));
      } catch (Exception e) {
        builder.append(renderStandardType(field.getType()));
      }
    } else {
      builder.append(renderStandardType(field.getType()));
    }

    builder.append(" ");
    builder.append(field.getName());
    return builder.toString();
  }
  
  @Override
  public String renderStyle() {
    return "";
  }
  
  protected String renderClassName() {
    return clazz.getName();
  }

  protected String renderClassType() {
    return getClassType();
  }

  protected String getClassType() {
    int modifier = clazz.getModifiers();
    if (soot.Modifier.isEnum(modifier)) {
      return "enum";
    }
    if (soot.Modifier.isInterface(modifier)) {
      return "interface";
    }
    if (soot.Modifier.isAbstract(modifier)) {
      return "abstract";
    }
    return "class";
  }

  protected String renderFields() {
    StringBuilder builder = new StringBuilder();
    clazz.getFields().forEach(f -> {
      if(passesFilters(f)) {
        builder.append(renderField(f));
        builder.append(System.lineSeparator());        
      }
    });
    return builder.toString();
  }

  protected boolean passesFilters(SootField f) {
    for(Filter filter : filters) {
      if (!filter.shouldProcess(f)) return false;
    }
    return true;
  }
  

  protected String renderMethods() {
    StringBuilder builder = new StringBuilder();
    clazz.getMethods().forEach(m -> {
      if(passesFilters(m)) {
        builder.append(renderMethod(m));
        builder.append(System.lineSeparator());
      }
    });
    return builder.toString();
  }

  protected boolean passesFilters(SootMethod m) {
    for(Filter filter : filters) {
      if (!filter.shouldProcess(m)) return false;
    }
    return true;
  }
  
  protected String parseReturnType(SootMethod m) {
    StringBuilder builder = new StringBuilder();
    Tag signatureTag = m.getTag("SignatureTag");
    if (signatureTag != null) {
      MethodEvaluator evaluator = new MethodEvaluator(signatureTag.toString());
      try {
        builder.append(renderGenericType(evaluator.getReturnType()));
      } catch (IllegalStateException e) {
        builder.append(renderStandardType(m.getReturnType()));
      }
    } else {
      builder.append(renderStandardType(m.getReturnType()));
    }
    return builder.toString();
  }

  protected String parseMethodName(SootMethod m) {
    String methodName = Scene.v().quotedNameOf(m.getName());
    if (methodName.contains("<init>")) {
      return trimQualifiedName(Scene.v().quotedNameOf(m.getDeclaringClass().getName()));
    } else {
      return methodName;
    }
  }

  protected String parseMethodParameters(SootMethod m) {
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

  protected String parse(Collection<GenericType> parameterTypes) {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    for (GenericType t : parameterTypes) {
      builder.append(renderGenericType(t));
      builder.append(",");
    }
    return builder.substring(0, builder.length() - 1) + ")";
  }

  protected String parse(List<Type> types) {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    for (int i = 0; i < types.size(); i++) {
      builder.append(renderStandardType(types.get(i)));
      if (i < types.size() - 1) {
        builder.append(",");
      }
    }
    builder.append(")");
    return builder.toString();
  }

  protected String getAccessModifier(int m) {
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

  protected String getStaticAbstractModifier(int m) {
    if (soot.Modifier.isAbstract(m)) {
      return "{abstract} ";
    }
    if (soot.Modifier.isStatic(m)) {
      return "{static} ";
    }
    return "";
  }

  protected String renderGenericType(GenericType type) {
    StringBuilder builder = new StringBuilder();
    builder.append(trimQualifiedName(type.getContainerType()));
    List<GenericType> elementTypes = type.getElementTypes();
    if (!elementTypes.isEmpty()) {
      builder.append("<");
      for (int i = 0; i < elementTypes.size(); ++i) {
        builder.append(renderGenericType(elementTypes.get(i)));

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

  protected String renderStandardType(Type t) {
    return trimQualifiedName(t.toQuotedString());
  }

  protected String trimQualifiedName(String s) {
    String[] parts = s.split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }
}
