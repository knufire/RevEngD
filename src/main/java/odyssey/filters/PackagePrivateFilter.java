package odyssey.filters;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class PackagePrivateFilter implements Filter {

	@Override
	public boolean shouldProcess(SootClass clazz) {
		return !soot.Modifier.isPrivate(clazz.getModifiers());
	}

	@Override
	public boolean shouldProcess(SootMethod method) {
		return !soot.Modifier.isPrivate(method.getModifiers());
	}

	@Override
	public boolean shouldProcess(SootField field) {
		return !soot.Modifier.isPrivate(field.getModifiers());
	}

}
