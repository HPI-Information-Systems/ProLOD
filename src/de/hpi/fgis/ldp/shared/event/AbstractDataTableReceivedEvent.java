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

package de.hpi.fgis.ldp.shared.event;

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

public abstract class AbstractDataTableReceivedEvent<E extends AbstractDataTableReceivedEvent<?>>
    extends GwtEvent<DataTableReceivedEvent.Handler<E>> implements DataTableReceivedEvent {
  private Cluster cluster;
  private IDataTable dataTable;
  private int offset = -1;

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  @Override
  public IDataTable getDataTable() {
    return dataTable;
  }

  public void setDataTable(IDataTable dataTable) {
    this.dataTable = dataTable;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void dispatch(final DataTableReceivedEvent.Handler<E> handler) {
    handler.onDataTableReceived((E) this);
  }
}
