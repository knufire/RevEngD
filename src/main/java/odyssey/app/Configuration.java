package odyssey.app;

import java.nio.file.Path;
import java.util.List;

public class Configuration {
	public List<String> classNames; 
	public Path directory; 
	public boolean parseAncestors;
	public int accessModifier;
	public boolean display;

	public Configuration(List<String> classNames, Path directory, boolean parseAncestors, int accessModifier,
			boolean display) {
		this.classNames = classNames;
		this.directory = directory;
		this.parseAncestors = parseAncestors;
		this.accessModifier = accessModifier;
		this.display = display;
	}
	
	
}
