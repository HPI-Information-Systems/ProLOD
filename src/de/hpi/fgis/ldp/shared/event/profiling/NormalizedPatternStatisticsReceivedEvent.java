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

import de.hpi.fgis.ldp.shared.event.AbstractDatatypeDataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;

public class NormalizedPatternStatisticsReceivedEvent extends
    AbstractDatatypeDataTableReceivedEvent<NormalizedPatternStatisticsReceivedEvent> {
  public static Type<DataTableReceivedEvent.Handler<NormalizedPatternStatisticsReceivedEvent>> TYPE =
      new Type<DataTableReceivedEvent.Handler<NormalizedPatternStatisticsReceivedEvent>>();

  @Override
  public Type<DataTableReceivedEvent.Handler<NormalizedPatternStatisticsReceivedEvent>> getAssociatedType() {
    return TYPE;
  }
}
