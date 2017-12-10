package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public interface Filter {
	boolean shouldProcess(SootClass clazz);
	boolean shouldProcess(SootMethod method);
	boolean shouldProcess(SootField field);
}
