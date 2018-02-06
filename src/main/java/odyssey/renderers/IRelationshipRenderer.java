package odyssey.renderers;

import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.SootClass;

public interface IRelationshipRenderer extends Renderer<Relationship> {
  public String renderLeft(SootClass clazz);
  public String renderRight(SootClass clazz);
  public String renderArrow(Relation relation, int cardinality);

}
