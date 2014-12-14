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

/**
 * this is the default implementation of the {@link IDataColumn} interface
 * 
 * @author toni.gruetze
 * 
 * @param <T> the data type of all values within this column
 */
public class DataColumn<T> implements IDataColumn<T> {
  private static final long serialVersionUID = 4836752209434582040L;

  private ArrayList<T> elements;
  private String label;
  private boolean visible;

  protected DataColumn() {
    // hide default constructor
  }

  /**
   * creates a new {@link DataColumn} instance with the label
   * 
   * @param label the label of the column
   * @param visible indicates if the column is visible per default
   */
  public DataColumn(String label, boolean visible) {
    this.label = label;
    this.visible = visible;
    this.elements = new ArrayList<T>();
  }

  /**
   * sets the element value in this column
   * 
   * @param rowIndex the index of the row
   * @param element the value of the cell
   */
  public void setElement(int rowIndex, T element) {
    while (this.elements.size() <= rowIndex) {
      this.elements.add(null);
    }
    this.elements.set(rowIndex, element);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#getElement(int)
   */
  @Override
  public T getElement(int rowIndex) {
    if (rowIndex >= this.elements.size()) {
      return null;
    }

    return this.elements.get(rowIndex);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#getLabel()
   */
  @Override
  public String getLabel() {
    return this.label;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#isVisible()
   */
  @Override
  public boolean isVisible() {
    return this.visible;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#getElementCount()
   */
  @Override
  public int getElementCount() {
    return this.elements.size();
  }
}
