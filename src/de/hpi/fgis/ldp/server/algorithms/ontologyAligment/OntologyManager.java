package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.sql.SQLException;
import java.util.ArrayList;

import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class OntologyManager {

  private TIntObjectHashMap<TIntSet> predicateToTypeMap = new TIntObjectHashMap<TIntSet>();

  private TIntIntHashMap predicateToSourceTypeMap = new TIntIntHashMap();
  private TIntObjectHashMap<TIntSet> typeToPredicateMap = new TIntObjectHashMap<TIntSet>();

  public boolean predicateIsSetForType(int predicate, int currentType) {
    TIntSet types = getPredicateToTypeMap().get(predicate);
    if (types == null) {
      return false;
    }

    return types.contains(currentType);
  }

  public void setPredicateToTypeMap(TIntObjectHashMap<TIntSet> predicateToTypeMap) {
    this.predicateToTypeMap = predicateToTypeMap;
  }

  public TIntObjectHashMap<TIntSet> getPredicateToTypeMap() {
    return predicateToTypeMap;
  }

  public void createPredicateTypeMappings(TaxonomyManager tm) {
    TaxonomyTree taxonomyTree = tm.gettTree();
    for (int property : predicateToSourceTypeMap.keys()) {

      int type = predicateToSourceTypeMap.get(property);
      if (!tm.getSubjectToClusterMap().containsKey(type)) {
        continue;
      }

      TIntSet types = new TIntHashSet();
      types.add(predicateToSourceTypeMap.get(property));
      types.addAll(getChildTypes(taxonomyTree.getNodePool().get(
          predicateToSourceTypeMap.get(property))));
      predicateToTypeMap.put(property, types);
    }

    for (int property : predicateToTypeMap.keys()) {

      for (int type : predicateToTypeMap.get(property).toArray()) {
        if (typeToPredicateMap.containsKey(type)) {
          typeToPredicateMap.get(type).add(property);
        } else {
          TIntSet newPropertySet = new TIntHashSet();
          newPropertySet.add(property);
          typeToPredicateMap.put(type, newPropertySet);
        }

      }

    }

  }

  private TIntSet getChildTypes(TaxonomyNode node) {
    TIntSet types = new TIntHashSet();
    if (node == null) {
      return types;
    }
    ArrayList<TaxonomyNode> children = node.getChildren();
    for (TaxonomyNode child : children) {
      types.add(child.getValue());
      types.addAll(getChildTypes(child));
    }
    return types;

  }

  public void readPredicateSourceTypeMappings(IEntitySchemaLoader entityLoader, DataSource source,
      IProgress progress) {
    try {
      predicateToSourceTypeMap = entityLoader.getPredicateDomain(source, progress);

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public TIntObjectHashMap<TIntSet> getTypeToPredicateMap() {
    return typeToPredicateMap;
  }

  public void setTypeToPredicateMap(TIntObjectHashMap<TIntSet> typeToPredicateMap) {
    this.typeToPredicateMap = typeToPredicateMap;
  }

  public void setPredicateToSourceTypeMap(TIntIntHashMap predicateToSourceTypeMap) {
    this.predicateToSourceTypeMap = predicateToSourceTypeMap;
  }

  public TIntIntHashMap getPredicateToSourceTypeMap() {
    return predicateToSourceTypeMap;
  }

}
