package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class PublicFilter implements Filter {

	@Override
	public boolean shouldProcess(SootClass clazz) {
		return clazz.isPublic();
	}

	@Override
	public boolean shouldProcess(SootMethod method) {
		return method.isPublic();
	}

	@Override
	public boolean shouldProcess(SootField field) {
		return field.isPublic();
	}

}
