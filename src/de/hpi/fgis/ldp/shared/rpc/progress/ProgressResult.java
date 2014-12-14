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

package de.hpi.fgis.ldp.shared.rpc.progress;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * represents the result of a progress request
 * 
 * @author toni.gruetze
 * 
 * @param <ResultType> expected result type
 */
public class ProgressResult<ResultType> implements Result, IsSerializable {
  private static final long serialVersionUID = 2828757090948175674L;
  private long progressIdentifier;
  private String message;
  private String detailMessage;
  private double progress;
  private Throwable exception;

  private boolean finished;
  private ResultType result;

  private ProgressResult<ResultType> child;

  /**
   * gets the progress identifier
   * 
   * @return the progress identifier
   */
  public long getProgressIdentifier() {
    return progressIdentifier;
  }

  /**
   * sets the progress identifier
   * 
   * @param progressIdentifier the progress identifier to set
   */
  public void setProgressIdentifier(long progressIdentifier) {
    this.progressIdentifier = progressIdentifier;
  }

  /**
   * gets the message
   * 
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * sets the message
   * 
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * gets the detail message
   * 
   * @return the detail message
   */
  public String getDetailMessage() {
    return detailMessage;
  }

  /**
   * sets the detail message
   * 
   * @param detailMessage the detail message to set
   */
  public void setDetailMessage(String detailMessage) {
    this.detailMessage = detailMessage;
  }

  /**
   * gets the progress
   * 
   * @return the progress
   */
  public double getProgress() {
    return progress;
  }

  /**
   * sets the progress
   * 
   * @param progress the progress to set
   */
  public void setProgress(double progress) {
    this.progress = progress;
  }

  /**
   * gets the exception
   * 
   * @return the exception
   */
  public Throwable getException() {
    return exception;
  }

  /**
   * sets the exception
   * 
   * @param exception the exception to set
   */
  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * gets the child
   * 
   * @return the child
   */
  public ProgressResult<ResultType> getChild() {
    return child;
  }

  /**
   * sets the child
   * 
   * @param child the child to set
   */
  public void setChild(ProgressResult<ResultType> child) {
    this.child = child;
  }

  /**
   * gets the result instance
   * 
   * @return the result
   */
  public ResultType getResult() {
    return result;
  }

  /**
   * sets the result instance
   * 
   * @param result the result to set
   */
  public void setResult(ResultType result) {
    this.result = result;
  }

  /**
   * is the progress finished
   * 
   * @return is finished
   */
  public boolean isFinished() {
    return this.finished;
  }

  /**
   * sets if the progress is finished
   * 
   * @param finished is the progress finished
   */
  public void setFinished(boolean finished) {
    this.finished = finished;
  }
}
