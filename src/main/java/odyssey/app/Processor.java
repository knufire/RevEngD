package odyssey.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;
import odyssey.analyzers.AncestorAnalyzer;
import odyssey.analyzers.AssociationAnalyzer;
import odyssey.analyzers.DependencyAnalyzer;
import odyssey.analyzers.EmptyAnalyzer;
import odyssey.analyzers.InheritanceAnalyzer;
import odyssey.analyzers.SceneAnalyzer;
import odyssey.analyzers.SootAnalyzer;
import odyssey.analyzers.UMLAnalyzer;
import odyssey.analyzers.UMLParser;
import odyssey.filters.ClassNameFilter;
import odyssey.filters.ClinitFilter;
import odyssey.filters.DollarSignFilter;
import odyssey.filters.Filter;
import odyssey.filters.PackagePrivateFilter;
import odyssey.filters.ProtectedFilter;
import odyssey.filters.PublicFilter;
import odyssey.filters.RelationshipFilter;

public class Processor {

	private List<Analyzer> pipeline;
	private AnalyzerBundle bundle;
	private Configuration config;

	Processor(AnalyzerBundle bundle, Configuration config) {
		this.bundle = bundle;
		this.config = config;
		createPipeline();
	}

	private void createPipeline() {
		pipeline = new ArrayList<>();
		pipeline.add(createSceneAnalyzer());
		pipeline.add(createSootAnalyzer());
		pipeline.add(createAncestorAnalyzer());
		pipeline.add(createInheritanceAnalyzer());
		pipeline.add(createAssociationAnalyzer());
		pipeline.add(createDependencyAnalyzer());
		pipeline.add(createUMLAnalyzer());
	}

	public AnalyzerBundle executePipeline() {
		for (int i = 0; i < pipeline.size(); i++) {
			this.bundle = pipeline.get(i).execute(bundle);
		}
		return bundle;
	}

	public static Processor getProcessor(Configuration config) {
		AnalyzerBundle bundle = new AnalyzerBundle();
		return new Processor(bundle, config);
	}
	
  private Analyzer createSceneAnalyzer() {
    return new SceneAnalyzer(config, Collections.emptyList());
  }

  private Analyzer createSootAnalyzer() {
    List<Filter> sootAnalyzerFilters = new ArrayList<Filter>();
    sootAnalyzerFilters.add(new ClassNameFilter(config));
    return new SootAnalyzer(config, sootAnalyzerFilters);
  }

  private Analyzer createAncestorAnalyzer() {
    if (config.parseAncestors) {
      return new AncestorAnalyzer(config, Collections.emptyList());
    }
    return new EmptyAnalyzer(config, Collections.emptyList());
  }

  private Analyzer createInheritanceAnalyzer() {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    relationShipFilters.add(new DollarSignFilter());
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    return new InheritanceAnalyzer(config, relationShipFilters);
  }
  
  private Analyzer createDependencyAnalyzer() {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    relationShipFilters.add(new DollarSignFilter());
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    return new DependencyAnalyzer(config, relationShipFilters);
  }
  
  private Analyzer createAssociationAnalyzer() {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    relationShipFilters.add(new DollarSignFilter());
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    return new AssociationAnalyzer(config, relationShipFilters);
  }
  
  private Analyzer createUMLAnalyzer() {
    List<Filter> UMLFilters = new ArrayList<Filter>();
    addModifierFilter(UMLFilters);
    UMLFilters.add(new DollarSignFilter());
    UMLFilters.add(new ClinitFilter());
    return new UMLAnalyzer(config, UMLFilters, new UMLParser());
  }

  private void addModifierFilter(List<Filter> filters) {
    String mod = config.accessModifier;
    switch (mod) {
    case "protected":
      filters.add(new ProtectedFilter());
      return;
    case "public":
      filters.add(new PublicFilter());
      return;
    case "package":
      filters.add(new PackagePrivateFilter());
      return;
    default: // "private" by default, therefore no filter
      return;
    }

  }

}
