/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.Element;

/**
 * A <code>DropTarget</code> implementation for Grids. Supports both inserts and
 * appends, specified using
 * {@link #setOperation(com.extjs.gxt.ui.client.dnd.DND.Operation)}.
 * <p />
 * Supported drag data:
 * <ul>
 * <li>A single ModelData instance.</li>
 * <li>A List of ModelData instances.</li>
 * <li>A List of TreeStoreModel instances (children are ignored).
 * </ul>
 */
public class GridDropTarget extends DropTarget {

  protected Grid<ModelData> grid;
  protected int insertIndex;
  protected ModelData activeItem;
  boolean before;

  /**
   * Creates a new drop target instance.
   * 
   * @param grid the target grid
   */
  @SuppressWarnings("unchecked")
  public GridDropTarget(Grid grid) {
    super(grid);
    this.grid = grid;
  }

  /**
   * Returns the target grid component.
   * 
   * @return the grid
   */
  public Grid<ModelData> getGrid() {
    return grid;
  }

  @Override
  protected void onDragDrop(DNDEvent e) {
    super.onDragDrop(e);
    Object data = e.getData();
    List<ModelData> models = prepareDropData(data, true);
    if (models.size() > 0) {
      if (feedback == Feedback.APPEND) {
        grid.getStore().add(models);
      } else {
        grid.getStore().insert(models, insertIndex);
      }
    }
    insertIndex = -1;
    activeItem = null;
  }

  @Override
  protected void onDragEnter(DNDEvent e) {
    super.onDragEnter(e);
    e.setCancelled(false);
    e.getStatus().setStatus(true);
  }

  @Override
  protected void onDragLeave(DNDEvent e) {
    super.onDragLeave(e);
  }

  @Override
  protected void onDragMove(DNDEvent event) {
    if (!event.within(grid.getView().getBody().dom)) {
      event.setCancelled(true);
      event.getStatus().setStatus(false);
      return;
    }

    event.setCancelled(false);
    event.getStatus().setStatus(true);
  }

  @Override
  protected void showFeedback(DNDEvent event) {
    event.getStatus().setStatus(true);
    if (feedback == Feedback.INSERT) {
      Element row = grid.getView().findRow(event.getTarget()).cast();

      if (row == null && grid.getStore().getCount() > 0) {
        row = grid.getView().getRow(grid.getStore().getCount() - 1).cast();
      }

      if (row != null) {
        int height = row.getOffsetHeight();
        int mid = height / 2;
        mid += row.getAbsoluteTop();
        int y = event.getClientY();
        before = y < mid;
        int idx = grid.getView().findRowIndex(row);

        activeItem = grid.getStore().getAt(idx);
        insertIndex = adjustIndex(event, idx);

        showInsert(event, row);
      } else {
        insertIndex = 0;
      }
    }
  }

  private int adjustIndex(DNDEvent event, int index) {
    Object data = event.getData();
    List<ModelData> models = prepareDropData(data, true);
    for (ModelData m : models) {
      int idx = grid.getStore().indexOf(m);
      if (idx > -1 && (before ? idx < index : idx <= index)) {
        index--;
      }
    }
    return before ? index : index + 1;
  }

  private void showInsert(DNDEvent event, Element row) {
    Insert insert = Insert.get();
    insert.show(row);
    Rectangle rect = El.fly(row).getBounds();
    int y = !before ? (rect.y + rect.height - 4) : rect.y - 2;
    insert.el().setBounds(rect.x, y, rect.width, 6);
  }

}
