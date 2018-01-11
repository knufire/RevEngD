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
	public Path umlImageLocation;
	public boolean includeObject;
	public boolean expandJDK;
	public String entryMethodName;
	public int maxCallDepth;
  public Path seqImageLocation;
  public boolean showInheritance;
  public boolean showDependence;
  public boolean showAssociation;

	public Configuration() {
		classNames = Collections.emptyList();
		mainClassName = "";
		projectDirectory = Paths.get("");
		parseAncestors = false;
		accessModifier = "private";
		display = true;
		umlImageLocation = Paths.get(System.getProperty("user.dir"), "build", "plantuml", "uml.svg");
		includeObject = false;
		
		showInheritance = false;
		showDependence = false;
		showAssociation = false;
		
		//Sequence Diagrams
		expandJDK = false;
		entryMethodName = "";
		maxCallDepth = 5;
		seqImageLocation = Paths.get(System.getProperty("user.dir"), "build", "plantuml", "sequence.svg");
	}

}
