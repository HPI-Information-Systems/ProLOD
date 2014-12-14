/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.charts.client.model;

import com.extjs.gxt.charts.client.model.charts.ChartConfig;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.charts.client.model.charts.PieChart.Slice;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * <code>DataProvider</code> implementation for pie charts.
 */
public class PieDataProvider extends DataProvider {

  /**
   * Creates a new pie data provider.
   * 
   * @param valueProperty the property name which contains the value
   */
  public PieDataProvider(String valueProperty) {
    this.valueProperty = valueProperty;
  }

  public PieDataProvider(String valueProperty, String labelProperty) {
    this.valueProperty = valueProperty;
    this.labelProperty = labelProperty;
  }

  public PieDataProvider(String valueProperty, String labelProperty, String textProperty) {
    this.valueProperty = valueProperty;
    this.labelProperty = labelProperty;
    this.textProperty = textProperty;
  }

  @Override
  public void populateData(ChartConfig config) {
    PieChart chart = (PieChart) config;
    chart.getValues().clear();

    for (ModelData m : store.getModels()) {
      Number n = getValue(m);
      if (n == null) {
        chart.addNullValue();
      } else {
        chart.addSlices(new Slice(n, getLabel(m), getText(m)));
      }
    }
  }
}
