package odyssey.modules;

import java.lang.reflect.Constructor;
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
import odyssey.methodresolution.AggregateAlgorithm;
import odyssey.methodresolution.AggregationStrategy;
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

  @SuppressWarnings("unchecked")
  private void populateUserAnalyzers(Queue<Analyzer> que, List<Filter> filters)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException, ClassNotFoundException {
    String[] analyzerNames = System.getProperty("-analyzers").split(" ");
    System.out.println(analyzerNames[0]);
    Class<? extends Analyzer> analyzerClass;
    Constructor<? extends Analyzer> constructor;

    for (int i = 0; i < analyzerNames.length; i++) {
      analyzerClass = (Class<? extends Analyzer>) Class.forName(analyzerNames[i]);
      constructor = analyzerClass.getDeclaredConstructor(List.class);
      constructor.setAccessible(true);
      que.add(constructor.newInstance(filters));
    }
  }

  @Provides
  @Named("Resolution")
  Algorithm getAlgorithm() {
    try {
      return createAlgorithm();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private Algorithm createAlgorithm() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    String[] algorithms = System.getProperty("-mra").split(" ");
    if (algorithms.length == 1) {
      return getClassFromName(Algorithm.class, algorithms[0]);
    } else {
      Class<AggregateAlgorithm> aggregate = AggregateAlgorithm.class;
      List<Algorithm> singleAlgorithms = new ArrayList<>();
      for (int i = 0; i < algorithms.length; i++) {
        singleAlgorithms.add(getClassFromName(Algorithm.class, algorithms[i]));
      }
      AggregationStrategy strat = getClassFromName(AggregationStrategy.class, System.getProperty("-mrs"));
      Constructor<AggregateAlgorithm> constructor = aggregate.getDeclaredConstructor(AggregationStrategy.class);
      AggregateAlgorithm aggregateAlgorithm = constructor.newInstance(strat);
      for (Algorithm a : singleAlgorithms) {
        aggregateAlgorithm.addAlgorithm(a);
      }
      return aggregateAlgorithm;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T getClassFromName(Class<T> clazz, String name) {
    try {
      Class<T> algorithm = (Class<T>) Class.forName(name);
      return algorithm.newInstance();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException
        | ClassNotFoundException e) {
      System.err.println("Could not instantiate method resolver algorithm class.");
      e.printStackTrace();
      return null;
    }
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

  private void loadRenderers(Map<String, PatternRenderer> map)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    String[] rendererNames = System.getProperty("-renderers").split(" ");
    PatternRenderer renderer = null;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (PatternRenderer) Class.forName(rendererNames[i]).newInstance();
      map.put(renderer.getName(), renderer);
    }
  }

}
