package odyssey.app;

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
  
  

}
