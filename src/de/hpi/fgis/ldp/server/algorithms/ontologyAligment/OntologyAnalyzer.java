package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.associationrules.AdvancedARF;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.AssociationRule;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IOntologyAlignmentStorage;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class OntologyAnalyzer {

  @Inject
  private Log logger;
  @Inject
  private Provider<AdvancedARF> finder;
  private DataSource source;

  private double support;
  private double confidence;
  @Inject
  private IEntitySchemaLoader entityLoader;

  @Inject
  private IOntologyAlignmentStorage ontologyWriter;

  // public OntologyAnalyzer(Log logger,
  // Provider<AdvancedARF> finder, IMetaLoader metaLoader,
  // IEntitySchemaLoader entityLoader) {
  // this.logger = logger;
  // this.finder = finder;
  // this.metaLoader = metaLoader;
  // this.entityLoader = entityLoader;
  // }

  public void init(DataSource source) {
    this.init(source, 0.001, 0.9);
  }

  public void init(DataSource source, double support, double confidence) {
    this.source = source;
    this.support = support;
    this.confidence = confidence;
  }

  public void identifyAllUnderSpecifications(IProgress progress) {

    // retrieve hierarchy

    TaxonomyManager tm = new TaxonomyManager(this.entityLoader, source, progress);
    // create ontology manager
    OntologyManager om = new OntologyManager();
    // assign also correct definition classes
    om.readPredicateSourceTypeMappings(this.entityLoader, source, progress);
    om.createPredicateTypeMappings(tm);

    // cleaned ontology

    OntologyManager cleanedOntoM = new OntologyManager();
    cleanedOntoM.readPredicateSourceTypeMappings(this.entityLoader, source, progress);
    cleanedOntoM.createPredicateTypeMappings(tm);

    // identify overspecifications
    identifyOverspecification(om, cleanedOntoM, progress, tm);

    TaxonomyTree tTree = tm.gettTree();

    ObjectOpenHashSet<TaxonomyNode> leafNodes = tTree.getAllLeafNodes();
    // identify node root
    for (TaxonomyNode node : tTree.getNodePool().valueCollection()) {
      if (node.getParentNode() == null) {
        System.out.println("root:" + node);
      }
    }
    TIntIntHashMap predicateToSubjectMapping = null;
    try {
      predicateToSubjectMapping = entityLoader.getPredicateIDMappings(source, progress, "ps");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    ObjectOpenHashSet<TaxonomyNode> restNodes = new ObjectOpenHashSet<TaxonomyNode>();
    int count = tTree.getNodePool().size();
    Cluster cluster = new Cluster(source);
    while (!leafNodes.isEmpty()) {
      // iterate through all layers of the ontology
      for (TaxonomyNode tNode : leafNodes) {
        int s_clusterID = tNode.getValue();
        int c_clusterID = tm.getSubjectToClusterMap().get(s_clusterID);
        ArrayList<TaxonomyNode> untouchedNodes = tNode.getUntouchedChildren();
        if (untouchedNodes.size() > 0) {
          // for all untouchedNodes check whether they will be handlen
          // in this iteration in the other case add the for the list
          // of the next iteration
          System.out.println("postpone:" + c_clusterID);
          restNodes.add(tNode);
          continue;
        }
        System.out.println(count + " nodes remaining");
        count--;
        // analyze prediccates of each node concept
        AdvancedARF arf = this.finder.get();

        cluster.setId(c_clusterID);
        arf.setCluster(cluster, progress.continueWithSubProgress(10), "sp");
        arf.findLargeItemSets(support, 2, progress.continueWithSubProgress(75), "sp");
        // all possible rules that satisfy the given confidence and
        // correlation coefficient are generated and added to the
        // transport
        // Model.
        ArrayList<AssociationRule> rules = arf.generateAssociationRules(confidence, 0.0);
        // setSchema
        TIntSet currentSchema = cleanedOntoM.getTypeToPredicateMap().get(s_clusterID);
        TIntSet oldSchema = om.getTypeToPredicateMap().get(s_clusterID);
        if (currentSchema != null) {
          tNode.setSchema(currentSchema);
        }
        if (oldSchema != null) {
          oldSchema.removeAll(tNode.getSchema());
          tNode.setOverspecifications(oldSchema);
        }
        // candidate set of current tNode
        TIntSet candidates = tNode.getCandidates();
        // collect candidates
        for (AssociationRule rule : rules) {
          int p_condition = rule.getCondition().get(0);
          int p_consequence = rule.getConsequence().get(0);
          if (cleanedOntoM.predicateIsSetForType(predicateToSubjectMapping.get(p_condition),
              s_clusterID)
              && !cleanedOntoM.predicateIsSetForType(predicateToSubjectMapping.get(p_consequence),
                  s_clusterID)) {
            // ad to candidates
            candidates.add(p_consequence);
            // if the predicate is removed from superclass because
            // of overspecification mark it
            if (om.predicateIsSetForType(predicateToSubjectMapping.get(p_consequence), s_clusterID)) {
              // identify the source of the predicate
              tNode.getPushedDownDandidates().put(
                  predicateToSubjectMapping.get(p_consequence),
                  om.getPredicateToSourceTypeMap()
                      .get(predicateToSubjectMapping.get(p_consequence)));
            }
          }
        }

        // check subclasses for the candidates
        for (int candidate : candidates.toArray()) {
          ArrayList<TaxonomyNode> nodesContainingCandidate =
              tNode.getBranchOccurrencesOfPredicate(candidate, predicateToSubjectMapping);
          if (nodesContainingCandidate.size() == 0) {
            // do nothing
          } else if (nodesContainingCandidate.size() == 1) {
            // remove candidate
            candidates.remove(candidate);
            tNode.getPushedDownDandidates().remove(predicateToSubjectMapping.get(candidate));
          } else {
            // store candidate
            // remove from candidates or suggest schema removal
            for (TaxonomyNode nodeContainingCandidate : nodesContainingCandidate) {
              if (nodeContainingCandidate.getSchema().contains(
                  predicateToSubjectMapping.get(candidate))) {
                nodeContainingCandidate.removeFromSchema(predicateToSubjectMapping.get(candidate));
              } else {
                nodeContainingCandidate.getCandidates().remove(candidate);
                nodeContainingCandidate.getPushedDownDandidates().remove(
                    predicateToSubjectMapping.get(candidate));
              }
            }
          }
        }

        tNode.setTouched(true);

      }
      // add restNodes that have not been considered.
      leafNodes = tTree.getParentNodes(leafNodes);
      leafNodes.addAll(restNodes);
      restNodes.clear();
    }

    // TODO write into the database
    loggResults(tTree);

    writeToDB(tm, progress, predicateToSubjectMapping);

  }

  private void writeToDB(TaxonomyManager tm, IProgress progress, TIntIntHashMap predicateToSourceMap) {
    try {
      ontologyWriter.createAlignment(source, tm, progress);

      ontologyWriter.writeOntologyChanges(source, tm, progress, predicateToSourceMap);
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private void identifyOverspecification(OntologyManager om, OntologyManager cleanedOntoM,
      IProgress progress, TaxonomyManager tm) {

    TIntIntHashMap predicateAndType = om.getPredicateToSourceTypeMap();
    TIntIntHashMap instanceCounts = new TIntIntHashMap();
    try {

      // PreparedStatement countStmt =
      // dbc.getPreparedStatement("select type , count(*) from (Select distinct type , "+table+".object from "+table+","+ontoTable+" where "+table+".object = "+ontoTable+".subject) group by type ");
      System.out.println("retrieve transactional database counts");
      instanceCounts = entityLoader.getInstanceCounts(source, progress);
      TIntObjectHashMap<TIntSet> p2tMap = cleanedOntoM.getPredicateToTypeMap();
      TIntObjectHashMap<TIntSet> t2pMap = cleanedOntoM.getTypeToPredicateMap();
      TIntIntHashMap prunedPredicateAndType = new TIntIntHashMap();
      TIntIntHashMap sToClusterMap = tm.getSubjectToClusterMap();
      for (int predicate : predicateAndType.keys()) {
        int s_id = predicateAndType.get(predicate);
        if (sToClusterMap.containsKey(s_id)) {
          prunedPredicateAndType.put(predicate, sToClusterMap.get(s_id));
        }
      }
      if (prunedPredicateAndType.isEmpty()) {
        return;
      }
      TIntIntHashMap predicateCounts =
          entityLoader.getPredicateCounts(source, progress, prunedPredicateAndType);
      for (int predicate : predicateCounts.keys()) {
        int freq = predicateCounts.get(predicate);
        int cluster = predicateAndType.get(predicate);

        int minfreq = (int) (support * instanceCounts.get(cluster));
        if (freq < minfreq) {
          TIntSet alltypes = p2tMap.remove(predicate);

          for (int type : alltypes.toArray()) {
            t2pMap.get(type).remove(predicate);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private void loggResults(TaxonomyTree tTree) {
    int nodeCount = 0;
    for (TaxonomyNode node : tTree.getNodePool().valueCollection()) {
      if (node.getCandidates().size() > 0 || node.getRemovedPredicates().size() > 0
          || !node.getOverspecifications().isEmpty()) {
        logger.info((++nodeCount) + "." + node);
      }
    }
  }

}
