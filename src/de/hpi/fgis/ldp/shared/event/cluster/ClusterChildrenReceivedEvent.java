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

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ClusterChildrenReceivedEvent extends GwtEvent<ClusterChildrenReceivedEventHandler> {
  public static Type<ClusterChildrenReceivedEventHandler> TYPE =
      new Type<ClusterChildrenReceivedEventHandler>();
  // FIXME move package to client package instead of shared
  private final Cluster parent;
  private final ArrayList<Cluster> children;

  public ClusterChildrenReceivedEvent(final Cluster parent, final ArrayList<Cluster> children) {
    this.parent = parent;
    this.children = children;
  }

  public Cluster getParent() {
    return this.parent;
  }

  public ArrayList<Cluster> getChildren() {
    return this.children;
  }

  @Override
  public Type<ClusterChildrenReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final ClusterChildrenReceivedEventHandler handler) {
    handler.onChildrenReceived(this);
  }

}
