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

package de.hpi.fgis.ldp.shared.event.progress;

import java.util.HashMap;

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.rpc.progress.ProgressResult;

/**
 * represents a events for a progress update
 * 
 * @author toni.gruetze
 * 
 */
public class ProgressEvent<ResultType> extends GwtEvent<ProgressEventHandler<ResultType>> {
  private static HashMap<Class<?>, Type<?>> TYPES = new HashMap<Class<?>, Type<?>>();

  /**
   * determines the {@link GwtEvent.Type} for a generic {@link ProgressEvent} -event type
   * 
   * @param genericClass the class of the generic parameter of the progress event
   * @return the {@link GwtEvent.Type}
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> Type<ProgressEventHandler<T>> getGenericEventTypeFor(Class<T> genericClass) {
    synchronized (TYPES) {
      if (!TYPES.containsKey(genericClass)) {
        TYPES.put(genericClass, new Type<ProgressEventHandler<T>>());
      }
      return (Type) TYPES.get(genericClass);
    }
  }

  public final Type<ProgressEventHandler<ResultType>> typeRef;

  private final ProgressResult<ResultType> progress;

  /**
   * creates a new Progress event instance
   * 
   * @param progress the progress result
   * @param genericClass the class of the generic type
   */
  public ProgressEvent(ProgressResult<ResultType> progress, Class<ResultType> genericClass) {
    this.progress = progress;
    this.typeRef = getGenericEventTypeFor(genericClass);
  }

  /**
   * gets the progress result instance from server
   * 
   * @return the progress
   */
  public ProgressResult<ResultType> getProgressResult() {
    return progress;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
   */
  @Override
  public Type<ProgressEventHandler<ResultType>> getAssociatedType() {
    return this.typeRef;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared .EventHandler)
   */
  @Override
  protected void dispatch(final ProgressEventHandler<ResultType> handler) {
    handler.onProgressUpdate(this);
  }

}
