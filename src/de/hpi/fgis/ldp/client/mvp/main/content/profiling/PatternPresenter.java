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
import de.hpi.fgis.ldp.shared.event.profiling.PatternStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.PatternStatisticsRequest;

@Singleton
public class PatternPresenter extends AbstractMainContentPresenter<PatternPresenter.Display> {
  protected Cluster currentCluster = null;
  // inject as setting
  protected @Inject @Named("gui.maxTableRowCount") int entryCount;

  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  protected ObjectPresenter objectPresenter;

  public PatternPresenter init(ObjectPresenter objectPresenter) {
    this.objectPresenter = objectPresenter;
    return this;
  }

  public interface Display extends MainContentView {
    public void setDataTable(DataTablePanel table);

    public void setDataChart(AbstractDataTableChartPanel chart);

    public CallbackDisplay getPatternDisplay();
  }

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */
  @Inject
  public PatternPresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus, dispatcher);
  }

  // @Override
  // public Place getPlace() {
  // return PLACE;
  // }

  @Override
  protected void onBind() {
    eventBus.addHandler(ClusterChangedEvent.TYPE, new ClusterChangedEventHandler() {
      @Override
      public void onClusterChanged(ClusterChangedEvent event) {
        final Cluster cluster = event.getCluster();
        synchronized (PatternPresenter.this) {
          PatternPresenter.this.currentCluster = cluster;
        }
      }
    });
    eventBus.addHandler(PatternStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<PatternStatisticsReceivedEvent>() {

          @Override
          public void onDataTableReceived(PatternStatisticsReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (PatternPresenter.this) {
              if (cluster == null || !cluster.equals(PatternPresenter.this.currentCluster)) {
                return;
              }
            }
            final ArrayList<Predicate> predicates = event.getPredicates();
            final Datatype datatype = event.getDatatype();
            final NormalizedPattern normPattern = event.getNormalizedPattern();
            final int start = event.getOffset();

            // create data table
            final IDataTable table = event.getDataTable();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setSelectionListenter(new SingleSelectionListener() {
              @Override
              public void onSelection(int selectedRow) {
                final Pattern pattern = (Pattern) table.getColumn(0).getElement(selectedRow);

                PatternPresenter.this.requestObjects(cluster, predicates, datatype, pattern);
              }
            });

            if (start > 0) {
              tablePanel.setToolButton("x-tool-left", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  PatternPresenter.this.requestPattern(cluster, predicates, datatype, normPattern,
                      start - PatternPresenter.this.entryCount);
                }
              });
            }

            if (table.getRowCount() >= PatternPresenter.this.entryCount) {
              tablePanel.setToolButton("x-tool-right", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  PatternPresenter.this.requestPattern(cluster, predicates, datatype, normPattern,
                      start + PatternPresenter.this.entryCount);
                }
              });
            }

            tablePanel.setData(table);

            tablePanel.setTableTitle("Pattern");

            // set table
            PatternPresenter.this.getDisplay().setDataTable(tablePanel);

            if (start <= 0) {
              // create data chart
              GXTDataTableChartPanel chart =
                  new GXTDataTableChartPanel(ChartType.HBar, event.getDataTable(), 0, 1, 2);

              chart.setChartTitle("Pattern");

              // set chart
              PatternPresenter.this.getDisplay().setDataChart(chart);
            }
          }
        });
  }

  protected void requestObjects(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, final Pattern pattern) {
    this.objectPresenter.requestObjects(cluster, predicates, datatype, pattern, 0);
  }

  protected void requestPattern(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, final NormalizedPattern normalizedPattern, final int start) {
    this.requestActivation();
    getDispatcher().execute(
        new PatternStatisticsRequest(cluster, predicates, datatype, normalizedPattern, start, start
            + this.entryCount - 1),
        callbackBuilder.build(getDisplay().getPatternDisplay(),
            new AsyncDisplayCallback.Handler<DataTableResult>() {
              @Override
              protected boolean handleSuccess(DataTableResult result) {
                final PatternStatisticsReceivedEvent event = new PatternStatisticsReceivedEvent();
                event.setCluster(cluster);
                event.setPredicates(predicates);
                event.setDatatype(datatype);
                event.setNormalizedPattern(normalizedPattern);
                event.setDataTable(result.getDataTable());
                event.setOffset(start);
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }

}
