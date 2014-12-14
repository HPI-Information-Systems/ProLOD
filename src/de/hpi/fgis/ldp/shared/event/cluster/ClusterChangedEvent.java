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

public class ClusterChangedEvent extends GwtEvent<ClusterChangedEventHandler> {
  public static Type<ClusterChangedEventHandler> TYPE = new Type<ClusterChangedEventHandler>();

  private final Cluster cluster;

  public ClusterChangedEvent(final Cluster cluster) {
    this.cluster = cluster;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  @Override
  public Type<ClusterChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final ClusterChangedEventHandler handler) {
    handler.onClusterChanged(this);
  }

}
