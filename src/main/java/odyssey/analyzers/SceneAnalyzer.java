package odyssey.analyzers;

import java.util.Arrays;
import java.util.List;

import csse374.revengd.soot.MainMethodMatcher;
import csse374.revengd.soot.SceneBuilder;
import odyssey.app.Configuration;
import odyssey.filters.Filter;

public class SceneAnalyzer extends Analyzer {

  public SceneAnalyzer(Configuration configuration, List<Filter> filters) {
    super(configuration, filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    SceneBuilder sceneBuilder = SceneBuilder.create();

    sceneBuilder = sceneBuilder.addDirectory(config.directory.toFile().getAbsolutePath());

    sceneBuilder = sceneBuilder.setEntryClass(config.mainClassName);
    sceneBuilder = sceneBuilder.addEntryPointMatcher(new MainMethodMatcher(config.mainClassName));

    sceneBuilder = sceneBuilder.addExclusions(Arrays.asList("java.*", "javax.*", "sun.*"))
        .addExclusions(Arrays.asList("soot.*", "polygot.*")).addExclusions(Arrays.asList("org.*", "com.*"));

    sceneBuilder = sceneBuilder.addClasses(config.classNames);
    
    bundle.scene = sceneBuilder.build();

    return bundle;
  }

}
