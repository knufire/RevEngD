package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

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
  
  protected boolean passesFilters(SootClass c) {
    for (Filter filter: filters) {
      if (!filter.shouldProcess(c)) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean passesFilters(SootMethod m) {
    for (Filter filter: filters) {
      if (!filter.shouldProcess(m)) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean passesFilters(SootField f) {
    for (Filter filter: filters) {
      if (!filter.shouldProcess(f)) {
        return false;
      }
    }
    return true;
  }

  
}
