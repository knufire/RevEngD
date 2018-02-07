package odyssey.renderers;

public class InheritanceOverCompositionRelationshipRenderer extends RelationshipRenderer{
  
  @Override
  public String getName() {
    return "IoverC";
  }
  
  @Override
  protected String renderArrowStyle() {
    return "[#orange]";
  }

}
