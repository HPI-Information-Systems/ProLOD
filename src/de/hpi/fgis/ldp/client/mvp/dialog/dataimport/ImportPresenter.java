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

package de.hpi.fgis.ldp.client.mvp.dialog.dataimport;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import java.util.Arrays;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.ui.Hidden;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.DialogView;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter.CompletionListener;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.shared.config.dataimport.FileType;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterUpdatedEvent;
import de.hpi.fgis.ldp.shared.event.schema.ImportInitializedEvent;
import de.hpi.fgis.ldp.shared.event.schema.ImportInitializedEventHandler;
import de.hpi.fgis.ldp.shared.event.schema.SchemaAddedEvent;
import de.hpi.fgis.ldp.shared.rpc.schema.ImportProcessRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.ImportProcessResult;

@Singleton
public class ImportPresenter extends AbstractDialogPresenter<ImportPresenter.Display> {
  public interface ImportActionListener {
    public void submitSchema(String label);
  }

  public interface Display extends DialogView {
    public void setImportActionListener(ImportActionListener listener);

    public void startNameInput();

    public void startUpload(SingleUploader uploader);

    public void showProgress(double progress, String message);
  }

  protected final ProgressPresenter progressPresenter;
  protected final AsyncDisplayCallback.Builder callbackBuilder;

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */

  @Inject
  public ImportPresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher, ProgressPresenter progressPresenter,
      AsyncDisplayCallback.Builder callbackBuilder) {
    super(display, eventBus, dispatcher);

    this.progressPresenter = progressPresenter;
    this.callbackBuilder = callbackBuilder;
  }

  @Override
  protected void onBind() {
    this.getDisplay().setImportActionListener(new ImportActionListener() {
      @Override
      public void submitSchema(String label) {
        createSchema(label);
      }

    });
    eventBus.addHandler(ImportInitializedEvent.TYPE, new ImportInitializedEventHandler() {
      @Override
      public void onImportInitialized(ImportInitializedEvent event) {
        SingleUploader uploader = new SingleUploader();

        // extract valid file types
        FileType[] fileTypes = FileType.values();
        String[] validExtensions = new String[fileTypes.length];
        for (int i = 0; i < fileTypes.length; i++) {
          validExtensions[i] = fileTypes[i].getExtension();
        }

        Info.display("Dialog", Arrays.toString(validExtensions));
        uploader.setValidExtensions(validExtensions);

        final String schema = event.getSchema();
        final String label = event.getLabel();
        uploader.add(new Hidden("schema", schema));
        uploader.add(new Hidden("label", label));
        uploader.addOnStartUploadHandler(new IUploader.OnStartUploaderHandler() {
          @Override
          public void onStart(IUploader uploader) {
            // add the file type parameter
            FileType fileType = FileType.getType(uploader.getFileName());
            if (fileType != null) {
              uploader.add(new Hidden("fileType", fileType.toString()));
            } else {
              Log.error("Unable to determine file type of \"" + uploader.getFileName() + "\"!");
            }
            uploader.getStatusWidget().setVisible(true);

          }

        });
        uploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {
          @SuppressWarnings({"synthetic-access", "boxing"})
          @Override
          public void onFinish(IUploader uploader) {
            // open progress view
            Info.display("Dialog", "Upload finished");

            String response = uploader.getServerResponse();

            getDisplay().hide();
            final Long identifier = Long.parseLong(response);
            final Cluster newSchema = new Cluster(new DataSource(schema));
            newSchema.setLabel(label);
            newSchema.setProgressIdentifier(identifier);
            getEventBus().fireEvent(new SchemaAddedEvent(newSchema));
            progressPresenter.showProgress(identifier.longValue(), Cluster.class,
                new CompletionListener<Cluster>() {
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
          }

        });
        ImportPresenter.this.getDisplay().startUpload(uploader);
      }
    });
  }

  protected void createSchema(final String label) {
    // TODO submit to server&check validity

    getDispatcher().execute(
        new ImportProcessRequest(label),
        callbackBuilder.build(getDisplay(),
            new AsyncDisplayCallback.Handler<ImportProcessResult>() {
              @Override
              protected boolean handleSuccess(ImportProcessResult result) {
                // take the result from the
                // server and notify interested
                // client components
                getEventBus().fireEvent(
                    new ImportInitializedEvent(result.getRequest().getSchema(), result.getRequest()
                        .getLabel()));
                return true;
              }
            }));

    // TODO wait for response
    // TODO later on open upload panel
  }
}
