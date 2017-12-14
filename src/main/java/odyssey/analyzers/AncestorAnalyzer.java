package odyssey.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.SootClass;

public class AncestorAnalyzer extends Analyzer {
	
	private Set<SootClass> processedClasses;

	public AncestorAnalyzer(Configuration configuration, List<Filter> filters) {
		super(configuration, filters);
		this.processedClasses = new HashSet<>();
	}

	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		for (SootClass c: bundle.classes) {
			if (passesFilters(c)) {
				ancestorHelper(c, bundle);
			}
		}
		return bundle;
	}
	
	public void ancestorHelper(SootClass clazz, AnalyzerBundle bundle) {
		if (processedClasses.contains(clazz)) {
			
		}
	}

}
