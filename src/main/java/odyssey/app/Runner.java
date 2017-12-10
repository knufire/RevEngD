package odyssey.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Modifier;

public class Runner {

  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println("Must provide directory and class names");
      return;
    }

    Configuration config = populateConfiguration(args);

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
      }
      if (current.startsWith("-")) {
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
    }
    // TODO: Finish flags for -d, -c, --include-ancestors
  }

  private static int parseModifier(String string) {
    String lower = string.toLowerCase();
    if(lower.equals("public")){
      return Modifier.PUBLIC;
    }
    if(lower.equals("protected")){
      return Modifier.PROTECTED;
    }
    return Modifier.PRIVATE;
  }

}
