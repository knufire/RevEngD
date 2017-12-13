package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

public class RelationshipAnalyzer extends Analyzer {

	public RelationshipAnalyzer(Configuration configuration, List<Filter> filters) {
		super(configuration, filters);
	}

	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		//TODO: Get the classes, look through the classes, build relationships
		for (SootClass c : bundle.classes) {
			Chain<SootField> fields = c.getFields();
			for (SootField f : fields) {
				//TODO: Figure out type the field is.
			}
		}
		
		return bundle;
	}

}
