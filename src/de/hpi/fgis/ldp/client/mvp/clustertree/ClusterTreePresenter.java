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

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hpi.fgis.ldp.client.mvp.dialog.cluster.ClusterInfoPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.dataimport.ImportPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter.CompletionListener;
import de.hpi.fgis.ldp.client.mvp.dialog.subject.SubjectSamplePresenter;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.shared.config.clustering.KMeansConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChildrenReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChildrenReceivedEventHandler;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterInProgressEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterInProgressEventHandler;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterMergeEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterMergeEventHandler;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterRenameEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterRenameEventHandler;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterUpdatedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterUpdatedEventHandler;
import de.hpi.fgis.ldp.shared.event.cluster.SubclusterEvent;
import de.hpi.fgis.ldp.shared.event.cluster.SubclusterEventHandler;
import de.hpi.fgis.ldp.shared.event.schema.SchemaAddedEvent;
import de.hpi.fgis.ldp.shared.event.schema.SchemaAddedEventHandler;
import de.hpi.fgis.ldp.shared.event.schema.SchemaDroppedEvent;
import de.hpi.fgis.ldp.shared.event.schema.SchemaDroppedEventHandler;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterChildrenRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterChildrenRequestResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterMergeRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterMergeResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.RenameClusterRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.RenameClusterResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubclusterRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubclusterResult;
import de.hpi.fgis.ldp.shared.rpc.schema.ChangeUserViewRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.ChangeUserViewResult;
import de.hpi.fgis.ldp.shared.rpc.schema.SchemaDropRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.SchemaDropResult;

@Singleton
public class ClusterTreePresenter extends WidgetPresenter<ClusterTreePresenter.Display> {

  // // inject as setting
  // private @Inject @Named("gui.maxTableRowCount") int entryCount;
  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  protected final SubjectSamplePresenter subjectSamplePresenter;
  protected final ClusterInfoPresenter clusterInfoPresenter;
  protected final ImportPresenter importPresenter;
  protected final ProgressPresenter progressPresenter;

  public interface ClusterRequestSource {
    public void activateCluster(Cluster currentCluster);

    public void getClusterChildren(Cluster parent);

    public void showSubjects(Cluster currentCluster);

    public void renameCluster(Cluster clusterWithNewName);

    public void createSubClusters(Cluster parent, int amount);

    public void mergeClusters(Cluster parent, ArrayList<Cluster> clusters);

    public void showClusterInfo(Cluster cluster);

    public void importDataset();

    public void dropSchema(Cluster rootCluster);

    public void changeUserView(Cluster rootCluster, String userView);

    public void switchUserView(Cluster rootCluster, String userView);
  }

  public interface Display extends WidgetDisplay {
    public void setClusterSource(final ClusterRequestSource source);

    public void addClusters(final Cluster parent, ArrayList<Cluster> children);

    public void addSchema(final Cluster schema);

    public void setClusterInProgress(final Cluster schema);

    public void updateCluster(final Cluster cluster);

    public void setSchemaInProgress(final DataSource schema);

    public void removeSchema(final DataSource schema);

    public CallbackDisplay getCallbackDisplay(final Cluster cluster);
    // public CallbackDisplay getCallbackDisplay(final DataSource schema);
  }

  // /**
  // * The message displayed to the user when the server cannot be reached or
  // * returns an error.
  // */
  // private static final String SERVER_ERROR =
  // "An error occurred while attempting to contact the server for cluster tree information. Please check your network connection and try again.";
  public static final Place PLACE = new Place("ClusterTree");
  private final DispatchAsync dispatcher;

  @Inject
  public ClusterTreePresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher, final SubjectSamplePresenter subjectSamplePresenter,
      final ClusterInfoPresenter clusterInfoPresenter, ImportPresenter importPresenter,
      ProgressPresenter progressPresenter) {
    super(display, eventBus);

    this.dispatcher = dispatcher;

    this.subjectSamplePresenter = subjectSamplePresenter;
    this.clusterInfoPresenter = clusterInfoPresenter;
    this.importPresenter = importPresenter;
    this.progressPresenter = progressPresenter;

    bind();
  }

  @Override
  public Place getPlace() {
    return PLACE;
  }

  @Override
  protected void onBind() {
    // 'display' is a final global field containing the Display passed into
    // the constructor.
    this.display.setClusterSource(new ClusterRequestSource() {
      @Override
      public void getClusterChildren(Cluster parent) {
        onChildRequest(parent);
      }

      @Override
      public void activateCluster(Cluster currentCluster) {
        onClusterSelection(currentCluster);
      }

      @Override
      public void showSubjects(Cluster currentCluster) {
        onSubjectSampleRequest(currentCluster);
      }

      @Override
      public void renameCluster(Cluster clusterWithNewName) {
        onRenameClusterRequest(clusterWithNewName);
      }

      @Override
      public void createSubClusters(Cluster parent, int amount) {
        onSubclusteringRequest(parent, amount);
      }

      @Override
      public void mergeClusters(Cluster parent, ArrayList<Cluster> clusters) {
        onClusterMergeRequest(parent, clusters);
      }

      @Override
      public void showClusterInfo(Cluster cluster) {
        onClusterInfoRequest(cluster);
      }

      @Override
      public void importDataset() {
        onImport();
      }

      @Override
      public void dropSchema(Cluster rootCluster) {
        onDrop(rootCluster);
      }

      @Override
      public void changeUserView(Cluster rootCluster, String userView) {
        onChangeUserView(rootCluster, userView);
      }

      @Override
      public void switchUserView(Cluster rootCluster, String userView) {
        onSwitchUserView(rootCluster, userView);
      }
    });

    eventBus.addHandler(ClusterChildrenReceivedEvent.TYPE,
        new ClusterChildrenReceivedEventHandler() {
          @Override
          public void onChildrenReceived(ClusterChildrenReceivedEvent event) {
            getDisplay().addClusters(event.getParent(), event.getChildren());
          }
        });

    eventBus.addHandler(ClusterRenameEvent.TYPE, new ClusterRenameEventHandler() {
      @Override
      public void onRenameReceived(ClusterRenameEvent event) {
        getDisplay().updateCluster(event.getCluster());
      }
    });

    eventBus.addHandler(SubclusterEvent.TYPE, new SubclusterEventHandler() {
      @Override
      public void onSubclusterReceived(SubclusterEvent event) {
        getDisplay().addClusters(event.getParent(), event.getChildren());
      }
    });

    eventBus.addHandler(ClusterMergeEvent.TYPE, new ClusterMergeEventHandler() {
      @Override
      public void onClusterMergeReceived(ClusterMergeEvent event) {
        getDisplay().addClusters(event.getParent(), event.getChildren());
      }
    });

    eventBus.addHandler(ClusterUpdatedEvent.TYPE, new ClusterUpdatedEventHandler() {
      @Override
      public void onClusterUpdated(ClusterUpdatedEvent event) {
        onChildRequest(event.getCluster());
      }
    });

    eventBus.addHandler(SchemaAddedEvent.TYPE, new SchemaAddedEventHandler() {
      @Override
      public void onSchemaImport(SchemaAddedEvent event) {
        getDisplay().addSchema(event.getCluster());
      }
    });

    eventBus.addHandler(ClusterInProgressEvent.TYPE, new ClusterInProgressEventHandler() {
      @Override
      public void onClusterInProgress(ClusterInProgressEvent event) {
        getDisplay().setClusterInProgress(event.getCluster());
      }
    });

    eventBus.addHandler(SchemaDroppedEvent.TYPE, new SchemaDroppedEventHandler() {
      @Override
      public void onSchemaDrop(SchemaDroppedEvent event) {
        getDisplay().removeSchema(event.getDataSource());
      }
    });
  }

  @Override
  protected void onPlaceRequest(PlaceRequest request) {
    // Grab the 'name' from the request and put it into the 'name' field.
    // This allows a tag of '#Greeting;name=Foo' to populate the name
    // field.

    // TODO open the tree structure
    // final String clusterID = request.getParameter("clusterID", null);

    // if (clusterID != null) {
    // display.getData().getValue().collapseAll();
    // display.getData().getValue().getSelectionModel().select(TODO, false);
    // }
  }

  @Override
  protected void onUnbind() {
    // Add unbind functionality here for more complex presenters.
  }

  @Override
  public void refreshDisplay() {
    // TODO
    // This is called when the presenter should pull the latest data
    // from the server, etc. In this case, there is nothing to do.
  }

  @Override
  public void revealDisplay() {
    // Nothing to do. This is more useful in UI which may be buried
    // in a tab bar, tree, etc.
  }

  protected void onChangeUserView(final Cluster oldRootCluster, String newView) {
    dispatcher.execute(new ChangeUserViewRequest(oldRootCluster.getDataSource(), newView),
        callbackBuilder.build(getDisplay().getCallbackDisplay(oldRootCluster),
            new AsyncDisplayCallback.Handler<ChangeUserViewResult>() {
              @Override
              protected boolean handleSuccess(ChangeUserViewResult result) {
                // take the result from the
                // server and notify client
                // interested components
                ClusterChildrenReceivedEvent event =
                    new ClusterChildrenReceivedEvent(result.getRootCluster(), result
                        .getTopLevelClusters());
                event.getParent().setId(oldRootCluster.getId());
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }

  protected void onSwitchUserView(final Cluster oldRootCluster, final String newView) {
    dispatcher.execute(new ChangeUserViewRequest(oldRootCluster.getDataSource(), newView, false),
        callbackBuilder.build(getDisplay().getCallbackDisplay(oldRootCluster),
            new AsyncDisplayCallback.Handler<ChangeUserViewResult>() {
              @Override
              protected boolean handleFailure(Throwable t) {
                Info.display("Switching hierarchy view", "Unable to switch hierarchy view to "
                    + newView + "!");
                return false;
              }

              @Override
              protected boolean handleSuccess(ChangeUserViewResult result) {
                // take the result from the
                // server and notify client
                // interested components
                ClusterChildrenReceivedEvent event =
                    new ClusterChildrenReceivedEvent(result.getRootCluster(), result
                        .getTopLevelClusters());
                event.getParent().setId(oldRootCluster.getId());
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }

  protected void onDrop(final Cluster rootCluster) {
    dispatcher.execute(new SchemaDropRequest(rootCluster.getDataSource()), callbackBuilder.build(
        getDisplay().getCallbackDisplay(rootCluster),
        new AsyncDisplayCallback.Handler<SchemaDropResult>() {
          @Override
          protected boolean handleSuccess(SchemaDropResult result) {
            // take the result from the server and notify
            // client interested components
            SchemaDroppedEvent event = new SchemaDroppedEvent(result.getParent());
            getEventBus().fireEvent(event);

            return true;
          }
        }));
  }

  protected void onImport() {
    this.importPresenter.getDisplay().show();
    // this.importPresenter.getDisplay().processing();
    this.importPresenter.getDisplay().startNameInput();
  }

  protected void onClusterMergeRequest(final Cluster parent, ArrayList<Cluster> clusters) {
    dispatcher.execute(new ClusterMergeRequest(parent, clusters), callbackBuilder.build(
        getDisplay().getCallbackDisplay(parent),
        new AsyncDisplayCallback.Handler<ClusterMergeResult>() {
          @Override
          protected boolean handleSuccess(ClusterMergeResult result) {
            // take the result from the server and notify
            // client interested components
            ClusterMergeEvent event =
                new ClusterMergeEvent(result.getParent(), result.getChildren());
            getEventBus().fireEvent(event);

            return true;
          }
        }));
  }

  protected void onSubclusteringRequest(final Cluster parent, final int numOfClusters) {
    KMeansConfig config = new KMeansConfig();
    config.setNumberOfClusters(numOfClusters);

    dispatcher.execute(new SubclusterRequest(parent, config), callbackBuilder.build(getDisplay()
        .getCallbackDisplay(parent), new AsyncDisplayCallback.Handler<SubclusterResult>() {
      @Override
      protected boolean handleSuccess(SubclusterResult result) {
        final Cluster updatedSchema = result.getParent();

        // take the long running process id from server
        // and start progress visualization
        Long identifier = updatedSchema.getProgressIdentifier();

        // publish progress
        getEventBus().fireEvent(new ClusterInProgressEvent(updatedSchema));

        // progressPresenter.showProgress(new
        // ProgressRequest.Cluster(identifier.longValue()),
        // Cluster.class, new
        // CompletionListener<Cluster>() {
        progressPresenter.showProgress(identifier.longValue(), Cluster.class,
            new CompletionListener<Cluster>() {
              @Override
              public boolean onCompletion(boolean success, Cluster result) {
                if (success) {
                  if (result != null) {
                    onChildRequest(result);
                    getEventBus().fireEvent(new ClusterUpdatedEvent(result));
                  } else {
                    // try to reload the
                    // expected result
                    Log.error("getting update for " + updatedSchema);
                    onChildRequest(updatedSchema);
                  }
                  return true;
                }
                return false;
              }
            }, false);

        return true;
      }
    }));
  }

  protected void onRenameClusterRequest(final Cluster cluster) {
    dispatcher.execute(new RenameClusterRequest(cluster), callbackBuilder.build(getDisplay()
        .getCallbackDisplay(cluster), new AsyncDisplayCallback.Handler<RenameClusterResult>() {
      @Override
      protected boolean handleSuccess(RenameClusterResult result) {
        // take the result from the
        // server and notify client
        // interested components
        ClusterRenameEvent event = new ClusterRenameEvent(result.getCluster());
        getEventBus().fireEvent(event);

        return true;
      }
    }));
  }

  protected void onClusterInfoRequest(final Cluster cluster) {
    clusterInfoPresenter.requestClusterInfo(cluster);
  }

  protected void onSubjectSampleRequest(final Cluster cluster) {
    subjectSamplePresenter.requestSampleSubjects(cluster, 0);
  }

  protected void onChildRequest(final Cluster parent) {
    dispatcher.execute(new ClusterChildrenRequest(parent), callbackBuilder.build(getDisplay()
        .getCallbackDisplay(parent),
        new AsyncDisplayCallback.Handler<ClusterChildrenRequestResult>() {
          @Override
          protected boolean handleSuccess(ClusterChildrenRequestResult result) {
            // take the result from the
            // server and notify client
            // interested components
            getEventBus().fireEvent(new ClusterChildrenReceivedEvent(parent, result.getClusters()));

            return true;
          }
        }));
  }

  protected void onClusterSelection(final Cluster currentCluster) {
    this.getEventBus().fireEvent(new ClusterChangedEvent(currentCluster));
  }

  protected EventBus getEventBus() {
    return this.eventBus;
  }
}
