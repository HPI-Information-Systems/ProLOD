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

import de.hpi.fgis.ldp.shared.event.AbstractDataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;

public class ClusterInfoReceivedEvent extends
    AbstractDataTableReceivedEvent<ClusterInfoReceivedEvent> {
  public static Type<DataTableReceivedEvent.Handler<ClusterInfoReceivedEvent>> TYPE =
      new Type<DataTableReceivedEvent.Handler<ClusterInfoReceivedEvent>>();

  @Override
  public Type<DataTableReceivedEvent.Handler<ClusterInfoReceivedEvent>> getAssociatedType() {
    return TYPE;
  }
}
