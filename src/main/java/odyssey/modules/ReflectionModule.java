package odyssey.modules;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.analyzers.Analyzer;
import odyssey.filters.Filter;
import odyssey.methodresolution.Algorithm;
import odyssey.renderers.PatternRenderer;

public class ReflectionModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  @Provides
  @Named("analyzers")
  Queue<Analyzer> getUserAnalyzers() {
    try {
      Queue<Analyzer> que = new LinkedList<>();
      List<Filter> filters = new ArrayList<>();

      populateUserAnalyzers(que, filters);

      return que;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new LinkedList<>();
  }

  private void populateUserAnalyzers(Queue<Analyzer> que, List<Filter> filters)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException, ClassNotFoundException {
    String[] analyzerNames = System.getProperty("-analyzers").split(" ");
    for (int i = 0; i < analyzerNames.length; i++) {
      que.add((Analyzer) Class.forName(analyzerNames[i]).getConstructor(List.class).newInstance(filters));
    }
  }

  @Provides
  @Named("Resolution")
  Algorithm getAlgorithm() {
    return null;

  }

  @Provides
  @Named("renderers")
  Map<String, PatternRenderer> getRenderers() {
    Map<String, PatternRenderer> map = new HashMap<>();

    try {
      loadRenderers(map);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return map;
  }

  private void loadRenderers(Map<String, PatternRenderer> map) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    String[] rendererNames = System.getProperty("-renderers").split(" ");
    PatternRenderer renderer = null;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (PatternRenderer) Class.forName(rendererNames[i]).newInstance();
      map.put(renderer.getName(), renderer);
    }
  }

}
