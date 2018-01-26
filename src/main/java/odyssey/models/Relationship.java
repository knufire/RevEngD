package odyssey.models;

import soot.SootClass;

public class Relationship {

  private SootClass from;
  private SootClass to;
  private Relation relation;
  private int cardinality;

  public Relationship(SootClass from, Relation relation, SootClass to, int cardinality) {
    this.from = from;
    this.relation = relation;
    this.to = to;
    this.cardinality = cardinality;
  }

  public SootClass getFromClass() {
    return from;
  }

  public SootClass getToClass() {
    return to;
  }

  public Relation getRelation() {
    return relation;
  }

  public int getCardinality() {
    return cardinality;
  }


  @Override
  public String toString() {
    return "Relationship [from=" + from + ", to=" + to + ", relation=" + relation + ", cardinality=" + cardinality
        + "]";
  }

  // Needed for use in Set
  @Override
  public int hashCode() {
    return to.hashCode() + (13 * from.hashCode()) + relation.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Relationship) {
      Relationship r = (Relationship) obj;
      if (r.cardinality != cardinality) {
        return false;
      }
      if (!r.to.equals(to)) {
        return false;
      }
      if (!r.from.equals(from)) {
        return false;
      }
      if (!r.relation.equals(relation)) {
        return false;
      }
      return true;
    }
    return false;
  }

}
