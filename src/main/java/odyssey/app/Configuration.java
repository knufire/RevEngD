package odyssey.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import csse374.revengd.soot.SceneBuilder;

public class Configuration {
	public final Logger logger = LogManager.getLogger(SceneBuilder.class.getName());
	public List<String> classNames;
	public String mainClassName;
	public Path projectDirectory;
	public boolean parseAncestors;
	public String accessModifier;
	public boolean display;
	public Path imageLocation;
	public boolean includeObject;
	public boolean expandJDK;
	public String entryMethodName;
	public int maxCallDepth;

	public Configuration() {
		classNames = Collections.emptyList();
		mainClassName = "";
		projectDirectory = Paths.get("");
		parseAncestors = false;
		accessModifier = "private";
		display = true;
		imageLocation = Paths.get(System.getProperty("user.dir"), "build", "plantuml", "diagram.svg");
		includeObject = false;
		
		//Sequence Diagrams
		expandJDK = false;
		entryMethodName = "";
		maxCallDepth = 5;
	}

}
