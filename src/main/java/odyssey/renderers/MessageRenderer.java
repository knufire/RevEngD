package odyssey.renderers;

import java.util.Collection;
import java.util.List;

import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import odyssey.models.Message;
import odyssey.models.Pattern;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.Tag;

public class MessageRenderer implements IMessageRenderer {
  protected Message message;
  protected Pattern pattern;

  @Override
  public final String render(Message t) {
    this.message = t;
    StringBuilder builder = new StringBuilder();
    
    builder.append(renderFromClass(t.getMethodCallerClass()));
    builder.append(" ");
    builder.append(renderArrow());
    builder.append(" ");
    builder.append(renderToClass(t.getReceiverClass()));
    builder.append(" : ");
    builder.append(renderMessage(t.getMethod()));
    
    return builder.toString();
  }

  @Override
  public final String render(Message t, Pattern pattern) {
    this.pattern = pattern;
    return render(t);
  }

  
  
  @Override
  public String renderFromClass(SootClass clazz) {
    return clazz.getShortName().replaceAll("\\$", "");
  }

  @Override
  public String renderToClass(SootClass clazz) {
    return clazz.getShortName().replaceAll("\\$", "");
  }

  @Override
  public String renderArrow() {
    return "->";
  }

  @Override
  public String renderMessage(SootMethod method) {
    if (message.getMethodCallerClass().equals(message.getReceiverClass())) {
      return method.getName() + parseMethodParameters(method);
    }
    return method.getName() + parseMethodParameters(method) + System.lineSeparator() + "activate " + method.getDeclaringClass().getShortName();
  }
  
  @Override
  public String getName() {
    return "default";
  }
  
  protected String parseReturnType(SootMethod m) {
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
  
  protected String parse(GenericType type) {
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
  
  protected String parse(Type t) {
    return trimQualifiedName(t.toQuotedString());
  }
  
  protected String trimQualifiedName(String s) {
    String[] parts = s.split("\\.");
    return parts[parts.length - 1];
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
      builder.append(parse(t));
      builder.append(",");
    }
    return builder.substring(0, builder.length() - 1) + ")";
  }

  protected String parse(List<Type> types) {
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

}
