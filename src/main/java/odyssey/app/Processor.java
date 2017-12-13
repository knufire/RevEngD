package odyssey.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;
import odyssey.analyzers.RelationshipAnalyzer;
import odyssey.analyzers.SceneAnalyzer;
import odyssey.analyzers.SootAnalyzer;
import odyssey.filters.Filter;
import odyssey.filters.RelationshipFilter;

public class Processor {

	private Configuration config;
	private List<Analyzer> pipeline;
	private AnalyzerBundle bundle;

	Processor(Configuration config) {
		this.config = config;
		this.bundle = new AnalyzerBundle();
		createPipeline();
	}

	private void createPipeline() {
    // TODO: Create the pipeline
    pipeline = new ArrayList<>();
    pipeline.add(new SceneAnalyzer(config, Collections.emptyList()));
    pipeline.add(new SootAnalyzer(config, Collections.emptyList()));
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    pipeline.add(new RelationshipAnalyzer(config, relationShipFilters));
  }

	public AnalyzerBundle executePipeline() {
		for (int i = 0; i < pipeline.size(); i++) {
			this.bundle = pipeline.get(i).execute(bundle);
		}
		return bundle;
	}

	public static Processor getProcessor(Configuration config) {
		return new Processor(config);
	}

}
