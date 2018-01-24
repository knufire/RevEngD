package odyssey.analyzers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import odyssey.filters.Filter;
import soot.SootClass;
import soot.util.Chain;

public class AncestorAnalyzer extends Analyzer {
	
	private Set<SootClass> processedClasses;

	public AncestorAnalyzer(List<Filter> filters) {
		super(filters);
		this.processedClasses = new HashSet<>();
	}

	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		// ensures base case, Object has no superClass
		SootClass objectClass = bundle.scene.getSootClass("java.lang.Object");
		processedClasses.add(objectClass);
		for (SootClass c: bundle.classes) {
			if (passesFilters(c)) {
				ancestorHelper(c);
			}
		}
		
		boolean includeObject = Boolean.parseBoolean(System.getProperty("--include-object"));
		// This is to remove the java.lang.Object from the UML, set in the configuration
		if (!includeObject) {
			processedClasses.remove(objectClass);
		}
		
		List<SootClass> newClasses = new ArrayList<>(processedClasses);
		bundle.classes = newClasses;
		return bundle;
	}
	
	private void ancestorHelper(SootClass clazz) {
		if (processedClasses.contains(clazz)) {
			return;
		}
		processedClasses.add(clazz);
		
		Chain<SootClass> interfaces = clazz.getInterfaces();
		for (SootClass i: interfaces) {
			ancestorHelper(i);
		}
		if (clazz.hasSuperclass()) {
			SootClass superClass = clazz.getSuperclass();
			ancestorHelper(superClass);
		}
	}

}
