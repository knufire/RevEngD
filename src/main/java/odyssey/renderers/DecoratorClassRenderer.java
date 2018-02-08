package odyssey.renderers;

public class DecoratorClassRenderer extends ClassRenderer {
  public String getName() {
    return "decorator";
  }

  public String renderStyle() {
    StringBuilder builder = new StringBuilder();
    builder.append("skinparam class {" + System.lineSeparator() + "BackgroundColor<<Decorator>> PaleGreen"
        + System.lineSeparator() + "}");
    builder.append(System.lineSeparator());
    builder.append("skinparam class {" + System.lineSeparator() + "BackgroundColor<<Component>> PaleGreen"
        + System.lineSeparator() + "}");
    builder.append(System.lineSeparator());
    builder.append("skinparam class {" + System.lineSeparator() + "BackgroundColor<<BadDecorator>> PaleGreen"
        + System.lineSeparator() + "}");
    return builder.toString();
  }

  protected String renderClassName() {
    String className = clazz.getName();
    String key = pattern.getKey(clazz);
    if (key.equals("decorator")) {
      return className + " <<Decorator>>";
    }
    if (key.equals("badDecorator")) {
      return className + " <<Bad Decorator>>";
    }
    return className + " <<Component>>";
  }

  protected String renderMethods() {
    System.err.println(pattern.getKey(clazz));
    if (pattern.getKey(clazz).equals("badDecorator")) {
      StringBuilder builder = new StringBuilder();
      
      clazz.getMethods().forEach(m -> {
        builder.append(renderMethod(m));
        builder.append(System.lineSeparator());
      });
      
      pattern.getMethods(clazz.getName() + " badMethod").forEach(m -> {
        builder.append(getAccessModifier(m.getModifiers()));
        builder.append("<font color=”#FF0000”>");
        builder.append(getStaticAbstractModifier(m.getModifiers()));
        builder.append(parseReturnType(m));
        builder.append(" ");
        builder.append(parseMethodName(m));
        builder.append(parseMethodParameters(m));
        builder.append("</font>");
        builder.append("\n");
      });
      return builder.toString();
    }
    return super.renderMethods();
  }
}
