package odyssey.renderers;

import soot.SootClass;

public class AdapterRelationshipRenderer extends RelationshipRenderer {
  public String getName() {
    return "decorator";
  }

  public String renderRight(SootClass clazz) {
    String key = pattern.getKey(relationship);
    if (key.equals("adapts")) {
      return super.renderRight(clazz) + " : <<adapts>>";      
    }
    return super.renderRight(clazz);
  }

  protected String renderArrowStyle() {
    return "[#DarkRed]";
  }
}
