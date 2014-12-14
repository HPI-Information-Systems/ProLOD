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

public class SubclusterEvent extends GwtEvent<SubclusterEventHandler> {
  public static Type<SubclusterEventHandler> TYPE = new Type<SubclusterEventHandler>();
  private final Cluster parent;
  private ArrayList<Cluster> children = null;

  public SubclusterEvent(final Cluster parent, final ArrayList<Cluster> children) {
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
  public Type<SubclusterEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final SubclusterEventHandler handler) {
    handler.onSubclusterReceived(this);
  }

}
