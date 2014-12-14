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

package de.hpi.fgis.ldp.client.mvp.main;

import java.util.Arrays;
import java.util.List;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter.CompletionListener;
import de.hpi.fgis.ldp.client.mvp.main.content.AbstractMainContentPresenter.ActivationRequestListener;
import de.hpi.fgis.ldp.client.mvp.main.content.MainContentView;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.DatatypePresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.GeneralPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.NormalizedPatternPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.ObjectPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.PatternPresenter;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataElement;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEventHandler;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterUpdatedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.DataTypeDistributionReceivedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.NormalizedPatternStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.ObjectStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.PatternStatisticsReceivedEvent;

@Singleton
public class MainWidgetManagerPresenter extends WidgetPresenter<MainWidgetManagerPresenter.Display> {
  protected final GeneralPresenter predicatePresenter;
  protected final DatatypePresenter datatypePresenter;
  protected final NormalizedPatternPresenter normPatternPresenter;
  protected final PatternPresenter patternPresenter;
  protected final ObjectPresenter objectPresenter;
  protected final ProgressPresenter progressPresenter;
  protected NavigationPoint[] navigationPoints = new NavigationPoint[0];

  public abstract class NavigationPoint {
    private final String title;
    private final String category;
    private final String[] names;
    private final MainContentView content;

    @Override
    public String toString() {
      return "NavigationPoint [category=" + category + ", content=" + content + ", names="
          + Arrays.toString(names) + "]";
    }

    // TODO remove constructor?
    public NavigationPoint(String title, String category, MainContentView content, String name) {
      this.title = title;
      this.category = category;
      this.content = content;
      this.names = new String[] {name};
    }

    public NavigationPoint(String title, String category, MainContentView content,
        DataElement... elements) {
      this(title, category, content, Arrays.asList(elements));
    }

    public NavigationPoint(String title, String category, MainContentView content,
        List<? extends DataElement> elements) {
      this.title = title;
      this.category = category;
      this.content = content;
      this.names = new String[elements.size()];
      for (int i = 0; i < elements.size(); i++) {
        if (elements.get(i) != null) {
          this.names[i] = elements.get(i).getLabel();
        }
      }
    }

    public String getTitle() {
      return this.title;
    }

    public String getCategory() {
      return this.category;
    }

    public String[] getNames() {
      return this.names;
    }

    public Widget getWidget() {
      return content.asWidget();
    }

    public abstract void activate();
  }

  @Inject
  public MainWidgetManagerPresenter(Display display, EventBus eventBus,
      GeneralPresenter predicatePresenter, DatatypePresenter datatypePresenter,
      NormalizedPatternPresenter normPatternPresenter, PatternPresenter patternPresenter,
      ObjectPresenter objectPresenter, ProgressPresenter progressPresenter) {
    super(display, eventBus);

    this.predicatePresenter = predicatePresenter.init(progressPresenter, datatypePresenter);
    this.datatypePresenter =
        datatypePresenter.init(normPatternPresenter, patternPresenter, objectPresenter);
    this.normPatternPresenter = normPatternPresenter.init(patternPresenter);
    this.patternPresenter = patternPresenter.init(objectPresenter);
    this.objectPresenter = objectPresenter;
    this.progressPresenter = progressPresenter;

    this.bind();
  }

  public static final Place PLACE = new Place("MainWidgetManager");

  public interface Display extends CallbackDisplay, WidgetDisplay {
    // public void setContent(final MainContentView view);
    public NavigationPoint[] setNavigationPoints(NavigationPoint... navPoints);

    public boolean selectNavigationPoint(String cathegory);

    public void setDefaultView();
  }

  @Override
  public Place getPlace() {
    return PLACE;
  }

  protected EventBus getEventBus() {
    return eventBus;
  }

  @Override
  protected void onBind() {
    final String predicateCategory = "Predicates";
    final String datatypeCategory = "Datatypes";
    final String normPatternCategory = "Normalized Pattern";
    final String patternCategory = "Pattern";
    final String objectCategory = "Objects";
    final String processing = "Processing...";

    this.predicatePresenter.addActivationRequestListener(new ActivationRequestListener() {
      @Override
      public void onActivationRequest() {
        if (!getDisplay().selectNavigationPoint(predicateCategory)) {
          navigationPoints =
              getDisplay().setNavigationPoints(
                  new NavigationPoint(processing, predicateCategory, predicatePresenter
                      .getDisplay(), processing) {
                    @Override
                    public void activate() {}
                  });
        }
      }
    });

    this.datatypePresenter.addActivationRequestListener(new ActivationRequestListener() {
      @Override
      public void onActivationRequest() {
        if (!getDisplay().selectNavigationPoint(datatypeCategory)) {
          navigationPoints =
              getDisplay().setNavigationPoints(
                  navigationPoints[0],
                  new NavigationPoint(processing, datatypeCategory, datatypePresenter.getDisplay(),
                      processing) {
                    @Override
                    public void activate() {}
                  });
        }
      }
    });
    this.normPatternPresenter.addActivationRequestListener(new ActivationRequestListener() {
      @Override
      public void onActivationRequest() {
        if (!getDisplay().selectNavigationPoint(normPatternCategory)) {
          navigationPoints =
              getDisplay().setNavigationPoints(
                  navigationPoints[0],
                  navigationPoints[1],
                  new NavigationPoint(processing, normPatternCategory, normPatternPresenter
                      .getDisplay(), processing) {
                    @Override
                    public void activate() {}
                  });
        }
      }
    });
    this.patternPresenter.addActivationRequestListener(new ActivationRequestListener() {
      @Override
      public void onActivationRequest() {
        if (!getDisplay().selectNavigationPoint(patternCategory)) {
          navigationPoints =
              getDisplay().setNavigationPoints(
                  navigationPoints[0],
                  navigationPoints[1],
                  navigationPoints.length > 2 ? navigationPoints[2] : null,
                  new NavigationPoint(processing, patternCategory, patternPresenter.getDisplay(),
                      processing) {
                    @Override
                    public void activate() {}
                  });
        }
      }
    });
    this.objectPresenter.addActivationRequestListener(new ActivationRequestListener() {
      @Override
      public void onActivationRequest() {
        if (!getDisplay().selectNavigationPoint(objectCategory)) {
          navigationPoints =
              getDisplay().setNavigationPoints(
                  navigationPoints[0],
                  navigationPoints[1],
                  navigationPoints.length > 2 ? navigationPoints[2] : null,
                  navigationPoints.length > 3 ? navigationPoints[3] : null,
                  new NavigationPoint(processing, objectCategory, objectPresenter.getDisplay(),
                      processing) {
                    @Override
                    public void activate() {}
                  });
        }
      }
    });

    eventBus.addHandler(ClusterChangedEvent.TYPE, new ClusterChangedEventHandler() {
      @Override
      public void onClusterChanged(final ClusterChangedEvent event) {
        final Cluster cluster = event.getCluster();
        // progress cluster
        if (cluster.isInProgress()) {
          getDisplay().setDefaultView();
          // progressPresenter.showProgress(new
          // ProgressRequest.Cluster(cluster.getProgressIdentifier().longValue()),
          // Cluster.class, new CompletionListener<Cluster>()
          // {
          progressPresenter.showProgress(cluster.getProgressIdentifier().longValue(),
              Cluster.class, new CompletionListener<Cluster>() {
                @Override
                public boolean onCompletion(boolean success, Cluster result) {
                  if (success) {
                    if (result != null) {
                      getEventBus().fireEvent(new ClusterUpdatedEvent(result));
                    }
                    return true;
                  }
                  return false;
                }
              }, false);
        } else {
          navigationPoints =
              getDisplay().setNavigationPoints(
                  new NavigationPoint("Predicates in Cluster \"" + cluster.getLabel() + "\"",
                      predicateCategory, predicatePresenter.getDisplay(), cluster.getLabel()) {
                    @Override
                    public void activate() {
                      getEventBus().fireEvent(event);
                    }
                  });
        }
      }
    });
    eventBus.addHandler(DataTypeDistributionReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<DataTypeDistributionReceivedEvent>() {
          @Override
          public void onDataTableReceived(final DataTypeDistributionReceivedEvent event) {
            String title = "Datatypes for " + event.getPredicates().size() + " Predicate(s)";
            if (event.getPredicates().size() == 1) {
              title = "Datatypes for Predicate \"" + event.getPredicates().get(0).getLabel() + "\"";
            }
            navigationPoints =
                getDisplay().setNavigationPoints(
                    navigationPoints[0],
                    new NavigationPoint(title, datatypeCategory, datatypePresenter.getDisplay(),
                        event.getPredicates()) {
                      @Override
                      public void activate() {
                        getEventBus().fireEvent(event);
                      }
                    });
          }
        });
    eventBus.addHandler(NormalizedPatternStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<NormalizedPatternStatisticsReceivedEvent>() {
          @Override
          public void onDataTableReceived(final NormalizedPatternStatisticsReceivedEvent event) {
            navigationPoints =
                getDisplay().setNavigationPoints(
                    navigationPoints[0],
                    navigationPoints[1],
                    new NavigationPoint("Normalized Pattern for data type \""
                        + event.getDatatype().getLabel() + "\"", normPatternCategory,
                        normPatternPresenter.getDisplay(), event.getDatatype()) {
                      @Override
                      public void activate() {
                        getEventBus().fireEvent(event);
                      }
                    });
          }
        });
    eventBus.addHandler(PatternStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<PatternStatisticsReceivedEvent>() {
          @Override
          public void onDataTableReceived(final PatternStatisticsReceivedEvent event) {
            if (navigationPoints.length > 2 && event.getNormalizedPattern() != null) {
              navigationPoints =
                  getDisplay().setNavigationPoints(
                      navigationPoints[0],
                      navigationPoints[1],
                      navigationPoints[2],
                      new NavigationPoint("Pattern like \""
                          + event.getNormalizedPattern().getLabel() + "\"", patternCategory,
                          patternPresenter.getDisplay(), event.getNormalizedPattern()) {
                        @Override
                        public void activate() {
                          getEventBus().fireEvent(event);
                        }
                      });
            } else {
              navigationPoints =
                  getDisplay().setNavigationPoints(
                      navigationPoints[0],
                      navigationPoints[1],
                      new NavigationPoint("Pattern for type \"" + event.getDatatype().getLabel()
                          + "\"", patternCategory, patternPresenter.getDisplay(), event
                          .getDatatype()) {
                        @Override
                        public void activate() {
                          getEventBus().fireEvent(event);
                        }
                      });
            }
          }
        });
    eventBus.addHandler(ObjectStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<ObjectStatisticsReceivedEvent>() {
          @Override
          public void onDataTableReceived(final ObjectStatisticsReceivedEvent event) {
            navigationPoints =
                getDisplay().setNavigationPoints(
                    navigationPoints[0],
                    navigationPoints[1],
                    navigationPoints.length > 2 ? navigationPoints[2] : null,
                    navigationPoints.length > 3 ? navigationPoints[3] : null,
                    new NavigationPoint("Objects for pattern \""
                        + ((event.getPattern() == null) ? "null" : event.getPattern().getLabel())
                        + "\"", objectCategory, objectPresenter.getDisplay(), event.getPattern()) {
                      @Override
                      public void activate() {
                        getEventBus().fireEvent(event);
                      }
                    });
          }
        });
  }

  @Override
  protected void onPlaceRequest(PlaceRequest request) {
    // nothing to do
  }

  @Override
  protected void onUnbind() {
    // nothing to do
  }

  @Override
  public void refreshDisplay() {
    // nothing to do
  }

  @Override
  public void revealDisplay() {
    // nothing to do
  }
}
