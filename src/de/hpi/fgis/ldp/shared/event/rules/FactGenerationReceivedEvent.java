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

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;
import de.hpi.fgis.ldp.shared.event.AbstractDataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;

public class FactGenerationReceivedEvent extends
    AbstractDataTableReceivedEvent<FactGenerationReceivedEvent> {
  public static Type<DataTableReceivedEvent.Handler<FactGenerationReceivedEvent>> TYPE =
      new Type<DataTableReceivedEvent.Handler<FactGenerationReceivedEvent>>();

  public FactGenerationReceivedEvent(Cluster cluster, DataTable data) {
    setDataTable(data);
    setCluster(cluster);
  }

  @Override
  public Type<DataTableReceivedEvent.Handler<FactGenerationReceivedEvent>> getAssociatedType() {
    return TYPE;
  }
}
