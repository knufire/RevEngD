package odyssey.methodresolution;

import java.util.List;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;

public interface Algorithm {
  List<SootMethod> resolve(Unit u, SootMethod m, Scene scene);
}
