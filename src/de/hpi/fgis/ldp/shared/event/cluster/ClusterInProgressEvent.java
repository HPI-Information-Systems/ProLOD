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

package de.hpi.fgis.ldp.shared.event.cluster;

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ClusterInProgressEvent extends GwtEvent<ClusterInProgressEventHandler> {
  public static Type<ClusterInProgressEventHandler> TYPE =
      new Type<ClusterInProgressEventHandler>();

  private final Cluster cluster;

  public ClusterInProgressEvent(final Cluster cluster) {
    this.cluster = cluster;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  @Override
  public Type<ClusterInProgressEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final ClusterInProgressEventHandler handler) {
    handler.onClusterInProgress(this);
  }

}
