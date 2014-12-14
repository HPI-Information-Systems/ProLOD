/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.charts.client.model;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.ChartManager;
import com.extjs.gxt.charts.client.model.charts.ChartConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;

/**
 * Base class for objects that provide chart data form models in a store.
 */
public abstract class DataProvider {

  protected ChartConfig chartConfig;
  protected String labelProperty, textProperty, valueProperty;
  protected ModelStringProvider<ModelData> labelProvider, textProvider;
  protected ListStore<ModelData> store;
  protected Double maxYValue, minYValue;

  private String chartId;
  private StoreListener<ModelData> storeListener;

  /**
   * Creates a new data provider.
   */
  public DataProvider() {
    storeListener = new StoreListener<ModelData>() {
      @Override
      public void handleEvent(StoreEvent<ModelData> e) {
        onStoreChange(e);
      }
    };
  }

  /**
   * Returns the label property.
   * 
   * @return the label property
   */
  public String getLabelProperty() {
    return labelProperty;
  }
  
  /**
   * Returns the model label provider.
   * 
   * @return the label provider
   */
  public ModelStringProvider<ModelData> getLabelProvider() {
    return labelProvider;
  }

  /**
   * Returns the text property.
   * 
   * @return the text property
   */
  public String getTextProperty() {
    return textProperty;
  }

  /**
   * Returns the model text provider.
   * 
   * @return the model text provider
   */
  public ModelStringProvider<ModelData> getTextProvider() {
    return textProvider;
  }

  /**
   * Returns the value property.
   * 
   * @return the value property
   */
  public String getValueProperty() {
    return valueProperty;
  }

  /**
   * Binds the data provider the the given store.
   * 
   * @param store the store
   */
  @SuppressWarnings("unchecked")
  public void bind(ListStore store) {
    if (this.store != null) {
      this.store.removeStoreListener(storeListener);
    }
    this.store = store;
    this.store.addStoreListener(storeListener);
  }

  public abstract void populateData(ChartConfig config);

  /**
   * Sets the label property. Works when models contains a property with the
   * label value, an alternative is to use a label provider (
   * {@link #setLabelProvider(ModelStringProvider)}.
   * 
   * @param labelProperty the label property
   */
  public void setLabelProperty(String labelProperty) {
    this.labelProperty = labelProperty;
  }

  /**
   * Sets the label provider.
   * 
   * @param labelProvider the label provider
   */
  public void setLabelProvider(ModelStringProvider<ModelData> labelProvider) {
    this.labelProvider = labelProvider;
  }

  /**
   * Sets the text property.
   **/
  public void setTextProperty(String textProperty) {
    this.textProperty = textProperty;
  }

  /**
   * Sets the test provider.
   * 
   * @param textProvider the text provider
   */
  public void setTextProvider(ModelStringProvider<ModelData> textProvider) {
    this.textProvider = textProvider;
  }

  /**
   * Sets the value property.
   **/
  public void setValueProperty(String valueProperty) {
    this.valueProperty = valueProperty;
  }

  protected String getLabel(ModelData model) {
    String label = null;
    if (labelProvider != null) {
      label = labelProvider.getStringValue(model, labelProperty);
    } else if (labelProperty != null) {
      Object o = model.<Object> get(labelProperty);
      label = o != null ? o.toString() : null;
    }
    return label == null ? "" : label;
  }

  protected double getMaxYValue() {
    return maxYValue;
  }

  protected double getMinYValue() {
    return minYValue;
  }

  protected String getText(ModelData model) {
    String text = null;
    if (textProvider != null) {
      text = textProvider.getStringValue(model, textProperty);
    } else if (textProperty != null) {
      Object o = model.<Object> get(textProperty);
      text = o != null ? o.toString() : null;
    }
    return text == null ? "" : text;
  }

  protected Number getValue(ModelData model) {
    Number value = null;
    if (valueProperty != null) {
      Object o = model.<Object> get(valueProperty);
      if (o != null) {
        if (o instanceof String) {
          value = Double.parseDouble((String) o);
        } else if (o instanceof Number) {
          value = (Number) o;
        }
      }
    }
    return value;
  }

  protected void onStoreChange(StoreEvent<ModelData> se) {
    Chart chart = ChartManager.get().getChart(chartId);
    if (chart != null) {
      chart.delayedRefresh(50);
    }
  }
}
