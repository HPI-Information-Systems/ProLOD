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

package de.hpi.fgis.ldp.client.mvp.clustertree;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ClusterTreeDataModel extends BaseTreeModel {
  private static final long serialVersionUID = -168134665165708937L;
  private Cluster cluster;
  private String key;

  protected ClusterTreeDataModel() {
    // hide default constructor
  }

  public ClusterTreeDataModel(final Cluster cluster) {
    super();
    this.cluster = cluster;

    if (cluster.getId() < 0) {
      this.key = "node_root_" + cluster.getDataSource().getLabel();
    } else {
      this.key = "node_" + cluster.getId() + "_" + cluster.getDataSource().getLabel();
    }

    final String userView =
        (this.cluster.getDataSource().getUserView() == null) ? "[default]" : this.cluster
            .getDataSource().getUserView();
    final String label = this.cluster.getLabel();
    this.set("label", label);
    this.set("userview", userView);
    this.set("size", Integer.valueOf(this.cluster.getSize()));
    this.set("clusterid", Integer.valueOf(this.cluster.getId()));
    this.set("id", this.key);
    this.set("childSession", Integer.valueOf(this.cluster.getChildSessionID()));
    final String displayname =
        ((label != null ? label.trim() : null) + " (" + this.cluster.getSize() + ")").replaceAll(
            "<", "&lt;").replaceAll(">", "&gt;")
            + " @ " + userView;

    this.set("displayname", displayname);
  }

  /**
   * gets the key of the {@link ClusterTreeDataModel}
   * 
   * @return the key
   */
  public String getKey() {
    return this.key;
  }

  /**
   * gets the cluster on which this model is based on
   * 
   * @return the cluster instance
   */
  public Cluster getCluster() {
    return this.cluster;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cluster == null) ? 0 : cluster.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ClusterTreeDataModel other = (ClusterTreeDataModel) obj;
    if (cluster == null) {
      if (other.cluster != null) {
        return false;
      }
    } else if (!cluster.equals(other.cluster)) {
      return false;
    }
    return true;
  }
}
