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

import java.util.Date;

import com.google.inject.Provider;

/**
 * Command line implementation of {@link IProgress}
 * 
 * @author toni.gruetze
 * @deprecated use {@link DebugProgress} instead
 */
@Deprecated
public class CMDProgress implements IProgress {
  // private static final CMDProgress INSTANCE = new CMDProgress();
  // private static Logger logger
  private final CMDProgress parent;
  private final long parentContingent;
  private final long parentAtStart;
  private String name;
  private long count = -1;
  private long current = 0;
  private boolean running = false;
  private final static long MSG_DELAY = 1000;

  private long lastMsgTime = 0;

  /**
   * Get an instance.
   * 
   * @return the an instance
   * @deprecated use injection with {@link Provider} of {@link DebugProgress} instead
   */
  @Deprecated
  public static CMDProgress getInstance() {
    return new CMDProgress();// CMDProgress.INSTANCE;
  }

  /**
   * hide default constructor -> singleton instance
   */
  private CMDProgress() {
    this(null, -1);
    // nothing to do
  }

  private CMDProgress(final CMDProgress parent, final long parentContingent) {
    this.parent = parent;
    this.parentContingent = parentContingent;
    this.parentAtStart = parent != null ? parent.current : -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#continueProgressAt(long)
   */
  @Override
  public void continueProgressAt(final long current) {
    this.continueProgressAt(current, "");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#continueProgressAt(long, java.lang.String)
   */
  @Override
  public void continueProgressAt(final long current, final String msg) {
    if (this.running) {
      final long currentTime = System.currentTimeMillis();

      // make log entry if last progess feedback is "long time ago"
      if (currentTime - this.lastMsgTime > CMDProgress.MSG_DELAY) {
        this.lastMsgTime = currentTime;
        if (((int) (this.current * 100.0 / this.count)) != ((int) (current * 100.0 / this.count))) {

          if (this.isChildProgress()) {
            this.parent.continueProgressAt(this.parentAtStart
                + (int) Math.round(this.parentContingent * current * 1d / this.count));

            System.out.println("\tchild of " + this.parent.name + ": "
                + (int) (current * 100.0 / this.count) + "%"
                + ((!"".equals(msg)) ? " - " + msg : "") + " of " + this.name);
          } else {
            System.out.println((int) (current * 100.0 / this.count) + "%"
                + ((!"".equals(msg)) ? " - " + msg : "") + " of " + this.name);
          }
        }
      }
      this.current = current;
      // if(this.current>=this.count)
      // this.stopProgress();
    } else {
      System.err.println(new Date() + ": ===[" + this.name
          + " - Continuing a finished progress (current:" + this.current + ", max:" + this.count
          + ")!]===");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#startProgress(java.lang .String)
   */
  @Override
  public void startProgress(final String msg) {
    this.startProgress(msg, -1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#startProgress(java.lang .String, long)
   */
  @Override
  public void startProgress(final String msg, final long max) {
    this.count = max;
    this.current = 0;
    this.running = true;
    this.name = msg;
    if (this.isChildProgress()) {
      System.out.println("\t" + new Date() + ": child of " + this.parent.name + ": ===["
          + this.name + "]===");
    } else {
      System.out.println(new Date() + ": ===[" + this.name + "]===");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#stopProgress()
   */
  @Override
  public void stopProgress() {
    if (this.running) {
      this.current = this.count;
      this.running = false;
      if (this.isChildProgress()) {
        System.out.println("\t" + new Date() + ": child of " + this.parent.name + ": ===["
            + this.name + " - done!]===");
      } else {
        System.out.println(new Date() + ": ===[" + this.name + " - done!]===");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#continueWithSubProgress (long)
   */
  @Override
  public IProgress continueWithSubProgress(long size) {
    // check depth
    // TODO inject?
    // private int maxLoggingDepth = 3;
    if (this.parent != null && this.parent.parent != null) {
      return new IProgress() {
        @Override
        public void stopProgress() {
          // ignore all
        }

        @Override
        public void startProgress(String msg, long max) {
          // ignore all
        }

        @Override
        public void startProgress(String msg) {
          // ignore all
        }

        @Override
        public void continueProgressAt(long currentStep, String msg) {
          // ignore all
        }

        @Override
        public void continueProgressAt(long currentStep) {
          // ignore all
        }

        @Override
        public void continueProgress(String msg) {
          // ignore all
        }

        @Override
        public void continueProgress() {
          // ignore all
        }

        @Override
        public IProgress continueWithSubProgressAt(long current, long size) {
          return this;
        }

        @Override
        public IProgress continueWithSubProgress(long size) {
          return this;
        }
      };
    }
    return new CMDProgress(this, size);
  }

  private boolean isChildProgress() {
    return this.parent != null && this.parentContingent > 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#continueWithSubProgressAt (long, long)
   */
  @Override
  public IProgress continueWithSubProgressAt(long current, long size) {
    this.continueProgressAt(current);
    return this.continueWithSubProgress(size);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#continueProgress()
   */
  @Override
  public void continueProgress() {
    this.continueProgressAt(this.current + 1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.progress.IProgress#continueProgress(java. lang.String)
   */
  @Override
  public void continueProgress(String msg) {
    this.continueProgressAt(this.current + 1, msg);
  }
}
