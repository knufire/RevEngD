package odyssey.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.SingletonAnalyzer;
import odyssey.methodresolution.Algorithm;
import odyssey.renderers.PatternRenderer;
import odyssey.renderers.SingletonRenderer;

public class ReflectionModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  // TODO: Actually implement these.

  @Provides
  @Named("analyzers")
  Queue<Analyzer> getUserAnalyzers() {
    Queue<Analyzer> que = new LinkedList<>();
    que.add(new SingletonAnalyzer(Collections.emptyList()));
    return que;

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
    map.put("singleton", new SingletonRenderer());
    return map;
  }

}
