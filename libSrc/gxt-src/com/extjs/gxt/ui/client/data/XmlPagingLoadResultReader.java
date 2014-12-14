/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.List;

/**
 * A <code>XmlReader</code> implementation that reads XML data using a
 * <code>ModelType</code> definition and returns a paging list load result
 * instance.
 * 
 * @param <D> the type of list load result being returned by the reader
 */
public class XmlPagingLoadResultReader<D extends PagingLoadResult<? extends ModelData>> extends XmlLoadResultReader<D> {
  /**
   * Creates a new reader.
   * 
   * @param modelType the model type definition
   */
  public XmlPagingLoadResultReader(ModelType modelType) {
    super(modelType);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Object createReturnData(Object loadConfig, List<ModelData> records, int totalCount) {
    ListLoadResult<?> result = (ListLoadResult<?>) super.createReturnData(loadConfig, records, totalCount);

    if (result instanceof PagingLoadResult) {
      PagingLoadResult<?> r = (PagingLoadResult<?>) result;
      r.setTotalLength(totalCount);

      if (loadConfig instanceof PagingLoadConfig) {
        PagingLoadConfig config = (PagingLoadConfig) loadConfig;
        r.setOffset(config.getOffset());
      }
    }
    return result;

  }

  @Override
  protected BasePagingLoadResult<ModelData> newLoadResult(Object loadConfig, List<ModelData> models) {
    return new BasePagingLoadResult<ModelData>(models);
  }

}
