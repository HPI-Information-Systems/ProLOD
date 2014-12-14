/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;

/**
 * Cell based selection model for a grid.
 * 
 * @param <M> the model type
 */
public class CellSelectionModel<M extends ModelData> extends GridSelectionModel<M> {

  public class CellSelection {
    public M model;
    public int row;
    public int cell;

    public CellSelection(M model, int row, int cell) {
      this.model = model;
      this.row = row;
      this.cell = cell;
    }
  }

  private Callback callback = new Callback(this);
  protected CellSelection selection;

  @Override
  @SuppressWarnings("unchecked")
  public void bindGrid(Grid grid) {
    if (this.grid != null) {
      this.grid.removeListener(Events.CellMouseDown, this);
      this.grid.removeListener(Events.BeforeEdit, this);
      this.grid.getView().removeListener(Events.Refresh, this);
      this.grid.getView().removeListener(Events.RowUpdated, this);
      keyNav.bind(null);
      bind(null);
      this.listStore = null;
    }
    this.grid = grid;
    if (grid != null) {
      grid.setTrackMouseOver(false);
      grid.addListener(Events.BeforeEdit, this);
      grid.addListener(Events.CellMouseDown, this);
      grid.getView().addListener(Events.Refresh, this);
      grid.getView().addListener(Events.RowUpdated, this);
      keyNav.bind(grid);
      bind(grid.getStore());
      this.listStore = (ListStore) grid.getStore();
    }
  }

  @Override
  public void deselectAll() {
    if (selection != null) {
      // index may change with tree grid on expand / collapse
      // ask store for current row index
      int row = listStore.indexOf(selection.model);
      grid.getView().onCellDeselect(row, selection.cell);
      selection = null;
    }
  }

  /**
   * Returns the selected cell.
   * 
   * @return the selection cell
   */
  public CellSelection getSelectCell() {
    return selection;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void handleEvent(BaseEvent e) {
    if (e.getType() == Events.CellMouseDown) {
      handleMouseDown((GridEvent) e);
    } else if (e.getType() == Events.Refresh) {
      refresh();
    } else if (e.getType() == Events.BeforeEdit) {
      GridEvent ge = (GridEvent) e;
      selectCell(ge.getRowIndex(), ge.getColIndex());
    }else if (e.getType() == Events.RowUpdated) {
      onRowUpdated((GridEvent) e);
    }
  }

  /**
   * Selects the cell.
   * 
   * @param row the row index
   * @param cell the cell index
   */
  public void selectCell(int row, int cell) {
    deselectAll();
    M m = listStore.getAt(row);
    selection = new CellSelection(m, row, cell);
    grid.getView().onCellSelect(row, cell);
    grid.getView().focusCell(row, cell, true);
  }
  @Override
  protected void onRowUpdated(GridEvent<M> ge) {
    if (selection != null && selection.model == ge.getModel()) {
      grid.getView().onCellSelect(selection.row, selection.cell);
      grid.getView().focusCell(selection.row, selection.cell, true);
    }
  }

  @Override
  protected void handleMouseDown(GridEvent<M> e) {
    if (e.getEvent().getButton() != Event.BUTTON_LEFT || isLocked()) {
      return;
    }
    selectCell(e.getRowIndex(), e.getColIndex());
  }

  @Override
  protected void onClear(StoreEvent<M> se) {
    super.onClear(se);
    selection = null;
  }

  @Override
  protected void onKeyPress(GridEvent<M> e) {
    if (grid.editSupport != null) {
      // ignore events whose source is an input element
      String tag = e.getTarget().getTagName();
      if (tag.equals("INPUT") && !e.getTarget().getClassName().equals("_focus")) {
        return;
      }
    }
    if (selection == null) {
      e.stopEvent();
      Cell cell = grid.walkCells(0, 0, 1, callback, false);
      if (cell != null) {
        selectCell(cell.row, cell.cell);
      }
      return;
    }

    int r = selection.row;
    int c = selection.cell;

    Cell newCell = null;

    switch (e.getKeyCode()) {
      case KeyCodes.KEY_TAB:
        if (e.isShiftKey()) {
          newCell = grid.walkCells(r, c - 1, -1, callback, false);
        } else {
          newCell = grid.walkCells(r, c + 1, 1, callback, false);
        }
        break;
      case KeyCodes.KEY_DOWN: {
        newCell = grid.walkCells(r + 1, c, 1, callback, false);
        break;
      }
      case KeyCodes.KEY_UP: {
        newCell = grid.walkCells(r - 1, c, -1, callback, false);
        break;
      }
      case KeyCodes.KEY_LEFT:
        newCell = grid.walkCells(r, c - 1, -1, callback, false);
        break;
      case KeyCodes.KEY_RIGHT:
        newCell = grid.walkCells(r, c + 1, 1, callback, false);
        break;
      case KeyCodes.KEY_ENTER:
        if (grid.editSupport != null) {
          if (!grid.editSupport.isEditing()) {
            grid.editSupport.startEditing(r, c);
            e.stopEvent();
            return;
          }
        }
        break;

    }
    if (newCell != null) {
      selectCell(newCell.row, newCell.cell);
      e.stopEvent();
    }
  }

  @Override
  protected void onRemove(M model) {
    super.onRemove(model);
    if (selection != null && selection.model == model) {
      selection = null;
    }
  }

}
