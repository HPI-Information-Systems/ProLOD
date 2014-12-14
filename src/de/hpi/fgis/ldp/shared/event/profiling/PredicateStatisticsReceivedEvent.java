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

import de.hpi.fgis.ldp.shared.event.AbstractDataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;

public class PredicateStatisticsReceivedEvent extends
    AbstractDataTableReceivedEvent<PredicateStatisticsReceivedEvent> {
  public static Type<DataTableReceivedEvent.Handler<PredicateStatisticsReceivedEvent>> TYPE =
      new Type<DataTableReceivedEvent.Handler<PredicateStatisticsReceivedEvent>>();

  @Override
  public Type<DataTableReceivedEvent.Handler<PredicateStatisticsReceivedEvent>> getAssociatedType() {
    return TYPE;
  }
}
