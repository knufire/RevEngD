package odyssey.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class PropertiesSetter {
  
  private static final String DEFAULT_CONFIG = "config.txt";
  
  static void set(String[] args) {
    // "-config" to specify filepath to properties file
    try {
      setConfig(args);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static void setConfig(String[] args) throws IOException {
    String[] processedArgs = preprocessArgs(args);
    ListMultimap<String, String> argMap = parse(processedArgs);
    File configFile = loadConfigFile(argMap);
    setSystemProperties(configFile);
    
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
  
  private static File loadConfigFile(ListMultimap<String, String> argMap) {
    List<String> fileNames = argMap.get("-config");
    File file;
    if (fileNames.size() > 0) {
      file = new File(fileNames.get(0));
    } else {
      ClassLoader classLoader = PropertiesSetter.class.getClassLoader();
      file = new File(classLoader.getResource(DEFAULT_CONFIG).getFile());
    }
    return file;
  }
  
  private static void setSystemProperties(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    Properties p = new Properties(System.getProperties());
    p.load(input);
    System.setProperties(p);
    System.getProperties().list(System.out);
  }
  
  private static void overrideSystemProperties(ListMultimap<String, String> argMap) {
    argMap.entries().forEach(e -> {
      //System.setProperty(e.getKey(), processCLArgs(e.getValue()));
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
