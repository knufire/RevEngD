package odyssey.renderers;

public class DIPRelationshipRenderer extends RelationshipRenderer {
  @Override
  public String getName() {
    return "dip";
  }
  
  @Override
  protected String renderArrowStyle() {
    return "[#green]";
  }
}
