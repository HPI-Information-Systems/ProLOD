/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;

/**
 * Default implementation of the <code>RemoteSortTreeLoader</code> interface.
 *
 * @param <M> the model type
 */
public class BaseRemoteSortTreeLoader<M extends ModelData> extends BaseTreeLoader<M> implements RemoteSortTreeLoader<M> {

  private boolean remoteSort;
  private String sortField;
  private SortDir sortDir = SortDir.NONE;

  @SuppressWarnings("unchecked")
  public BaseRemoteSortTreeLoader(DataProxy proxy) {
    super(proxy);
  }

  @SuppressWarnings("unchecked")
  public BaseRemoteSortTreeLoader(DataProxy proxy, DataReader reader) {
    super(proxy, reader);
  }

  @SuppressWarnings("unchecked")
  public BaseRemoteSortTreeLoader(DataReader reader) {
    super(reader);
  }

  public SortDir getSortDir() {
    return sortDir;
  }
  
  public String getSortField() {
    return sortField;
  }

  public boolean isRemoteSort() {
    return remoteSort;
  }
  
  @Override
  public boolean loadChildren(M parent) {
    RemoteSortTreeLoadConfig lc = (RemoteSortTreeLoadConfig)newLoadConfig();
    prepareLoadConfig(lc);
    lc.setParent(parent);
    children.add(parent);
    return load(lc);
  };
  
  @Override
  protected Object newLoadConfig() {
    return new BaseRemoteSortTreeLoadConfig();
  }
  
  @Override
  @SuppressWarnings("unchecked")
  protected void onLoadFailure(Object loadConfig, Throwable t) {
    RemoteSortTreeLoadConfig lc = (RemoteSortTreeLoadConfig)loadConfig;
    TreeLoadEvent evt = new TreeLoadEvent(this, (M) loadConfig, t);
    if (loadConfig != null && children.contains(lc.getParent())) {
      evt.parent = (M) lc.getParent();
      children.remove(lc.getParent());
    }
    fireEvent(LoadException, evt);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void onLoadSuccess(Object loadConfig, List<M> result) {
    RemoteSortTreeLoadConfig lc = (RemoteSortTreeLoadConfig)loadConfig;
    TreeLoadEvent evt = new TreeLoadEvent(this, (M) loadConfig, result);
    if (loadConfig != null && children.contains(lc.getParent())) {
      evt.parent = (M) lc.getParent();
      children.remove(lc.getParent());
    }
    fireEvent(Load, evt);
  }

  /**
   * Template method to allow custom subclasses to prepare the load config prior
   * to loading data
   */
  @Override
  protected Object prepareLoadConfig(Object config) {
    super.prepareLoadConfig(config);
    ListLoadConfig listConfig = (ListLoadConfig) config;
    listConfig.setSortField(sortField);
    listConfig.setSortDir(sortDir);
    return config;
  }

  public void setRemoteSort(boolean remoteSort) {
    this.remoteSort = remoteSort;
  }

  public void setSortDir(SortDir sortDir) {
    this.sortDir = sortDir;
  }

  public void setSortField(String sortField) {
    this.sortField = sortField;
  }

}
