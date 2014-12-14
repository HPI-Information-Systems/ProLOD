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

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class RenameClusterRequest implements Action<RenameClusterResult>, IsSerializable {
  private static final long serialVersionUID = 3405144325771892012L;
  private Cluster cluster = null;

  protected RenameClusterRequest() {
    // hide default constructor
  }

  public RenameClusterRequest(final Cluster cluster) {
    this.cluster = cluster;
  }

  public Cluster getCluster() {
    return this.cluster;
  }
}
