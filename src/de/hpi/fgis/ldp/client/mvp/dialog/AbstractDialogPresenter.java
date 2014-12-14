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

package de.hpi.fgis.ldp.client.mvp.dialog;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.Display;
import net.customware.gwt.presenter.client.DisplayCallback;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;

public abstract class AbstractDialogPresenter<D extends DialogView> extends WidgetPresenter<D> {

  public static final Place PLACE = new Place("Dialog");
  private final DispatchAsync dispatcher;

  public abstract class DialogCallback<T> extends DisplayCallback<T> {

    // public DialogCallback() {
    // super(AbstractDialogPresenter.this.getDisplay());
    // }
    public DialogCallback(Display display) {
      super(display);
    }

    @Override
    protected void handleFailure(Throwable cause) {
      Log.error("Handle Failure:", cause);

      Window.alert("Unable to get data from server (cause: " + cause.getMessage() + ")!");
    }

    @Override
    protected void handleSuccess(T value) {
      AbstractDialogPresenter.this.getEventBus().fireEvent(this.createEvent(value));
    }

    protected abstract GwtEvent<?> createEvent(T value);
  }

  public AbstractDialogPresenter(final D display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus);

    this.dispatcher = dispatcher;

    this.bind();
  }

  @Override
  public Place getPlace() {
    return PLACE;
  }

  protected EventBus getEventBus() {
    return this.eventBus;
  }

  protected DispatchAsync getDispatcher() {
    return this.dispatcher;
  }

  @Override
  protected void onPlaceRequest(PlaceRequest request) {
    // Grab the 'name' from the request and put it into the 'name' field.
    // This allows a tag of '#Greeting;name=Foo' to populate the name
    // field.
  }

  @Override
  protected void onUnbind() {
    // Add unbind functionality here for more complex presenters.
  }

  @Override
  public void refreshDisplay() {
    // TODO
    // This is called when the presenter should pull the latest data
    // from the server, etc. In this case, there is nothing to do.
  }

  @Override
  public void revealDisplay() {
    // Nothing to do. This is more useful in UI which may be buried
    // in a tab bar, tree, etc.
  }
}
