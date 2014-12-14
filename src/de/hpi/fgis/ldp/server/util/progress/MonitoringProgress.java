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

package de.hpi.fgis.ldp.server.util.progress;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * this class is used to monitor the progress of a process (e.g. for remote observation)
 * 
 * @author toni.gruetze
 *
 * @param <T> the type of the connected job
 */
public class MonitoringProgress<T> implements IPersistentProgress<T>, IProgress {

  // used for logging purposes
  private IProgress debugLogger;
  private MonitoringProgress<T> child = null;
  private MonitoringProgress<T> parent = null;

  private boolean doDebugLogging;
  private Throwable e;
  private String message;
  private String detailMessage;
  private double progress;
  private double stepSize;

  private double parentContingent;
  private double parentStartValue;
  private double parentStepSize = -1.0;

  private boolean finished = false;

  private T result;

  private long identifier;
  private Provider<DebugProgress> debugProcess;

  private MonitoringProgress() {
    // hide default constructor
  }

  @Inject
  protected MonitoringProgress(Provider<DebugProgress> debugProcess, Log logger) {
    if (logger.isDebugEnabled()) {
      this.debugProcess = debugProcess;
      debugLogger = debugProcess.get();
      doDebugLogging = true;
    } else {
      this.debugProcess = null;
      debugLogger = null;
      doDebugLogging = false;
    }
  }

  @Override
  public String getDetailMessage() {
    return this.detailMessage;
  }

  @Override
  public Throwable getException() {
    return this.e;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public double getProgress() {
    if (this.stepSize < 0) {
      return 0.0;
    }
    return this.progress;
  }

  @Override
  public IPersistentProgress<T> getSubProgress() {
    return this.child;
  }

  @Override
  public boolean isFinished() {
    return this.finished;
  }

  @Override
  public void continueProgress() {
    synchronized (this) {
      this.continueProgressAt(this.progress + this.stepSize, null);
    }
  }

  @Override
  public void continueProgress(String msg) {
    synchronized (this) {
      this.continueProgressAt(this.progress + this.stepSize, msg);
    }
  }

  @Override
  public void continueProgressAt(long currentStep) {
    this.continueProgressAt(currentStep * this.stepSize, null);
  }

  @Override
  public void continueProgressAt(long currentStep, String msg) {
    this.continueProgressAt(currentStep * this.stepSize, msg);
  }

  public void continueProgressAt(double progress, String msg) {
    if (!this.isFinished() && this.progress < progress && this.progress < 1.0) {
      if (this.doDebugLogging) {
        this.debugLogger.continueProgressAt(Math.round(progress / this.stepSize));
      }
      synchronized (this) {
        this.progress = progress;
      }
      if (this.isChildProgress()) {

        this.parent.continueProgressAt(this.parentStartValue + (this.parentContingent * progress),
            null);
      }
      if (msg != null) {
        this.detailMessage = msg;
      }
    }
  }

  @Override
  public MonitoringProgress<T> continueWithSubProgress(long size) {
    MonitoringProgress<T> newChild = new MonitoringProgress<T>();
    newChild.parent = this;
    if (this.debugLogger != null && debugProcess != null) {
      newChild.debugProcess = debugProcess;
      newChild.debugLogger = debugProcess.get();
    }
    newChild.doDebugLogging = this.doDebugLogging;
    newChild.parentContingent = size * this.stepSize;
    newChild.parentStartValue = this.progress;
    newChild.parentStepSize = this.stepSize;
    newChild.identifier = this.identifier;
    this.child = newChild;

    return newChild;
  }

  @Override
  public MonitoringProgress<T> continueWithSubProgressAt(long current, long size) {
    this.continueProgressAt(current);
    return this.continueWithSubProgress(size);
  }

  @Override
  public void startProgress(String msg) {
    this.startProgress(msg, -1);
  }

  @Override
  public void startProgress(String msg, long max) {
    if (this.doDebugLogging) {
      this.debugLogger.startProgress(msg, max);
    }
    this.message = msg;
    if (max > 0) {
      // calculate step size (relative frequency / percentage)
      this.stepSize = (1.0 / max);
    } else {
      this.stepSize = -1;
    }
  }

  @Override
  public void stopProgress() {
    if (this.doDebugLogging) {
      this.debugLogger.stopProgress();
    }
    this.finished = true;
  }

  public void setException(Throwable e) {
    this.e = e;
  }

  private boolean isChildProgress() {
    return this.parent != null && this.parentContingent > 0 && this.parentStepSize >= 0;
  }

  @Override
  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }

  public void setIdentifier(long identifier) {
    this.identifier = identifier;
  }

  @Override
  public long getIdentifier() {
    return this.identifier;
  }
}
