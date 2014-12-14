/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.treepanel.TreeStyle;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A hierarchical tree grid bound to a <code>TreeStore</code>.
 * 
 * <p />
 * A <code>TreeGridCellRenderer</code> can be assigned to the
 * <code>ColumnConfig</code> in which the tree will be displayed.
 * 
 * <p />
 * With state enabled, TreePanel will save and restore the expand state of the
 * nodes in the tree. A <code>ModelKeyProvider</code> must specified with the
 * <code>TreeStore</code> this tree is bound to. Save and restore works with
 * both local, and asynchronous loading of children.
 * 
 * @param <M> the model type
 */
public class TreeGrid<M extends ModelData> extends Grid<M> {
  public class TreeNode {

    protected String id;
    protected Element joint, check, text;
    protected M m;
    private boolean childrenRendered;
    private boolean expand;
    private boolean expandDeep;
    private boolean expanded;
    private boolean leaf = true;
    private boolean loaded;

    public TreeNode(String id, M m) {
      this.id = id;
      this.m = m;
      if (loader != null && !loaded) {
        leaf = !loader.hasChildren(m);
      }
    }

    public int getItemCount() {
      return treeStore.getChildCount(m);
    }

    public M getModel() {
      return m;
    }

    public TreeNode getParent() {
      M p = treeStore.getParent(m);
      return findNode(p);
    }

    public int indexOf(TreeNode child) {
      M c = child.getModel();
      return store.indexOf(c);
    }

    public boolean isExpanded() {
      return expanded;
    }

    public boolean isLeaf() {
      return !hasChildren(m);
    }

    public void setExpanded(boolean expand) {
      TreeGrid.this.setExpanded(m, expand);
    }

    public void setLeaf(boolean leaf) {
      this.leaf = leaf;
      TreeGrid.this.refresh(m);
    }
  }

  protected Map<M, String> cache;

  protected TreeLoader<M> loader;

  protected Map<String, TreeNode> nodes = new FastMap<TreeNode>();
  protected TreeGridView treeGridView;
  protected TreeStore<M> treeStore;
  private boolean autoLoad, filtering, autoExpand;
  private boolean caching = true;
  private boolean expandOnFilter = true;

  private ModelIconProvider<M> iconProvider;
  private ListStore<M> listStore = new ListStore<M>() {
    public Record getRecord(M model) {
      return treeStore.getRecord(model);
    };

    public boolean hasRecord(M model) {
      return treeStore.hasRecord(model);

    };

  };
  private StoreListener<M> storeListener = new StoreListener<M>() {
    @Override
    public void storeAdd(StoreEvent<M> se) {
      onAdd((TreeStoreEvent<M>) se);
    }

    @Override
    public void storeClear(StoreEvent<M> se) {
      onDataChanged((TreeStoreEvent<M>) se);
    }

    @Override
    public void storeDataChanged(StoreEvent<M> se) {
      onDataChanged((TreeStoreEvent<M>) se);
    }

    @Override
    public void storeFilter(StoreEvent<M> se) {
      onFilter((TreeStoreEvent<M>) se);
    }

    @Override
    public void storeRemove(StoreEvent<M> se) {
      onRemove((TreeStoreEvent<M>) se);
    }

    @Override
    public void storeUpdate(StoreEvent<M> se) {
      onUpdate((TreeStoreEvent<M>) se);
    }
  };
  private TreeStyle style = new TreeStyle();
  private Boolean useKeyProvider = null;

  @SuppressWarnings("unchecked")
  public TreeGrid(TreeStore store, ColumnModel cm) {
    this.store = listStore;
    this.cm = cm;
    focusable = true;
    baseStyle = "x-grid-panel";

    this.treeStore = store;
    this.loader = treeStore.getLoader();

    addStyleName("x-treegrid");
    disabledStyle = null;
    treeStore.addStoreListener(storeListener);

    setView(new TreeGridView());

    setSelectionModel(new TreeGridSelectionModel<M>());
  }

  /**
   * Collapses all nodes.
   */
  public void collapseAll() {
    for (M child : treeStore.getRootItems()) {
      setExpanded(child, false, true);
    }
  }
  
  /**
   * Expands all nodes.
   */
  public void expandAll() {
    for (M child : treeStore.getRootItems()) {
      setExpanded(child, true, true);
    }
  }
  
  /**
   * Returns the tree node for the given target.
   * 
   * @param target the target element
   * @return the tree node or null if no match
   */
  public TreeNode findNode(Element target) {
    Element row = (Element) getView().findRow(target);
    El item = fly(row).selectNode(".x-tree3-node");
    if (item != null) {
      String id = item.getId();
      TreeNode node = nodes.get(id);
      return node;
    }
    return null;
  }

  /**
   * Returns the model icon provider.
   * 
   * @return the icon provider
   */
  public ModelIconProvider<M> getIconProvider() {
    return iconProvider;
  }

  /**
   * Returns the tree style.
   * 
   * @return the tree style
   */
  public TreeStyle getStyle() {
    return style;
  }

  /**
   * Returns the tree's tree store.
   * 
   * @return the tree store
   */
  public TreeStore<M> getTreeStore() {
    return treeStore;
  }

  /**
   * Returns the tree's view.
   * 
   * @return the view
   */
  public TreeGridView getTreeView() {
    return treeGridView;
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
   * Returns true if auto load is enabled.
   * 
   * @return the auto load state
   */
  public boolean isAutoLoad() {
    return autoLoad;
  }

  /**
   * Returns true when a loader is queried for it's children each time a node is
   * expanded. Only applies when using a loader with the tree store.
   * 
   * @return true if caching
   */
  public boolean isCaching() {
    return caching;
  }

  /**
   * Returns true if the model is expanded.
   * 
   * @param model the model
   * @return true if expanded
   */
  public boolean isExpanded(M model) {
    TreeNode node = findNode(model);
    return node.isExpanded();
  }

  /**
   * Returns the if expand all and collapse all is enabled on filter changes.
   * 
   * @return the expand all collapse all state
   */
  public boolean isExpandOnFilter() {
    return expandOnFilter;
  }

  /**
   * Returns true if the model is a leaf node. The leaf state allows a tree item
   * to specify if it has children before the children have been realized.
   * 
   * @param model the model
   * @return the leaf state
   */
  public boolean isLeaf(M model) {
    TreeNode node = findNode(model);
    return node != null && node.isLeaf();
  }

  /**
   * If set to true, all non leaf nodes will be expanded automatically (defaults
   * to false).
   * 
   * @param autoExpand the auto expand state to set.
   */
  public void setAutoExpand(boolean autoExpand) {
    this.autoExpand = autoExpand;
  }

  /**
   * Sets whether all children should automatically be loaded recursively
   * (defaults to false). Useful when the tree must be fully populated when
   * initially rendered.
   * 
   * @param autoLoad true to auto load
   */
  public void setAutoLoad(boolean autoLoad) {
    this.autoLoad = autoLoad;
  }

  /**
   * Sets whether the children should be cached after first being retrieved from
   * the store (defaults to true). When <code>false</code>, a load request will
   * be made each time a node is expanded.
   * 
   * @param caching the caching state
   */
  public void setCaching(boolean caching) {
    this.caching = caching;
  }

  /**
   * Sets the item's expand state.
   * 
   * @param model the model
   * @param expand true to expand
   */
  public void setExpanded(M model, boolean expand) {
    setExpanded(model, expand, false);
  }

  /**
   * Sets the item's expand state.
   * 
   * @param model the model
   * @param expand true to expand
   * @param deep true to expand all children recursively
   */
  @SuppressWarnings("unchecked")
  public void setExpanded(M model, boolean expand, boolean deep) {
    TreeNode node = findNode(model);
    if (node != null) {
      if (expand) {
        // make parents visible
        List<M> list = new ArrayList<M>();
        M p = model;
        while ((p = treeStore.getParent(p)) != null) {
          if (!findNode(p).isExpanded()) {
            list.add(p);
          }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
          M item = list.get(i);
          setExpanded(item, expand, false);
        }
      }

      TreeGridEvent<M> tge = new TreeGridEvent<M>(this);
      tge.setModel(model);
      if (expand) {
        if (!node.isLeaf()) {
          // if we have a loader and node is not loaded make
          // load request and exit method
          if (!node.expanded && loader != null && (!node.loaded || !caching) && !filtering) {
            treeStore.removeAll(model);
            node.expand = true;
            node.expandDeep = deep;
            treeGridView.onLoading(node);
            loader.loadChildren(model);
            return;
          }
          if (!node.expanded && fireEvent(Events.BeforeExpand, tge)) {
            node.expanded = true;

            if (!node.childrenRendered) {
              renderChildren(model, false);
              node.childrenRendered = true;
            }
            // expand
            treeGridView.expand(node);

            if (isStateful() && treeStore.getKeyProvider() != null) {
              Map<String, Object> state = getState();
              List<String> expanded = (List) state.get("expanded");
              if (expanded == null) {
                expanded = new ArrayList<String>();
                state.put("expanded", expanded);
              }
              String id = treeStore.getKeyProvider().getKey(model);
              if (!expanded.contains(id)) {
                expanded.add(id);
                saveState();
              }
            }
            fireEvent(Events.Expand, tge);
          }
        }
        if (deep) {
          setExpandChildren(model, true);
        }

      } else {
        if (node.expanded && fireEvent(Events.BeforeCollapse, tge)) {
          node.expanded = false;
          // collapse
          treeGridView.collapse(node);

          if (isStateful() && treeStore.getKeyProvider() != null) {
            Map<String, Object> state = getState();
            List<String> expanded = (List) state.get("expanded");
            String id = treeStore.getKeyProvider().getKey(model);
            if (expanded != null && expanded.contains(id)) {
              expanded.remove(id);
              saveState();
            }
          }

          fireEvent(Events.Collapse, tge);
        }
        if (deep) {
          setExpandChildren(model, false);
        }
      }
    }
  }

  /**
   * Sets whether the tree should expand all and collapse all when filters are
   * applied (defaults to true).
   * 
   * @param expandOnFilter true to expand and collapse on filter changes
   */
  public void setExpandOnFilter(boolean expandOnFilter) {
    this.expandOnFilter = expandOnFilter;
  }

  /**
   * Sets the tree's model icon provider which provides the icon style for each
   * model.
   * 
   * @param iconProvider the icon provider
   */
  public void setIconProvider(ModelIconProvider<M> iconProvider) {
    this.iconProvider = iconProvider;
  }

  /**
   * Sets the item's leaf state. The leaf state allows control of the expand
   * icon before the children have been realized.
   * 
   * @param model the model
   * @param leaf the leaf state
   */
  public void setLeaf(M model, boolean leaf) {
    TreeNode t = findNode(model);
    if (t != null) {
      t.setLeaf(leaf);
    }
  }

  @Override
  public void setView(GridView view) {
    assert view instanceof TreeGridView : "The view for a TreeGrid has to be an instance of TreeGridView";
    super.setView(view);
    treeGridView = (TreeGridView) view;
  }

  /**
   * Toggles the model's expand state.
   * 
   * @param model the model
   */
  public void toggle(M model) {
    TreeNode node = findNode(model);
    if (node != null) {
      setExpanded(model, !node.expanded);
    }
  }

  protected Joint calcualteJoint(M model) {
    if (model == null) {
      return Joint.NONE;
    }
    TreeNode node = findNode(model);
    Joint joint = Joint.NONE;
    if (node == null) {
      return joint;
    }
    if (!node.isLeaf()) {
      boolean children = true;

      if (node.isExpanded()) {
        joint = children ? Joint.EXPANDED : Joint.NONE;
      } else {
        joint = children ? Joint.COLLAPSED : Joint.NONE;
      }
    }
    return joint;
  }

  protected AbstractImagePrototype calculateIconStyle(M model) {
    AbstractImagePrototype style = null;
    if (iconProvider != null) {
      AbstractImagePrototype iconStyle = iconProvider.getIcon((M) model);
      if (iconStyle != null) {
        return iconStyle;
      }
    }
    TreeNode node = findNode(model);
    TreeStyle ts = getStyle();
    if (!node.isLeaf()) {
      if (isExpanded(model)) {
        style = ts.getNodeOpenIcon();
      } else {
        style = ts.getNodeCloseIcon();
      }
    } else {
      style = ts.getLeafIcon();
    }
    return style;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new TreeGridEvent(this, event);
  }

  protected int findLastOpenChildIndex(M model) {
    TreeNode mark = findNode(model);
    M lc = null;
    while (mark != null && mark.expanded) {
      lc = treeStore.getLastChild(mark.m);
      mark = findNode(lc);
    }
    if (lc != null) {
      return store.indexOf(lc);
    }

    return store.indexOf(model);
  }

  protected TreeNode findNode(M m) {
    if (m == null || useKeyProvider == null) return null;
    return nodes.get(useKeyProvider ? generateModelId(m) : cache.get(m));
  }

  protected String generateModelId(M m) {
    return getId() + "_"
        + (treeStore.getKeyProvider() != null ? treeStore.getKeyProvider().getKey(m) : XDOM.getUniqueId());
  }

  protected boolean hasChildren(M model) {
    TreeNode node = findNode(model);
    if (loader != null && !node.loaded) {
      return loader.hasChildren(model);
    }
    if (!node.leaf || treeStore.getChildCount(model) > 0) {
      return true;
    }
    return false;
  }

  protected void onAdd(TreeStoreEvent<M> se) {
    if (viewReady) {
      M p = se.getParent();
      if (p == null) {
        for (M child : se.getChildren()) {
          register(child);
        }
        if (se.getIndex() > 0) {
          M prev = treeStore.getChild(se.getIndex() - 1);
          int index = findLastOpenChildIndex(prev);
          store.insert(se.getChildren(), index + 1);
        } else {
          store.insert(se.getChildren(), se.getIndex());
        }
      } else {
        TreeNode node = findNode(p);
        if (node != null) {
          for (M child : se.getChildren()) {
            register(child);
          }
          if (!node.expanded) {
            refresh(p);
            return;
          }
          int index = se.getIndex();
          int pindex = store.indexOf(p);

          if (index == 0) {
            store.insert(se.getChildren(), pindex + 1);
          } else {
            index = store.indexOf(treeStore.getChild(p, index - 1));
            TreeNode mark = findNode(store.getAt(index));
            index = findLastOpenChildIndex(mark.m);
            store.insert(se.getChildren(), index + 1);
          }
          refresh(p);
        }
      }
    }
  }

  @Override
  protected void onAfterRenderView() {
    super.onAfterRenderView();
    if (treeStore.getRootItems().size() == 0 && loader != null) {
      loader.load();
    } else {
      renderChildren(null, false);
      if (autoExpand) {
        expandAll();
      } else {
        statefulExpand(treeStore.getRootItems());
      }
    }
  }

  @Override
  protected void onClick(GridEvent<M> e) {
    M m = e.getModel();
    if (m != null) {
      TreeNode node = findNode(m);
      if (node != null) {
        Element jointEl = treeGridView.getJointElement(node);
        if (jointEl != null && e.within(jointEl)) {
          toggle(m);
        } else {
          super.onClick(e);
        }
      }
    }
  }

  protected void onDataChanged(TreeStoreEvent<M> se) {
    if (!isRendered() || !viewReady) {
      return;
    }
    
    M p = se.getParent();
    if (p == null) {
      store.removeAll();
      if (cache != null) {
        cache.clear();
      }
      nodes.clear();
      renderChildren(null, autoLoad);
      statefulExpand(treeStore.getRootItems());
    } else {
      TreeNode n = findNode(p);
      n.loaded = true;

      renderChildren(p, autoLoad);

      if (n.expand && !n.isLeaf()) {
        n.expand = false;
        boolean deep = n.expandDeep;
        n.expandDeep = false;
        boolean c = caching;
        caching = true;
        setExpanded(p, true, deep);
        caching = c;
      }
      statefulExpand(treeStore.getChildren(p));
    }
  }

  @Override
  protected void onDoubleClick(GridEvent<M> e) {
    super.onDoubleClick(e);
    toggle(e.getModel());
  }

  protected void onFilter(TreeStoreEvent<M> se) {
    onDataChanged(se);
    if (expandOnFilter && treeStore.isFiltered()) {
      expandAll();
    }
  }

  protected void onRemove(TreeStoreEvent<M> se) {
    if (viewReady) {
      unregister(se.getChild());
      store.remove(se.getChild());
      for (M child : se.getChildren()) {
        unregister(child);
        store.remove(child);
      }
      TreeNode p = findNode(se.getParent());
      if (p != null && p.expanded && p.getItemCount() == 0) {
        setExpanded(p.m, false);
      } else if (p != null && p.getItemCount() == 0) {
        refresh(se.getParent());
      }
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);

    el().setTabIndex(0);
    el().setElementAttribute("hideFocus", "true");

    sinkEvents(Event.ONCLICK | Event.ONDBLCLICK | Event.MOUSEEVENTS | Event.KEYEVENTS);
  }

  protected void onUpdate(TreeStoreEvent<M> se) {
    store.fireEvent(Store.Update, se);
  }

  protected void refresh(M model) {
    TreeNode node = findNode(model);
    if (rendered && viewReady && node != null) {
      AbstractImagePrototype style = calculateIconStyle(model);
      treeGridView.onIconStyleChange(node, style);
      Joint j = calcualteJoint(model);
      treeGridView.onJointChange(node, j);
    }
  }

  protected String register(M m) {
    if (useKeyProvider == null) {
      if (treeStore.getKeyProvider() == null) {
        useKeyProvider = false;
      } else {
        useKeyProvider = true;
      }
    }
    if (!useKeyProvider) {
      if (cache == null) {
        cache = new HashMap<M, String>();
      }
      String id = cache.get(m);
      if (id == null) {
        id = generateModelId(m);
        cache.put(m, id);
        nodes.put(id, new TreeNode(id, m));
      }
      return id;
    }
    String id = generateModelId(m);
    if (!nodes.containsKey(id)) {
      nodes.put(id, new TreeNode(id, m));
    }
    return id;
  }

  protected void renderChildren(M parent, boolean auto) {
    List<M> children = parent == null ? treeStore.getRootItems() : treeStore.getChildren(parent);

    for (M child : children) {
      register(child);
    }

    if (parent == null) {
      store.add(children);
    }

    for (M child : children) {
      if (autoExpand) {
        final M c = child;
        DeferredCommand.addCommand(new Command() {
          public void execute() {
            setExpanded(c, true);
          }
        });
      } else if (loader != null) {
        if (autoLoad) {
          if (store.isFiltered() || (!auto)) {
            renderChildren(child, auto);
          } else {
            loader.loadChildren(child);
          }
        }
      }
    }
  }

  protected void unregister(M m) {
    if (m != null && useKeyProvider != null) {
      TreeNode node = findNode(m);
      if (useKeyProvider) {
        nodes.remove(generateModelId(m));
      } else {
        nodes.remove(cache.remove(m));
      }
      TreeGridEvent<M> e = new TreeGridEvent<M>(this);
      e.setModel(m);
      e.setTreeNode(node);
      fireEvent(Events.Unregister, e);
    }
  }

  private void setExpandChildren(M m, boolean expand) {
    for (M child : treeStore.getChildren(m)) {
      setExpanded(child, expand, true);
    }
  }

  @SuppressWarnings("unchecked")
  private void statefulExpand(List<M> children) {
    if (isStateful() && treeStore.getKeyProvider() != null) {
      List<String> expanded = (List) getState().get("expanded");
      if (expanded != null) {
        for (M child : children) {
          String id = treeStore.getKeyProvider().getKey(child);
          if (expanded.contains(id)) {
            setExpanded(child, true);
          }
        }
      }
    }
  }
}
