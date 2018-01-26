package odyssey.analyzers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import odyssey.filters.Filter;
import odyssey.models.Pattern;
import odyssey.models.Relationship;
import odyssey.renderers.PatternRenderer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class UMLAnalyzer extends Analyzer {

  private Path umlImageLocation;
  private List<Pattern> patterns;
  private Map<String, PatternRenderer> renderers;

  public UMLAnalyzer(List<Filter> filters, Map<String, PatternRenderer> renderers) {
    super(filters);
    umlImageLocation = Paths.get(System.getProperty("-i"));
    this.renderers = renderers;
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    patterns = bundle.getList("patterns", Pattern.class);
    generateUMLImage(parse(bundle));
    return bundle;
  }

  private String parse(AnalyzerBundle bundle) {
    StringBuilder builder = new StringBuilder();
    builder.append("@startuml\n");
    builder.append("skinparam linetype ortho\n");
    for (PatternRenderer p : renderers.values()) {
      builder.append(p.getStyle());
      builder.append("\n");
    }
    for (SootClass c : bundle.getList("classes", SootClass.class)) {
      if (passesFilters(c)) {
        builder.append(UMLParser.parse(c));
        builder.append(" ");
        builder.append(renderPatterns(c));
        builder.append("{");
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
    for (Relationship r : bundle.getList("relationships", Relationship.class)) {
      builder.append(UMLParser.parse(r, renderPatterns(r)));
      //builder.append((UMLParser.parse(r)));
      builder.append("\n");
    }
    builder.append("@enduml\n");
    System.out.println("Generated UML\n" + builder.toString());
    return builder.toString();
  }

  private String renderPatterns(SootClass c) {
    String rendered = "";
    for (Pattern p : patterns) {
      if (p.contains(c)) {
        rendered += renderers.get(p.getName()).render(c, p);
      }
    }
    return rendered;
  }
  
  private String renderPatterns(Relationship r) {
    String rendered = "";
    for (Pattern p: patterns) {
      if (p.contains(r)) {
        rendered = renderers.get(p.getName()).render(r, p);
      }
    }
    return rendered;
  }

  private void generateUMLImage(String umlString) {
    SourceStringReader reader = new SourceStringReader(umlString);
    try {
      Files.createDirectories(umlImageLocation.getParent());

      OutputStream outStream = new FileOutputStream(umlImageLocation.toFile());
      FileFormatOption option = new FileFormatOption(FileFormat.SVG, false);
      reader.outputImage(outStream, option);
    } catch (Exception e) {
      System.err.println("Cannot create file to store the UML diagram.\n");
      e.printStackTrace();
    }

  }
}
