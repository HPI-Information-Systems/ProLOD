/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

@SuppressWarnings("unchecked")
public class TreePanelDropTarget extends DropTarget {

  protected TreePanel<ModelData> tree;
  protected TreeNode activeItem, appendItem;
  protected int status;

  private boolean allowDropOnLeaf = false;
  private boolean autoExpand = true;
  private int autoExpandDelay = 800;
  private boolean restoreTrackMouse;

  public TreePanelDropTarget(TreePanel tree) {
    super(tree);
    this.tree = tree;
  }

  /**
   * Returns the target's tree.
   * 
   * @return the tree
   */
  public TreePanel<?> getTree() {
    return tree;
  }

  /**
   * Returns whether drops are allowed on leaf nodes.
   * 
   * @return true of drops on leafs are allowed
   */
  public boolean isAllowDropOnLeaf() {
    return allowDropOnLeaf;
  }

  /**
   * Returns true if auto expand is enabled.
   * 
   * @return the auto expand state
   */
  public boolean isAutoExpand() {
    return autoExpand;
  }

  /**
   * True to allow drops on leaf nodes (defaults to false).
   * 
   * @param allowDropOnLeaf true to enable drops on leaf nodes
   */
  public void setAllowDropOnLeaf(boolean allowDropOnLeaf) {
    this.allowDropOnLeaf = allowDropOnLeaf;
  }

  /**
   * True to automatically expand the active tree item when the user hovers over
   * a collapsed item (defaults to true). Use {@link #setAutoExpandDelay(int)}
   * to set the delay.
   * 
   * @param autoExpand true to auto expand
   */
  public void setAutoExpand(boolean autoExpand) {
    this.autoExpand = autoExpand;
  }

  /**
   * Sets the delay used to auto expand items (defaults to 800).
   * 
   * @param autoExpandDelay the delay in milliseconds
   */
  public void setAutoExpandDelay(int autoExpandDelay) {
    this.autoExpandDelay = autoExpandDelay;
  }

  protected void appendModel(ModelData p, List<ModelData> models, int index) {
    if (models.size() == 0) return;
    if (models.get(0) instanceof TreeStoreModel) {
      // drop is in form from tree store

      List<ModelData> children = new ArrayList<ModelData>();

      for (ModelData tm : models) {
        ModelData child = tm.get("model");
        children.add(child);
      }
      if (p == null) {
        tree.getStore().insert(children, index, false);
      } else {
        tree.getStore().insert(p, children, index, false);
      }
      for (ModelData tm : models) {
        ModelData child = tm.get("model");
        List sub = (List) ((TreeModel) tm).getChildren();
        appendModel(child, sub, 0);
      }
      return;
    }
    if (p == null) {
      tree.getStore().insert(models, index, false);
    } else {
      tree.getStore().insert(p, models, index, false);
    }
  }

  protected void handleAppend(DNDEvent event, final TreeNode item) {
    // clear any active append item
    if (activeItem != null && activeItem != item) {
      tree.getView().onDropChange(activeItem, false);
    }
    status = -1;

    Insert.get().hide();
    event.getStatus().setStatus(true);
    if (activeItem != null) {
      tree.getView().onDropChange(activeItem, false);
    }

    if (item != null && item != appendItem && autoExpand && !item.isExpanded()) {
      Timer t = new Timer() {
        @Override
        public void run() {
          if (item == appendItem) {

            item.setExpanded(true);
          } else {
          }
        }
      };
      t.schedule(autoExpandDelay);
    }
    appendItem = item;
    activeItem = item;
    if (activeItem != null) {
      tree.getView().onDropChange(activeItem, true);
    }
  }

  protected void handleAppendDrop(DNDEvent event, TreeNode item) {
    List sel = prepareDropData(event.getData(), false);
    if (sel.size() > 0) {
      ModelData p = null;
      if (item != null) {
        p = item.getModel();
        appendModel(p, sel, tree.getStore().getChildCount(item.getModel()));
      } else {
        appendModel(p, sel, 0);
      }
    }
  }

  protected void handleInsert(DNDEvent event, final TreeNode item) {
    // clear any active append item
    if (activeItem != null && activeItem != item) {
      tree.getView().onDropChange(activeItem, false);
    }

    int height = item.getElement().getOffsetHeight();
    int mid = height / 2;
    int top = item.getElement().getAbsoluteTop();
    mid += top;
    int y = event.getClientY();
    boolean before = y < mid;

    if (!item.isLeaf() || allowDropOnLeaf) {
      if ((before && y > top + 4) || (!before && y < top + height - 4)) {
        handleAppend(event, item);
        return;
      }
    }

    appendItem = null;

    status = before ? 0 : 1;

    if (activeItem != null) {
      tree.getView().onDropChange(activeItem, false);
    }

    activeItem = item;

    int idx = -1;
    if (activeItem.getParent() == null) {
      idx = tree.getStore().getRootItems().indexOf(activeItem);
    } else {
      idx = activeItem.getParent().indexOf(item);
    }

    String status = "x-tree-drop-ok-between";
    if (before && idx == 0) {
      status = "x-tree-drop-ok-above";
    } else if (idx > 1 && !before && item.getParent() != null && idx == item.getParent().getItemCount() - 1) {
      status = "x-tree-drop-ok-below";
    }
    event.getStatus().setStatus(true, status);

    if (before) {
      showInsert(event, item.getElement(), true);
    } else {
      showInsert(event, item.getElement(), false);
    }
  }

  protected void handleInsertDrop(DNDEvent event, TreeNode item, int index) {
    List sel = event.getData();
    if (sel.size() > 0) {
      int idx = -1;
      if (item.getParent() == null) {
        idx = tree.getStore().getRootItems().indexOf(item.getModel());
      } else {
        idx = activeItem.getParent().indexOf(item);
      }
      
      idx = status == 0 ? idx : idx + 1;
      if (item.getParent() == null) {
        appendModel(null, sel, idx);
      } else {
        ModelData p = item.getParent().getModel();
        appendModel(p, sel, idx);
      }
    }
  }

  @Override
  protected void onDragDrop(DNDEvent event) {
    super.onDragDrop(event);
    if (event.getData() == null) return;

    if (activeItem == null && status == -1) {
      handleAppendDrop(event, activeItem);
    } else if (activeItem != null && status == -1) {
      tree.getView().onDropChange(activeItem, false);
      handleAppendDrop(event, activeItem);
    } else if (activeItem != null && status != -1) {
      handleInsertDrop(event, activeItem, status);
    } else {
      event.setCancelled(true);
    }
    tree.setTrackMouseOver(restoreTrackMouse);
    status = -1;
    activeItem = null;
    appendItem = null;
  }

  @Override
  protected void onDragEnter(DNDEvent e) {
    super.onDragEnter(e);
    e.getStatus().setStatus(false);
    restoreTrackMouse = tree.isTrackMouseOver();
    tree.setTrackMouseOver(false);
  }

  @Override
  protected void onDragLeave(DNDEvent e) {
    super.onDragLeave(e);
    if (activeItem != null) {
      tree.getView().onDropChange(activeItem, false);
      activeItem = null;
    }
    tree.setTrackMouseOver(restoreTrackMouse);
  }

  @Override
  protected void onDragMove(DNDEvent event) {
    event.setCancelled(false);
  }

  protected void clearStyles(DNDEvent event) {
    Insert.get().hide();
    event.getStatus().setStatus(false);
    if (activeItem != null) {
      tree.getView().onDropChange(activeItem, false);
    }
  }

  @Override
  protected void showFeedback(DNDEvent event) {
    final TreeNode overItem = tree.findNode(event.getTarget());
    if (overItem == null) {
      clearStyles(event);
    }

    if (overItem != null && event.getDropTarget().component == event.getDragSource().component) {
      TreePanel source = (TreePanel) event.getDragSource().component;

      List<ModelData> list = source.getSelectionModel().getSelection();
      ModelData overModel = overItem.getModel();
      for (int i = 0; i < list.size(); i++) {
        ModelData sel = list.get(i);
        if (overModel == sel) {
          clearStyles(event);
          return;
        }

        List<ModelData> children = tree.getStore().getChildren(sel, true);
        if (children.contains(overItem.getModel())) {
          clearStyles(event);
          return;
        }
      }
    }

    boolean append = feedback == Feedback.APPEND || feedback == Feedback.BOTH;
    boolean insert = feedback == Feedback.INSERT || feedback == Feedback.BOTH;

    if (overItem == null) {
      handleAppend(event, overItem);
    } else if (insert) {
      handleInsert(event, overItem);
    } else if ((!overItem.isLeaf() || allowDropOnLeaf) && append) {
      handleAppend(event, overItem);
    } else {
      if (activeItem != null) {
        tree.getView().onDropChange(activeItem, false);
      }
      status = -1;
      activeItem = null;
      appendItem = null;
      Insert.get().hide();
      event.getStatus().setStatus(false);
    }
  }

  private void showInsert(DNDEvent event, Element elem, boolean before) {
    Insert insert = Insert.get();
    insert.show(elem);
    Rectangle rect = El.fly(elem).getBounds();
    int y = before ? rect.y - 2 : (rect.y + rect.height - 4);
    insert.setBounds(rect.x, y, rect.width, 6);
  }

}
