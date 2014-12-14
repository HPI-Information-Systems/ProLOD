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

import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * this is a implementation of the {@link IDataTable} interface to store and represent association
 * rules ({@link AssociationRuleModel} instances)
 * 
 * @author toni.gruetze
 * 
 */
public class AssociationRuleTable implements IDataTable {
  private static final long serialVersionUID = 4024085070748353783L;

  protected ArrayList<AssociationRuleModel> models;

  protected IDataColumn<?>[] columns = new IDataColumn<?>[] {new ColumnMapper<String>("Condition") {
    private static final long serialVersionUID = 1L;

    @Override
    public String getElement(int rowIndex) {
      return models.get(rowIndex).getCondition();
    }
  }, new ColumnMapper<String>("Consequence") {
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
  }, new ColumnMapper<Double>("Confidence") {
    private static final long serialVersionUID = 1L;

    @Override
    public Double getElement(int rowIndex) {
      return Double.valueOf(models.get(rowIndex).getConfidence());
    }
  }, new ColumnMapper<Double>("Correllation") {
    private static final long serialVersionUID = 1L;

    @Override
    public Double getElement(int rowIndex) {
      return Double.valueOf(models.get(rowIndex).getCorrelationCoefficient());
    }
  }};

  protected AssociationRuleTable() {
    // hide default constructor
  }

  /**
   * creates a new {@link AssociationRuleTable}
   * 
   * @param models the models to be represented within this table
   */
  public AssociationRuleTable(ArrayList<AssociationRuleModel> models) {
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
   * gets the sample subjects of a association rule
   * 
   * @param rowIndex the row index of the association rule
   * @return a table with the sample subjects
   */
  public IDataTable getSampleSubjects(int rowIndex) {
    DataColumn<String> subject = new DataColumn<String>("Subject", true);
    DataTable result = new DataTable(subject);

    int i = 0;
    for (String currentSubject : this.models.get(rowIndex).getSubjects()) {
      subject.setElement(i++, currentSubject);
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
