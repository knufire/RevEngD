package odyssey.analyzers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.util.Chain;

public class AncestorAnalyzer extends Analyzer {
	
	private Set<SootClass> processedClasses;

	public AncestorAnalyzer(Configuration configuration, List<Filter> filters) {
		super(configuration, filters);
		this.processedClasses = new HashSet<>();
	}

	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		// ensures base case, Object has no superClass
		processedClasses.add(bundle.scene.getSootClass("java.lang.Object"));
		for (SootClass c: bundle.classes) {
			if (passesFilters(c)) {
				ancestorHelper(c);
			}
		}
		List<SootClass> newClasses = new ArrayList<>(processedClasses);
		bundle.classes = newClasses;
		return bundle;
	}
	
	public void ancestorHelper(SootClass clazz) {
		if (processedClasses.contains(clazz)) {
			return;
		}
		processedClasses.add(clazz);
		
		Chain<SootClass> interfaces = clazz.getInterfaces();
		for (SootClass i: interfaces) {
			ancestorHelper(i);
		}
		SootClass superClass = clazz.getSuperclass();
		ancestorHelper(superClass);
	}

}
