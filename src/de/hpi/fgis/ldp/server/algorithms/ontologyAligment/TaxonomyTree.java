package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

import gnu.trove.map.hash.TIntObjectHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class TaxonomyTree {

  public TIntObjectHashMap<TaxonomyNode> getNodePool() {
    return nodePool;
  }

  private final TIntObjectHashMap<TaxonomyNode> nodePool = new TIntObjectHashMap<TaxonomyNode>();
  private TaxonomyNode root = null;

  public void addEdge(int subClass, int superClass) {

    if (nodePool.containsKey(subClass) && nodePool.containsKey(superClass)) {
      TaxonomyNode childNode = nodePool.get(subClass);

      TaxonomyNode parentNode = nodePool.get(superClass);
      // link from child to parent is implicit in addChild
      parentNode.addChild(childNode);
    } else if (nodePool.containsKey(superClass)) {
      TaxonomyNode parentNode = nodePool.get(superClass);
      TaxonomyNode newChildNode = new TaxonomyNode(subClass);
      nodePool.put(subClass, newChildNode);
      parentNode.addChild(newChildNode);
    } else if (nodePool.containsKey(subClass)) {

      TaxonomyNode childNode = nodePool.get(subClass);

      TaxonomyNode newParentNode = new TaxonomyNode(superClass);
      nodePool.put(superClass, newParentNode);
      newParentNode.addChild(childNode);
    } else {
      TaxonomyNode newParentNode = new TaxonomyNode(superClass);
      TaxonomyNode newChildNode = new TaxonomyNode(subClass);
      nodePool.put(subClass, newChildNode);
      nodePool.put(superClass, newParentNode);
      newParentNode.addChild(newChildNode);
    }

  }

  public TaxonomyNode getTreeRoot() {
    if (root == null) {
      if (nodePool.size() > 0) {

        TaxonomyNode rootCandidate = nodePool.valueCollection().iterator().next();
        while (rootCandidate.getParentNode() != null) {
          rootCandidate = rootCandidate.getParentNode();
        }
        root = rootCandidate;

      }

    }
    return root;
  }

  public ObjectOpenHashSet<TaxonomyNode> getAllLeafNodes() {
    ObjectOpenHashSet<TaxonomyNode> leafNodes = new ObjectOpenHashSet<TaxonomyNode>();
    for (TaxonomyNode tNode : nodePool.valueCollection()) {
      if (tNode.getChildCount() == 0) {
        leafNodes.add(tNode);
      }
    }
    return leafNodes;
  }

  public ObjectOpenHashSet<TaxonomyNode> getParentNodes(ObjectOpenHashSet<TaxonomyNode> leafNodes) {
    ObjectOpenHashSet<TaxonomyNode> parentNodes = new ObjectOpenHashSet<TaxonomyNode>();
    for (TaxonomyNode node : leafNodes) {
      if (node.getParentNode() != null) {
        if (!node.getParentNode().isTouched()) {
          parentNodes.add(node.getParentNode());
        }
      }
    }
    return parentNodes;
  }

  public boolean isSubClassOf(int arg0, int arg1) {
    // if (arg0.contains("dbpedia") && arg1.contains("dbpedia")) {
    TaxonomyNode firstNode = getNodePool().get(arg0);
    while (firstNode.getParentNode() != null) {
      if (firstNode.getParentNode().getValue() == arg1) {
        return true;
      }
      firstNode = firstNode.getParentNode();
    }
    // }
    return false;
  }

}
