package odyssey.app;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {

  public static void main(String[] args) {
    System.out.println(Arrays.toString(args));
    if (args.length < 4) {
      System.out.println("Must provide directory and class names");
      return;
    }

    Configuration config = populateConfiguration(args);
    UMLGenerationApp app = new UMLGenerationApp(config);
    app.generate();
  }

  private static Configuration populateConfiguration(String[] args) {
    Configuration config = new Configuration();
    Map<String, List<String>> parsedArgs = parse(args);
    parsedArgs.forEach((key, value) -> addToConfiguration(key, value, config));
    return config;
  }

  private static Map<String, List<String>> parse(String[] args) {
    Map<String, List<String>> parsedArgs = new HashMap<>();
    String current = null;

    for (int i = 0; i < args.length; i++) {
      current = args[i];
      if (current.startsWith("--")) {
        parsedArgs.put(current, Collections.emptyList());
      } else if (current.startsWith("-")) {
        i++;
        List<String> values = new ArrayList<>();
        for (int j = i; j < args.length; j++) {
          if (args[j].startsWith("-")) {
            i = j - 1;
            break;
          }
          values.add(args[j]);
        }
        parsedArgs.put(current, values);
      }

    }
    return parsedArgs;
  }

  private static void addToConfiguration(String flag, List<String> values, Configuration config) {
    switch (flag) {
    case "-a":
      config.accessModifier = parseModifier(values.get(0));
      return;
    case "-c":
      config.classNames = values;
      return;
    case "-d":
      config.projectDirectory = Paths.get(values.get(0).trim(), "build", "classes", "main");
      return;
    case "-i":
    	config.umlImageLocation = Paths.get(values.get(0).trim());
    	return;
    case "-s":
      config.seqImageLocation = Paths.get(values.get(0).trim());
      return;
    case "-m":
      config.mainClassName = values.get(0);
      return;
    case "--include-ancestors":
      config.parseAncestors = true;
      return;
    case "--include-Object":
    	config.includeObject = true;
    	return;
    case "-e":
      config.entryMethodName = values.get(0);
      return;
    case "-max-depth": 
      config.maxCallDepth = Integer.parseInt(values.get(0));
      return;
    case "--expand-jdk":
      config.expandJDK = true;
      return;
    case "--show-inheritance":
      config.showInheritance = true;
      return;
    case "--show-dependence":
      config.showDependence = true;
      return;
    case "--show-association":
      config.showAssociation = true;
      return;
    default:
    	return;
    }
  }

  private static String parseModifier(String string) {
    String lower = string.toLowerCase();
    if (lower.equals("public")) {
      return "public";
    }
    if (lower.equals("protected")) {
      return "protected";
    }
    if (lower.equals("package")) {
    	return "package";
    }
    return "private";
  }

}
