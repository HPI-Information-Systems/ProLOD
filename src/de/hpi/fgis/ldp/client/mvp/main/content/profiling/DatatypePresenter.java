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

package de.hpi.fgis.ldp.client.mvp.main.content.profiling;

import java.util.ArrayList;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.client.mvp.main.content.AbstractMainContentPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.MainContentView;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.client.view.datatable.AbstractDataTableChartPanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel.SingleSelectionListener;
import de.hpi.fgis.ldp.client.view.datatable.GXTDataTableChartPanel;
import de.hpi.fgis.ldp.client.view.datatable.GXTDataTableChartPanel.ChartType;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEventHandler;
import de.hpi.fgis.ldp.shared.event.profiling.DataTypeDistributionReceivedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.PredicateLinkLiteralStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.DataTypeDistributionRequest;

@Singleton
public class DatatypePresenter extends AbstractMainContentPresenter<DatatypePresenter.Display> {
  protected Cluster currentCluster = null;
  // inject as settings
  protected @Inject @Named("gui.maxTableRowCount") int entryCount;

  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  protected PatternPresenter patternPresenter;
  protected NormalizedPatternPresenter normPatternPresenter;
  protected ObjectPresenter objectPresenter;

  public DatatypePresenter init(NormalizedPatternPresenter normPatternPresenter,
      PatternPresenter patternPresenter, ObjectPresenter objectPresenter) {
    this.normPatternPresenter = normPatternPresenter;
    this.patternPresenter = patternPresenter;
    this.objectPresenter = objectPresenter;
    return this;
  }

  public interface Display extends MainContentView {
    public void setDataTable(DataTablePanel table);

    public void setDatatypeChart(AbstractDataTableChartPanel chart);

    public void setLinkLiteralChart(AbstractDataTableChartPanel chart);

    public CallbackDisplay getDatatypeDisplay();

    public CallbackDisplay getLinkLiteralDisplay();
  }

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */

  @Inject
  public DatatypePresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus, dispatcher);
  }

  @Override
  public Place getPlace() {
    return PLACE;
  }

  @Override
  protected void onBind() {
    eventBus.addHandler(ClusterChangedEvent.TYPE, new ClusterChangedEventHandler() {
      @Override
      public void onClusterChanged(ClusterChangedEvent event) {
        final Cluster cluster = event.getCluster();
        synchronized (DatatypePresenter.this) {
          DatatypePresenter.this.currentCluster = cluster;
        }
      }
    });
    eventBus.addHandler(DataTypeDistributionReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<DataTypeDistributionReceivedEvent>() {

          @Override
          public void onDataTableReceived(DataTypeDistributionReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (DatatypePresenter.this) {
              if (cluster == null || !cluster.equals(DatatypePresenter.this.currentCluster)) {
                return;
              }
            }
            final ArrayList<Predicate> predicates = event.getPredicates();

            // create data table
            final IDataTable table = event.getDataTable();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setSelectionListenter(new SingleSelectionListener() {
              @Override
              public void onSelection(int selectedRow) {
                final Datatype datatype = (Datatype) table.getColumn(0).getElement(selectedRow);

                if (Datatype.STRING.equals(datatype)) {
                  DatatypePresenter.this.requestNormalizedPattern(cluster, predicates, datatype);
                } else if (Datatype.TEXT.equals(datatype)) {
                  DatatypePresenter.this.requestObjects(cluster, predicates, datatype);
                } else {
                  DatatypePresenter.this.requestPattern(cluster, predicates, datatype);
                }
              }
            });

            tablePanel.setData(table);

            tablePanel.setTableTitle("Data Type Distribution");

            // set table
            DatatypePresenter.this.getDisplay().setDataTable(tablePanel);

            // create data chart
            GXTDataTableChartPanel chart =
                new GXTDataTableChartPanel(ChartType.Pie, event.getDataTable(), 0, 1, 2);

            chart.setChartTitle("Data Type Distribution");

            // set chart
            DatatypePresenter.this.getDisplay().setDatatypeChart(chart);
          }
        });

    eventBus.addHandler(PredicateLinkLiteralStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<PredicateLinkLiteralStatisticsReceivedEvent>() {
          @Override
          public void onDataTableReceived(PredicateLinkLiteralStatisticsReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (DatatypePresenter.this) {
              if (cluster == null || !cluster.equals(DatatypePresenter.this.currentCluster)) {
                return;
              }
            }
            // create data chart
            GXTDataTableChartPanel chart =
                new GXTDataTableChartPanel(ChartType.Pie, event.getDataTable(), 0, 1);

            chart.setChartTitle("Link Literal Ratio");

            // set chart
            DatatypePresenter.this.getDisplay().setLinkLiteralChart(chart);
          }
        });
  }

  protected void requestDatatypes(final Cluster cluster, final ArrayList<Predicate> predicates) {
    this.requestActivation();
    // requesting data type distribution
    getDispatcher().execute(new DataTypeDistributionRequest(cluster, predicates),
        callbackBuilder.build(getDisplay(), new AsyncDisplayCallback.Handler<DataTableResult>() {
          @Override
          protected boolean handleSuccess(DataTableResult value) {
            final DataTypeDistributionReceivedEvent event = new DataTypeDistributionReceivedEvent();
            event.setCluster(cluster);
            event.setPredicates(predicates);
            event.setDataTable(value.getDataTable());
            getEventBus().fireEvent(event);

            return true;
          }
        }));
  }

  protected void requestNormalizedPattern(final Cluster cluster,
      final ArrayList<Predicate> predicates, final Datatype datatype) {
    this.normPatternPresenter.requestNormalizedPattern(cluster, predicates, datatype, 0);
  }

  protected void requestPattern(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype) {
    this.patternPresenter.requestPattern(cluster, predicates, datatype, null, 0);
  }

  protected void requestObjects(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype) {
    this.objectPresenter.requestObjects(cluster, predicates, datatype, null, 0);
  }
}
