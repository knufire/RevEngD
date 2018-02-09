package odyssey.renderers;

public class InheritanceOverCompositionClassRenderer extends ClassRenderer {

  @Override
  protected String renderClassName() {
    return super.renderClassName() + " #orange";
  }

  @Override
  public String getName() {
    return "IoverC";
  }

}
