/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.util.job;

import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * repository for job names
 * 
 * @author toni.gruetze
 *
 */
public class JobNameSource {
  /**
   * gets the name of a job for a association rule calculation
   * 
   * @param cluster the cluster to get association rules for
   * @return the name of the job
   */
  public String getARJobName(Cluster cluster) {
    return this.getJobName(cluster) + "_AssociationRule";
  }

  /**
   * gets the name of a job for a synonym calculation
   * 
   * @param cluster the cluster to get association rules for
   * @return the name of the job
   */
  public String getSynonymsJobName(Cluster cluster) {
    return this.getJobName(cluster) + "_Synonyms";
  }

  /**
   * gets the name of a job of a cluster action
   * 
   * @param cluster the cluster
   * @return the name of the job
   */
  public String getJobName(Cluster cluster) {
    if (cluster == null) {
      return "[no valid cluster specified]";
    }
    // String looks as follows: ClusterAction_<schema>_<cluster_id>
    StringBuilder progressName =
        new StringBuilder("ClusterAction_").append(cluster.getDataSource().getLabel()).append("_");
    // root cluster in progress means no id available
    if (cluster.getId() < 0) {
      // root
      progressName.append("ROOT");
    } else {
      // normal cluster
      progressName.append(cluster.getId());
    }

    return progressName.toString();
  }
}
