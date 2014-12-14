/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * <code>TreePanel</code> event type.
 * 
 * @param <M> the model type contained in the tree
 */
@SuppressWarnings("unchecked")
public class TreePanelEvent<M extends ModelData> extends BoxComponentEvent {

  private M parent;
  private M child;
  private M item;
  private TreeStore<M> store;
  private TreeNode node;
  private TreePanel<M> treePanel;
  private boolean checked;

  public TreePanelEvent(TreePanel tree) {
    super(tree);
    this.treePanel = tree;
  }

  public TreePanelEvent(TreePanel tree, Event event) {
    super(tree, event);
    this.treePanel = tree;
  }

  public TreePanelEvent(TreePanel tree, M item) {
    this(tree);
    setItem(item);
  }

  /**
   * Returns the child model.
   * 
   * @return the child
   */
  public M getChild() {
    return child;
  }

  /**
   * Returns the item.
   * 
   * @return the item
   */
  public M getItem() {
    if (item == null) {
      if (getNode() != null) {
        item = (M) getNode().getModel();
      }
    }
    return item;
  }

  /**
   * Returns the tree node.
   * 
   * @return the tree node
   */
  public TreeNode getNode() {
    if (node == null) {
      node = treePanel.findNode((Element) event.getEventTarget().cast());
    }
    return node;
  }

  /**
   * Returns the parent.
   * 
   * @return the parent
   */
  public M getParent() {
    return parent;
  }

  /**
   * Returns the source tree store.
   * 
   * @return the tree store
   */
  public TreeStore getStore() {
    return store;
  }

  /**
   * Returns the source tree panel.
   * 
   * @return the source tree panel
   */
  public TreePanel<M> getTreePanel() {
    return treePanel;
  }

  /**
   * Returns true if checked.
   * 
   * @return true if checked
   */
  public boolean isChecked() {
    return checked;
  }

  /**
   * Sets the checked state.
   * 
   * @param checked the checked state
   */
  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  /**
   * Sets the child.
   * 
   * @param child the child
   */
  public void setChild(M child) {
    this.child = child;
  }

  /**
   * Sets the item.
   * 
   * @param item the item
   */
  public void setItem(M item) {
    this.item = item;
  }

  /**
   * Sets the tree node.
   * 
   * @param node the tree node
   */
  public void setNode(TreeNode node) {
    this.node = node;
  }

  /**
   * Sets the parent.
   * 
   * @param parent the parent
   */
  public void setParent(M parent) {
    this.parent = parent;
  }

  /**
   * Sets the source tree store.
   * 
   * @param store the tree store
   */
  public void setStore(TreeStore store) {
    this.store = store;
  }

  /**
   * Sets the source tree panel.
   * 
   * @param treePanel the tree panel
   */
  public void setTreePanel(TreePanel<M> treePanel) {
    this.treePanel = treePanel;
  }

}
