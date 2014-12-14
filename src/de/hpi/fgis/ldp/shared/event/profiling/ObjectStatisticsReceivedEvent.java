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

import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.event.AbstractDatatypeDataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;

public class ObjectStatisticsReceivedEvent extends
    AbstractDatatypeDataTableReceivedEvent<ObjectStatisticsReceivedEvent> {
  public static Type<DataTableReceivedEvent.Handler<ObjectStatisticsReceivedEvent>> TYPE =
      new Type<DataTableReceivedEvent.Handler<ObjectStatisticsReceivedEvent>>();

  private Pattern pattern;

  @Override
  public Type<DataTableReceivedEvent.Handler<ObjectStatisticsReceivedEvent>> getAssociatedType() {
    return TYPE;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public Pattern getPattern() {
    return pattern;
  }
}
