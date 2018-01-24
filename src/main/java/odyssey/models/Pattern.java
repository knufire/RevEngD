package odyssey.models;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.SetMultimap;

import soot.SootClass;

public class Pattern {

  private SetMultimap<String, Relationship> keyToRelationships;
  private SetMultimap<String, SootClass> keyToClass;
  private String name;

  public Set<Relationship> getRelationships(String key) {
    return keyToRelationships.get(key);
  }

  public Set<SootClass> getClasses(String key) {
    return keyToClass.get(key);
  }

  public boolean contains(Relationship r) {
    return keyToRelationships.containsValue(r);

  }

  public String getKey(Relationship r) {
    Iterator<Entry<String, Relationship>> iter = keyToRelationships.entries().iterator();
    Entry<String, Relationship> entry = null;
    while (iter.hasNext()) {
      entry = iter.next();
      if (entry.getValue().equals(r)) {
        return entry.getKey();
      }
    }
    return "";
  }

  public String getKey(SootClass clazz) {
    Iterator<Entry<String, SootClass>> iter = keyToClass.entries().iterator();
    Entry<String, SootClass> entry = null;
    while (iter.hasNext()) {
      entry = iter.next();
      if (entry.getValue().equals(clazz)) {
        return entry.getKey();
      }
    }
    return "";

  }

  public String getName() {
    return name;
  }

}
