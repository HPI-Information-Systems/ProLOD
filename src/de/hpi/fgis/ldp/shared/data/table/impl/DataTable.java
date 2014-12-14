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

package de.hpi.fgis.ldp.shared.data.table.impl;

import java.util.ArrayList;

import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * this is the default implementation of the {@link IDataTable} interface
 * 
 * @author toni.gruetze
 * 
 */
public class DataTable implements IDataTable {
  private static final long serialVersionUID = -7425453931018698012L;
  private ArrayList<IDataColumn<?>> columns;

  protected DataTable() {
    // hide default constructor
  }

  /**
   * creates a new {@link DataTable} instance with the given primary key column
   * 
   * @param primaryColumn the primary key column
   */
  public DataTable(IDataColumn<?> primaryColumn) {
    this.columns = new ArrayList<IDataColumn<?>>();
    this.columns.add(primaryColumn);
  }

  /**
   * adds the given column to this {@link IDataTable} instance
   * 
   * @param column the column to be added
   */
  public void addColumn(IDataColumn<?> column) {
    this.columns.add(column);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataTable#getColumn(int)
   */
  @Override
  public IDataColumn<?> getColumn(int columnIndex) {
    return this.columns.get(columnIndex);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataTable#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return this.columns.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataTable#getRowCount()
   */
  @Override
  public int getRowCount() {
    return this.columns.get(0).getElementCount();
  }

}
