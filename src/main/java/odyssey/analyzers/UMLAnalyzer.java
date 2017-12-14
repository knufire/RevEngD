package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.app.Relation;
import odyssey.app.Relationship;
import odyssey.filters.Filter;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;

public class UMLAnalyzer extends Analyzer {
	
	UMLParser parser; 

	public UMLAnalyzer(Configuration configuration, List<Filter> filters, UMLParser parser) {
		super(configuration, filters);
		this.parser = parser;
	}
	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		//TODO: Probably want to do something other than printing to sysout.
		StringBuilder builder = new StringBuilder();
		for (SootClass c : bundle.classes) {
			if (passesFilters(c)) {
				builder.append(parser.parse(c));
			}
			for (SootField f : c.getFields()) {
				if (passesFilters(f)) {
					builder.append("  ");
					builder.append(parser.parse(f));
					builder.append("\n");
				}
			}
			for (SootMethod m : c.getMethods()) {
				if (passesFilters(m)) {
					builder.append("  ");
					builder.append(parser.parse(m));
					builder.append("\n");
				}
			}
			builder.append("}\n");
		}
		for (Relationship r : bundle.relationships) {
			System.out.println(parser.parse(r));
		}
		
		return bundle;
	}
}
