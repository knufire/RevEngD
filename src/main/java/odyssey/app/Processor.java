package odyssey.app;

import java.util.List;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;

public class Processor {

  private Configuration config;
  private List<Analyzer> pipeline;

  Processor(Configuration config) {
    this.config = config;
  }

  private void createPipeline() {
    // TODO: Create the pipeline
  }

  public AnalyzerBundle executePipeline() {
    AnalyzerBundle bundle = new AnalyzerBundle();
    for (Analyzer a : pipeline) {
      bundle = a.execute(bundle);
    }
    return bundle;
  }

}
