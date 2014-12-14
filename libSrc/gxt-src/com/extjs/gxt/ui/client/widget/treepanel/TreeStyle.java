/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treepanel;

import com.extjs.gxt.ui.client.GXT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Style information for Trees. There are two types for tree items: nodes and
 * leafs. Leafs are item's without children. Nodes are items with children or
 * items with their leaf flag set to false.
 */
public class TreeStyle {

  private AbstractImagePrototype jointCollapsedIcon = GXT.IMAGES.tree_collapsed();
  private AbstractImagePrototype jointExpandedIcon = GXT.IMAGES.tree_expanded();
  private AbstractImagePrototype leafIcon = null;
  private AbstractImagePrototype nodeOpenIcon = GXT.IMAGES.tree_folder();
  private AbstractImagePrototype nodeCloseIcon = GXT.IMAGES.tree_folder_closed();

  /**
   * @return the jointExpandedIcon
   */
  public AbstractImagePrototype getJointExpandedIcon() {
    return jointExpandedIcon;
  }

  /**
   * @return the jointCollapsedIcon
   */
  public AbstractImagePrototype getJointCollapsedIcon() {
    return jointCollapsedIcon;
  }

  /**
   * Returns the icon style for leaf items.
   * 
   * @return the icon style
   */
  public AbstractImagePrototype getLeafIcon() {
    return leafIcon;
  }

  /**
   * Returns the global icon style for closed nodes.
   * 
   * @return the style name
   */
  public AbstractImagePrototype getNodeCloseIcon() {
    return nodeCloseIcon;
  }

  /**
   * Returns the global icon style for open nodes.
   * 
   * @return the style name
   */
  public AbstractImagePrototype getNodeOpenIcon() {
    return nodeOpenIcon;
  }

  /**
   * @param jointCollapsedIcon the jointCollapsedIcon to set
   */
  public void setJointCollapsedIcon(AbstractImagePrototype jointCollapsedIcon) {
    this.jointCollapsedIcon = jointCollapsedIcon;
  }

  /**
   * @param jointExpandedIcon the jointExpandedIcon to set
   */
  public void setJointExpandedIcon(AbstractImagePrototype jointExpandedIcon) {
    this.jointExpandedIcon = jointExpandedIcon;
  }

  /**
   * Sets the global icon style for leaf tree items. Individual tree items can
   * override this value by setting the the item's icon style.
   * 
   * @param itemIcon the icon
   */
  public void setLeafIcon(AbstractImagePrototype itemIcon) {
    this.leafIcon = itemIcon;
  }

  /**
   * Sets the icon style used for closed tree items.
   * 
   * @param folderCloseIcon the icon
   */
  public void setNodeCloseIcon(AbstractImagePrototype folderCloseIcon) {
    this.nodeCloseIcon = folderCloseIcon;
  }

  /**
   * Sets the global icon style for expanded tree items (defaults to
   * 'tree-folder-open'). Individual tree items can override this value by
   * setting the the item's icon style.
   * 
   * @param folderOpenIcon the open node icon
   */
  public void setNodeOpenIcon(AbstractImagePrototype folderOpenIcon) {
    this.nodeOpenIcon = folderOpenIcon;
  }

}
