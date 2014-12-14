package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

import gnu.trove.map.hash.TIntIntHashMap;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class TaxonomyManager {

  private final TaxonomyTree tTree = new TaxonomyTree();
  private TIntIntHashMap subjectToClusterMap = new TIntIntHashMap();

  public TaxonomyManager(IEntitySchemaLoader entityLoader, DataSource source, IProgress progress) {
    readFromLoader(entityLoader, source, progress);
  }

  private void readFromLoader(IEntitySchemaLoader entityLoader, DataSource source,
      IProgress progress) {
    try {
      setSubjectToClusterMap(entityLoader.getClusterIdMappings(source, progress, "sc"));
      Map<Integer, Integer> map = entityLoader.getClassHierarchy(source, progress);
      for (Entry<Integer, Integer> entry : map.entrySet()) {
        if (subjectToClusterMap.containsKey(entry.getKey())
            && subjectToClusterMap.containsKey(entry.getValue())) {
          tTree.addEdge(entry.getKey(), entry.getValue());
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public TaxonomyTree gettTree() {
    return tTree;
  }

  public TIntIntHashMap getSubjectToClusterMap() {
    return subjectToClusterMap;
  }

  public void setSubjectToClusterMap(TIntIntHashMap subjectToClusterMap) {
    this.subjectToClusterMap = subjectToClusterMap;
  }

}
