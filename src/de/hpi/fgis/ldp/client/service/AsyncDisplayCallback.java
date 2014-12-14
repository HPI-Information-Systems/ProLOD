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

package de.hpi.fgis.ldp.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter.CompletionListener;
import de.hpi.fgis.ldp.client.util.exception.ExceptionHandler;
import de.hpi.fgis.ldp.shared.exception.ResponseDelayException;

public class AsyncDisplayCallback<T> implements AsyncCallback<T> {
  protected final ProgressPresenter progressPresenter;
  protected final ExceptionHandler exceptionHandler;
  private final CallbackDisplay display;
  private final Handler<T> handler;
  private static final CallbackDisplay ignoreCallback = new CallbackDisplay() {
    @Override
    public void displayError() {
      // ignore
    }

    @Override
    public void startProcessing() {
      // ignore
    }

    @Override
    public void stopProcessing() {
      // ignore
    }
  };

  public static abstract class Handler<T> {
    protected AsyncDisplayCallback<T> callback = null;

    protected final Handler<T> setCallbackInstance(AsyncDisplayCallback<T> callback) {
      this.callback = callback;
      return this;
    }

    /**
     * handle the successful execution
     * 
     * @param value the value to be handled
     * @return true indicates that the connected display should be reset (default = true) afterwards
     */
    protected abstract boolean handleSuccess(T value);

    /**
     * handle a failure of the call
     * 
     * @param t the exception
     * @return true indicates that the connected display should be reset (default = true) afterwards
     */
    protected boolean handleFailure(Throwable t) {
      if (t instanceof ResponseDelayException) {
        // this exception will occur often
        // TODO show message box that asks the user to choose to wait
        // (waiting dialog) or to retry/reload later
        ResponseDelayException e = (ResponseDelayException) t;
        callback.progressPresenter.showProgress(e.getProgressIdentifier(), null,
            new CompletionListener<T>() {
              @Override
              public boolean onCompletion(boolean success, T result) {
                if (success) {
                  callback.onSuccess(result);
                  return true;
                }
                callback.display.displayError();
                return false;
              }
            }, true);
      } else {
        callback.exceptionHandler.handle(t, new ExceptionHandler.AcceptanceListener() {
          @Override
          public void onAccept() {
            callback.display.displayError();
          }
        });
      }

      return false;
    }
  }

  public static class Builder {
    private final ProgressPresenter progressPresenter;
    private final ExceptionHandler exceptionHandler;

    @Inject
    protected Builder(ProgressPresenter progressPresenter, ExceptionHandler exceptionHandler) {
      this.progressPresenter = progressPresenter;
      this.exceptionHandler = exceptionHandler;
    }

    public <T> AsyncDisplayCallback<T> build(Handler<T> handler) {
      return this.build(null, handler);
    }

    public <T> AsyncDisplayCallback<T> build(CallbackDisplay display, Handler<T> handler) {
      return new AsyncDisplayCallback<T>(progressPresenter, display, handler, exceptionHandler);
    }
  }

  protected AsyncDisplayCallback(ProgressPresenter progressPresenter, CallbackDisplay display,
      Handler<T> handler, ExceptionHandler exceptionHandler) {
    this.progressPresenter = progressPresenter;
    this.exceptionHandler = exceptionHandler;
    this.display = (display == null) ? ignoreCallback : display;
    this.handler = handler.setCallbackInstance(this);
    this.display.startProcessing();
  }

  @Override
  public final void onSuccess(T value) {
    boolean resetView = true;
    try {
      resetView = handler.handleSuccess(value);
    } finally {
      if (resetView) {
        reset(null);
      }
    }

  }

  @Override
  public final void onFailure(Throwable e) {
    boolean resetView = true;
    try {
      resetView = handler.handleFailure(e);
    } finally {
      if (resetView) {
        reset(e);
      }
    }
  }

  protected final void reset(Throwable e) {
    display.stopProcessing();
  }
}
