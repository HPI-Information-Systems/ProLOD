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

package de.hpi.fgis.ldp.shared.data.table;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * this is a abstract model of a data table
 * 
 * @author toni.gruetze
 * 
 */
public interface IDataTable extends IsSerializable, Serializable {
  /**
   * gets the amount of rows within this table instance
   * 
   * @return the amount of rows
   */
  public int getRowCount();

  /**
   * gets the amount of columns within this table instance
   * 
   * @return the amount of columns
   */
  public int getColumnCount();

  /**
   * gets a specific column
   * 
   * @param columnIndex the index of the column to get
   * @return the chosen column
   */
  public IDataColumn<?> getColumn(int columnIndex);
}
