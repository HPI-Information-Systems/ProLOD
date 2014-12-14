package de.hpi.fgis.ldp.client.util.dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SuggestionList {
  ArrayList<String> suggestionValues = new ArrayList<String>();
  HashMap<String, Double> confidenceList = new HashMap<String, Double>();

  public void addSuggestion(String value, double confidence) {
    suggestionValues.add(value);
    confidenceList.put(value, confidence);

  }

  public void sortSuggestions() {
    Collections.sort(suggestionValues, new SuggestionComparator());
  }

  public ArrayList<String> getSuggestionsWithConfidence(double confidence) {
    int i;
    for (i = suggestionValues.size() - 1; i >= 0; i--) {
      if (confidenceList.get(suggestionValues.get(i)) < confidence) {
        break;
      }
    }
    if (i == 0) {
      return suggestionValues;
    }
    return (ArrayList<String>) suggestionValues.subList(i + 1, suggestionValues.size());
  }

  public ArrayList<String> getSuggestionvalues() {
    return suggestionValues;
  }

  public void setSuggestionvalues(ArrayList<String> suggestionvalues) {
    this.suggestionValues = suggestionvalues;
  }

  public HashMap<String, Double> getConfidenceList() {
    return confidenceList;
  }

  public void setConfidenceList(HashMap<String, Double> confidenceList) {
    this.confidenceList = confidenceList;
  }

  public List<String> getTopSuggestions(int k) {
    if (k > suggestionValues.size()) {
      k = suggestionValues.size();
    }
    List<String> topSuggestions = new ArrayList<String>();

    topSuggestions.addAll(suggestionValues.subList(suggestionValues.size() - k,
        suggestionValues.size()));
    return topSuggestions;
  }

  public int getPositionOfValue(String value) {
    for (int k = suggestionValues.size() - 1; k >= 0; k--) {
      if (suggestionValues.get(k).equals(value)) {
        return k + 1;
      }
    }

    return 0;

  }

  private class SuggestionComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {

      double distance = confidenceList.get(o1) - confidenceList.get(o2);
      if (distance < 0) {
        return -1;
      }
      if (distance == 0) {
        return 0;
      }
      return 1;

    }

  }

}
