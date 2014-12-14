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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterChildrenRequest;

/**
 * Dispatcher which support caching of data in memory
 * 
 */
public class CachingDispatchAsync implements DispatchAsync {
  private final DispatchAsync dispatcher;
  protected Map<Action<Result>, Result> cache = new HashMap<Action<Result>, Result>();
  protected LinkedList<Action<Result>> queue = new LinkedList<Action<Result>>();
  // TODO inject
  protected int maxCacheSize = 20;

  @Inject
  public CachingDispatchAsync(final DispatchAsync dispatcher) {
    this.dispatcher = dispatcher;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.customware.gwt.dispatch.client.DispatchAsync#execute(A,
   * com.google.gwt.user.client.rpc.AsyncCallback)
   */
  @Override
  public <A extends Action<R>, R extends Result> void execute(final A action,
      final AsyncCallback<R> callback) {
    dispatcher.execute(action, callback);
  }

  /**
   * Execute the give Action. If the Action was executed before it will get fetched from the cache
   * 
   * @param <A> Action implementation
   * @param <R> Result implementation
   * @param action the action
   * @param callback the callback
   */
  @SuppressWarnings("unchecked")
  public <A extends Action<R>, R extends Result> void executeWithCache(final A action,
      final AsyncCallback<R> callback) {
    // ignore cluster children requests
    if (action instanceof ClusterChildrenRequest) {
      return;
    }

    Result r = null;
    synchronized (cache) {
      r = cache.get(action);
    }

    if (r != null) {
      callback.onSuccess((R) r);
    } else {
      dispatcher.execute(action, new AsyncCallback<R>() {

        @Override
        public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        @Override
        public void onSuccess(R result) {
          synchronized (cache) {
            while (cache.size() >= maxCacheSize) {
              cache.remove(queue.poll());
            }
            cache.put((Action) action, (Result) result);
            queue.offer((Action) action);
          }
          callback.onSuccess(result);
        }

      });
    }
  }

  /**
   * Clear the cache
   */
  public void clear() {
    cache.clear();
  }
}
