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
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEventHandler;
import de.hpi.fgis.ldp.shared.event.profiling.ObjectStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.ObjectStatisticsRequest;

@Singleton
public class ObjectPresenter extends AbstractMainContentPresenter<ObjectPresenter.Display> {
  protected Cluster currentCluster = null;
  // inject as setting
  protected @Inject @Named("gui.maxTableRowCount") int entryCount;

  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  public interface Display extends MainContentView {
    public void setDataTable(DataTablePanel table);

    public CallbackDisplay getObjectDisplay();
  }

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */
  @Inject
  public ObjectPresenter(final Display display, final EventBus eventBus,
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
        synchronized (ObjectPresenter.this) {
          ObjectPresenter.this.currentCluster = cluster;
        }
      }
    });
    eventBus.addHandler(ObjectStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<ObjectStatisticsReceivedEvent>() {

          @Override
          public void onDataTableReceived(ObjectStatisticsReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (ObjectPresenter.this) {
              if (cluster == null || !cluster.equals(ObjectPresenter.this.currentCluster)) {
                return;
              }
            }
            final ArrayList<Predicate> predicates = event.getPredicates();
            final Datatype datatype = event.getDatatype();
            final Pattern pattern = event.getPattern();
            final int start = event.getOffset();

            // create data table
            final IDataTable table = event.getDataTable();
            final DataTablePanel tablePanel = new DataTablePanel();

            if (start > 0) {
              tablePanel.setToolButton("x-tool-left", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  ObjectPresenter.this.requestObjects(cluster, predicates, datatype, pattern, start
                      - ObjectPresenter.this.entryCount);
                }
              });
            }

            if (table.getRowCount() >= ObjectPresenter.this.entryCount) {
              tablePanel.setToolButton("x-tool-right", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  ObjectPresenter.this.requestObjects(cluster, predicates, datatype, pattern, start
                      + ObjectPresenter.this.entryCount);
                }
              });
            }

            tablePanel.setData(table);

            tablePanel.setTableTitle("Objects");

            // set table
            ObjectPresenter.this.getDisplay().setDataTable(tablePanel);
          }
        });
  }

  protected void requestObjects(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, final Pattern pattern, final int start) {
    this.requestActivation();
    getDispatcher().execute(
        new ObjectStatisticsRequest(cluster, predicates, datatype, pattern, start, start
            + this.entryCount - 1),
        callbackBuilder.build(getDisplay().getObjectDisplay(),
            new AsyncDisplayCallback.Handler<DataTableResult>() {
              @Override
              protected boolean handleSuccess(DataTableResult result) {
                final ObjectStatisticsReceivedEvent event = new ObjectStatisticsReceivedEvent();
                event.setCluster(cluster);
                event.setPredicates(predicates);
                event.setDatatype(datatype);
                event.setPattern(pattern);
                event.setDataTable(result.getDataTable());
                event.setOffset(start);
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }

}
