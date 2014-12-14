/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import com.extjs.gxt.ui.client.Style.SortDir;

/**
 * Load config interface for list based data.
 */
public interface ListLoadConfig extends LoadConfig {

  /**
   * Returns the current sort direction.
   * 
   * @return the sort direction
   */
  public SortDir getSortDir();

  /**
   * Returns the current sort field.
   * 
   * @return the sort field
   */
  public String getSortField();

  /**
   * Returns the sort info.
   */
  public SortInfo getSortInfo();

  /**
   * Sets the sort direction.
   * 
   * @param sortDir the sort direction
   */
  public void setSortDir(SortDir sortDir);

  /**
   * Sets the sort field.
   * 
   * @param sortField the sort field
   */
  public void setSortField(String sortField);

  /**
   * Sets the sort info.
   */
  public void setSortInfo(SortInfo info);
}