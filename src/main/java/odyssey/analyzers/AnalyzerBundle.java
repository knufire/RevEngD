package odyssey.analyzers;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import odyssey.app.Call;
import odyssey.app.Relationship;
import soot.Scene;
import soot.SootClass;

public class AnalyzerBundle {
  public List<SootClass> classes;
  public List<Relationship> relationships;
  public String UML;
  public Scene scene;
  public OutputStream out;
  public List<Call> calls;

  public AnalyzerBundle() {
    classes = new ArrayList<>();
    relationships = new ArrayList<>();
    UML = "";
    calls = new LinkedList<>();
  }

}
