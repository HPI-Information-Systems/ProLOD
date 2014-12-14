/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.client.mvp.clustertree;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class ClusterTreePanel<M extends ModelData> extends TreePanel<M> {

  public ClusterTreePanel(TreeStore<M> store) {
    super(store);
  }

  /**
   * initialized the progress indicator
   * 
   * @param element the element to be marked for loading
   */
  public void startLoading(M element) {
    // start loading indicator
    final TreeNode node = findNode(element);
    view.onLoading(node);
  }

  /**
   * stops the progress indicator
   * 
   * @param element the element to be unmarked for loading
   */
  public void stopLoading(M element) {
    // TODO stop loading indicator
  }

  /**
   * set the element to be marked as loaded
   * 
   * @param element the element to be marked as loaded
   */
  public void setLoaded(M element) {
    TreeNode node = findNode(element);
    if (node != null) {
      // node.loaded = true;
      this.setLoaded(node, true);
    }
  }

  private native void setLoaded(TreeNode node, boolean value) /*-{
                                                              // Write loaded field on node
                                                              node.@com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode::loaded = value;
                                                              }-*/;
}
