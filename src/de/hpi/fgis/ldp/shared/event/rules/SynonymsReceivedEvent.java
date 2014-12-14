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
import de.hpi.fgis.ldp.shared.data.SynonymPairModel;

public class SynonymsReceivedEvent extends GwtEvent<SynonymsReceivedEventHandler> {
  public static Type<SynonymsReceivedEventHandler> TYPE = new Type<SynonymsReceivedEventHandler>();
  private final Cluster cluster;
  private ArrayList<SynonymPairModel> model = null;

  public SynonymsReceivedEvent(final Cluster cluster, final ArrayList<SynonymPairModel> model) {
    this.cluster = cluster;
    this.model = model;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  public ArrayList<SynonymPairModel> getModel() {
    return this.model;
  }

  @Override
  public Type<SynonymsReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final SynonymsReceivedEventHandler handler) {
    handler.onSynonymsReceived(this);
  }

}
