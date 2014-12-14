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

import gwtupload.client.IUploadStatus;
import gwtupload.client.ModalUploadStatus;
import gwtupload.client.SingleUploader;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogView;
import de.hpi.fgis.ldp.client.mvp.dialog.dataimport.ImportPresenter.ImportActionListener;

public class ImportView extends AbstractDialogView implements ImportPresenter.Display {
  protected ImportActionListener actionListener;
  // protected TextField<String> schema;
  protected TextField<String> label;
  private FormPanel nameInput;
  private Button submit;
  private Button cancel;

  public ImportView() {
    super(550, 200);

    this.init();
  }

  public void init() {
    nameInput = new FormPanel();
    nameInput.setHeading("Dateset details");
    nameInput.setHeaderVisible(false);
    nameInput.setBorders(false);
    nameInput.setFrame(false);

    final FormData formData = new FormData("-20");

    // schema = new TextField<String>();
    // schema.setFieldLabel("Schema");
    // schema.setEmptyText("Enter the schema name ...");
    // schema.setAllowBlank(false);
    // schema.setData("aria-previous", nameInput.getButtonBar().getId());
    // nameInput.add(schema, formData);

    label = new TextField<String>();
    label.setFieldLabel("Label");
    label.setEmptyText("Enter the label ...");
    label.setAllowBlank(false);
    nameInput.add(label, formData);

    submit = new Button("Submit", new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent be) {
        if (ImportView.this.actionListener != null) {
          ImportView.this.actionListener.submitSchema(label.getValue());

          ImportView.this.startProcessing();
        } else {
          Info.display("Error", "No action listener set!");
          Log.warn("No action listener set for \"" + ImportView.class.getName() + "\"!");
        }
      }
    });
    nameInput.addButton(submit);
    cancel = new Button("Cancel", new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent be) {
        ImportView.this.hide();
      }
    });
    nameInput.addButton(cancel);

    nameInput.setButtonAlign(HorizontalAlignment.CENTER);

    final FormButtonBinding binding = new FormButtonBinding(nameInput);
    binding.addButton(submit);

    // // Create a new uploader panel and attach it to the document
    // MultiUploader defaultUploader = new MultiUploader();
    // ContentPanel mainPanel = new ContentPanel(new RowLayout());
    // mainPanel.setHeaderVisible(false);
    // mainPanel.setBorders(false);
    //
    // mainPanel.add(defaultUploader, new RowData(1, 1));
    // this.setWidget(mainPanel);
  }

  // public void toDo() {
  // synchronized (this) {
  // // ContentPanel data = new ContentPanel();
  // // data.addText("hihi hoho2!");
  // // this.setWidget(data);
  // ContentPanel mainPanel = new ContentPanel(new RowLayout());
  // IUploadStatus uploadStatus = new ModalUploadStatus().newInstance();
  // SingleUploader defaultUploader = new SingleUploader(uploadStatus);
  // defaultUploader.setStatusWidget(uploadStatus);
  // defaultUploader.addOnStartUploadHandler(new
  // IUploader.OnStartUploaderHandler() {
  // @Override
  // public void onStart(IUploader uploader) {
  // uploader.getStatusWidget().setVisible(true);
  //
  // }
  //
  // });
  // defaultUploader.addOnFinishUploadHandler(new
  // IUploader.OnFinishUploaderHandler() {
  // @Override
  // public void onFinish(IUploader uploader) {

  // }
  //
  // });
  // mainPanel.add(uploadStatus.getWidget(), new RowData(1, 0.5));
  // mainPanel.add(defaultUploader, new RowData(1, 0.5));
  // this.setWidget(mainPanel);
  // this.show();
  // }
  // }

  @Override
  public void startNameInput() {
    nameInput.setEnabled(true);

    this.setWidget(nameInput);
  }

  @Override
  public void setImportActionListener(ImportActionListener listener) {
    this.actionListener = listener;
  }

  @Override
  public void showProgress(double progress, String message) {
    // TODO Auto-generated method stub

  }

  @Override
  public void startUpload(SingleUploader uploader) {

    nameInput.setEnabled(false);

    // add the uploader as widget
    // TODO layout?
    IUploadStatus uploadStatus = new ModalUploadStatus().newInstance();
    uploader.setStatusWidget(uploadStatus);
    uploadStatus.setVisible(true);
    new SingleUploader();
    // SingleUploader single3 = new SingleUploader(FileInputType.LABEL);
    uploader.setAutoSubmit(true);
    // single3.setValidExtensions("jpg", "gif", "png");
    // single3.addOnFinishUploadHandler(onFinishUploaderHandler);
    // single3.getFileInput().getWidget().setStyleName("customButton");
    // single3.getFileInput().getWidget().setSize("159px", "27px");
    uploader.avoidRepeatFiles(true);

    this.setWidget(uploader);

  }
}
