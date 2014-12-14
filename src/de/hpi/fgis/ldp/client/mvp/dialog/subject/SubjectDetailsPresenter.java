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

package de.hpi.fgis.ldp.client.mvp.dialog.subject;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.DialogView;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.SubjectDetailsReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubjectDetailRequest;

@Singleton
public class SubjectDetailsPresenter extends
    AbstractDialogPresenter<SubjectDetailsPresenter.Display> {
  public interface Display extends DialogView {
    public void setDataTable(DataTablePanel table);

    @Override
    public void showOnHide(DialogView view);
  }

  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  @Inject
  public SubjectDetailsPresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus, dispatcher);

  }

  @Override
  protected void onBind() {
    eventBus.addHandler(SubjectDetailsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<SubjectDetailsReceivedEvent>() {
          @Override
          public void onDataTableReceived(final SubjectDetailsReceivedEvent event) {
            // final Cluster cluster = event.getCluster();
            final Subject subject = event.getSubject();

            // create data table
            final IDataTable table = event.getDataTable();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setData(table);

            tablePanel.setTableTitle("Triples of \"" + subject.getLabel() + "\"");

            // set table
            SubjectDetailsPresenter.this.getDisplay().setDataTable(tablePanel);
          }
        });

  }

  public void requestSubjectDetails(final Cluster cluster, final Subject subject) {
    this.getDisplay().show();

    getDispatcher().execute(new SubjectDetailRequest(cluster, subject),
        callbackBuilder.build(getDisplay(), new AsyncDisplayCallback.Handler<DataTableResult>() {
          @Override
          protected boolean handleSuccess(DataTableResult result) {
            // take the result from the server and notify
            // client interested components
            SubjectDetailsReceivedEvent event = new SubjectDetailsReceivedEvent();
            event.setCluster(cluster);
            event.setSubject(subject);
            event.setDataTable(result.getDataTable());

            getEventBus().fireEvent(event);

            return true;
          }
        }));
  }
}
