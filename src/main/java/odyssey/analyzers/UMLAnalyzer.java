package odyssey.analyzers;

import java.util.List;

import odyssey.app.Configuration;
import odyssey.filters.Filter;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;

public class UMLAnalyzer extends Analyzer {

	public UMLAnalyzer(Configuration configuration, List<Filter> filters) {
		super(configuration, filters);
	}
	@Override
	public AnalyzerBundle execute(AnalyzerBundle bundle) {
		for (SootClass c : bundle.classes) {
			for (SootMethod m : c.getMethods()) {
				System.out.println(parseMethod(m));
			}
		}
		return bundle;
	}
	
	private String parseClass(SootClass c) {
		return "";
	}
	
	private String parseField(SootField f) {
		return "";
	}
	
	private String parseMethod (SootMethod m) {
		List<Type> params = m.getParameterTypes();
		StringBuilder builder = new StringBuilder();
		builder.append(getAccessModifier(m.getModifiers()));
		builder.append(" ");
		builder.append(getMethodModifier(m.getModifiers()));
		builder.append(trimQualifiedName(m.getReturnType().toQuotedString()));
		builder.append(" ");
		
		//TODO: Possibly deal with lambdas in methodName
		String methodName = Scene.v().quotedNameOf(m.getName()); 
		if (methodName.contains("<init>")) {
			builder.append(trimQualifiedName(Scene.v().quotedNameOf(m.getDeclaringClass().getName())));
		} else {
			builder.append(methodName);
		}
		builder.append("(");
		for (int i = 0; i < params.size(); i++) {
			builder.append(trimQualifiedName(params.get(i).toQuotedString()));
			if (i < params.size() - 1) {
				builder.append(",");				
			}
		}
		builder.append(")");
		return builder.toString();
	}
	
	//TODO: This should probably be somewhere else since we'll need it somewhere else eventually.
	private String trimQualifiedName(String s) {
		String[] parts = s.split("\\.");
		return parts[parts.length-1];
	}
	
	private String getAccessModifier(int m) {
		if (soot.Modifier.isPublic(m)) {
			return "+";
		}
		if (soot.Modifier.isProtected(m)) {
			return "#";
		}
		if (soot.Modifier.isPrivate(m)) {
			return "-";
		}
		return "~"; //Return package-level access by default
	}
	
	private String getMethodModifier(int m) {
		if (soot.Modifier.isAbstract(m)) {
			return "{abstract} ";
		}
		if (soot.Modifier.isStatic(m)) {
			return "{static} ";
		}
		return "";
	}
	
	

}
