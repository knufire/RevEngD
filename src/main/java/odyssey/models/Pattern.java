package odyssey.models;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class Pattern {

  private SetMultimap<String, Relationship> keyToRelationships;
  private SetMultimap<String, SootClass> keyToClass;
  private SetMultimap<String, SootMethod> keyToMethods;
  private SetMultimap<String, SootField> keyToFields;

  private String name;

  public Pattern(String name) {
    keyToRelationships = HashMultimap.create();
    keyToClass = HashMultimap.create();
    keyToMethods = HashMultimap.create();
    keyToFields = HashMultimap.create();
    this.name = name;

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Relationship> getRelationships(String key) {
    return keyToRelationships.get(key);
  }

  public Set<SootClass> getClasses(String key) {
    return keyToClass.get(key);
  }

  public Set<SootField> getFields(String key) {
    return keyToFields.get(key);
  }

  public Set<SootMethod> getMethods(String key) {
    return keyToMethods.get(key);
  }

  public boolean contains(Relationship r) {
    return keyToRelationships.containsValue(r);
  }

  public boolean contains(SootClass c) {
    return keyToClass.containsValue(c);
  }

  public boolean contains(SootField f) {
    return keyToFields.containsValue(f);
  }

  public boolean contains(SootMethod m) {
    return keyToMethods.containsValue(m);
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

  public String getKey(SootField clazz) {
    Iterator<Entry<String, SootField>> iter = keyToFields.entries().iterator();
    Entry<String, SootField> entry = null;
    while (iter.hasNext()) {
      entry = iter.next();
      if (entry.getValue().equals(clazz)) {
        return entry.getKey();
      }
    }
    return "";

  }

  public String getKey(SootMethod clazz) {
    Iterator<Entry<String, SootMethod>> iter = keyToMethods.entries().iterator();
    Entry<String, SootMethod> entry = null;
    while (iter.hasNext()) {
      entry = iter.next();
      if (entry.getValue().equals(clazz)) {
        return entry.getKey();
      }
    }
    return "";

  }

  public void put(String key, SootClass clazz) {
    keyToClass.put(key, clazz);
  }

  public void put(String key, Relationship relationship) {
    keyToRelationships.put(key, relationship);
  }

  public void put(String key, SootMethod method) {
    keyToMethods.put(key, method);
  }

  public void put(String key, SootField field) {
    keyToFields.put(key, field);
  }

}
