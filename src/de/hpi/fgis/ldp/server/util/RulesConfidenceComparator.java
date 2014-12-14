package de.hpi.fgis.ldp.server.util;

import java.util.Comparator;

import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;

public class RulesConfidenceComparator implements Comparator<AssociationRuleModel> {

  @Override
  public int compare(AssociationRuleModel arg0, AssociationRuleModel arg1) {

    double value = arg0.getConfidence() - arg1.getConfidence();
    if (value == 0.0) {
      return 0;
    }
    if (value > 0.0) {
      return 1;
    } else {
      return -1;
    }
  }

}
