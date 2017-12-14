package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class ClinitFilter implements Filter {

	@Override
	public boolean shouldProcess(SootClass clazz) {
		return true;
	}

	@Override
	public boolean shouldProcess(SootMethod method) {
		return !method.getName().contains("<clinit>");
	}

	@Override
	public boolean shouldProcess(SootField field) {
		return true;
	}

}
