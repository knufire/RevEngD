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
import odyssey.renderers.ClassRenderer;
import odyssey.renderers.RelationshipRenderer;
import soot.SootClass;

public class UMLAnalyzer extends Analyzer {

  private Path umlImageLocation;
  private List<Pattern> patterns;
  private Map<String, ClassRenderer> classRenderers;
  private Map<String, RelationshipRenderer> relationshipRenderers;

  public UMLAnalyzer(List<Filter> filters, List<Pattern> patterns, Map<String, ClassRenderer> classRenderers,
      Map<String, RelationshipRenderer> relationshipRenderers) {
    super(filters);
    this.patterns = patterns;
    this.classRenderers = classRenderers;
    this.relationshipRenderers = relationshipRenderers;
    umlImageLocation = Paths.get(System.getProperty("-i"));
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    patterns = bundle.getList("patterns", Pattern.class);
    String umlString = parse(bundle);
    generateUMLImage(umlString);
    bundle.put("umlString", umlString);
    return bundle;
  }

  private String parse(AnalyzerBundle bundle) {
    StringBuilder builder = new StringBuilder();
    builder.append("@startuml\n");
    builder.append("skinparam linetype ortho\n");

    // Render all classes
    for (SootClass c : bundle.getList("classes", SootClass.class)) {
      if (passesFilters(c)) parse(c, builder);
      builder.append("\n");
    }
    // Render all relationships
    for (Relationship r : bundle.getList("relationships", Relationship.class)) {
      parse(r, builder);
      builder.append("\n");
    }
    builder.append("@enduml\n");
    System.out.println("Generated UML\n" + builder.toString());
    return builder.toString();
  }

  private void parse(SootClass c, StringBuilder builder) {
    for (Pattern p : patterns) {
      if (p.contains(c)) {
        ClassRenderer renderer = classRenderers.get(p.getName());
        if (renderer == null) throw new RuntimeException("Class or Relationship matched a pattern, but no renderer was found for the associated pattern.");
        builder.append(renderer.render(c, p));
        return;
      }
    }
    builder.append(classRenderers.get("default").render(c));
  }

  private void parse(Relationship r, StringBuilder builder) {
    for (Pattern p : patterns) {
      if (p.contains(r)) {
        RelationshipRenderer renderer = relationshipRenderers.get(p.getName());
        if (renderer == null) throw new RuntimeException("Class or Relationship matched a pattern, but no renderer was found for the associated pattern.");
        builder.append(renderer.render(r, p));
        return;
      }
    }
    builder.append(relationshipRenderers.get("default").render(r));
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
