package odyssey.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;
import odyssey.analyzers.RelationshipAnalyzer;
import odyssey.analyzers.SceneAnalyzer;
import odyssey.analyzers.SootAnalyzer;

public class Processor {

  private Configuration config;
  private List<Analyzer> pipeline;

  Processor(Configuration config) {
    this.config = config;
    createPipeline();
  }

  private void createPipeline() {
    // TODO: Create the pipeline
    pipeline = new ArrayList<>();
    pipeline.add(new SceneAnalyzer(config, Collections.emptyList()));
    pipeline.add(new SootAnalyzer(config, Collections.emptyList()));
    pipeline.add(new RelationshipAnalyzer(config, Collections.emptyList()));
  }

  public AnalyzerBundle executePipeline() {
    AnalyzerBundle bundle = new AnalyzerBundle();
    for (int i = 0; i < pipeline.size(); i++) {
      bundle = pipeline.get(i).execute(bundle);
    }
    return bundle;
  }

  public static Processor getProcessor(Configuration config) {
    return new Processor(config);
  }

}
