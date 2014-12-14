package de.hpi.fgis.ldp.server.algorithms.factgeneration;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.AssociationRule;
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.persistency.loading.IFactGenerationLoader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;

public class EntityEnricher {
  @Inject
  private IFactGenerationLoader factLoader;

  private static final Logger logger = Logger
      .getLogger(EntityEnricher.class.getPackage().getName());

  private ArrayList<AssociationRule> predicateRules;
  private ArrayList<AssociationRule> objectRules;
  private Cluster cluster;

  private TIntObjectHashMap<TIntDoubleHashMap> predicateMatrix;
  private TIntObjectHashMap<TIntHashSet> objectToConditionsMap;

  private int totalCount;

  public void init(ArrayList<AssociationRule> predicateRules,
      ArrayList<AssociationRule> objectRules, Cluster cluster) {
    this.predicateRules = predicateRules;
    predicateMatrix = new TIntObjectHashMap<TIntDoubleHashMap>();
    objectToConditionsMap = new TIntObjectHashMap<TIntHashSet>();
    totalCount = 0;
    if (objectRules != null) {
      this.objectRules = new ArrayList<AssociationRule>();
      for (AssociationRule rule : objectRules) {
        if (rule.getConfidence() < 1.0) {
          this.objectRules.add(rule);
          int consequence = rule.getConsequence().get(0);
          int condition = rule.getCondition().get(0);
          if (objectToConditionsMap.containsKey(consequence)) {
            objectToConditionsMap.get(consequence).add(condition);
          } else {
            TIntHashSet newConditions = new TIntHashSet();
            newConditions.add(condition);
            objectToConditionsMap.put(consequence, newConditions);
          }
        }
      }
      System.out.println("high-confidence Object Rules: " + this.objectRules.size());
      logger.info("high-confidence Object Rules: " + this.objectRules.size());
    }

    this.cluster = cluster;
    if (predicateRules != null) {
      createPredicateMatrix();
    }

  }

  private void createPredicateMatrix() {
    // map conditions to all existing consequences and map each to its
    // confidence
    for (AssociationRule rule : predicateRules) {
      if (predicateMatrix.containsKey(rule.getCondition().get(0))) {
        TIntDoubleHashMap consequenceToConfidence = predicateMatrix.get(rule.getCondition().get(0));
        consequenceToConfidence.put(rule.getConsequence().get(0), rule.getConfidence());
      } else {
        TIntDoubleHashMap consequenceToConfidence = new TIntDoubleHashMap();
        consequenceToConfidence.put(rule.getConsequence().get(0), rule.getConfidence());
        predicateMatrix.put(rule.getCondition().get(0), consequenceToConfidence);
      }
    }

  }

  public TIntList enrichData(IProgress progress) {
    TIntList newFacts = new TIntArrayList();
    // retrieveTotalFrequencies();
    for (int objectConsequence : objectToConditionsMap.keys()) {

      TIntObjectHashMap<int[]> entities =
          retrieveSubjects(progress, objectToConditionsMap.get(objectConsequence),
              objectConsequence);
      int[] designators = retrieveDesignators(progress, objectConsequence);
      for (int subject : entities.keys()) {
        int[] schema = entities.get(subject);
        totalCount++;
        int designator = getTopDesignator(designators, schema);
        if (designator == -1) {
          continue;
        }

        newFacts.add(subject);
        newFacts.add(designator);
        newFacts.add(objectConsequence);
        if (totalCount % 50 == 0) {
          System.out.println(subject + "     " + designator + "    " + objectConsequence);
        }
        // TODO retrieve new tuples.
      }
    }
    return newFacts;

  }

  private int getTopDesignator(int[] designators, int[] schema) {
    Arrays.sort(schema);
    double[] ratings = new double[designators.length];
    boolean ratingsExist = false;
    for (int i = 0; i < designators.length; i++) {
      // if (!containsEnough(schema, designators[i])) {
      ratings[i] = retrieveRating(schema, designators[i]);
      if (ratings[i] > 0.0) {
        ratingsExist = true;
      }
      // }else{
      // ratings[i] = 0.0;
      // }
      // }
    }
    if (!ratingsExist) {
      // norating++;
      return -1;
    }
    // retrieveMaxpos

    int pos = retrieveMaxrating(ratings);

    return designators[pos];
  }

  private int retrieveMaxrating(double[] ratings) {
    int temp = 0;
    for (int i = 1; i < ratings.length; i++) {
      if (ratings[i] > ratings[temp]) {
        temp = i;
      }
    }
    return temp;
  }

  private double retrieveRating(int[] schema, int designator) {
    double meanConfidence = 0;
    for (int predicate : schema) {
      if (predicateMatrix.containsKey(predicate)) {
        TIntDoubleHashMap temp = predicateMatrix.get(predicate);
        if (temp.containsKey(designator)) {
          meanConfidence = temp.get(designator) + meanConfidence;
        }
      }
    }

    return meanConfidence / schema.length;
  }

  private int[] retrieveDesignators(IProgress progress, int consequence) {
    int[] designators = null;
    try {
      designators = factLoader.getDesignators(this.cluster, consequence, progress);
    } catch (SQLException e) {
      logger.error("Unable to get designator predicates!", e);
    }
    return designators;

  }

  private TIntObjectHashMap<int[]> retrieveSubjects(IProgress progress, TIntHashSet objects,
      int missingObject) {
    if (this.factLoader == null) {
      throw new IllegalStateException("Please inject a IFactGenerationLoader instance!");
    }

    TIntObjectHashMap<int[]> entitiySchemaMap = new TIntObjectHashMap<int[]>();
    try {
      IEntitySchemaList entities = null;
      // FIXME handle entities==null;
      TIntIterator objectIterator = objects.iterator();
      while (objectIterator.hasNext()) {
        if (this.cluster.getId() < 0) {
          entities =
              factLoader.getEntityList(this.cluster.getDataSource(), objectIterator.next(),
                  missingObject, progress);
        } else {
          entities =
              factLoader
                  .getEntityList(this.cluster, objectIterator.next(), missingObject, progress);
        }
        if (entities == null) {
          continue;
        }
        for (int i = 0; i < entities.getEntityCount(); i++) {
          entitiySchemaMap.putIfAbsent(entities.getEntityID(i), entities.getSchema(i));
        }
      }

    } catch (SQLException e) {
      logger.error("Unable to get violating subjects!", e);
    }

    return entitiySchemaMap;
  }

  // public void closeAll() throws SQLException {
  //
  // subjectRetrieveStatement.close();
  // schemaRetrieveStatement.close();
  // designatorRetrieveStatement.close();
  // subjectsWithBothRetrieveStatement.close();
  // desiredPredicateRetrieveStatement.close();
  //
  // }

}
