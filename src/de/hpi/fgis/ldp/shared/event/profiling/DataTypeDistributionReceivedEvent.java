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

package de.hpi.fgis.ldp.shared.event.profiling;

import de.hpi.fgis.ldp.shared.event.AbstractPredicateDataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;

public class DataTypeDistributionReceivedEvent extends
    AbstractPredicateDataTableReceivedEvent<DataTypeDistributionReceivedEvent> {
  public static Type<DataTableReceivedEvent.Handler<DataTypeDistributionReceivedEvent>> TYPE =
      new Type<DataTableReceivedEvent.Handler<DataTypeDistributionReceivedEvent>>();

  @Override
  public Type<DataTableReceivedEvent.Handler<DataTypeDistributionReceivedEvent>> getAssociatedType() {
    return TYPE;
  }
}
