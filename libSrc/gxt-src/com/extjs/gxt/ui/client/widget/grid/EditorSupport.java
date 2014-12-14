/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class EditorSupport<M extends ModelData> {

  protected Grid<M> grid;
  protected ListStore<M> store;
  protected ColumnModel cm;
  protected CellEditor activeEditor;
  protected Listener<DomEvent> editorListener;
  protected Record activeRecord;
  protected boolean editing;
  protected boolean ignoreScroll;
  protected ClicksToEdit clicksToEdit = ClicksToEdit.ONE;
  protected Listener<GridEvent<M>> gridListener;

  @SuppressWarnings("unchecked")
  public void bind(Grid grid) {
    this.grid = grid;
    this.store = grid.getStore();
    this.cm = grid.getColumnModel();
  }
  
  public void doRender() {
    gridListener = new Listener<GridEvent<M>>() {
      public void handleEvent(GridEvent<M> e) {
        EventType type = e.getType();
        if (type == Events.BodyScroll) {
          if (!ignoreScroll) {
            stopEditing(true);
          }
        } else if (type == Events.CellClick || type == Events.CellDoubleClick) {
          e.cancelBubble();
          onCellDoubleClick(e);
        }
      }
    };

    grid.addListener(Events.BodyScroll, gridListener);

    if (clicksToEdit == ClicksToEdit.ONE) {
      grid.addListener(Events.CellClick, gridListener);
    } else {
      grid.addListener(Events.CellDoubleClick, gridListener);
    }

    grid.addStyleName("x-edit-grid");
    if (GXT.isSafari) {
      grid.el().setTop(0);
      grid.el().setScrollTop(0);
      grid.el().makePositionable();
    }
  }
  
  public CellEditor getActiveEditor() {
    return activeEditor;
  }

  /**
   * Returns the clicks to edit.
   * 
   * @return the clicks to edit
   */
  public ClicksToEdit getClicksToEdit() {
    return clicksToEdit;
  }

  /**
   * Returns true if editing is active.
   * 
   * @return the editing state
   */
  public boolean isEditing() {
    return editing;
  }

  public boolean onDoubleClick(GridEvent<M> e) {
    if (clicksToEdit == ClicksToEdit.TWO) {
      if (e.getRowIndex() != -1) {
        grid.fireEvent(Events.RowDoubleClick, e);
        if (e.getColIndex() != -1) {
          grid.fireEvent(Events.CellDoubleClick, e);
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Sets the number of clicks to edit (defaults to ONE).
   * 
   * @param clicksToEdit the clicks to edit
   */
  public void setClicksToEdit(ClicksToEdit clicksToEdit) {
    this.clicksToEdit = clicksToEdit;
  }

  public void startEditing(final int row, final int col) {
    stopEditing();
    if (cm.isCellEditable(col)) {
      grid.getView().ensureVisible(row, col, false);

      final M m = store.getAt(row);
      activeRecord = store.getRecord(m);

      final String field = cm.getDataIndex(col);
      GridEvent<M> e = new GridEvent<M>(grid);
      e.setModel(m);
      e.setRecord(activeRecord);
      e.setProperty(field);
      e.setRowIndex(row);
      e.setColIndex(col);
      e.setValue(m.get(field));
      if (grid.fireEvent(Events.BeforeEdit, e)) {
        DeferredCommand.addCommand(new Command() {
          public void execute() {
            deferStartEditing(m, field, row, col);
          }
        });
      }
    }
  }
  

  public void stopEditing() {
    stopEditing(false);
  }
  
  /**
   * Stops any active editing.
   * 
   * @param cancel true to cancel, false to complete
   */
  public void stopEditing(boolean cancel) {
    if (activeEditor != null) {
      if (cancel) {
        activeEditor.cancelEdit();
      } else {
        activeEditor.completeEdit();
      }
    }
  }
  
  protected void deferStartEditing(M m, String field, int row, int col) {
    editing = true;
    CellEditor ed = cm.getEditor(col);
    ed.row = row;
    ed.col = col;

    if (!ed.isRendered()) {
      ed.render((Element) grid.getView().getEditorParent());
    }

    if (editorListener == null) {
      editorListener = new Listener<DomEvent>() {
        public void handleEvent(DomEvent e) {
          if (e.getType() == Events.Complete) {
            EditorEvent ee = (EditorEvent) e;
            onEditComplete((CellEditor) ee.getEditor(), ee.getValue(), ee.getStartValue());
          } else if (e.getType() == Events.SpecialKey) {
            grid.getSelectionModel().onEditorKey(e);
          } else if (e.getType() == Events.CancelEdit) {
            EditorEvent ee = (EditorEvent) e;
            onEditCancel((CellEditor) ee.getEditor(), ee.getValue(), ee.getStartValue());
          }
        }
      };
    }

    ed.addListener(Events.Complete, editorListener);
    ed.addListener(Events.SpecialKey, editorListener);
    ed.addListener(Events.CancelEdit, editorListener);

    activeEditor = ed;
    // when inserting the editor into the last row, the body is
    // scrolling and edit is being cancelled
    ignoreScroll = true;
    ed.startEdit((Element) grid.getView().getCell(row, col), m.get(field));
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        ignoreScroll = false;
      }
    });

  }
  
  protected void onAutoEditClick(GridEvent<M> e) {
    if (e.getEvent().getButton() != Event.BUTTON_LEFT) {
      return;
    }
    int row = grid.getView().findRowIndex(e.getTarget());
    int cell = grid.getView().findRowIndex(e.getTarget());
    if (row != -1 && cell != -1) {
      stopEditing();
    }
  }
  
  protected void onCellDoubleClick(GridEvent<M> e) {
    startEditing(e.getRowIndex(), e.getColIndex());
  }
  
  protected void onEditCancel(CellEditor ed, Object value, Object startValue) {
    editing = false;
    activeEditor = null;
    ed.removeListener(Events.SpecialKey, editorListener);
    ed.removeListener(Events.Complete, editorListener);
    ed.removeListener(Events.CancelEdit, editorListener);
    grid.getView().focusCell(ed.row, ed.col, false);
  }

  protected void onEditComplete(CellEditor ed, Object value, Object startValue) {
    editing = false;
    activeEditor = null;
    ed.removeListener(Events.SpecialKey, editorListener);
    ed.removeListener(Events.Complete, editorListener);
    ed.removeListener(Events.CancelEdit, editorListener);
    Record r = activeRecord;
    String field = cm.getDataIndex(ed.col);
    if ((value == null && startValue != null)
        || (value != null && !value.equals(startValue))) {
      GridEvent<M> ge = new GridEvent<M>(grid);
      ge.setRecord(r);
      ge.setProperty(field);
      ge.setValue(value);
      ge.setStartValue(startValue);
      ge.setRowIndex(ed.row);
      ge.setColIndex(ed.col);

      if (grid.fireEvent(Events.ValidateEdit, ge)) {
        r.setValid(ge.getProperty(), ed.getField().isValid(true));
        r.set(ge.getProperty(), ge.getValue());
        grid.fireEvent(Events.AfterEdit, ge);
      }
    }

    grid.getView().focusCell(ed.row, ed.col, false);
  }

}
