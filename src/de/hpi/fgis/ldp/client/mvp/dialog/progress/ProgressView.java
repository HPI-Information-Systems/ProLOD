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

package de.hpi.fgis.ldp.client.mvp.dialog.progress;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ProgressBar;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogView;

public class ProgressView extends AbstractDialogView implements ProgressPresenter.Display {
  private ContentPanel progressPanel;
  private String detailMessage;
  private ProgressBar progressBar;
  private Label message;
  private boolean running;

  public ProgressView() {
    super(400, 50);

    this.init();
    this.reset();
  }

  public void init() {
    this.progressBar = new ProgressBar();
    // progressBar.render(body.dom);
    this.message = new Label("&nbsp;");
    this.message.setStyleAttribute("font-size", "12pt");

    this.progressPanel = new ContentPanel();
    this.progressPanel.setHeaderVisible(false);
    this.progressPanel.setBorders(false);

    this.progressPanel.add(this.message);
    this.progressPanel.add(this.progressBar);

    this.setWidget(this.progressPanel);
  }

  @Override
  public boolean isProgressStarted() {
    return this.running;
  }

  @Override
  public void reset() {
    this.detailMessage = "";
    this.running = false;
    this.setMessage(" ");
    progressBar.updateProgress(0, "");
  }

  private void setMessage(String message) {
    // this.message.setText(message);
    this.window.setHeading(message);
  }

  @Override
  public void startProgress(boolean controlled, String message) {
    if (message != null) {
      this.setMessage(message);
    }

    if (controlled) {
      progressBar.reset();
      progressBar.updateText("0% Complete");
    } else {
      progressBar.auto();
      progressBar.updateText("Please wait ...");
    }
    this.running = true;
    this.waitingForFirstFeedback = true;
  }

  @Override
  public void continueProgress(double progress, String detailMessage) {
    if (!this.isProgressStarted()) {
      this.startProgress(true, null);
    }
    if (detailMessage != null && !this.detailMessage.endsWith(detailMessage)) {
      this.detailMessage = " - " + detailMessage;
    }

    // if auto is activated .. deactivate it
    if (progressBar.isRunning()) {
      progressBar.reset();
      progressBar.updateText("0% Complete");
    }
    progressBar.updateProgress(progress, ((int) Math.round(progress * 100)) + "% Complete"
        + this.detailMessage);
  }

  @Override
  public void stopProgress(String message) {
    this.detailMessage = "";
    this.running = false;
    this.setMessage(message);
    progressBar.updateProgress(1.0, "100% Complete");
    this.waitingForFirstFeedback = true;
  }

  @Override
  public void startSubProgress(boolean controlled, String message) {
    // TODO Auto-generated method stub
  }

  @Override
  public void continueSubProgress(double progress, String message) {
    // TODO Auto-generated method stub
  }

  @Override
  public void stopSubProgress(String message) {
    // TODO Auto-generated method stub
  }

  private boolean waitingForFirstFeedback;

  @Override
  public void startProcessing() {
    if (waitingForFirstFeedback) {
      super.startProcessing();
    }
  }

  @Override
  public void stopProcessing() {
    if (waitingForFirstFeedback) {
      this.setContent2(this.progressPanel);
      this.waitingForFirstFeedback = false;
      super.stopProcessing();
    }
  }

  @Override
  public void displayError() {
    if (waitingForFirstFeedback) {
      this.setContent2(this.progressPanel);
      this.waitingForFirstFeedback = false;
      super.displayError();
    }
  }
}
