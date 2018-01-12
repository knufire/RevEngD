package odyssey.models;
/**
 * ASSOCIATION: A has a B
 * 
 * EXTENDS: A is a subclass of B
 * 
 * IMPLEMENTS: A implements the interface B
 * 
 * DEPENDENCY: A uses B but B is not a field of A
 * 
 * @author moorect
 *
 */
public enum Relation {
	ASSOCIATION, EXTENDS, IMPLEMENTS, DEPENDENCY 
}
