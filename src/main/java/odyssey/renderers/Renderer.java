package odyssey.renderers;

import odyssey.models.Pattern;

public interface Renderer<T> {
  public String render(T t);
  public String render(T t, Pattern patter);
}
