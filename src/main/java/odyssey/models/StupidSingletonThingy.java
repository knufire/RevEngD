package odyssey.models;

public class StupidSingletonThingy {
  private static StupidSingletonThingy instance;
  
  private StupidSingletonThingy() {

  }
  
  public static StupidSingletonThingy getInstance() {
    return new StupidSingletonThingy();
  }
}
