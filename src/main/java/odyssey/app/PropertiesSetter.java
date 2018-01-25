package odyssey.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class PropertiesSetter {

  private static final String DEFAULT_CONFIG = "config.txt";

  static void set(String[] args) {
    // "-config" to specify filepath to properties file
    try {
      setConfig(args);
      System.getProperties().list(System.out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setConfig(String[] args) throws IOException {
    String[] processedArgs = preprocessArgs(args);
    ListMultimap<String, String> argMap = parse(processedArgs);
    InputStream configFile = loadConfigFile(argMap);
    setSystemProperties(configFile);
    overrideSystemProperties(argMap);
  }

  private static String[] preprocessArgs(String[] args) {
    String[] processed = new String[args.length];
    for (int i = 0; i < processed.length; i++) {
      processed[i] = args[i].trim();
    }
    return processed;
  }

  private static ListMultimap<String, String> parse(String[] args) {
    ListMultimap<String, String> parsedArgs = ArrayListMultimap.create();
    String current = null;

    for (int i = 0; i < args.length; i++) {
      current = args[i];
      if (current.startsWith("--")) {
        parsedArgs.put(current, "");
      } else if (current.startsWith("-")) {
        i++;
        for (int j = i; j < args.length; j++) {
          if (args[j].startsWith("-")) {
            i = j - 1;
            break;
          }
          parsedArgs.put(current, args[j]);
        }
      }
    }
    return parsedArgs;
  }

  private static InputStream loadConfigFile(ListMultimap<String, String> argMap) throws IOException {
    List<String> fileNames = argMap.get("-config");
    InputStream file;
    if (fileNames.size() > 0) {
      file = new FileInputStream(new File(fileNames.get(0)));
    } else {
      ClassLoader classLoader = PropertiesSetter.class.getClassLoader();
      file = classLoader.getResourceAsStream(DEFAULT_CONFIG);
    }
    return file;
  }

  private static void setSystemProperties(InputStream file) throws IOException {
    Properties p = new Properties(System.getProperties());
    p.load(file);
    System.setProperties(p);
  }

  private static void overrideSystemProperties(ListMultimap<String, String> argMap) {
    argMap.keys().forEach(k -> {
      System.setProperty(k, processesCLArgs(argMap.get(k)));
    });
  }

  private static String processesCLArgs(List<String> ls) {
    StringBuilder builder = new StringBuilder();
    ls.forEach(s -> {
      builder.append(s);
      builder.append(" ");
    });
    return builder.toString().trim();
  }
}
