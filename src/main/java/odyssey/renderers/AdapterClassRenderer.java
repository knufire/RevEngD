package odyssey.renderers;

public class AdapterClassRenderer extends ClassRenderer {
  
  public String renderStyle() {
    StringBuilder builder = new StringBuilder();
    builder.append("skinparam class {" + System.lineSeparator() + "BackgroundColor<<adapter>> DarkRed"
        + System.lineSeparator() + "}");
    builder.append(System.lineSeparator());
    builder.append("skinparam class {" + System.lineSeparator() + "BackgroundColor<<adaptee>> DarkRed"
        + System.lineSeparator() + "}");
    builder.append(System.lineSeparator());
    builder.append("skinparam class {" + System.lineSeparator() + "BackgroundColor<<target>> DarkRed"
        + System.lineSeparator() + "}");
    return builder.toString();
  }
  
  @Override
  protected String renderClassName() {
    String className = clazz.getName();
    String key = pattern.getKey(clazz);
    return super.renderClassName() + " <<" + key + ">>";
  }

  @Override
  public String getName() {
    return "adapter";
  }
}
