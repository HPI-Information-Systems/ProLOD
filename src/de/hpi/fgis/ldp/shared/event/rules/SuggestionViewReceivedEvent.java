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

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.SuggestionSetModel;

public class SuggestionViewReceivedEvent extends GwtEvent<SuggestionViewReceivedEventHandler> {
  public static Type<SuggestionViewReceivedEventHandler> TYPE =
      new Type<SuggestionViewReceivedEventHandler>();
  private final Cluster cluster;
  private SuggestionSetModel model = null;

  public SuggestionViewReceivedEvent(final Cluster cluster, final SuggestionSetModel model) {
    this.cluster = cluster;
    this.model = model;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  public SuggestionSetModel getModel() {
    return this.model;
  }

  @Override
  public Type<SuggestionViewReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final SuggestionViewReceivedEventHandler handler) {
    handler.onSuggestionViewReceived(this);
  }
}
