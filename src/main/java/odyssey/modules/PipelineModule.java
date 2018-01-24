package odyssey.modules;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.analyzers.Analyzer;
import odyssey.app.Processor;
import odyssey.methodresolution.Algorithm;
import odyssey.renderers.PatternRenderer;

public class PipelineModule extends AbstractModule {

  protected void configure() {
    bind(Processor.class).to(Processor.class);
  }

  @Provides
  @Named("analyzers")
  List<Analyzer> getUserAnalyzers() {
    return null;

  }

  @Provides
  @Named("Resolution")
  Algorithm getAlgorithm() {
    return null;

  }

  @Provides
  @Named("pipeline")
  List<Analyzer> getPipeline(@Named("analyzers") Queue<Analyzer> userAnalyzers) {
    return null;

  }

  @Provides
  @Named("renderers")
  Map<String, Class<? extends PatternRenderer>> getRenderers() {
    return null;
  }

}
