package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class DollarSignFilter implements Filter {

	@Override
	public boolean shouldProcess(SootClass clazz) {
		return !clazz.getName().contains("$");
	}

	@Override
	public boolean shouldProcess(SootMethod method) {
		return !method.getName().contains("$");
	}

	@Override
	public boolean shouldProcess(SootField field) {
		return !field.getName().contains("$");
	}

}
