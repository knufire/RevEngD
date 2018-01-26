package odyssey.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import odyssey.models.Message;
import odyssey.models.Pattern;
import odyssey.models.Relationship;
import soot.SootClass;

public class AnalyzerBundle {
  private Map<String, Object> bundle;


  public AnalyzerBundle() {
    this.bundle = new HashMap<>();
    this.bundle.put("classes", new ArrayList<SootClass>());
    this.bundle.put("relationships", new ArrayList<Relationship>());
    this.bundle.put("messages", new ArrayList<Message>());
    this.bundle.put("patterns", new ArrayList<Pattern>());
  }
  
  @SuppressWarnings("unchecked")
  public <T> T get(String name, Class<T> clazz) {
    return (T)this.bundle.get(name);
  }
  
  public void put(String name, Object object) {
    this.bundle.put(name, object);
  }
  
  @SuppressWarnings("unchecked")
  public <T> List<T> getList(String name, Class<T> clazz) {
    return (List<T>)this.bundle.get(name);
  }

}
