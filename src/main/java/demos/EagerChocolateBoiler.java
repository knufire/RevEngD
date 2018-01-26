package demos;

public class EagerChocolateBoiler {
  private static final EagerChocolateBoiler boiler = new EagerChocolateBoiler();

  private EagerChocolateBoiler() {
  }

  public static EagerChocolateBoiler getInstance() {
    return boiler;
  }

}
