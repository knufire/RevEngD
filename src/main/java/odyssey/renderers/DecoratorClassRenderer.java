package odyssey.renderers;

public class DecoratorClassRenderer extends ClassRenderer {
  public String getName() {
    return "decorator";
  }

  public String renderStyle() {
    StringBuilder builder = new StringBuilder();
    builder.append(
        "skinparam class {" + System.lineSeparator() + "BackgroundColor<<Decorator>> PaleGreen" + System.lineSeparator() + "}");
    builder.append(System.lineSeparator());
    builder.append(
        "skinparam class {" + System.lineSeparator() + "BackgroundColor<<Component>> PaleGreen" + System.lineSeparator() + "}");
    return builder.toString();
  }

  protected String renderClassName() {
    String className = clazz.getName();
    String key = pattern.getKey(clazz);
    if (key.equals("decorator")) {
      return className + " <<Decorator>>";
    }
    if (key.equals("badDecorator")) {
      return className + " <<Bad Decorator>";
    }
    return className + " <<Component>>";
  }
}
