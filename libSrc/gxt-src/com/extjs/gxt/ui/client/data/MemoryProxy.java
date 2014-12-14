/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A <code>DataProxy</code> implementation that simply passes the data specified
 * in the constructor to the reader when its load method is called.
 * 
 * @param <D> the data type being returned by the proxy
 */
public class MemoryProxy<D> implements DataProxy<D> {

  protected Object data;

  /**
   * Creates new memory proxy.
   * 
   * @param data the local data
   */
  public MemoryProxy(Object data) {
    this.data = data;
  }

  /**
   * Returns the proxy data.
   * 
   * @return the data
   */
  public Object getData() {
    return data;
  }

  @SuppressWarnings("unchecked")
  public void load(DataReader<D> reader, Object loadConfig, AsyncCallback<D> callback) {
    try {
      D d = null;
      if (reader != null) {
        d = reader.read(loadConfig, data);
      } else {
        d = (D) data;
        if (d instanceof List) {
          d = (D) new ArrayList((List) d);
        }
      }
      callback.onSuccess(d);
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  /**
   * Sets the proxy data.
   * 
   * @param data the data
   */
  public void setData(Object data) {
    this.data = data;
  }

}
