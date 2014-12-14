package de.hpi.fgis.ldp.client.util.dataStructures;

import java.util.HashMap;
import java.util.List;

import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.SuggestionSetModel;

public class RuleMatrixManager {

  private static final double minConf = 0.1;
  private HashMap<String, HashMap<String, Double>> predicateMatrix =
      new HashMap<String, HashMap<String, Double>>();
  private HashMap<String, HashMap<String, Double>> objectMatrix =
      new HashMap<String, HashMap<String, Double>>();

  public RuleMatrixManager(SuggestionSetModel model) {

    List<AssociationRuleModel> predicateRules = model.getPredicateRules();
    List<AssociationRuleModel> objectRules = model.getObjectRules();
    predicateMatrix = createRuleMatrix(predicateRules);
    objectMatrix = createRuleMatrix(objectRules);
  }

  private HashMap<String, HashMap<String, Double>> createRuleMatrix(List<AssociationRuleModel> rules) {
    // map conditions to all existing consequences and map each to its
    // confidence
    HashMap<String, HashMap<String, Double>> predicateMatrix =
        new HashMap<String, HashMap<String, Double>>();
    for (AssociationRuleModel rule : rules) {
      if (predicateMatrix.containsKey(rule.getCondition())) {
        HashMap<String, Double> consequenceToConfidence = predicateMatrix.get(rule.getCondition());
        consequenceToConfidence.put(rule.getConsequence(), rule.getConfidence());
      } else {
        HashMap<String, Double> consequenceToConfidence = new HashMap<String, Double>();
        consequenceToConfidence.put(rule.getConsequence(), rule.getConfidence());
        predicateMatrix.put(rule.getCondition(), consequenceToConfidence);
      }
    }
    return predicateMatrix;

  }

  public SuggestionList generatePredicateSugestions(List<String> existingItems) {
    return generateSuggestions(predicateMatrix, existingItems);
  }

  public SuggestionList generateObjectSugestions(List<String> existingItems) {
    return generateSuggestions(objectMatrix, existingItems);
  }

  private SuggestionList generateSuggestions(HashMap<String, HashMap<String, Double>> targetMatrix,
      List<String> existingItems) {
    // retrieve all rules that have remainingPredicates as antecedents
    SuggestionList candidates = new SuggestionList();
    HashMap<String, Double> consequence2ConfidenceMap = new HashMap<String, Double>();
    HashMap<String, Double> suggestion2ConfidenceMap = new HashMap<String, Double>();
    // aggregate the confidence values for each consequent that has an
    // schema element as its antecedent
    for (String predicate : existingItems) {
      if (!targetMatrix.containsKey(predicate)) {
        continue;
      }
      consequence2ConfidenceMap = targetMatrix.get(predicate);
      for (String consequence : consequence2ConfidenceMap.keySet()) {
        // if (!remainingPredicates.contains(consequence))
        if (suggestion2ConfidenceMap.containsKey(consequence)) {
          suggestion2ConfidenceMap.put(consequence, suggestion2ConfidenceMap.get(consequence)
              + consequence2ConfidenceMap.get(consequence));

        } else {
          suggestion2ConfidenceMap.put(consequence, consequence2ConfidenceMap.get(consequence));
        }
      }
    }
    // TODO chose consequences with rules above a threshold
    double normalizedMinconf = minConf * existingItems.size();
    for (String candidate : suggestion2ConfidenceMap.keySet()) {
      double conf = suggestion2ConfidenceMap.get(candidate);
      if (conf > normalizedMinconf) {
        candidates.addSuggestion(candidate, conf);
      }
    }
    return candidates;
  }

}
