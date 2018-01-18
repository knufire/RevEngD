package odyssey.analyzers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import odyssey.models.Message;
import odyssey.models.Relationship;
import soot.Scene;
import soot.SootClass;

public class AnalyzerBundle {
  public List<SootClass> classes;
  public List<Relationship> relationships;
  public Scene scene;
  public List<Message> messages;

  public AnalyzerBundle() {
    classes = new ArrayList<>();
    relationships = new ArrayList<>();
    messages = new LinkedList<>();
  }

}
