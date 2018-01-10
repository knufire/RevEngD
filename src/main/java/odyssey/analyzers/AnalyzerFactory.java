package odyssey.analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.ClassNameFilter;
import odyssey.filters.ClinitFilter;
import odyssey.filters.DollarSignFilter;
import odyssey.filters.Filter;
import odyssey.filters.PackagePrivateFilter;
import odyssey.filters.ProtectedFilter;
import odyssey.filters.PublicFilter;
import odyssey.filters.RelationshipFilter;

public class AnalyzerFactory {

	private Configuration config;
	private AnalyzerBundle bundle;

	public AnalyzerFactory(Configuration config, AnalyzerBundle bundle) {
		this.config = config;
		this.bundle = bundle;
	}

	public Analyzer createSceneAnalyzer() {
		return new SceneAnalyzer(config, Collections.emptyList());
	}

	public Analyzer createSootAnalyzer() {
		List<Filter> sootAnalyzerFilters = new ArrayList<Filter>();
		sootAnalyzerFilters.add(new ClassNameFilter(config));
		return new SootAnalyzer(config, sootAnalyzerFilters);
	}

	public Analyzer createAncestorAnalyzer() {
		if (config.parseAncestors) {
			return new AncestorAnalyzer(config, Collections.emptyList());
		}
		return new EmptyAnalyzer(config, Collections.emptyList());
	}

	public Analyzer createRelationshipAnalyzer() {
		List<Filter> relationShipFilters = new ArrayList<Filter>();
		relationShipFilters.add(new DollarSignFilter());
		relationShipFilters.add(new RelationshipFilter(this.bundle));
		return new InheritanceAnalyzer(config, relationShipFilters);
	}

	public Analyzer createUMLAnalyzer() {
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
		default: //"private" by default, therefore no filter
			return;
		}
		
	}

}
