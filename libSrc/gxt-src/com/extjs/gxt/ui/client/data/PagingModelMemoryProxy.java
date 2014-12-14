/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.extjs.gxt.ui.client.util.DefaultComparator;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A specialized <code>DataProxy</code> the supports paging when the entire data
 * set is in memory.
 */
public class PagingModelMemoryProxy extends MemoryProxy<PagingLoadResult<? extends ModelData>> {

  private Comparator<Object> comparator;

  public PagingModelMemoryProxy(Object data) {
    super(data);
  }

  /**
   * Returns the comparator.
   * 
   * @return the comparator
   */
  public Comparator<Object> getComparator() {
    return comparator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void load(DataReader<PagingLoadResult<? extends ModelData>> reader, Object loadConfig,
      AsyncCallback<PagingLoadResult<? extends ModelData>> callback) {
    try {
      PagingLoadResult d = null;
      if (reader != null) {
        d = reader.read(loadConfig, data);
      } else {
        if (data instanceof List) {
          d = new BasePagingLoadResult(new ArrayList((List) data));
        } else {
          PagingLoadResult r = (PagingLoadResult) data;
          d = new BasePagingLoadResult(new ArrayList(r.getData()), r.getOffset(), r.getTotalLength());
        }
      }

      PagingLoadConfig config = (PagingLoadConfig) loadConfig;

      if (config.getSortInfo().getSortField() != null) {
        final String sortField = config.getSortInfo().getSortField();
        if (sortField != null) {
          Collections.sort(d.getData(), config.getSortInfo().getSortDir().comparator(new Comparator<ModelData>() {

            public int compare(ModelData o1, ModelData o2) {
              Object v1 = (Object) o1.get(sortField);
              Object v2 = (Object) o2.get(sortField);

              if (comparator != null) {
                return comparator.compare(v1, v2);
              } else {
                return DefaultComparator.INSTANCE.compare(v1, v2);
              }

            }
          }));
        }

      }
      List<ModelData> sublist = new ArrayList<ModelData>();
      int start = config.getOffset();
      int limit = d.getData().size();
      if (config.getLimit() > 0) {
        limit = Math.min(start + config.getLimit(), limit);
      }
      for (int i = config.getOffset(); i < limit; i++) {
        sublist.add((ModelData) d.getData().get(i));
      }
      callback.onSuccess(new BasePagingLoadResult<ModelData>(sublist, config.getOffset(), d.getData().size()));
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  /**
   * Sets the comparator used to sort the list of models (default to
   * DefaultComparator).
   * 
   * @param comparator the comparator
   */
  public void setComparator(Comparator<Object> comparator) {
    this.comparator = comparator;
  }

}
