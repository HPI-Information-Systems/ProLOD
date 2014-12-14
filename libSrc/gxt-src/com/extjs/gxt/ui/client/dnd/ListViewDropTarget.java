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
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.user.client.Element;

/**
 * A <code>DropTarget</code> implementation for the ListView component.
 * 
 * Supported drag data:
 * <ul>
 * <li>A single ModelData instance.</li>
 * <li>A List of ModelData instances.</li>
 * <li>A List of TreeStoreModel instances (children are ignored).
 * </ul>
 */
public class ListViewDropTarget extends DropTarget {

  protected ListView<ModelData> listView;
  protected int insertIndex;
  protected ModelData activeItem;
  protected boolean before;

  private boolean autoSelect;

  /**
   * Creates a new list view drop target instance.
   * 
   * @param listView the target list view
   */
  @SuppressWarnings("unchecked")
  public ListViewDropTarget(ListView listView) {
    super(listView);
    this.listView = listView;
  }

  /**
   * Returns the target's list view component.
   * 
   * @return the list view
   */
  public ListView<ModelData> getListView() {
    return listView;
  }

  /**
   * Returns true if auto select is enabled.
   * 
   * @return the auto select state
   */
  public boolean isAutoSelect() {
    return autoSelect;
  }

  @Override
  protected void onDragDrop(DNDEvent e) {
    super.onDragDrop(e);
    Object data = e.getData();
    List<ModelData> models = prepareDropData(data, true);
    if (models.size() > 0) {
      if (feedback == Feedback.APPEND) {
        listView.getStore().add(models);
      } else {
        listView.getStore().insert(models, insertIndex);
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
    Insert insert = Insert.get();
    insert.setVisible(false);
  }

  @Override
  protected void onDragMove(DNDEvent event) {
    if (!event.within(listView.getElement())) {
      event.setCancelled(true);
      event.getStatus().setStatus(false);
      return;
    }

    event.setCancelled(false);
    event.getStatus().setStatus(true);
  }

  /**
   * True to automatically select and new items created after a drop (defaults
   * to false).
   * 
   * @param autoSelect true to auto select
   */
  public void setAutoSelect(boolean autoSelect) {
    this.autoSelect = autoSelect;
  }

  @Override
  protected void showFeedback(DNDEvent event) {
    event.getStatus().setStatus(true);
    if (feedback == Feedback.INSERT) {
      event.getStatus().setStatus(true);
      Element row = listView.findElement(event.getTarget()).cast();

      if (row == null && listView.getStore().getCount() > 0) {
        row = listView.getElement(listView.getStore().getCount() - 1).cast();
      }

      if (row != null) {
        int height = row.getOffsetHeight();
        int mid = height / 2;
        mid += row.getAbsoluteTop();
        int y = event.getClientY();
        before = y < mid;
        int idx = listView.findElementIndex(row);

        activeItem = listView.getStore().getAt(idx);
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
      int idx = listView.getStore().indexOf(m);
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
