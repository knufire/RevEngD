package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;

/**
 * Does nothing. Used for when no analyzers are needed for a step in the
 * pipeline
 * 
 * @author moorect
 *
 */
public class EmptyAnalyzer extends Analyzer {

	public EmptyAnalyzer(Configuration configuration, List<Filter> filters) {
		super(configuration, filters);
	}

	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		return bundle;
	}

}
