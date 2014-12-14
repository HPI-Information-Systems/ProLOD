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

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ClusterChildrenRequestResult implements Result, IsSerializable {
  private static final long serialVersionUID = 5995050105013074573L;
  private ArrayList<Cluster> clusters = null;

  protected ClusterChildrenRequestResult() {
    // hide default constructor
  }

  public ClusterChildrenRequestResult(final ArrayList<Cluster> clusters) {

    this.clusters = clusters;
  }

  public ArrayList<Cluster> getClusters() {
    // list of Clusters
    return this.clusters;
  }
}
