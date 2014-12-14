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

package de.hpi.fgis.ldp.server.service.guice;

import net.customware.gwt.dispatch.client.service.DispatchService;
import net.customware.gwt.dispatch.server.Dispatch;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.Result;

import org.apache.commons.logging.Log;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.googlecode.concurrentlinkedhashmap.CapacityLimiter;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import de.hpi.fgis.ldp.server.util.exception.ExceptionFactory;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.server.util.job.UnspecifiedJob;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.exception.ResponseDelayException;
import de.hpi.fgis.ldp.shared.rpc.CachableAction;
import de.hpi.fgis.ldp.shared.rpc.CachableResult;

@Singleton
public class CacheDispatchServiceServlet extends RemoteServiceServlet implements DispatchService {
  private static final long serialVersionUID = 3112625987315056094L;
  protected final Dispatch dispatch;
  protected final ConcurrentLinkedHashMap<CachableAction<?>, CachableResult> cache;
  protected final long minCacheMillis;
  protected final long requestTimeout;
  protected final boolean activeCache;

  @Inject
  protected final JobManager manager;
  @Inject
  protected ExceptionFactory exceptionFactory;
  @Inject
  private Log logger;

  @Inject
  public CacheDispatchServiceServlet(Dispatch dispatch, JobManager manager,
      @Named("server.cache.minMillis") long minCacheMillis,
      @Named("server.cache.maxCapacity") final int maxCacheCapacity,
      @Named("server.cache.active") boolean active,
      @Named("server.requestTimeout") long requestTimeout) {
    this.dispatch = dispatch;
    this.manager = manager;
    this.minCacheMillis = minCacheMillis;
    this.activeCache = active;
    this.requestTimeout = requestTimeout;

    ConcurrentLinkedHashMap.Builder<CachableAction<?>, CachableResult> cacheBuilder =
        new ConcurrentLinkedHashMap.Builder<CachableAction<?>, CachableResult>();

    cacheBuilder.initialCapacity(maxCacheCapacity / 5);
    cacheBuilder.maximumWeightedCapacity(maxCacheCapacity);
    cacheBuilder.concurrencyLevel(4);
    // no weighed entities now
    // cacheBuilder.weigher(new Weigher<CachableResult>() {
    // public int weightOf(CachableResult instance) {
    // // idea: the bigger e.g. the result table weight it has
    // return 1;
    // }
    // });
    cacheBuilder.capacityLimiter(new CapacityLimiter() {
      @Override
      public boolean hasExceededCapacity(ConcurrentLinkedHashMap<?, ?> cache) {
        return cache.size() >= maxCacheCapacity;
      }

    });

    this.cache = cacheBuilder.build();
  }

  @Override
  public Result execute(Action<?> action) throws ActionException {
    if (this.activeCache && action instanceof CachableAction) {

      final Job job = new Job((CachableAction<?>) action);
      job.start();
      try {
        job.join(this.requestTimeout);
      } catch (InterruptedException e) {
        logger.error(
            "Unable to wait for job " + action.getClass().getName() + ": " + e.getMessage(), e);

        throw new RPCException(e);
      }
      // execution is going on
      if (job.isAlive()) {
        // TODO put Job to Threadmanager and return the id
        final long id = this.manager.monitorJob(job);
        throw new ResponseDelayException(id);

        // execution ended erroneously
      } else if (job.getThrowable() != null) {
        throw exceptionFactory.encapsulateException(job.getThrowable(), action);
        // execution ended successfully
      } else {
        return job.getResult();
      }
    } else {
      try {
        return dispatch.execute(action);
      } catch (Throwable t) {
        throw exceptionFactory.encapsulateException(t, action);
      }
    }

  }

  /**
   * this class represent a thread encapsulating the execution for cachable actions
   * 
   * @author toni.gruetze
   *
   */
  private class Job extends UnspecifiedJob<CachableResult> {
    private final CachableAction<? extends CachableResult> action;

    public Job(CachableAction<? extends CachableResult> action) {
      super("The request seems to take a moment, please be patient!");
      this.action = action;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      try {
        this.setResult(cache.get(action));

        if (this.getResult() == null) {
          final long start = System.currentTimeMillis();
          this.setResult(dispatch.execute(action));
          final long duration = System.currentTimeMillis() - start;

          if (duration >= minCacheMillis) {
            // cachedResult.setDuration(duration);
            cache.put(action, this.getResult());
          }
        }
      } catch (Error e) {
        logger.fatal(
            "Unable to execute job " + action.getClass().getName() + ": " + e.getMessage(), e);
      } catch (Throwable t) {
        this.setThrowable(t);
      }
    }

    /**
     * gets the action of the job
     * 
     * @return the action
     */
    public CachableAction<? extends CachableResult> getAction() {
      return action;
    }
  }
}
