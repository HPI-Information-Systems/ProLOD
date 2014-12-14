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

package de.hpi.fgis.ldp.shared.rpc.cluster;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ClusterMergeRequest implements Action<ClusterMergeResult>, IsSerializable {
  private static final long serialVersionUID = -7878825748448717948L;
  private Cluster parent = null;
  private ArrayList<Cluster> clusters = null;

  protected ClusterMergeRequest() {
    // hide default constructor
  }

  public ClusterMergeRequest(final Cluster parent, final ArrayList<Cluster> clusters) {
    this.parent = parent;
    this.clusters = clusters;
  }

  public Cluster getParent() {
    return this.parent;
  }

  public ArrayList<Cluster> getClusters() {
    return this.clusters;
  }
}
