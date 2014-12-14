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

package de.hpi.fgis.ldp.server.util.job;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.util.exception.ExceptionFactory;
import de.hpi.fgis.ldp.server.util.progress.IPersistentProgress;
import de.hpi.fgis.ldp.server.util.progress.MonitoringProgress;
import de.hpi.fgis.ldp.server.util.progress.UnspecifiedJobProgress;

@Singleton
public class JobManager {
  private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
  protected Long2ObjectOpenHashMap<IPersistentProgress<?>> runningProgress =
      new Long2ObjectOpenHashMap<IPersistentProgress<?>>();
  protected Object2LongOpenHashMap<String> jobs = new Object2LongOpenHashMap<String>();
  private final Timer timer = new Timer(true);

  public class JobDeletionTask extends TimerTask {
    private final long identifier;
    private final String jobName;

    public JobDeletionTask(final long identifier, final String jobName) {
      this.identifier = identifier;
      this.jobName = jobName;
    }

    @Override
    public void run() {
      // final String progressName = job.getName();
      synchronized (JobManager.this) {
        if (runningProgress.get(identifier).isFinished()) {
          runningProgress.remove(identifier);
        }
      }
      if (jobName != null) {
        synchronized (jobs) {
          jobs.removeLong(jobName);
        }
      }
    }
  }

  @Inject
  private Provider<UnspecifiedJobProgress<?>> threadProgressProvider;
  @Inject
  private Provider<MonitoringProgress<?>> monitoredProgressProvider;
  @Inject
  protected Log logger;
  @Inject
  protected ExceptionFactory exceptionFactory;

  @Inject
  void setMaxSize(@Named("server.maxJobCount") int size) {
    executor.setCorePoolSize(size);
  }

  public <T> long monitorJob(final UnspecifiedJob<T> unspecifiedJob) {
    final UnspecifiedJobProgress<T> processInstance =
        this.createUnspecifiedProgressFor(unspecifiedJob);
    return processInstance.getIdentifier();
  }

  public <T> long executeJob(final MonitoredJob<T> job) {
    final MonitoringProgress<T> progressInstance = this.createMonitorProgressFor(job);

    final String name = job.getName();
    if (name != null) {
      synchronized (jobs) {
        jobs.put(name, progressInstance.getIdentifier());
      }
    }
    final Thread exec = new Thread() {
      @Override
      public void run() {
        logger.info("starting new job: " + progressInstance.getIdentifier());
        try {
          job.execute();
          progressInstance.setResult(job.getResult());
        } catch (Throwable e) {
          logger.error("Job terminated erroneous!", e);
          // register error
          progressInstance.setException(exceptionFactory.encapsulateException(e));
        } finally {
          progressInstance.stopProgress();
          deleteJob(progressInstance.getIdentifier(), name);
          job.cleanup();
        }
      }
    };
    exec.setDaemon(true);
    executor.execute(exec);

    return progressInstance.getIdentifier();

  }

  protected void deleteJob(final long identifier, final String jobName) {
    // delete after 10 minutes
    timer.schedule(new JobDeletionTask(identifier, jobName), 600000);
  }

  @SuppressWarnings("unchecked")
  private <T> MonitoringProgress<T> createMonitorProgressFor(MonitoredJob<T> job) {
    final long identifier = this.registerNewIdentifier();

    MonitoringProgress<T> progress = (MonitoringProgress<T>) this.monitoredProgressProvider.get();
    progress.setIdentifier(identifier);
    this.runningProgress.put(identifier, progress);

    job.init(progress);

    return progress;
  }

  @SuppressWarnings("unchecked")
  private <T> UnspecifiedJobProgress<T> createUnspecifiedProgressFor(UnspecifiedJob<T> job) {
    final long identifier = this.registerNewIdentifier();

    UnspecifiedJobProgress<T> progress =
        (UnspecifiedJobProgress<T>) this.threadProgressProvider.get();
    progress.init(identifier, job);
    this.runningProgress.put(identifier, progress);

    return progress;
  }

  private long registerNewIdentifier() {
    long identifier;
    synchronized (runningProgress) {
      do {
        identifier = new Random().nextLong();
      } while (this.runningProgress.containsKey(identifier));

      logger.debug("register new job: " + identifier);
      this.runningProgress.put(identifier, null);
    }
    return identifier;
  }

  public IPersistentProgress<?> getProgressInstance(long identifier) {
    // TODO error handling (no such progress)
    return this.runningProgress.get(identifier);
  }

  public boolean isInProgress(String progressName) {
    return this.jobs.containsKey(progressName)
        && !this.runningProgress.get(this.jobs.getLong(progressName)).isFinished();
  }

  public long getProgressIdentifier(String progressName) {
    return this.jobs.getLong(progressName);
  }
  // public boolean isInProgress(Cluster cluster) {
  // return this.isInProgress(this.getProgressName(cluster));
  // }
  // public long getProgressIdentifier(Cluster cluster) {
  // return this.getProgressIdentifier(this.getProgressName(cluster));
  // }
}
