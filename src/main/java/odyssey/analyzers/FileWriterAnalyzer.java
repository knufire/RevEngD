package odyssey.analyzers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import odyssey.filters.Filter;

public class FileWriterAnalyzer extends Analyzer {

  public FileWriterAnalyzer(List<Filter> filters) {
    super(filters);
  }

  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    String umlLocation = System.getProperty("-uml");
    String seqLocation = System.getProperty("-seq");
    if (umlLocation != null) {
      writeStringToFile(umlLocation, bundle.get("umlString", String.class));
    }
    if (seqLocation != null) {
      writeStringToFile(seqLocation, bundle.get("seqString", String.class));
    }
    return bundle;
  }

  private void writeStringToFile(String location, String string) {
    try {
      writeFile(location, string);
    } catch (IOException e) {
      System.err.println("Unable to save data to " + location);
      e.printStackTrace();
    }
  }

  private void writeFile(String location, String string) throws IOException {
    File file = new File(location);
    file.createNewFile();
    FileWriter writer = new FileWriter(file);
    writer.write(string);
    writer.flush();
    writer.close();
  }

}
