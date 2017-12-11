package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;

public abstract class Analyzer {

  List<Filter> filters;
  Configuration config;
  AnalyzerBundle bundle;

  public Analyzer(Configuration configuration, List<Filter> filters){
    this.config = configuration;
    this.filters = filters;
  }
  
  public abstract AnalyzerBundle execute(AnalyzerBundle bundle);

  public void addFilter(Filter filter) {
    filters.add(filter);
  }

  
}
