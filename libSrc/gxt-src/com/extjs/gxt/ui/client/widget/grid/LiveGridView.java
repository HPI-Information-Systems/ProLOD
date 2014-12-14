/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LiveGridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

/**
 * LiveGridView for displaying large amount of data. Data is loaded on demand as
 * the user scrolls the grid.
 */
public class LiveGridView extends GridView {

  protected El liveScroller;
  protected ListStore<ModelData> liveStore;
  protected int liveStoreOffset = 0;
  protected int totalCount = 0;
  protected int viewIndex;

  private int cacheSize = 200;
  private boolean isLoading;
  // to prevent flickering
  private boolean isMasked;
  private int loadDelay = 200;
  private PagingLoader<PagingLoadResult<ModelData>> loader;
  private int loaderOffset;
  private DelayedTask loaderTask;
  private double prefetchFactor = .2;
  private int rowHeight = 20;
  private int viewIndexReload = -1;

  /**
   * Returns the numbers of rows that should be cached.
   * 
   * @return the cache size
   */
  public int getCacheSize() {
    return cacheSize;
  }

  /**
   * Returns the amount of time before loading is done.
   * 
   * @return the load delay in milliseconds
   */
  public int getLoadDelay() {
    return loadDelay;
  }

  /**
   * Returns the prefetchFactor.
   * 
   * @return the prefetchFactor
   */
  public double getPrefetchFactor() {
    return prefetchFactor;
  }

  /**
   * Returns the height of one row.
   * 
   * @return the height of one row
   */
  public int getRowHeight() {
    return rowHeight;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handleComponentEvent(GridEvent ge) {
    super.handleComponentEvent(ge);
    int type = ge.getEventTypeInt();
    Element target = ge.getTarget();
    if ((type == Event.ONSCROLL && liveScroller.dom.isOrHasChild(target))
        || (type == Event.ONMOUSEWHEEL && mainBody.dom.isOrHasChild(target))) {
      ge.stopEvent();
      if (type == Event.ONMOUSEWHEEL) {
        int v = ge.getEvent().getMouseWheelVelocityY() * getCalculatedRowHeight();
        liveScroller.setScrollTop(liveScroller.getScrollTop() + v);
      } else {
        updateRows(liveScroller.getScrollTop() / getCalculatedRowHeight(), false);
      }
    }
  }

  /**
   * Refreshed the view.
   */
  public void refresh() {
    loadLiveStore(liveStoreOffset);
  }

  /**
   * Sets the amount of rows that should be cached (default to 200). The cache
   * size is the number of rows that are retrieved each time a data request is made.
   * The cache size should always be greater than the number of visible rows of
   * the grid. The number of visible rows will vary depending on the grid height
   * and the height of each row.
   * 
   * @param cacheSize the new cache size
   */
  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  /**
   * Sets the amount of time before loading is done (defaults to 200).
   * 
   * @param loadDelay the new load delay in milliseconds
   */
  public void setLoadDelay(int loadDelay) {
    this.loadDelay = loadDelay;
  }

  /**
   * Sets the pre-fetch factor (defaults to .2). The pre-fetch factor is used to
   * determine when new data should be fetched as the user scrolls the grid. The
   * factor is used with the cache size.
   * 
   * <p />
   * For example, if the cache size is 1000 with a pre-fetch of .20, the grid
   * will request new data when the 800th (1000 * .20) row of the grid becomes
   * visible.
   * 
   * @param prefetchFactor the pre-fetch factor
   */
  public void setPrefetchFactor(double prefetchFactor) {
    this.prefetchFactor = prefetchFactor;
  }

  /**
   * Sets the height of one row (defaults to 20). <code>LiveGridView</code> will
   * only work with fixed row heights with all rows being the same height.
   * Changing this value will not physically resize the row heights, rather, the
   * specified height will be used internally for calculations.
   * 
   * @param rowHeight the new row height.
   */
  public void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
  }

  @Override
  protected void afterRender() {
    mainBody.setInnerHtml(renderRows(0, -1));
    renderWidgets(0, -1);
    processRows(0, true);
    applyEmptyText();
    refresh();
  }

  @Override
  protected void calculateVBar(boolean force) {
    if (force) {
      layout();
    }
  }

  protected void doLoad() {
    loader.load(loaderOffset, cacheSize);
  }

  protected int getCalculatedRowHeight() {
    return rowHeight + borderWidth;
  }

  protected int getLiveScrollerHeight() {
    return liveScroller.getHeight(true);
  }

  protected int getLiveStoreCalculatedIndex(int index) {
    int calcIndex = index - (cacheSize / 2) + getVisibleRowCount();
    calcIndex = Math.max(0, calcIndex);
    calcIndex = Math.min(totalCount - cacheSize, calcIndex);
    calcIndex = Math.min(index, calcIndex);
    return calcIndex;
  }

  @Override
  protected int getScrollAdjust() {
    return scrollOffset;
  }

  protected int getVisibleRowCount() {
    int rh = getCalculatedRowHeight();
    int visibleHeight = getLiveScrollerHeight();
    return (int) ((visibleHeight < 1) ? 0 : Math.floor((double) visibleHeight / rh));
  }

  @SuppressWarnings("unchecked")
  protected void initData(ListStore ds, ColumnModel cm) {
    liveStore = ds;
    super.initData(new ListStore() {
      @Override
      public void sort(String field, SortDir sortDir) {
        LiveGridView.this.liveStore.sort(field, sortDir);
        sortInfo = liveStore.getSortState();
      }

    }, cm);

    loader = (PagingLoader) liveStore.getLoader();
    liveStore.addStoreListener(new StoreListener<ModelData>() {

      public void storeDataChanged(StoreEvent<ModelData> se) {
        liveStoreOffset = loader.getOffset();

        if (totalCount != loader.getTotalCount()) {
          totalCount = loader.getTotalCount();
          int height = (totalCount + 1) * getCalculatedRowHeight();
          // 1000000 as browser maxheight hack
          int count = height / 1000000 + 1;
          int h = height / count;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < count; i++) {
            sb.append("<div style=\"height:");
            sb.append(h);
            sb.append("px;\"></div>");
          }
          liveScroller.setInnerHtml(sb.toString());

        }
        if (viewIndexReload != -1 && !isCached(viewIndexReload)) {
          loadLiveStore(getLiveStoreCalculatedIndex(viewIndexReload));
        } else {
          viewIndexReload = -1;
          updateRows(viewIndex, true);
          isLoading = false;
          if (isMasked) {
            isMasked = false;
            scroller.unmask();
          }
        }

      }
    });
  }

  protected boolean isCached(int index) {
    if ((index < liveStoreOffset) || (index > (liveStoreOffset + cacheSize - getVisibleRowCount()))) {
      return false;
    }
    return true;
  }

  protected boolean isHorizontalScrollBarShowing() {
    return cm.getTotalWidth() > scroller.getStyleWidth();
  }

  protected boolean loadLiveStore(int offset) {
    if (loaderTask == null) {
      loaderTask = new DelayedTask(new Listener<BaseEvent>() {
        public void handleEvent(BaseEvent be) {
          doLoad();
        }
      });
    }
    loaderOffset = offset;
    loaderTask.delay(loadDelay);
    if (isLoading) {
      return true;
    } else {
      isLoading = true;
      return false;
    }
  }

  @Override
  protected void onColumnWidthChange(int column, int width) {
    super.onColumnWidthChange(column, width);
    updateRows(viewIndex, false);
  }

  @Override
  protected void renderUI() {
    super.renderUI();
    scroller.setStyleAttribute("overflowY", "hidden");
    liveScroller = grid.el().insertFirst("<div class=\"x-livegrid-scroller\"></div>");

    liveScroller.setTop(mainHd.getHeight());

    liveScroller.addEventsSunk(Event.ONSCROLL);
    mainBody.addEventsSunk(Event.ONMOUSEWHEEL);
  }

  @Override
  protected void resize() {
    int oldCount = getVisibleRowCount();
    super.resize();
    if (mainBody != null) {
      int h = grid.getHeight(true) - mainHd.getHeight(true);
      if (isHorizontalScrollBarShowing()) {
        h -= 19;
      }
      liveScroller.setHeight(h, true);
      scroller.setWidth(grid.getWidth() - getScrollAdjust(), true);

      if (oldCount != getVisibleRowCount()) {
        updateRows(viewIndex, true);
      }
    }
  }

  protected boolean shouldCache(int index) {
    int i = (int) (cacheSize * prefetchFactor);
    double low = liveStoreOffset + i;
    double high = liveStoreOffset + cacheSize - getVisibleRowCount() - i;
    if ((index < low && liveStoreOffset != 0) || (index > high && liveStoreOffset != totalCount - cacheSize)) {
      return true;
    }
    return false;
  }

  protected void updateRows(int newIndex, boolean reload) {
    int diff = newIndex - viewIndex;
    int delta = Math.abs(diff);

    // nothing has changed and we are not forcing a reload
    if (delta == 0 && !reload) {
      return;
    }
    int rowCount = getVisibleRowCount();
    viewIndex = Math.min(newIndex, Math.abs(totalCount - rowCount));

    int liveStoreIndex = Math.max(0, viewIndex - liveStoreOffset);

    // load data if not already cached
    if (!isCached(viewIndex)) {
      if (!isMasked) {
        scroller.mask(GXT.MESSAGES.loadMask_msg());
        isMasked = true;
      }
      if (loadLiveStore(getLiveStoreCalculatedIndex(viewIndex))) {
        viewIndexReload = viewIndex;
      }
      return;
    }

    // do pre caching
    if (shouldCache(viewIndex) && !isLoading) {
      loadLiveStore(getLiveStoreCalculatedIndex(viewIndex));
    }

    if (delta > getVisibleRowCount() - 1) {
      reload = true;
    }

    if (reload) {
      delta = diff = getVisibleRowCount();
      ds.removeAll();
    }

    if (delta == 0) {
      return;
    }

    int count = ds.getCount();
    if (diff > 0) {
      // rolling forward
      for (int c = 0; c < delta && c < count; c++) {
        ds.remove(ds.getAt(0));
      }
      count = ds.getCount();
      ds.add(liveStore.getRange(liveStoreIndex + count, liveStoreIndex + count + delta - 1));
    } else {
      // rolling back
      for (int c = 0; c < delta && c < count; c++) {
        ds.remove(ds.getAt(ds.getCount() - 1));
      }

      ds.insert(liveStore.getRange(liveStoreIndex, liveStoreIndex + delta - 1), 0);
    }

    LiveGridEvent<ModelData> event = new LiveGridEvent<ModelData>(grid);
    event.setViewIndex(viewIndex);
    event.setPageSize(rowCount);
    event.setTotalCount(totalCount);
    fireEvent(Events.LiveGridViewUpdate, event);
  }
}
