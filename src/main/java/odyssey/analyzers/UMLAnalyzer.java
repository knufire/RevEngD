package odyssey.analyzers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import odyssey.app.Configuration;
import odyssey.filters.Filter;
import odyssey.models.Relationship;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class UMLAnalyzer extends Analyzer {

	public UMLAnalyzer(Configuration configuration, List<Filter> filters) {
		super(configuration, filters);
	}

	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		generateUMLImage(parse(bundle));
		return bundle;
	}

	private String parse(AnalyzerBundle bundle) {
		StringBuilder builder = new StringBuilder();
		builder.append("@startuml\n");
		for (SootClass c : bundle.classes) {
			if (passesFilters(c)) {
				builder.append(UMLParser.parse(c));
				builder.append("\n");
				for (SootField f : c.getFields()) {
					if (passesFilters(f)) {
						builder.append("  ");
						builder.append(UMLParser.parse(f));
						builder.append("\n");
					}
				}
				for (SootMethod m : c.getMethods()) {
					if (passesFilters(m)) {
						builder.append("  ");
						builder.append(UMLParser.parse(m));
						builder.append("\n");
					}
				}
				builder.append("}\n");
			}
		}
		for (Relationship r : bundle.relationships) {
			builder.append((UMLParser.parse(r)));
			builder.append("\n");
		}
		builder.append("@enduml\n");
		config.logger.log(Level.INFO, "Generated UML\n" + builder.toString());
		return builder.toString();
	}

	private void generateUMLImage(String umlString) {
		SourceStringReader reader = new SourceStringReader(umlString);
		try {
			Files.createDirectories(config.umlImageLocation.getParent());

			OutputStream outStream = new FileOutputStream(config.umlImageLocation.toFile());
			FileFormatOption option = new FileFormatOption(FileFormat.SVG, false);
			reader.outputImage(outStream, option);
		} catch (Exception e) {
			config.logger.error("Cannot create file to store the UML diagram.\n" + e);
		}

	}
}
