package odyssey.app;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;

public class Processor {

  private List<Analyzer> pipeline;
  private AnalyzerBundle bundle;

  
  @Inject
  Processor(@Named("pipeline") List<Analyzer> pipeline, AnalyzerBundle bundle) {
      this.pipeline = pipeline;
      this.bundle = bundle;
  }
  
  
  public AnalyzerBundle executePipeline() {
    for (int i = 0; i < pipeline.size(); i++) {
      this.bundle = pipeline.get(i).execute(bundle);
    }
    return bundle;
  }



}
