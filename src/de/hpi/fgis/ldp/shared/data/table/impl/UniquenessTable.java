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

import de.hpi.fgis.ldp.shared.data.UniquenessModel;
import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * this is a implementation of the {@link IDataTable} interface to store and represent uniqueness (
 * {@link UniquenessModel} instances)
 * 
 * @author anja.jentzsch
 * 
 */
public class UniquenessTable implements IDataTable {
  private static final long serialVersionUID = 6024085070748353783L;

  protected ArrayList<UniquenessModel> models;

  protected IDataColumn<?>[] columns = new IDataColumn<?>[] {new ColumnMapper<String>("Predicate") {
    private static final long serialVersionUID = 1L;

    @Override
    public String getElement(int rowIndex) {
      return models.get(rowIndex).getPredicateName();
    }
  }, new ColumnMapper<Double>("Uniqueness") {
    private static final long serialVersionUID = 1L;

    @Override
    public Double getElement(int rowIndex) {
      return models.get(rowIndex).getUniqueness();
    }
  }, new ColumnMapper<Double>("Density") {
    private static final long serialVersionUID = 1L;

    @Override
    public Double getElement(int rowIndex) {
      return models.get(rowIndex).getDensity();
    }
  }, new ColumnMapper<Double>("Keyness") {
    private static final long serialVersionUID = 1L;

    @Override
    public Double getElement(int rowIndex) {
      return models.get(rowIndex).getKeyness();
    }
  }, new ColumnMapper<Integer>("Values") {
    private static final long serialVersionUID = 1L;

    @Override
    public Integer getElement(int rowIndex) {
      return Integer.valueOf(models.get(rowIndex).getValues());
    }
  }, new ColumnMapper<Integer>("Unique Values") {
    private static final long serialVersionUID = 1L;

    @Override
    public Integer getElement(int rowIndex) {
      return Integer.valueOf(models.get(rowIndex).getUniqueValues());
    }
  }};

  protected UniquenessTable() {
    // hide default constructor
  }

  /**
   * creates a new {@link UniquenessTable}
   * 
   * @param models the models to be represented within this table
   */
  public UniquenessTable(ArrayList<UniquenessModel> models) {
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
   * gets the sample subjects of a antonym tuple
   * 
   * @param rowIndex the row index of the antonym
   * @return a table with the sample subjects
   */
  public IDataTable getSampleSubjects(int rowIndex) {
    DataColumn<String> subject1 = new DataColumn<String>("Predicate", true);
    DataTable result = new DataTable(subject1);

    // add sample subjects
    int i = 0;
    for (String subject : models.get(rowIndex).getExampleSubjects()) {
      subject1.setElement(i++, subject);
    }

    return result;
  }

  /**
   * a mapper for columns of this class
   * 
   * @author toni.gruetze
   * 
   * @param <T> the data type of this column
   */
  private abstract class ColumnMapper<T> implements IDataColumn<T> {
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
