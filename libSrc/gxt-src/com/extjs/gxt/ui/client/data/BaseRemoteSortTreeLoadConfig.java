/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * Default implementation of the <code>RemoteSortTreeLoadConfig</code>.
 * 
 * @see RemoteSortTreeLoader
 */
public class BaseRemoteSortTreeLoadConfig extends BaseListLoadConfig implements RemoteSortTreeLoadConfig {

  protected ModelData parent;

  public ModelData getParent() {
    return parent;
  }

  public void setParent(ModelData parent) {
    this.parent = parent;
  }

}
