package odyssey.analyzers;

import java.util.List;

import odyssey.app.Relation;
import odyssey.app.Relationship;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;

public class UMLParser {

	public UMLParser() {
	}
	
	public String parse(SootClass c) {
		StringBuilder builder = new StringBuilder();
		builder.append(getClassType(c.getModifiers()));
		builder.append(" ");
		builder.append(c.getShortName());
		builder.append(" {");
		return builder.toString();
	}
	
	public String parse(SootField f) {
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
	
	public String parse(SootMethod m) {
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
	
	public String parse(Type t) {
		//TODO: Doesn't get generics, not sure if it's available in SOOT.
		return trimQualifiedName(t.toQuotedString());

	}
	
	public String parse(Relationship r) {
		StringBuilder builder = new StringBuilder();
		builder.append(r.getFromClass().getShortName());
		builder.append(" ");
		builder.append(parse(r.getRelation()));
		builder.append(" ");
		builder.append(r.getToClass().getShortName());
		return builder.toString();
	}
	
	public String parse (Relation r) {
		switch(r) {
		//TODO: Make sure that the arrows are right.
			case ASSOCIATION:
				return "-->";
			case DEPENDENCY:
				return "--*";
			case EXTENDS:
				return "--|>";
			case IMPLEMENTS:
				return "..|>";
			default:
				return "-->";
		}
	}
	
	//TODO: This should probably be somewhere else since we'll need it somewhere else eventually.
	public String trimQualifiedName(String s) {
		String[] parts = s.split("\\.");
		return parts[parts.length-1];
	}
	
	public String getAccessModifier(int m) {
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
	
	public String getStaticAbstractModifier(int m) {
		if (soot.Modifier.isAbstract(m)) {
			return "{abstract} ";
		}
		if (soot.Modifier.isStatic(m)) {
			return "{static} ";
		}
		return "";
	}
	
	public String getClassType(int m) {
		if (soot.Modifier.isEnum(m)) {
			return "enum";
		}
		if (soot.Modifier.isInterface(m)) {
			return "interface";
		}
		if (soot.Modifier.isAbstract(m)) {
			return "abstract";
		}
		return "class";
	}
}
