package odyssey.renderers;

public class DIPClassRenderer extends ClassRenderer {
  @Override
  protected String renderClassName() {
    return super.renderClassName() + " #green";
  }

  @Override
  public String getName() {
    return "dip";
  }
}
