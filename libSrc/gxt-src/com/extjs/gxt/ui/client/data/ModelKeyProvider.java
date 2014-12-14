/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * Instances of this class provide unique keys for models.
 * 
 * @param <M> the model type
 */
public interface ModelKeyProvider<M extends ModelData> {

  /**
   * Returns a unique key for the given model. The key must remain constant for
   * a given model.
   * 
   * @param model the model
   * @return the unique key
   */
  public String getKey(M model);

}
