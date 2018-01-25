package odyssey.modules;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.analyzers.Analyzer;
import odyssey.methodresolution.Algorithm;
import odyssey.renderers.PatternRenderer;

public class ReflectionModule extends AbstractModule {

  @Override
  protected void configure() {
  }
  
  //TODO: Actually implement these. 
  
  @Provides
  @Named("analyzers")
  Queue<Analyzer> getUserAnalyzers() {
    return new LinkedList<>();

  }

  @Provides
  @Named("Resolution")
  Algorithm getAlgorithm() {
    return null;

  }
  
  @Provides
  @Named("renderers")
  Map<String, Class<? extends PatternRenderer>> getRenderers() {
    return null;
  }

}
