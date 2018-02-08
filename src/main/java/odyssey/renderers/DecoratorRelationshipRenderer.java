package odyssey.renderers;

import soot.SootClass;

public class DecoratorRelationshipRenderer extends RelationshipRenderer {
  public String getName() {
    return "decorator";
  }

  public String renderRight(SootClass clazz) {

    return super.renderRight(clazz) + " : <<Decorates>>";
  }

  protected String renderArrowStyle() {
    return "[#Green]";
  }
}
