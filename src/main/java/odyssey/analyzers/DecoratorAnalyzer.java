package odyssey.analyzers;

import java.util.List;

import odyssey.filters.Filter;
import odyssey.models.Pattern;
import soot.SootClass;

public class DecoratorAnalyzer extends Analyzer {

  protected DecoratorAnalyzer(List<Filter> filters) {
    super(filters);
  }

  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    List<Pattern> patterns = bundle.getList("patterns", Pattern.class);
    List<SootClass> classes = bundle.getList("classes", SootClass.class);
    addClassesToPatterns(classes, patterns);
    return bundle;
  }

  private void addClassesToPatterns(List<SootClass> classes, List<Pattern> patterns) {
    classes.forEach(c -> {
      addClassToPattern(c, patterns);
    });
  }

  private void addClassToPattern(SootClass c, List<Pattern> patterns) {
    if (!containsFieldOfItself(c)) {
      return;
    }
    // Decorator
    if (isBadDecorator(c)) {
      patterns.add(createBadDecoratorPattern(c));
    } else {
      patterns.add(createRegularDecoratorPattern(c));
    }
  }

  private boolean containsFieldOfItself(SootClass c) {
    return false;
  }

  private boolean isBadDecorator(SootClass c) {
    return false;
  }

  private Pattern createBadDecoratorPattern(SootClass c) {
    return null;
  }

  private Pattern createRegularDecoratorPattern(SootClass c) {
    return null;
  }
}
