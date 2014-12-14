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

package de.hpi.fgis.ldp.client.mvp.dialog.cluster;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.DialogView;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterInfoReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterInfoRequest;

@Singleton
public class ClusterInfoPresenter extends AbstractDialogPresenter<ClusterInfoPresenter.Display> {
  public interface Display extends DialogView {
    public void setDataTable(DataTablePanel table);
  }

  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */

  @Inject
  public ClusterInfoPresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus, dispatcher);

  }

  @Override
  protected void onBind() {
    eventBus.addHandler(ClusterInfoReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<ClusterInfoReceivedEvent>() {
          @Override
          public void onDataTableReceived(final ClusterInfoReceivedEvent event) {
            final Cluster cluster = event.getCluster();

            // create data table
            final IDataTable table = event.getDataTable();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setData(table);

            tablePanel.setTableTitle("Information for \"" + cluster.getLabel() + "\"");

            // set table
            ClusterInfoPresenter.this.getDisplay().setDataTable(tablePanel);
          }
        });

  }

  public void requestClusterInfo(final Cluster cluster) {
    getDispatcher().execute(new ClusterInfoRequest(cluster),
        callbackBuilder.build(getDisplay(), new AsyncDisplayCallback.Handler<DataTableResult>() {
          @Override
          protected boolean handleSuccess(DataTableResult result) {
            // take the result from the server and notify
            // client interested components
            ClusterInfoReceivedEvent event = new ClusterInfoReceivedEvent();
            event.setCluster(cluster);
            event.setDataTable(result.getDataTable());
            getEventBus().fireEvent(event);

            return true;
          }
        }));
    this.getDisplay().show();
  }
}
