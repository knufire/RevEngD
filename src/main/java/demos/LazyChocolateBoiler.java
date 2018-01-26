package demos;

public class LazyChocolateBoiler {

  private static LazyChocolateBoiler boiler = null;

  private LazyChocolateBoiler() {

  }

  public static LazyChocolateBoiler getInstance() {
    if (boiler == null) {
      synchronized (LazyChocolateBoiler.class) {
        if (boiler == null) {
          boiler = new LazyChocolateBoiler();
        }
      }
    }
    return boiler;
  }
}
