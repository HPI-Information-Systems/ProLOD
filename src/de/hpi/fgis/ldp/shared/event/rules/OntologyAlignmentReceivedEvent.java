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
import de.hpi.fgis.ldp.shared.data.OntologyAlignmentModel;

public class OntologyAlignmentReceivedEvent extends GwtEvent<OntologyAlignmentReceivedEventHandler> {
  public static Type<OntologyAlignmentReceivedEventHandler> TYPE =
      new Type<OntologyAlignmentReceivedEventHandler>();
  private final Cluster cluster;
  private OntologyAlignmentModel model = null;

  public OntologyAlignmentReceivedEvent(final Cluster cluster, final OntologyAlignmentModel model) {
    this.cluster = cluster;
    this.model = model;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  public OntologyAlignmentModel getModel() {
    return this.model;
  }

  @Override
  public Type<OntologyAlignmentReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final OntologyAlignmentReceivedEventHandler handler) {
    handler.onOntologyAlignmentReceived(this);
  }

}
