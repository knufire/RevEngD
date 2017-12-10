package odyssey.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import soot.Modifier;

public class Configuration {
  public List<String> classNames;
  public Path directory;
  public boolean parseAncestors;
  public int accessModifier;
  public boolean display;

  public Configuration() {
    classNames = Collections.emptyList();
    directory = Paths.get("");
    parseAncestors = false;
    accessModifier = Modifier.PRIVATE;
    display = true;
  }

}
