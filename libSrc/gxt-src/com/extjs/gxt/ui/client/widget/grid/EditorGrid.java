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
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.Element;

/**
 * Adds editing capabilities to Grid.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeEdit</b> : GridEvent(grid, record, property, value, rowIndex,
 * colIndex)<br>
 * <div>Fires before cell editing is triggered. Listeners can cancel the action
 * by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>grid : this</li>
 * <li>record : the record being edited</li>
 * <li>property : the property being edited</li>
 * <li>value : the value being edited</li>
 * <li>rowIndex : the current row</li>
 * <li>colIndex : the current column</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>AfterEdit</b> : GridEvent(grid, record, property, value, startValue,
 * rowIndex, colIndex)<br>
 * <div>Fires after a cell is edited.</div>
 * <ul>
 * <li>grid : this</li>
 * <li>record : the record being edited</li>
 * <li>property : the property being edited</li>
 * <li>value : the value being set</li>
 * <li>startValue : the value before the edit</li>
 * <li>rowIndex : the current row</li>
 * <li>colIndex : the current column</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>ValidateEdit</b> : GridEvent(grid, record, property, value,
 * startValue, rowIndex, colIndex)<br>
 * <div>Fires right before the record is updated. Listeners can cancel the
 * action by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>grid : this</li>
 * <li>record : the record being edited</li>
 * <li>property : the property being edited</li>
 * <li>value : the value being set</li>
 * <li>startValue : the value before the edit</li>
 * <li>rowIndex : the current row</li>
 * <li>colIndex : the current column</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 */
public class EditorGrid<M extends ModelData> extends Grid<M> {

  /**
   * ClicksToEdit enumeration.
   */
  public enum ClicksToEdit {
    /**
     * Editing start with one click.
     */
    ONE,
    /**
     * Editing starts with double click.
     */
    TWO;
  }

  /**
   * Creates a new editor grid.
   * 
   * @param store the store
   * @param cm the column model
   */
  @SuppressWarnings("unchecked")
  public EditorGrid(ListStore store, ColumnModel cm) {
    super(store, cm);
    setSelectionModel(new CellSelectionModel<M>());
    setTrackMouseOver(false);

    editSupport = getEditSupport();
    editSupport.bind(this);
  }

  /**
   * Returns the active editor.
   * 
   * @return the active editor
   */
  public CellEditor getActiveEditor() {
    return editSupport.getActiveEditor();
  }

  /**
   * Returns the clicks to edit.
   * 
   * @return the clicks to edit
   */
  public ClicksToEdit getClicksToEdit() {
    return editSupport.getClicksToEdit();
  }

  /**
   * Returns true if editing is active.
   * 
   * @return the editing state
   */
  public boolean isEditing() {
    return editSupport.isEditing();
  }
  
  @Override
  public void reconfigure(ListStore<M> store, ColumnModel cm) {
    super.reconfigure(store, cm);
    editSupport.bind(this);
  }

  /**
   * Sets the number of clicks to edit (defaults to ONE).
   * 
   * @param clicksToEdit the clicks to edit
   */
  public void setClicksToEdit(ClicksToEdit clicksToEdit) {
    editSupport.setClicksToEdit(clicksToEdit);
  }

  /**
   * Starts editing the specified for the specified row/column.
   * 
   * @param row the row index
   * @param col the column index
   */
  public void startEditing(final int row, final int col) {
    editSupport.startEditing(row, col);
  }

  /**
   * Stops any active editing.
   */
  public void stopEditing() {
    editSupport.stopEditing();
  }

  /**
   * Stops any active editing.
   * 
   * @param cancel true to cancel, false to complete
   */
  public void stopEditing(boolean cancel) {
    editSupport.stopEditing(cancel);
  }

  @Override
  protected void onDoubleClick(GridEvent<M> e) {
    if (editSupport.onDoubleClick(e)) {
      return;
    }
    super.onDoubleClick(e);
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    editSupport.doRender();
  }

}
