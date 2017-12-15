package odyssey.app;

import java.util.ArrayList;
import java.util.List;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;
import odyssey.analyzers.AnalyzerFactory;

public class Processor {

	private List<Analyzer> pipeline;
	private AnalyzerBundle bundle;
	private AnalyzerFactory factory;

	Processor(AnalyzerBundle bundle, AnalyzerFactory analyzerFactory) {
		this.bundle = bundle;
		this.factory = analyzerFactory;
		createPipeline();
	}

	private void createPipeline() {
		pipeline = new ArrayList<>();
		pipeline.add(factory.createSceneAnalyzer());
		pipeline.add(factory.createSootAnalyzer());
		pipeline.add(factory.createAncestorAnalyzer());
		pipeline.add(factory.createRelationshipAnalyzer());
		pipeline.add(factory.createUMLAnalyzer());
	}

	public AnalyzerBundle executePipeline() {
		for (int i = 0; i < pipeline.size(); i++) {
			this.bundle = pipeline.get(i).execute(bundle);
		}
		return bundle;
	}

	public static Processor getProcessor(Configuration config) {
		AnalyzerBundle bundle = new AnalyzerBundle();
		return new Processor(bundle, new AnalyzerFactory(config, bundle));
	}

}
