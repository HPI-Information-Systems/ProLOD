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
import de.hpi.fgis.ldp.shared.data.InversePredicateModel;

public class AntonymReceivedEvent extends GwtEvent<AntonymReceivedEventHandler> {
  public static Type<AntonymReceivedEventHandler> TYPE = new Type<AntonymReceivedEventHandler>();
  private final Cluster cluster;
  private ArrayList<InversePredicateModel> antonyms = null;

  public AntonymReceivedEvent(final Cluster cluster, final ArrayList<InversePredicateModel> antonyms) {
    this.cluster = cluster;
    this.antonyms = antonyms;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  public ArrayList<InversePredicateModel> getAntonyms() {
    return this.antonyms;
  }

  @Override
  public Type<AntonymReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final AntonymReceivedEventHandler handler) {
    handler.onAntonymReceived(this);
  }

}
