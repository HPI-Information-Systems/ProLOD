/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treepanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.selection.AbstractStoreSelectionModel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.user.client.Event;

/**
 * <code>TreePanel</code> selection model.
 * 
 * @param <M> the model type
 */
@SuppressWarnings("unchecked")
public class TreePanelSelectionModel<M extends ModelData> extends AbstractStoreSelectionModel<M> implements
    Listener<TreePanelEvent> {
  protected TreePanel tree;
  protected TreeStore<M> treeStore;
  protected KeyNav<TreePanelEvent<M>> keyNav = new KeyNav<TreePanelEvent<M>>() {
    @Override
    public void onDown(TreePanelEvent<M> e) {
      onKeyDown(e);
    }

    @Override
    public void onLeft(TreePanelEvent<M> ce) {
      onKeyLeft(ce);
    }

    @Override
    public void onRight(TreePanelEvent<M> ce) {
      onKeyRight(ce);
    }

    @Override
    public void onUp(TreePanelEvent<M> e) {
      onKeyUp(e);
    }
  };

  public TreePanelSelectionModel() {
    storeListener = new StoreListener<M>() {
      @Override
      public void storeAdd(StoreEvent<M> se) {
        TreeStoreEvent<M> tse = (TreeStoreEvent) se;
        onAdd(tse.getChildren());
      }

      @Override
      public void storeClear(StoreEvent<M> se) {
        onClear(se);
      }

      @Override
      public void storeRemove(StoreEvent<M> se) {
        TreeStoreEvent<M> tse = (TreeStoreEvent) se;
        onRemove(tse.getChild());
        for (M child : tse.getChildren()) {
          onRemove(child);
        }
      }

      @Override
      public void storeUpdate(StoreEvent<M> se) {
        onUpdate(se.getModel());
      }
    };
  }

  public void bindTree(TreePanel tree) {
    if (this.tree != null) {
      this.tree.removeListener(Events.OnMouseDown, this);
      this.tree.removeListener(Events.OnClick, this);
      keyNav.bind(null);
      bind(null);
      this.treeStore = null;
    }
    this.tree = tree;
    if (tree != null) {
      tree.addListener(Events.OnMouseDown, this);
      tree.addListener(Events.OnClick, this);
      keyNav.bind(tree);
      bind(tree.getStore());
      this.treeStore = (TreeStore) tree.getStore();
    }
  }

  @Override
  public void deselect(int index) {
  }

  @Override
  public void deselect(int start, int end) {
  }

  public void handleEvent(TreePanelEvent tpe) {
    int type = tpe.getEventTypeInt();
    switch (type) {
      case Event.ONMOUSEDOWN:
        onMouseDown(tpe);
        break;
      case Event.ONCLICK:
        onMouseClick(tpe);
        break;
    }
  }

  @Override
  public boolean isSelected(M item) {
    return selected.contains(item);
  }

  @Override
  public void select(int start, int end, boolean keepExisting) {
  }

  /**
   * Selects the item below the selected item in the tree, intelligently walking
   * the nodes.
   */
  public void selectNext() {
    M next = next();
    if (next != null) {
      doSingleSelect(next, false);
    }
  }

  /**
   * Selects the item above the selected item in the tree, intelligently walking
   * the nodes.
   */
  public void selectPrevious() {
    M prev = prev();
    if (prev != null) {
      doSingleSelect(prev, false);
    }
  }

  protected M next() {
    M sel = lastSelected;
    if (sel == null) {
      return null;
    }
    M first = treeStore.getFirstChild(sel);
    if (first != null && tree.isExpanded(sel)) {
      return first;
    } else {
      M nextSibling = treeStore.getNextSibling(sel);
      if (nextSibling != null) {
        return nextSibling;
      } else {
        M p = treeStore.getParent(sel);
        while (p != null) {
          nextSibling = treeStore.getNextSibling(p);
          if (nextSibling != null) {
            return nextSibling;
          }
          p = treeStore.getParent(p);
        }
      }
    }
    return null;
  }

  protected void onKeyDown(TreePanelEvent<M> e) {
    e.preventDefault();
    M next = next();
    if (next != null) {
      doSingleSelect(next, false);
      tree.scrollIntoView(next);
    }
  }

  protected void onKeyLeft(TreePanelEvent<M> ce) {
    ce.preventDefault();
    if (!tree.isLeaf(lastSelected) && tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, false);
    } else if (treeStore.getParent(lastSelected) != null) {
      doSingleSelect(treeStore.getParent(lastSelected), false);
    }
  }

  protected void onKeyRight(TreePanelEvent<M> ce) {
    ce.preventDefault();
    if (!tree.isLeaf(lastSelected) && !tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, true);
    }
  }

  protected void onKeyUp(TreePanelEvent<M> e) {
    e.preventDefault();
    M prev = prev();
    if (prev != null) {
      doSingleSelect(prev, false);
      tree.scrollIntoView(prev);
    }
  }

  protected void onMouseDown(TreePanelEvent be) {
    if (be.getItem() == null) return;
    if (!tree.getView().isSelectableTarget(be.getItem(), be.getTarget())) {
      return;
    }
    if (be.isRightClick() && isSelected((M) be.getItem())) {
      return;
    }
    M sel = (M) be.getItem();
    switch (selectionMode) {
      case SIMPLE:
        if (isSelected(sel)) {
          deselect(sel);
        } else {
          doSelect(Util.createList(sel), true, false);
        }
        break;
      case SINGLE:
        doSingleSelect(sel, false);
        break;
      case MULTI:
        if (isSelected(sel) && !be.isControlKey() && !be.isShiftKey()) {
          return;
        }
        if (be.isShiftKey() && lastSelected != null) {
          List<M> items = new ArrayList<M>();
          if (lastSelected == sel) {
            return;
          }
          TreeNode selNode = tree.findNode(lastSelected);
          TreeNode itemNode = tree.findNode(sel);
          if (selNode.element != null && itemNode.element != null) {
            if (selNode.element.getAbsoluteTop() < itemNode.element.getAbsoluteTop()) {
              M next = next();
              while (next != null) {
                items.add(next);
                lastSelected = next;
                if (next == sel) break;
                next = next();
              }
            } else {
              M prev = prev();
              while (prev != null) {
                items.add(prev);
                lastSelected = prev;
                if (prev == sel) break;
                prev = prev();
              }
            }
            doSelect(items, true, false);
          }
        } else if (be.isControlKey() && isSelected(sel)) {
          doDeselect(Arrays.asList(sel), false);
        } else {
          doSelect(Arrays.asList(sel), be.isControlKey(), false);
        }
        break;
    }
  }

  protected void onMouseClick(TreePanelEvent e) {
    if (isLocked()) {
      return;
    }
    if (selectionMode == SelectionMode.MULTI) {
      M sel = (M) e.getItem();
      if (isSelected(sel) && getSelectedItems().size() > 1) {
        if (!e.isControlKey() && !e.isShiftKey()) {
           select(Arrays.asList(sel), false);
        }
      }
    }
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    tree.getView().onSelectChange(model, select);
  }

  protected M prev() {
    M sel = lastSelected;
    if (sel == null) {
      return sel;
    }
    M prev = treeStore.getPreviousSibling(sel);
    if (prev != null) {
      if ((!tree.isExpanded(prev) || treeStore.getChildCount(prev) < 1)) {
        return prev;
      } else {
        M lastChild = treeStore.getLastChild(prev);
        while (lastChild != null && treeStore.getChildCount(lastChild) > 0 && tree.isExpanded(lastChild)) {
          lastChild = treeStore.getLastChild(lastChild);
        }
        return lastChild;
      }
    } else {
      M parent = treeStore.getParent(sel);
      if (parent != null) {
        return parent;
      }
    }
    return null;
  }
}