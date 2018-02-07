package odyssey.renderers;

import odyssey.models.Pattern;
import odyssey.models.Relation;
import odyssey.models.Relationship;
import soot.SootClass;

public class RelationshipRenderer implements IRelationshipRenderer {

  protected Pattern pattern;

  @Override
  public final String render(Relationship t) {
    StringBuilder builder = new StringBuilder();
    builder.append(renderLeft(!isReverse(t.getRelation()) ? t.getFromClass() : t.getToClass()));
    builder.append(" ");
    builder.append(renderArrow(t.getRelation(), t.getCardinality()));
    builder.append(" ");
    builder.append(renderRight(!isReverse(t.getRelation()) ? t.getToClass() : t.getFromClass()));
    return builder.toString();
  }

  @Override
  public final String render(Relationship t, Pattern pattern) {
    this.pattern = pattern;
    return render(t);
  }

  @Override
  public String renderLeft(SootClass clazz) {
    return clazz.getShortName().replaceAll("\\[\\]", "");
  }

  @Override
  public String renderRight(SootClass clazz) {
    return clazz.getShortName().replaceAll("\\[\\]", "");
  }

  @Override
  public String renderArrow(Relation relation, int cardinality) {
    StringBuilder builder = new StringBuilder();
    if (!isReverse(relation)) {
      builder.append(parseCardinality(cardinality));
      builder.append(" ");
      builder.append(parseArrow(relation));
    } else {
      builder.append(parseBackwardsArrow(relation));
      builder.append(" ");
      builder.append(parseCardinality(cardinality));
    }
    return builder.toString();
  }
  
  @Override
  public String getName() {
    return "default";
  }
  
  //TODO: Make this less ugly.
  protected String parseArrow(Relation relation) {
    switch (relation) {
    case ASSOCIATION:
      return "<-" + renderArrowStyle() + "-";
    case DEPENDENCY:
      return "<." + renderArrowStyle() + ".";
    case EXTENDS:
      return "<|-" + renderArrowStyle() + "-";
    case IMPLEMENTS:
      return "<|." + renderArrowStyle() + ".";
    default:
      return "<-" + renderArrowStyle() + "-";
    }
  }
  
  //TODO: Make this less ugly.
  protected String parseBackwardsArrow(Relation relation) {
    switch (relation) {
    case ASSOCIATION:
      return "-" + renderArrowStyle() + "->";
    case DEPENDENCY:
      return "." + renderArrowStyle() + ".>";
    case EXTENDS:
      return "-" + renderArrowStyle() + "-|>";
    case IMPLEMENTS:
      return "." + renderArrowStyle() + ".|>";
    default:
      return "-" + renderArrowStyle() + "->";
    }
  }
  
  protected String renderArrowStyle() {
    return "";
  }
  
  protected String parseCardinality(int cardinality) {
    if (cardinality == -1) {
      return "\"1..*\"";
    }
    if (cardinality == 0) {
      return "";
    }
    return "\"" + cardinality + "\"";
  }
  
  boolean isReverse(Relation r) {
    return (r == Relation.IMPLEMENTS || r == Relation.EXTENDS);
  }
  
  

}
