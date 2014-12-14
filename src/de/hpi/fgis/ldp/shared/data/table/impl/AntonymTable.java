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

import de.hpi.fgis.ldp.shared.data.InversePredicateModel;
import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * this is a implementation of the {@link IDataTable} interface to store and represent antonyms (
 * {@link InversePredicateModel} instances)
 * 
 * @author toni.gruetze
 * 
 */
public class AntonymTable implements IDataTable {
  private static final long serialVersionUID = 4024085070748353783L;

  protected ArrayList<InversePredicateModel> models;

  protected IDataColumn<?>[] columns = new IDataColumn<?>[] {
      new ColumnMapper<String>("Predicate A") {
        private static final long serialVersionUID = 1L;

        @Override
        public String getElement(int rowIndex) {
          return models.get(rowIndex).getPredicateNameOne();
        }
      }, new ColumnMapper<String>("Predicate B") {
        private static final long serialVersionUID = 1L;

        @Override
        public String getElement(int rowIndex) {
          return models.get(rowIndex).getPredicateNameTwo();
        }
      }, new ColumnMapper<Double>("Correllation") {
        private static final long serialVersionUID = 1L;

        @Override
        public Double getElement(int rowIndex) {
          return Double.valueOf(models.get(rowIndex).getCorrelation());
        }
      }, new ColumnMapper<Double>("Support") {
        private static final long serialVersionUID = 1L;

        @Override
        public Double getElement(int rowIndex) {
          return Double.valueOf(models.get(rowIndex).getSupport());
        }
      }};

  protected AntonymTable() {
    // hide default constructor
  }

  /**
   * creates a new {@link AntonymTable}
   * 
   * @param models the models to be represented within this table
   */
  public AntonymTable(ArrayList<InversePredicateModel> models) {
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
    DataColumn<String> subject1 = new DataColumn<String>("Subject A", true);
    DataColumn<String> subject2 = new DataColumn<String>("Subject B", true);
    DataTable result = new DataTable(subject1);
    result.addColumn(subject2);

    // add sample subjects
    int i = 0;
    for (String subject : models.get(rowIndex).getExampleSubjects1()) {
      subject1.setElement(i++, subject);
    }
    i = 0;
    for (String subject : models.get(rowIndex).getExampleSubjects2()) {
      subject2.setElement(i++, subject);
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
