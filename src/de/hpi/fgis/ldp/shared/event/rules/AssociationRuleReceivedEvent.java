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

import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.Cluster;

public class AssociationRuleReceivedEvent extends GwtEvent<AssociationRuleReceivedEventHandler> {
  public static Type<AssociationRuleReceivedEventHandler> TYPE =
      new Type<AssociationRuleReceivedEventHandler>();
  private final Cluster cluster;
  private ArrayList<AssociationRuleModel> model = null;

  public AssociationRuleReceivedEvent(final Cluster cluster,
      final ArrayList<AssociationRuleModel> model) {
    this.cluster = cluster;
    this.model = model;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  public ArrayList<AssociationRuleModel> getModel() {
    return this.model;
  }

  @Override
  public Type<AssociationRuleReceivedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final AssociationRuleReceivedEventHandler handler) {
    handler.onAssociationRuleReceived(this);
  }

}
