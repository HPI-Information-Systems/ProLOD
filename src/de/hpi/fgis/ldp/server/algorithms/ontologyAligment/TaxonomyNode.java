package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;

public class TaxonomyNode {

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  private TaxonomyNode parentNode;
  private final ArrayList<TaxonomyNode> children = new ArrayList<TaxonomyNode>();
  private TIntSet schema = new TIntHashSet();
  private TIntIntHashMap pushedDownDandidates = new TIntIntHashMap();
  private final TIntSet removedPredicates = new TIntHashSet();
  private TIntSet overspecifications = new TIntHashSet();

  public TIntSet getRemovedPredicates() {
    return removedPredicates;
  }

  public void addToRemovedPredicates(int predicate) {
    removedPredicates.add(predicate);
  }

  private TIntSet candidates = new TIntHashSet();

  public TIntSet getSchema() {
    return schema;
  }

  public void setSchema(TIntSet schema) {
    this.schema = schema;
  }

  public TIntSet getCandidates() {
    return candidates;
  }

  public void setCandidates(TIntHashSet candidates) {
    this.candidates = candidates;
  }

  private int value;
  private boolean touched = false;

  public void setTouched(boolean touched) {
    this.touched = touched;
  }

  public TaxonomyNode(int value) {
    this.value = value;
  }

  public void addChild(TaxonomyNode newChild) {
    newChild.setParentNode(this);
    children.add(newChild);

  }

  public void setParentNode(TaxonomyNode parentNode) {
    this.parentNode = parentNode;
  }

  public TaxonomyNode getParentNode() {
    return parentNode;
  }

  public int getChildCount() {
    return children.size();
  }

  public ArrayList<TaxonomyNode> getChildren() {
    return children;
  }

  public boolean isLeaf() {
    if (children.size() == 0) {
      return true;
    }
    return false;
  }

  public void addToSchema(int predicate) {
    schema.add(predicate);
  }

  public ArrayList<TaxonomyNode> getUntouchedChildren() {
    ArrayList<TaxonomyNode> untouchedChildren = new ArrayList<TaxonomyNode>();
    for (TaxonomyNode child : children) {
      if (!child.isTouched()) {
        untouchedChildren.add(child);
      }
    }
    return untouchedChildren;
  }

  public boolean isTouched() {
    return touched;
  }

  /**
   * gets all the most general nodes of each branch that contains the given candidate
   * 
   * @param candidate
   * @param predicateToSubjectMapping
   * @return
   */
  public ArrayList<TaxonomyNode> getBranchOccurrencesOfPredicate(int candidate,
      TIntIntHashMap predicateToSubjectMapping) {
    ArrayList<TaxonomyNode> branchOccurences = new ArrayList<TaxonomyNode>();
    for (TaxonomyNode child : children) {
      if (child.getCandidates().contains(candidate)
          || child.getSchema().contains(predicateToSubjectMapping.get(candidate))) {
        branchOccurences.add(child);
      } else {
        // traverse children of the tree
        branchOccurences.addAll(child.getBranchOccurrencesOfPredicate(candidate,
            predicateToSubjectMapping));
      }
    }
    return branchOccurences;
  }

  public void removeFromSchema(int predicate) {
    schema.remove(predicate);
    addToRemovedPredicates(predicate);

  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Type: " + value + "\n");
    sb.append("Schema: " + schema + "\n");
    if (!overspecifications.isEmpty()) {
      sb.append(overspecifications.size() + " Removed in Phase 1: " + overspecifications + "\n");
    }
    if (!removedPredicates.isEmpty()) {
      sb.append(removedPredicates.size() + " Removed Schema Predicates: " + removedPredicates
          + "\n");
    }
    if (!candidates.isEmpty()) {
      sb.append(candidates.size() + " Candidates: " + candidates + "\n");
    }
    if (!pushedDownDandidates.isEmpty()) {
      sb.append(pushedDownDandidates.size() + " Pushed Candidates: " + pushedDownDandidates + "\n");
    }
    return sb.toString();
  }

  public TIntSet getOverspecifications() {
    return overspecifications;
  }

  public void setOverspecifications(TIntSet oldSchema) {
    this.overspecifications = oldSchema;
  }

  public void setPushedDownDandidates(TIntIntHashMap pushedDownDandidates) {
    this.pushedDownDandidates = pushedDownDandidates;
  }

  public TIntIntHashMap getPushedDownDandidates() {
    return pushedDownDandidates;
  }

}
