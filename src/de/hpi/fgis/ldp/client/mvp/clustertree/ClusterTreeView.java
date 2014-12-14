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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.client.mvp.clustertree.ClusterTreePresenter.ClusterRequestSource;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class ClusterTreeView extends ContentPanel implements ClusterTreePresenter.Display {
  protected ClusterTreePanel<ClusterTreeDataModel> tree = null;
  protected TreeStore<ClusterTreeDataModel> store = null;

  protected MessageBox progressBox;

  private final boolean adminMode;

  // multiple schemas in this tree
  protected final HashMap<Cluster, AsyncCallback<List<ClusterTreeDataModel>>> openRequests =
      new HashMap<Cluster, AsyncCallback<List<ClusterTreeDataModel>>>();
  //
  // private final HashMap<Cluster, ClusterTreeDataModel>
  // clusterToTreeModelMap = new HashMap<Cluster, ClusterTreeDataModel>();
  protected ClusterRequestSource clusterRequestSource = null;

  @Inject
  public ClusterTreeView(@Named("gui.admin") boolean adminMode) {
    this.adminMode = adminMode;
    // this.setHeaderVisible(false);
    this.setHeaderVisible(true);
    this.setHeading("Cluster");
    this.setBorders(false);
    this.setLayout(new RowLayout());
  }

  private void init() {
    this.removeAll();

    if (this.clusterRequestSource != null) {
      initTreeStore();

      setTreeKeyProvider();

      setTreeSorter();

      initTreePanel();

      addContextMenu();

      this.add(new Text("Enter a search string:"), new RowData(1, 20));
      this.add(this.createFilter(), new RowData(1, 24));
      this.add(this.tree, new RowData(1, 1));
    }
  }

  private void showWaitingBox() {
    if (this.progressBox == null) {
      this.progressBox = MessageBox.wait("Please wait!", "Request sent!", "Processing ...");
    } else {
      this.progressBox.show();
    }
  }

  private void addContextMenu() {
    // add context menu
    Menu contextMenu = new Menu();
    // info item
    contextMenu.add(getClusterInfoMenuItem());
    contextMenu.add(getRefreshSubtreeMenuItem());
    // ---
    contextMenu.add(new SeparatorMenuItem());
    // show all subjects
    contextMenu.add(getShowSubjectsMenuItem());

    ArrayList<Item> adminItems = new ArrayList<Item>();

    // ---
    adminItems.add(new SeparatorMenuItem());
    // cluster items
    adminItems.add(getRenameClusterMenuItem());
    adminItems.add(getCreateSubclusterMenuItem());
    adminItems.add(getMergeClusterMenuItem());
    // ---
    adminItems.add(new SeparatorMenuItem());
    adminItems.add(getImportDatasetMenuItem());
    // deactivate schema editing functions if not in admin mode
    if (!this.adminMode) {
      for (Item currentItem : adminItems) {
        currentItem.disable();
      }
      adminItems.add(getSwitchUserViewMenuItem());
      for (Item currentItem : adminItems) {
        contextMenu.add(currentItem);
      }

      adminItems = new ArrayList<Item>();
    }
    adminItems.add(getChangeUserViewMenuItem());

    adminItems.add(getDropSchemaMenuItem());

    // deactivate schema editing functions if not in admin mode
    if (!this.adminMode) {
      for (Item currentItem : adminItems) {
        currentItem.disable();
      }
    } else {
      Log.info("Admin mode activated!");
    }

    for (Item currentItem : adminItems) {
      contextMenu.add(currentItem);
    }

    this.tree.setContextMenu(contextMenu);
  }

  /**
   * checks if the source is writable for cluster operations
   * 
   * @param source the data source
   * @return <code>true</code>, if the source is editable otherwise <code>false</code>
   */
  protected boolean isEditable(DataSource source) {
    if (source.getUserView() == null) {
      MessageBox.info("Access denied!", "Please change the user view!",
          new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
              // nothing to do
            }
          });
      return false;
    }
    return true;
  }

  private void initTreePanel() {
    this.tree = new ClusterTreePanel<ClusterTreeDataModel>(this.store);

    this.tree.setDisplayProperty("displayname");
    // statefull components need a defined id
    this.tree.setStateful(true);
    this.tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-list"));
    this.tree.setId("statefullasyncclustertreepanel");

    this.tree.setSelectionModel(new TreePanelSelectionModel<ClusterTreeDataModel>() {
      private ClusterTreeDataModel lastSelection = null;

      @Override
      @SuppressWarnings("rawtypes")
      protected void onMouseClick(TreePanelEvent e) {
        super.onMouseClick(e);

        final ClusterTreeDataModel currentSelection = (ClusterTreeDataModel) e.getItem();
        synchronized (this) {
          // sth. selected
          if (currentSelection != null) {
            // ignore repetitions
            if (!currentSelection.equals(lastSelection)) {
              ClusterTreeView.this.clusterRequestSource.activateCluster(currentSelection
                  .getCluster());
            }
          } else {
            // ignore repetitions
            if (lastSelection != null) {
              // remove selection
              ClusterTreeView.this._deselect();
            }
          }
          lastSelection = currentSelection;
        }
      }
    });
  }

  private void setTreeSorter() {
    this.store.setStoreSorter(new StoreSorter<ClusterTreeDataModel>() {
      @Override
      public int compare(Store<ClusterTreeDataModel> store, ClusterTreeDataModel m1,
          ClusterTreeDataModel m2, String property) {
        return Integer.valueOf(m1.getCluster().getIndex()).compareTo(
            Integer.valueOf(m2.getCluster().getIndex()));
      }
    });
  }

  private void setTreeKeyProvider() {
    this.store.setKeyProvider(new ModelKeyProvider<ClusterTreeDataModel>() {
      @Override
      public String getKey(ClusterTreeDataModel model) {
        return model.getKey();
      }
    });
  }

  private StoreFilterField<ClusterTreeDataModel> createFilter() {
    final StoreFilterField<ClusterTreeDataModel> filter =
        new StoreFilterField<ClusterTreeDataModel>() {
          @Override
          protected boolean doSelect(Store<ClusterTreeDataModel> store,
              ClusterTreeDataModel parent, ClusterTreeDataModel record, String property,
              String filter) {

            final Cluster cluster = record.getCluster();
            final String name = cluster.getLabel().toLowerCase();

            if (name.contains(filter.toLowerCase())) {
              return true;
            }
            return false;
          }
        };

    filter.bind(this.store);
    return filter;
  }

  private void initTreeStore() {
    final ClusterRequestSource source = this.clusterRequestSource;
    // data proxy
    RpcProxy<List<ClusterTreeDataModel>> proxy = new RpcProxy<List<ClusterTreeDataModel>>() {
      @Override
      protected void load(final Object loadConfig,
          final AsyncCallback<List<ClusterTreeDataModel>> callback) {

        Cluster parent = null;
        if (loadConfig == null) {
          // initial cluster view (w/o any tree nodes)
          source.getClusterChildren(null);
        } else {
          parent = ((ClusterTreeDataModel) loadConfig).getCluster();
          source.getClusterChildren(parent);
        }

        openRequests.put(parent, callback);

      }
    };

    // tree loader
    BaseTreeLoader<ClusterTreeDataModel> loader = new BaseTreeLoader<ClusterTreeDataModel>(proxy) {
      @Override
      public boolean hasChildren(ClusterTreeDataModel parent) {
        return parent.getCluster().getChildSessionID() >= 0;
      }
    };

    // trees store
    this.store = new TreeStore<ClusterTreeDataModel>(loader);
  }

  private MenuItem getMergeClusterMenuItem() {
    MenuItem mergeCluster = new MenuItem();
    mergeCluster.setText("Merge Cluster");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    mergeCluster.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        if (!isEditable(selectedCluster.getDataSource())) {
          return;
        }
        // ignore root clusters
        if (selectedCluster.getId() < 0) {
          Info.display("Failure", "Unable merge root clusters!");
          return;
        }

        final Cluster parent = ClusterTreeView.this._getParent(selectedCluster);

        final Dialog dialog = new Dialog();
        dialog.setBodyBorder(false);
        dialog.setHeading("Cluster Merge Dialog");
        dialog.setWidth(400);
        dialog.setHeight(225);
        dialog.setHideOnButtonClick(true);

        BorderLayout layout = new BorderLayout();
        dialog.setLayout(layout);

        // center
        ContentPanel panel = new ContentPanel();
        panel.setHeaderVisible(false);
        panel.setBorders(false);
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        dialog.add(panel, data);

        final ComboBox<ClusterTreeDataModel> combo = new ComboBox<ClusterTreeDataModel>();
        combo.setAutoWidth(true);
        combo.setStore(new ListStore<ClusterTreeDataModel>());
        combo.setDisplayField("displayname");
        for (Cluster currentCluster : ClusterTreeView.this._getChildren(parent)) {
          if (!selectedCluster.equals(currentCluster)) {
            combo.getStore().add(new ClusterTreeDataModel(currentCluster));
          }
        }

        panel.add(new Label("merge " + selectedCluster.getLabel() + " with:"));
        panel.add(combo);

        dialog.addListener(Events.Hide, new Listener<WindowEvent>() {
          @Override
          public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() != null
                && "OK".equalsIgnoreCase(be.getButtonClicked().getText())) {
              ArrayList<Cluster> clusters = new ArrayList<Cluster>(combo.getSelection().size() + 1);
              clusters.add(selectedCluster);
              for (ClusterTreeDataModel currentModel : combo.getSelection()) {
                clusters.add(currentModel.getCluster());
              }

              ClusterTreeView.this.clusterRequestSource.mergeClusters(parent, clusters);
            }
          }
        });

        dialog.show();
      }
    });
    return mergeCluster;
  }

  private MenuItem getCreateSubclusterMenuItem() {
    MenuItem subCluster = new MenuItem();
    subCluster.setText("(Re-)Create Subcluster");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    subCluster.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        if (!isEditable(selectedCluster.getDataSource())) {
          return;
        }
        final MessageBox box =
            MessageBox.prompt("Rename", "Please enter the number of Subclusters to create:");
        box.addCallback(new Listener<MessageBoxEvent>() {
          @Override
          public void handleEvent(MessageBoxEvent be) {
            if ("OK".equalsIgnoreCase(be.getButtonClicked().getText())) {
              try {
                int numOfClusters = Integer.valueOf(be.getValue()).intValue();

                if (numOfClusters > 1) {
                  ClusterTreeView.this.clusterRequestSource.createSubClusters(selectedCluster,
                      numOfClusters);
                } else {
                  Info.display("Failure", "Unable create less than 2 subclusters!");
                }
              } catch (NumberFormatException e) {
                Info.display("Failure", "Unable to read Number '{0}'!", new Params(be.getValue()));
              }
            }
          }
        });
      }
    });
    return subCluster;
  }

  private MenuItem getRenameClusterMenuItem() {
    MenuItem rename = new MenuItem();
    rename.setText("Rename Cluster");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    rename.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        if (!isEditable(selectedCluster.getDataSource())) {
          return;
        }
        // root cluster
        if (selectedCluster.getId() < 0) {
          MessageBox.alert("Error!", "Unable to rename a schema!", null);
        } else {
          final String oldName = selectedCluster.getLabel();
          final MessageBox box =
              MessageBox.prompt("Rename", "Please enter the new name for \"" + oldName + "\":");
          box.addCallback(new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
              if ("OK".equalsIgnoreCase(be.getButtonClicked().getText())) {
                final String newName = be.getValue();

                if (!oldName.equals(newName)) {
                  selectedCluster.setLabel(newName);
                  ClusterTreeView.this.clusterRequestSource.renameCluster(selectedCluster);
                }
              }
            }
          });
        }
      }
    });
    return rename;
  }

  private MenuItem getShowSubjectsMenuItem() {
    MenuItem samples = new MenuItem();
    samples.setText("Show all subjects");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    samples.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        ClusterTreeView.this.clusterRequestSource.showSubjects(selectedCluster);
      }
    });
    return samples;
  }

  private MenuItem getImportDatasetMenuItem() {
    MenuItem importDS = new MenuItem();
    importDS.setText("Import new dataset");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    importDS.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        ClusterTreeView.this.clusterRequestSource.importDataset();
      }
    });
    return importDS;
  }

  private MenuItem getChangeUserViewMenuItem() {
    MenuItem menuItem = new MenuItem();
    menuItem.setText("Change user view");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    menuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        final DataSource schema = selectedCluster.getDataSource();
        final Cluster rootCluster = ClusterTreeView.this._getRootCluster(schema);
        final String oldView = (schema.getUserView() != null) ? schema.getUserView() : "";
        final MessageBox box =
            MessageBox.prompt("Change user view", "Please enter the name of the user view:");
        box.getTextBox().setValue(oldView);
        box.addCallback(new Listener<MessageBoxEvent>() {
          @Override
          public void handleEvent(MessageBoxEvent be) {
            if ("OK".equalsIgnoreCase(be.getButtonClicked().getText())) {
              final String newView = ("".equals(be.getValue())) ? null : be.getValue();

              ClusterTreeView.this.clusterRequestSource.changeUserView(rootCluster, newView);
            }
          }
        });
      }
    });
    return menuItem;
  }

  // FIXME only temporary solution for non-admin interfaces .. please change
  // ..
  private MenuItem getSwitchUserViewMenuItem() {
    MenuItem menuItem = new MenuItem();
    menuItem.setText("Switch between clustering and ontology");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    menuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        final DataSource schema = selectedCluster.getDataSource();
        final Cluster rootCluster = ClusterTreeView.this._getRootCluster(schema);
        final String oldView =
            (schema.getUserView() != null) ? schema.getUserView() : DataSource.DEFAULT_USER_VIEW;
        String newView;
        if (oldView.equalsIgnoreCase(DataSource.ONTOLOGY_USER_VIEW)) {
          newView = DataSource.CLUSTER_USER_VIEW;
        } else {
          newView = DataSource.ONTOLOGY_USER_VIEW;
        }

        ClusterTreeView.this.clusterRequestSource.switchUserView(rootCluster, newView);
        // final MessageBox box = MessageBox.prompt("Change user view",
        // "Please enter the name of the user view:");
        // box.getTextBox().setValue(oldView);
        // box.addCallback(new Listener<MessageBoxEvent>() {
        // public void handleEvent(MessageBoxEvent be) {
        // if ("OK".equalsIgnoreCase(be.getButtonClicked().getText())) {
        // final String newView = ("".equals(be.getValue())) ? null : be
        // .getValue();
        //
        // ClusterTreeView.this.clusterRequestSource.changeUserView(rootCluster,
        // newView);
        // }
        // }
        // });
      }
    });
    return menuItem;
  }

  private MenuItem getDropSchemaMenuItem() {
    MenuItem drop = new MenuItem();
    drop.setText("Drop schema");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    drop.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        ClusterTreeView.this.clusterRequestSource.dropSchema(_getRootCluster(selectedCluster
            .getDataSource()));
      }
    });
    return drop;
  }

  private MenuItem getClusterInfoMenuItem() {
    MenuItem clusterInfo = new MenuItem();
    clusterInfo.setText("General Information");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    clusterInfo.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();
        ClusterTreeView.this.clusterRequestSource.showClusterInfo(selectedCluster);
      }
    });
    return clusterInfo;
  }

  private MenuItem getRefreshSubtreeMenuItem() {
    MenuItem clusterInfo = new MenuItem();
    clusterInfo.setText("Refresh subtree");
    // TODO set icon
    // insert.setIcon(Resources.ICONS.add());
    clusterInfo.addSelectionListener(new SelectionListener<MenuEvent>() {
      @Override
      public void componentSelected(MenuEvent ce) {
        final Cluster selectedCluster = ClusterTreeView.this._getSelected();

        if (selectedCluster == null) {
          // no cluster selected -> update the hole tree
          ClusterTreeView.this.clusterRequestSource.getClusterChildren(null);
        } else {
          // not necessary
          // openRequests.put(cluster, null);
          ClusterTreeView.this.clusterRequestSource.getClusterChildren(selectedCluster);
        }
      }
    });
    return clusterInfo;
  }

  @Override
  public void setClusterSource(final ClusterRequestSource source) {
    this.clusterRequestSource = source;
    this.tree = null;
    this.store = null;
    this.init();
  }

  @Override
  public void addClusters(final Cluster parent, ArrayList<Cluster> children) {
    final AsyncCallback<List<ClusterTreeDataModel>> callback = this.openRequests.remove(parent);

    if (children != null) {
      if (callback != null) {
        // create a cluster model list with the child clusters
        final ArrayList<ClusterTreeDataModel> result =
            new ArrayList<ClusterTreeDataModel>(children.size());
        for (final Cluster currentCluster : children) {
          result.add(new ClusterTreeDataModel(currentCluster));
        }

        callback.onSuccess(result);
      } else {
        this._addChildren(parent, children);
      }
    }
  }

  private void _addChildren(Cluster parent, ArrayList<Cluster> children) {
    if (parent == null) {
      // remove roots
      this.store.removeAll();

      // add roots
      for (Cluster currentChild : children) {
        final ClusterTreeDataModel childModel = new ClusterTreeDataModel(currentChild);
        this.store.add(childModel, currentChild.getChildSessionID() >= 0);
        this._updateLoadingState(childModel);
      }
    } else {
      ClusterTreeDataModel parentModel = this._findModel(parent);

      // remove old children
      this.store.removeAll(parentModel);

      this._setCluster(parent);

      // add new children
      for (Cluster currentChild : children) {
        final ClusterTreeDataModel childModel = new ClusterTreeDataModel(currentChild);
        this.store.add(parentModel, childModel, currentChild.getChildSessionID() >= 0);
        this._updateLoadingState(childModel);
      }
      this._setExpandedLoadedModel(parentModel);
    }

  }

  /**
   * Returns this widget as the {@link ClusterTreeView#asWidget()} value.
   */
  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void startProcessing() {
    this.showWaitingBox();
  }

  @Override
  public void stopProcessing() {
    if (this.progressBox != null) {
      this.progressBox.close();
    }
  }

  @Override
  public void addSchema(Cluster newSchema) {
    this._setCluster(newSchema);
  }

  @Override
  public void setSchemaInProgress(DataSource schema) {
    Cluster rootCluster = this._getRootCluster(schema);
    this.setClusterInProgress(rootCluster);
  }

  @Override
  public void setClusterInProgress(Cluster cluster) {
    ClusterTreeDataModel model = this._findModel(cluster);

    if (model != null) {
      model.getCluster().setProgressIdentifier(cluster.getProgressIdentifier());

      this._updateLoadingState(model);
    }
  }

  @Override
  public void updateCluster(Cluster cluster) {
    this._setCluster(cluster);
  }

  @Override
  public void removeSchema(DataSource schema) {
    Cluster rootCluster = this._getRootCluster(schema);
    this._removeRecursively(rootCluster);
  }

  @Override
  public CallbackDisplay getCallbackDisplay(Cluster cluster) {
    if (cluster == null) {
      return null;
    }

    return new CallbackDisplay(cluster);
  }

  // public CallbackDisplay getCallbackDisplay(DataSource schema) {
  // if(schema==null) {
  // return null;
  // }
  //
  // return new CallbackDisplay(schema);
  // }

  private void _setCluster(Cluster cluster) {
    if (cluster == null) {
      return;
    }

    // ClusterTreeDataModel clusterModel = this._findModel(cluster);
    // if (clusterModel != null) {
    // clusterModel = new ClusterTreeDataModel(cluster);
    // }
    ClusterTreeDataModel clusterModel = new ClusterTreeDataModel(cluster);

    final ClusterTreeDataModel parentModel = this.store.getParent(clusterModel);

    this._setCluster(parentModel, clusterModel);
  }

  private void _resetCluster(ClusterTreeDataModel parentModel, ClusterTreeDataModel clusterModel) {
    // add element
    if (parentModel != null) {
      this.store.remove(parentModel, clusterModel);
      this.store.add(parentModel, clusterModel, clusterModel.getCluster().getChildSessionID() >= 0);

      this._setExpandedLoadedModel(parentModel);
    } else if (clusterModel.getCluster().getId() < 0) {
      // add real root clusters
      this.store.remove(clusterModel);
      this.store.add(clusterModel, clusterModel.getCluster().getChildSessionID() >= 0);
    }
  }

  private void _setCluster(ClusterTreeDataModel parentModel, ClusterTreeDataModel clusterModel) {
    if (clusterModel == null || clusterModel.getCluster() == null) {
      return;
    }

    this._resetCluster(parentModel, clusterModel);

    this._updateLoadingState(clusterModel);
  }

  private void _setExpandedLoadedModel(ClusterTreeDataModel model) {
    this.tree.setLoaded(model);
    this.tree.setExpanded(model, true);
  }

  private void _updateLoadingState(ClusterTreeDataModel clusterModel) {
    if (clusterModel != null && clusterModel.getCluster() != null) {
      // set loading state
      if (clusterModel.getCluster().getProgressIdentifier() != null) {
        this._setInProgress(clusterModel);
      } else {
        this._removeInProgress(clusterModel);
      }
    }
  }

  protected Cluster _getRootCluster(DataSource schema) {
    for (ClusterTreeDataModel model : this.store.getRootItems()) {
      if (model != null && model.getCluster() != null
          && schema.equals(model.getCluster().getDataSource())) {
        return model.getCluster();
      }
    }
    return null;
  }

  protected void _removeRecursively(Cluster cluster) {
    ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
    clusterList.add(cluster);
    this._removeRecursively(clusterList);
  }

  protected void _removeRecursively(ArrayList<Cluster> clusters) {
    for (Cluster currentCluster : clusters) {
      ClusterTreeDataModel model = this._findModel(currentCluster);
      if (model != null) {
        if (!model.isLeaf()) {
          this._removeRecursively(this._getChildren(currentCluster));
        }
        this.store.removeAll(model);
        this.store.remove(model);
      }
    }
  }

  // private void _remove(Cluster cluster) {
  // ClusterTreeDataModel model = this._findModel(cluster);
  // if(model!=null) {
  // this.store2.remove(model);
  // }
  // }

  protected void _deselect() {
    // deselect all entries in TreeView
    this.tree.getSelectionModel().deselectAll();
  }

  protected Cluster _getSelected() {
    ClusterTreeDataModel model = this.tree.getSelectionModel().getSelectedItem();
    if (model != null) {
      return model.getCluster();
    }
    return null;
  }

  // private void _setExpanded(Cluster cluster) {
  // ClusterTreeDataModel model = this._findModel(cluster);
  // this.tree2.setExpanded(model, true);
  // }

  protected Cluster _getParent(Cluster child) {
    ClusterTreeDataModel model = this.store.getParent(this._findModel(child));
    if (model != null) {
      return model.getCluster();
    }
    return null;
  }

  protected ArrayList<Cluster> _getChildren(Cluster parent) {
    List<ClusterTreeDataModel> childModels = this.store.getChildren(this._findModel(parent));
    ArrayList<Cluster> children = new ArrayList<Cluster>(childModels.size());

    for (ClusterTreeDataModel currentModel : childModels) {
      if (currentModel != null) {
        children.add(currentModel.getCluster());
      }
    }

    return children;
  }

  protected void _setInProgress(ClusterTreeDataModel model) {
    if (model != null) {
      this.tree.startLoading(model);
    }
  }

  protected void _removeInProgress(ClusterTreeDataModel clusterModel) {
    if (clusterModel != null) {
      ClusterTreeDataModel parentModel = this.store.getParent(clusterModel);
      _resetCluster(parentModel, clusterModel);
      // this.tree.stopLoading(model);
    }
  }

  protected ClusterTreeDataModel _findModel(Cluster cluster) {
    return this.store.findModel(new ClusterTreeDataModel(cluster));
  }

  private class CallbackDisplay implements de.hpi.fgis.ldp.client.service.CallbackDisplay {
    private final Cluster cluster;

    // public CallbackDisplay(DataSource schema) {
    // this(_getRootCluster(schema));
    // }
    public CallbackDisplay(Cluster cluster) {
      this.cluster = cluster;
    }

    @Override
    public void displayError() {
      // TODO error icon?
      // _removeInProgress(_findModel(this.cluster));
    }

    @Override
    public void startProcessing() {
      _setInProgress(_findModel(this.cluster));
    }

    @Override
    public void stopProcessing() {
      // _updateLoadingState(_findModel(this.cluster));
      // _removeInProgress(model);
    }
  }
}
