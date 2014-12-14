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

package de.hpi.fgis.ldp.client.mvp.main.content;

import java.util.ArrayList;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public abstract class AbstractMainContentPresenter<D extends MainContentView> extends
    WidgetPresenter<D> {

  private final ArrayList<ActivationRequestListener> activationRequestListener =
      new ArrayList<ActivationRequestListener>(1);

  public interface ActivationRequestListener {
    public void onActivationRequest();
  }

  public static final Place PLACE = new Place("MainPanel");
  private final DispatchAsync dispatcher;

  public AbstractMainContentPresenter(final D display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus);

    this.dispatcher = dispatcher;

    this.bind();
  }

  public void addActivationRequestListener(ActivationRequestListener listener) {
    this.activationRequestListener.add(listener);
  }

  protected void requestActivation() {
    for (ActivationRequestListener listener : this.activationRequestListener) {
      listener.onActivationRequest();
    }
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
