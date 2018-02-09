package odyssey.renderers;

public class DIPClassRenderer extends ClassRenderer {
  @Override
  protected String renderClassName() {
    if (pattern.getKey(clazz).equals("from")) {
      return super.renderClassName() + " #green";
    }
    return super.renderClassName();
  }

  @Override
  public String getName() {
    return "dip";
  }
}
