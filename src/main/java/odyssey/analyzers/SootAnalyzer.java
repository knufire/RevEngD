package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;

public class SootAnalyzer extends Analyzer{

  public SootAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
    
    
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    return bundle;
  }

}
