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
import odyssey.models.Message;
import odyssey.renderers.MessageRenderer;

public class SequenceAnalyzer extends Analyzer {

  private AnalyzerBundle bundle;
  private Path seqImageLocation;
  private Map<String, MessageRenderer> messageRenderers;
  
  public SequenceAnalyzer(List<Filter> filters, Map<String, MessageRenderer> messageRenderers) {
    super(filters);
    seqImageLocation = Paths.get(System.getProperty("-s"));
    this.messageRenderers = messageRenderers;
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    this.bundle = bundle;
    String parseString = parseCalls();
    generateSeqImage(parseString);
    bundle.put("seqString", parseString);
    return this.bundle;
  }

  private String parseCalls() {
    StringBuilder builder = new StringBuilder();
    builder.append("@startuml\n");
    for (Message c : bundle.getList("messages", Message.class)) {
      MessageRenderer renderer = messageRenderers.get(c.getType());
      if (renderer != null) {
        builder.append(renderer.render(c));
        builder.append("\n");        
      }
    }
    builder.append("@enduml\n");
    System.out.println("Generated Sequence Diagram\n" + builder.toString());
    return builder.toString();
  }

  private void generateSeqImage(String umlString) {
    SourceStringReader reader = new SourceStringReader(umlString);
    try {
      Files.createDirectories(seqImageLocation.getParent());

      OutputStream outStream = new FileOutputStream(seqImageLocation.toFile());
      FileFormatOption option = new FileFormatOption(FileFormat.SVG, false);
      reader.outputImage(outStream, option);
    } catch (Exception e) {
      System.err.println("Cannot create file to store the UML diagram.\n" + e);
    }

  }
}
