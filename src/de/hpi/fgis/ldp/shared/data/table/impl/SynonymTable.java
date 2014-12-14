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

import de.hpi.fgis.ldp.shared.data.SynonymPairModel;
import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * this is a implementation of the {@link IDataTable} interface to store and represent synonyms (
 * {@link SynonymPairModel} instances)
 * 
 * @author ziawasch.abedjan
 * 
 */
public class SynonymTable implements IDataTable {
  private static final long serialVersionUID = -3048678029132741580L;

  protected ArrayList<SynonymPairModel> models;

  protected IDataColumn<?>[] columns = new IDataColumn<?>[] {
      new ColumnMapper<String>("First Predicate") {
        private static final long serialVersionUID = 1L;

        @Override
        public String getElement(int rowIndex) {
          return models.get(rowIndex).getCondition();
        }
      }, new ColumnMapper<String>("Second Predicate") {
        private static final long serialVersionUID = 1L;

        @Override
        public String getElement(int rowIndex) {
          return models.get(rowIndex).getConsequence();
        }
      }, new ColumnMapper<Integer>("Frequency") {
        private static final long serialVersionUID = 1L;

        @Override
        public Integer getElement(int rowIndex) {
          return Integer.valueOf(models.get(rowIndex).getFrequency());
        }
      }, new ColumnMapper<Double>("Correllation") {
        private static final long serialVersionUID = 1L;

        @Override
        public Double getElement(int rowIndex) {
          return Double.valueOf(models.get(rowIndex).getCorrelationCoefficient());
        }
      }};

  protected SynonymTable() {
    // hide default constructor
  }

  /**
   * creates a new {@link SynonymTable}
   * 
   * @param models the models to be represented within this table
   */
  public SynonymTable(ArrayList<SynonymPairModel> models) {
    this.models = models;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataTable#getColumn(int)
   */
  @Override
  public IDataColumn<?> getColumn(int columnIndex) {
    return this.columns[columnIndex];
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataTable#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return this.columns.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.table.IDataTable#getRowCount()
   */
  @Override
  public int getRowCount() {
    return this.models.size();
  }

  /**
   * gets the sample subject and objects of a association rule
   * 
   * @param rowIndex the row index of the association rule
   * @return a table with the sample subjects AND objects
   */
  public IDataTable getSubjectObjects(int rowIndex) {
    DataTable result = this.models.get(rowIndex).getDetails();
    return result;
  }

  /**
   * a mapper for columns of this class
   * 
   * @author toni.gruetze
   * 
   * @param <T> the data type of this column
   */
  protected abstract class ColumnMapper<T> implements IDataColumn<T> {
    private static final long serialVersionUID = 1L;
    private final String label;

    /**
     * creates a new column mapper
     * 
     * @param label the label of the new column
     */
    public ColumnMapper(String label) {
      this.label = label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#getElementCount()
     */
    @Override
    public int getElementCount() {
      return models.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#getLabel()
     */
    @Override
    public String getLabel() {
      return label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.shared.data.table.IDataColumn#isVisible()
     */
    @Override
    public boolean isVisible() {
      return true;
    }
  }

}
