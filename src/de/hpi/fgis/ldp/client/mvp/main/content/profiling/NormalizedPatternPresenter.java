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

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
import de.hpi.fgis.ldp.shared.data.NormalizedPattern;
import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEventHandler;
import de.hpi.fgis.ldp.shared.event.profiling.NormalizedPatternStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.ObjectStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.NormalizedPatternStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.ObjectStatisticsRequest;

@Singleton
public class NormalizedPatternPresenter extends
    AbstractMainContentPresenter<NormalizedPatternPresenter.Display> {
  protected Cluster currentCluster = null;
  // inject as setting
  protected @Inject @Named("gui.maxTableRowCount") int entryCount;

  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;
  protected PatternPresenter patternPresenter;

  public NormalizedPatternPresenter init(PatternPresenter patternPresenter) {
    this.patternPresenter = patternPresenter;
    return this;
  }

  public interface Display extends MainContentView {
    public void setDataTable(DataTablePanel table);

    public void setDataChart(AbstractDataTableChartPanel chart);

    public CallbackDisplay getNormalizedPatternDisplay();
  }

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */
  @Inject
  public NormalizedPatternPresenter(final Display display, final EventBus eventBus,
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
        synchronized (NormalizedPatternPresenter.this) {
          NormalizedPatternPresenter.this.currentCluster = cluster;
        }
      }
    });
    eventBus.addHandler(NormalizedPatternStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<NormalizedPatternStatisticsReceivedEvent>() {

          @Override
          public void onDataTableReceived(NormalizedPatternStatisticsReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (NormalizedPatternPresenter.this) {
              if (cluster == null
                  || !cluster.equals(NormalizedPatternPresenter.this.currentCluster)) {
                return;
              }
            }
            final ArrayList<Predicate> predicates = event.getPredicates();
            final Datatype datatype = event.getDatatype();
            final int start = event.getOffset();

            // create data table
            final IDataTable table = event.getDataTable();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setSelectionListenter(new SingleSelectionListener() {
              @Override
              public void onSelection(int selectedRow) {
                final NormalizedPattern normPattern =
                    (NormalizedPattern) table.getColumn(0).getElement(selectedRow);

                NormalizedPatternPresenter.this.requestPattern(cluster, predicates, datatype,
                    normPattern);
              }
            });

            if (start > 0) {
              tablePanel.setToolButton("x-tool-left", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  NormalizedPatternPresenter.this.requestNormalizedPattern(cluster, predicates,
                      datatype, start - NormalizedPatternPresenter.this.entryCount);
                }
              });
            }

            if (table.getRowCount() >= NormalizedPatternPresenter.this.entryCount) {
              tablePanel.setToolButton("x-tool-right", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  NormalizedPatternPresenter.this.requestNormalizedPattern(cluster, predicates,
                      datatype, start + NormalizedPatternPresenter.this.entryCount);
                }
              });
            }

            // TODO switching buttons

            tablePanel.setData(table);

            tablePanel.setTableTitle("Normalized Pattern");

            // set table
            NormalizedPatternPresenter.this.getDisplay().setDataTable(tablePanel);

            if (start <= 0) {
              // create data chart
              GXTDataTableChartPanel chart =
                  new GXTDataTableChartPanel(ChartType.HBar, event.getDataTable(), 0, 1, 2);

              chart.setChartTitle("Normalized Pattern");

              // set chart
              NormalizedPatternPresenter.this.getDisplay().setDataChart(chart);
            }
          }
        });
  }

  protected void requestObjects(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, final Pattern pattern) {

    this.requestActivation();
    getDispatcher()
        .execute(
            new ObjectStatisticsRequest(cluster, predicates, datatype, pattern, 0,
                this.entryCount - 1),
            callbackBuilder.build(getDisplay(),
                new AsyncDisplayCallback.Handler<DataTableResult>() {
                  @Override
                  protected boolean handleSuccess(DataTableResult result) {
                    final ObjectStatisticsReceivedEvent event = new ObjectStatisticsReceivedEvent();
                    event.setCluster(cluster);
                    event.setPredicates(predicates);
                    event.setDatatype(datatype);
                    event.setPattern(pattern);
                    event.setDataTable(result.getDataTable());
                    event.setOffset(0);
                    getEventBus().fireEvent(event);

                    return true;
                  }
                }));
  }

  protected void requestPattern(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, final NormalizedPattern normalizedPattern) {
    this.patternPresenter.requestPattern(cluster, predicates, datatype, normalizedPattern, 0);
  }

  protected void requestNormalizedPattern(final Cluster cluster,
      final ArrayList<Predicate> predicates, final Datatype datatype, final int start) {
    this.requestActivation();
    getDispatcher().execute(
        new NormalizedPatternStatisticsRequest(cluster, predicates, datatype, start, start
            + this.entryCount - 1),
        callbackBuilder.build(getDisplay().getNormalizedPatternDisplay(),
            new AsyncDisplayCallback.Handler<DataTableResult>() {
              @Override
              protected boolean handleSuccess(DataTableResult result) {
                final NormalizedPatternStatisticsReceivedEvent event =
                    new NormalizedPatternStatisticsReceivedEvent();
                event.setCluster(cluster);
                event.setPredicates(predicates);
                event.setDatatype(datatype);
                event.setDataTable(result.getDataTable());
                event.setOffset(start);
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }
}
