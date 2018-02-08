package odyssey.renderers;

public class InheritanceOverCompositionClassRenderer extends ClassRenderer {
  
  @Override
  protected String renderClassName() {
    if (pattern.getKey(clazz).equals("from"))
      return super.renderClassName() + " #orange";
    return super.renderClassName();
  }
  
  @Override
  public String getName() {
    return "IoverC";
  }

}
