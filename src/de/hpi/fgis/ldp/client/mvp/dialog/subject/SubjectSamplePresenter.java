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

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.DialogView;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel.SingleSelectionListener;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.SubjectSampleReceivedEvent;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubjectSampleRequest;

@Singleton
public class SubjectSamplePresenter extends AbstractDialogPresenter<SubjectSamplePresenter.Display> {
  // inject as settings
  protected @Inject @Named("gui.maxTableRowCount") int entryCount;
  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  protected SubjectDetailsPresenter subjectDetailsPresenter;

  public interface Display extends DialogView {
    public void setDataTable(DataTablePanel table);
  }

  @Inject
  public SubjectSamplePresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher, final SubjectDetailsPresenter subjectDetailsPresenter) {
    super(display, eventBus, dispatcher);
    this.subjectDetailsPresenter = subjectDetailsPresenter;
    subjectDetailsPresenter.getDisplay().showOnHide(display);
  }

  @Override
  protected void onBind() {
    eventBus.addHandler(SubjectSampleReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<SubjectSampleReceivedEvent>() {
          @Override
          public void onDataTableReceived(final SubjectSampleReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            //
            // create data table
            final IDataTable table = event.getDataTable();
            final int start = event.getOffset();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setSelectionListenter(new SingleSelectionListener() {
              @Override
              public void onSelection(int selectedRow) {
                final Subject selectedSubject =
                    (Subject) table.getColumn(0).getElement(selectedRow);
                requestSubjectDetails(cluster, selectedSubject);
                // MessageBox.confirm("Subject selected",
                // selectedSubject.toString(), new
                // Listener<MessageBoxEvent>() {
                // public void
                // handleEvent(MessageBoxEvent be) {
                // Info.display("MessageBox",
                // "A button was pressed");
                // }
                // });
              }
            });

            if (start > 0) {
              tablePanel.setToolButton("x-tool-left", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  SubjectSamplePresenter.this.requestSampleSubjects(cluster, start
                      - SubjectSamplePresenter.this.entryCount);
                }
              });
            }

            if (table.getRowCount() >= SubjectSamplePresenter.this.entryCount) {
              tablePanel.setToolButton("x-tool-right", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  SubjectSamplePresenter.this.requestSampleSubjects(cluster, start
                      + SubjectSamplePresenter.this.entryCount);
                }
              });
            }
            tablePanel.setData(table);

            tablePanel.setTableTitle("Subjects");

            // set table
            SubjectSamplePresenter.this.getDisplay().setDataTable(tablePanel);

            // getDisplay().asWidget().show();
          }
        });

  }

  public void requestSampleSubjects(final Cluster cluster, final int start) {
    getDispatcher().execute(new SubjectSampleRequest(cluster, start, start + this.entryCount),
        callbackBuilder.build(getDisplay(), new AsyncDisplayCallback.Handler<DataTableResult>() {
          @Override
          protected boolean handleSuccess(DataTableResult result) {
            // take the result from the server and notify
            // client interested components
            SubjectSampleReceivedEvent event = new SubjectSampleReceivedEvent();
            event.setCluster(cluster);
            event.setDataTable(result.getDataTable());
            event.setOffset(start);
            getEventBus().fireEvent(event);

            return true;
          }
        }));
  }

  protected void requestSubjectDetails(final Cluster cluster, final Subject subject) {
    this.subjectDetailsPresenter.requestSubjectDetails(cluster, subject);
    this.getDisplay().hide();
  }
}
