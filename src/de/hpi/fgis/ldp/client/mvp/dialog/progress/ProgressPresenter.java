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

import java.util.HashMap;
import java.util.HashSet;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.DialogView;
import de.hpi.fgis.ldp.client.mvp.dialog.DialogView.ICloseListener;
import de.hpi.fgis.ldp.client.util.exception.ExceptionHandler;
import de.hpi.fgis.ldp.shared.event.progress.ProgressEvent;
import de.hpi.fgis.ldp.shared.event.progress.ProgressEventHandler;
import de.hpi.fgis.ldp.shared.rpc.progress.ProgressRequest;
import de.hpi.fgis.ldp.shared.rpc.progress.ProgressResult;

@Singleton
@SuppressWarnings("boxing")
public class ProgressPresenter extends AbstractDialogPresenter<ProgressPresenter.Display> {
  private Long activeIdentifier;
  private final static Class<?> DUMMY_TYPE_INSTANCE = new Object() {}.getClass();
  private final HashSet<Type<?>> eventHandlerClasses = new HashSet<Type<?>>();
  private final HashMap<Long, CompletionListener<?>> completionListeners =
      new HashMap<Long, CompletionListener<?>>();

  /**
   * represents a element which listens if the progress is finished
   * 
   * @author toni.gruetze
   * 
   * @param <ResultType> the type of the result
   */
  public interface CompletionListener<ResultType> {
    /**
     * will be called after the completion of the progress
     * 
     * @param success indicates the successful execution
     * @param result the result of the process
     * @return should return true if the progress dialog should be closed after the call of this
     *         method
     */
    public boolean onCompletion(boolean success, ResultType result);
  }

  public interface Display extends DialogView {
    public void reset();

    public void startProgress(boolean controlled, String message);

    public void continueProgress(double progress, String message);

    public void stopProgress(String message);

    public void startSubProgress(boolean controlled, String message);

    public void continueSubProgress(double progress, String message);

    public void stopSubProgress(String message);

    public boolean isProgressStarted();
  }

  /**
   * this class enables the scheduled job of repeating updates of the progress
   * 
   * @author toni.gruetze
   *
   */
  private class RefreshTask<ResultType> extends Timer {
    private final boolean continueOnClose;
    protected long progressIdentifier;
    private final Class<ResultType> resultClass;
    private double delayPercentage;

    /**
     * @param progressIdentifier
     * @param resultClass
     */
    protected RefreshTask(long progressIdentifier, Class<ResultType> resultClass,
        boolean continueOnClose) {
      this.progressIdentifier = progressIdentifier;
      this.resultClass = resultClass;
      this.delayPercentage = 10;
      this.continueOnClose = continueOnClose;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
      if (!continueOnClose) {
        if (activeIdentifier == null || progressIdentifier != activeIdentifier.longValue()) {
          this.cancel();
          abortProgress(progressIdentifier);
          return;
        }
      }
      updateProgress(progressIdentifier, resultClass, this);
      reRunLater();
    }

    private void reRunLater() {
      this.schedule((int) (refreshDelay * (this.delayPercentage / 100.0)));
      this.delayPercentage = Math.min(100, delayPercentage + 20);
    }
  }

  // private Timer timer = null;
  protected final int refreshDelay;

  protected final ExceptionHandler exceptionHandler;

  @Inject
  protected ProgressPresenter(final Display display, final EventBus eventBus,
      ExceptionHandler exceptionHandler, final DispatchAsync dispatcher,
      @Named("gui.progressRefreshDelay") int delay) {
    super(display, eventBus, dispatcher);
    this.refreshDelay = delay;
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  protected void onBind() {
    this.display.addOnCloseListener(new ICloseListener() {
      @Override
      public void onClose() {
        activeIdentifier = null;
      }
    });
  }

  protected void stopTimer(Timer timer) {
    if (timer != null) {
      timer.cancel();
    }
  }

  protected void startTimer(Timer timer) {
    if (timer != null) {
      timer.schedule(500);
    }
  }

  /**
   * shows the progress for the given progress identifier
   * 
   * @param <ResultType> the type of the result
   * @param progressIdentifier the identifier of the progress (known @ server)
   * @param resultClass the class of the result
   * @param completionListener a instance of the {@link CompletionListener} interface, which
   *        specifies the actions after the completion
   * @param ignoreResultAfterClosing specifies, that the completionListener will be ignored, if the
   *        dialog was already closed
   */
  @SuppressWarnings("unchecked")
  public <ResultType> void showProgress(long progressIdentifier, Class<ResultType> resultClass,
      CompletionListener<ResultType> completionListener, boolean ignoreResultAfterClosing) {
    if (resultClass == null) {
      resultClass = (Class<ResultType>) DUMMY_TYPE_INSTANCE;
    }
    this.createProgressEventHandler(resultClass);
    this.getDisplay().reset();
    this.getDisplay().show();
    this.activeIdentifier = Long.valueOf(progressIdentifier);
    this.completionListeners.put(progressIdentifier, completionListener);

    // restart timer
    this.startTimer(new RefreshTask<ResultType>(progressIdentifier, resultClass,
        !ignoreResultAfterClosing));
  }

  protected <ResultType> void updateProgress(final long progressIdentifier,
      final Class<ResultType> resultClass, final Timer timer) {
    // ignore false update requests
    if (!completionListeners.containsKey(progressIdentifier)) {
      return;
    }
    // submit request to server
    getDispatcher().execute(new ProgressRequest<ResultType>(progressIdentifier),
        new AsyncCallback<ProgressResult<ResultType>>() {

          @Override
          public void onFailure(Throwable caught) {
            ProgressResult<ResultType> error = new ProgressResult<ResultType>();
            error.setException(caught);
            error.setProgressIdentifier(progressIdentifier);

            // inform all connected instances
            this.publishProgress(error);
          }

          @Override
          public void onSuccess(ProgressResult<ResultType> result) {
            this.publishProgress(result);
          }

          private void publishProgress(final ProgressResult<ResultType> progress) {
            if (progress.isFinished()) {
              stopTimer(timer);
            }

            getEventBus().fireEvent(new ProgressEvent<ResultType>(progress, resultClass));
          }
        });
  }

  protected void abortProgress(final long progressIdentifier) {
    if (completionListeners.containsKey(progressIdentifier)) {
      final boolean hideDisplay =
          completionListeners.get(progressIdentifier).onCompletion(false, null);
      if (hideDisplay) {
        getDisplay().hide();
      }
      completionListeners.remove(progressIdentifier);
    }
  }

  @SuppressWarnings("unchecked")
  private <ResultType> void createProgressEventHandler(Class<ResultType> resultClass) {
    final Type<ProgressEventHandler<ResultType>> type =
        ProgressEvent.getGenericEventTypeFor(resultClass);
    if (!this.eventHandlerClasses.contains(type)) {
      this.eventHandlerClasses.add(type);

      eventBus.addHandler(type, new ProgressEventHandler<ResultType>() {
        @Override
        @SuppressWarnings({"synthetic-access"})
        public void onProgressUpdate(ProgressEvent<ResultType> event) {
          final ProgressResult<ResultType> progress = event.getProgressResult();
          // check either the identifier is an actual one or not
          if (!completionListeners.containsKey(progress.getProgressIdentifier())) {
            // ignore this result
            return;
          }

          // check either the ui is visible or not
          if (!getDisplay().isVisible()) {
            // completionListener=null;
            // stopTimer();
            return;
          }

          // finished progress
          if (progress.isFinished() || progress.getException() != null) {
            // no error/exception
            if (progress.getException() == null) {
              getDisplay().stopProgress("");
            } else {
              // show error message box
              exceptionHandler.handle(progress.getException());
            }
            if (completionListeners.containsKey(progress.getProgressIdentifier())) {
              final boolean hideDisplay =
                  ((CompletionListener<ResultType>) completionListeners.get(progress
                      .getProgressIdentifier())).onCompletion(progress.getException() == null,
                      progress.getResult());
              if (hideDisplay) {
                getDisplay().hide();
              }
              completionListeners.remove(progress.getProgressIdentifier());
            }
          }
          // started/unspecified progress (no progress available)
          else if (progress.getProgress() < 0) {
            if (!getDisplay().isProgressStarted()) {
              getDisplay().startProgress(false, progress.getMessage());
            }
          } else {
            // initialize the (new) controlled progress
            if (!getDisplay().isProgressStarted()) {
              getDisplay().startProgress(true, progress.getMessage());
            }
            // continuing progress
            getDisplay().continueProgress(progress.getProgress(), progress.getDetailMessage());
          }

          // TODO child progress
        }
      });
    }

  }
}
