package odyssey.renderers;

public class SingletonClassRenderer extends ClassRenderer {

  @Override
  public String renderStyle() {
    return "skinparam class {\n borderColor<<Singleton>> blue\n}";
  }
  
  @Override
  protected String renderClassName() {
    return super.renderClassName() + " <<Singleton>>";
  }
  
  @Override
  public String getName() {
    return "singleton";
  }
  
}
