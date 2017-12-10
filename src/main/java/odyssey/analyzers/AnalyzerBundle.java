package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;

import odyssey.app.Relationship;
import soot.SootClass;

public class AnalyzerBundle {
  public List<SootClass> classes;
  public List<Relationship> relationships;
  public String UML;

  public AnalyzerBundle(List<SootClass> classes, List<Relationship> relationships, String UML) {
    this.classes = classes;
    this.relationships = relationships;
    this.UML = UML;
  }

  public AnalyzerBundle() {
    classes = new ArrayList<>();
    relationships = new ArrayList<>();
    UML = "";
  }

}
