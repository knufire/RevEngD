package odyssey.analyzers;

import java.util.ArrayList;
import java.util.List;

import odyssey.filters.Filter;
import soot.Scene;
import soot.SootClass;
import soot.util.Chain;

public class SootAnalyzer extends Analyzer {

  private Scene scene;
  private String[] blackList;
  private String[] whiteList;

  public SootAnalyzer(List<Filter> filters) {
    super(filters);
  }

  @Override
  public AnalyzerBundle execute(AnalyzerBundle bundle) {
    scene = bundle.get("scene", Scene.class);
    Chain<SootClass> classes = scene.getClasses();
    List<SootClass> filteredClasses = new ArrayList<>();
    String bl = System.getProperty("-bl");
    String wl = System.getProperty("-c");
    if (bl != null) {
      blackList = bl.split(" ");
    } else {
      blackList = new String[0];
    }
    if (wl != null) {
      whiteList = wl.split(" ");
    } else {
      whiteList = new String[0];
    }
    for (SootClass c : classes) {
      if (passesFilters(c)) {
        if (!checkBlackList(c) || checkWhiteList(c)) {
          filteredClasses.add(c);
        }
      }
    }

    bundle.put("classes", filteredClasses);
    return bundle;
  }

  private boolean checkBlackList(SootClass clazz) {
    String className = clazz.getName();
    for (int i = 0; i < blackList.length; i++) {
      if (className.startsWith(blackList[i])) {
        return true;
      }
    }
    return false;
  }

  private boolean checkWhiteList(SootClass clazz) {
    String className = clazz.getName();
    for (int i = 0; i < whiteList.length; i++) {
      if (className.startsWith(whiteList[i])) {
        return true;
      }
    }
    return false;
  }

}
