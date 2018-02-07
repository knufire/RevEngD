package odyssey.renderers;

import odyssey.models.Relation;

public class SingletonRelationshipRenderer extends RelationshipRenderer {
  
  @Override
  public String getName() {
    return "singleton";
  }
  
  @Override
  protected String parseArrow(Relation relation) {
    if (relation == Relation.ASSOCIATION) return "<-[#blue]-";
    return super.parseArrow(relation);
  }
  
  @Override
  protected String parseBackwardsArrow(Relation relation) {
    if (relation == Relation.ASSOCIATION) return "-[#blue]->";
    return super.parseArrow(relation);
  }

}
