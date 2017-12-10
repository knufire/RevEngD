package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class ProtectedFilter implements Filter {

	@Override
	public boolean shouldProcess(SootClass clazz) {
		return clazz.isProtected() || clazz.isPublic();
	}

	@Override
	public boolean shouldProcess(SootMethod method) {
		return method.isProtected() || method.isPublic();
	}

	@Override
	public boolean shouldProcess(SootField field) {
		return field.isProtected() || field.isPublic();
	}

}
