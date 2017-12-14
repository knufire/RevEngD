package odyssey.analyzers;

import java.util.List;

import net.bytebuddy.dynamic.loading.MultipleParentClassLoader.Builder;
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
			System.out.print(parse(c));
		}
		return bundle;
	}
	
	private String parse(SootClass c) {
		StringBuilder builder = new StringBuilder();
		builder.append(getClassType(c.getModifiers()));
		builder.append(" ");
		builder.append(c.getShortName());
		builder.append(" {\n");
		for (SootField f : c.getFields()) {
			builder.append("  ");
			builder.append(parse(f));
			builder.append("\n");
		}
		for (SootMethod m : c.getMethods()) {
			builder.append("  ");
			builder.append(parse(m));
			builder.append("\n");
		}
		builder.append("}\n");
		return builder.toString();
	}
	
	private String parse(SootField f) {
		//TODO: Does something weird with enums, might not be a problem here as parse(SootClass c) might 
		//just not call this if the class is an enum.
		StringBuilder builder = new StringBuilder();
		builder.append(getAccessModifier(f.getModifiers()));
		builder.append(" ");
		builder.append(getStaticAbstractModifier(f.getModifiers()));
		builder.append(parse(f.getType()));
		builder.append(" ");
		builder.append(f.getName());
		return builder.toString();
	}
	
	private String parse(SootMethod m) {
		List<Type> params = m.getParameterTypes();
		StringBuilder builder = new StringBuilder();
		builder.append(getAccessModifier(m.getModifiers()));
		builder.append(" ");
		builder.append(getStaticAbstractModifier(m.getModifiers()));
		builder.append(parse(m.getReturnType()));
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
	
	private String parse(Type t) {
		//TODO: Doesn't get generics, not sure if it's available in SOOT.
		return trimQualifiedName(t.toQuotedString());

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
	
	private String getStaticAbstractModifier(int m) {
		if (soot.Modifier.isAbstract(m)) {
			return "{abstract} ";
		}
		if (soot.Modifier.isStatic(m)) {
			return "{static} ";
		}
		return "";
	}
	
	private String getClassType(int m) {
		if (soot.Modifier.isAbstract(m)) {
			return "abstract";
		}
		if (soot.Modifier.isEnum(m)) {
			return "enum";
		}
		if (soot.Modifier.isInterface(m)) {
			return "interface";
		}
		return "class";
	}
	
	

}
