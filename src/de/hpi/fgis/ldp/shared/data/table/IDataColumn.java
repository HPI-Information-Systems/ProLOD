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
 * this is a abstract model of a column of a {@link IDataTable}
 * 
 * @author toni.gruetze
 *
 * @param <T> the data type of this colunm (the data type of all cell values)
 */
public interface IDataColumn<T> extends IsSerializable, Serializable {
  /**
   * gets the label (header) of this column
   * 
   * @return the label of the column
   */
  public String getLabel();

  /**
   * checks if the column is visible (default)
   * 
   * @return <code>true</code> if the column is visible otherwise <code>false</code>
   */
  public boolean isVisible();

  /**
   * gets the value of the specified cell in this column
   * 
   * @param rowIndex the row index of the cell
   * @return the cell value
   */
  public T getElement(int rowIndex);

  /**
   * gets the amount of cell within this column
   * 
   * @return the amount of cells
   */
  public int getElementCount();
}
