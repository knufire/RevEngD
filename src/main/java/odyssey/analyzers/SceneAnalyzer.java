package odyssey.analyzers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import csse374.revengd.soot.MainMethodMatcher;
import csse374.revengd.soot.SceneBuilder;
import odyssey.filters.Filter;

public class SceneAnalyzer extends Analyzer {

	public SceneAnalyzer(List<Filter> filters) {
		super(filters);
	}

	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		SceneBuilder sceneBuilder = SceneBuilder.create();
		Path p = Paths.get(System.getProperty("-d"));
		sceneBuilder = sceneBuilder.addDirectory(p.toFile().getAbsolutePath());
		
		String mainClassName = System.getProperty("-m");
		sceneBuilder = sceneBuilder.setEntryClass(mainClassName);
		
		sceneBuilder = sceneBuilder.addEntryPointMatcher(new MainMethodMatcher(mainClassName));

		sceneBuilder = sceneBuilder.addExclusions(Arrays.asList("java.*", "javax.*", "sun.*"))
				.addExclusions(Arrays.asList("soot.*", "polygot.*")).addExclusions(Arrays.asList("org.*", "com.*"));
		
		List<String> classNames = Arrays.asList(System.getProperty("-c").split(" "));
		sceneBuilder = sceneBuilder.addClasses(classNames);

		bundle.scene = sceneBuilder.build();

		return bundle;
	}

}
