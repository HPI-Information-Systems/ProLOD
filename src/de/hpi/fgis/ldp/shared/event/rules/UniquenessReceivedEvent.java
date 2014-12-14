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

package de.hpi.fgis.ldp.shared.event.rules;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.UniquenessModel;

public class UniquenessReceivedEvent extends GwtEvent<UniquenessReceivedEventHandler> {
  public static Type<UniquenessReceivedEventHandler> TYPE =
      new Type<UniquenessReceivedEventHandler>();
  private final Cluster cluster;
  private ArrayList<UniquenessModel> uniqueness = null;

  public UniquenessReceivedEvent(final Cluster cluster, final ArrayList<UniquenessModel> arrayList) {
    this.cluster = cluster;
    this.uniqueness = arrayList;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  public ArrayList<UniquenessModel> getUniqueness() {
    return this.uniqueness;
  }

  @Override
  public Type<UniquenessReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final UniquenessReceivedEventHandler handler) {
    handler.onUniquenessReceived(this);
  }

}
