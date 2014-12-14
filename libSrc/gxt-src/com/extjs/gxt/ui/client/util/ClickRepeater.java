/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

import java.util.Date;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.ClickRepeaterEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.ComponentAttachable;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;

/**
 * A utility class that continues to fire a "click" event when the user holds
 * the mouse key down.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>OnClick</b> : ClickRepeaterEvent(source, el)<br>
 * <div>Fires when the user holds down the mouse button.</div>
 * <ul>
 * <li>source : this</li>
 * <li>el : the click element</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class ClickRepeater extends BaseObservable implements ComponentAttachable {

  private boolean accelerate;
  private int delay = 250;
  private El el;
  private int interval = 20;
  private Date mousedownTime;
  private BaseEventPreview preview;
  private String pressClass;
  private Timer timer;
  private boolean waitForMouseOut;
  private boolean waitForMouseOver;

  /**
   * Creates a new click repeater.
   * 
   * @param el the element to be clicked
   */
  public ClickRepeater(El el) {
    this.el = el;
    preview = new BaseEventPreview() {
      protected boolean onPreview(PreviewEvent pe) {
        if (pe.getEventTypeInt() == Event.ONMOUSEUP) {
          ClickRepeater.this.handleMouseUp();
        }
        return true;
      }
    };
    preview.setAutoHide(false);
    el.addEventsSunk(Event.ONMOUSEDOWN | Event.ONMOUSEOUT | Event.ONMOUSEOVER);
  }

  public void doAttach() {
    DOM.setEventListener(el.dom, new EventListener() {
      public void onBrowserEvent(Event event) {
        switch (event.getTypeInt()) {
          case Event.ONMOUSEDOWN:
            handleMouseDown();
            break;
          case Event.ONMOUSEOUT:
            handleMouseOut();
            break;
          case Event.ONMOUSEOVER:
            handleMouseReturn();
            break;
        }
      }
    });

    el.disableTextSelection(true);
    preview.add();
  }

  public void doDetach() {
    DOM.setEventListener(el.dom, null);
    el.disableTextSelection(false);
    preview.remove();
  }

  public boolean fireEvent(EventType eventType) {
    return fireEvent(eventType, new ClickRepeaterEvent(this, el));
  }

  /**
   * Returns the amount before events are fired once the user holds the mouse
   * down.
   * 
   * @return the delay in milliseconds
   */
  public int getDelay() {
    return delay;
  }

  /**
   * Returns the "click" element.
   * 
   * @return the element
   */
  public El getEl() {
    return el;
  }

  /**
   * Returns the amount of time between "clicks".
   * 
   * @return the time in milliseconds
   */
  public int getInterval() {
    return interval;
  }

  /**
   * Returns the press CSS style name.
   * 
   * @return the press class
   */
  public String getPressClass() {
    return pressClass;
  }

  /**
   * Returns true if acceleration is enabled.
   * 
   * @return true if enabled
   */
  public boolean isAccelerate() {
    return accelerate;
  }

  /**
   * True if autorepeating should start slowly and accelerate (defaults to
   * false). "interval" and "delay" are ignored.
   * 
   * @param accelerate true to accelerate
   */
  public void setAccelerate(boolean accelerate) {
    this.accelerate = accelerate;
  }

  /**
   * The initial delay before the repeating event begins firing (defaults to
   * 250). Similar to an autorepeat key delay.
   * 
   * @param delay the delay in milliseconds
   */
  public void setDelay(int delay) {
    this.delay = delay;
  }

  /**
   * Sets the interval (defaults to 250).
   * 
   * @param interval the interval in milliseconds
   */
  public void setInterval(int interval) {
    this.interval = interval;
  }

  /**
   * A CSS class name to be applied to the element while pressed.
   * 
   * @param pressClass the style name
   */
  public void setPressClass(String pressClass) {
    this.pressClass = pressClass;
  }

  // private
  protected void click() {
    fireEvent(Events.OnClick);
    timer.schedule(accelerate ? easeOutExpo(new Date().getTime() - mousedownTime.getTime(), 400, -390, 12000)
        : interval);
  }

  protected int easeOutExpo(long t, int b, int c, int d) {
    return (int) ((t == d) ? b + c : c * (-Math.pow(2, -10 * t / d) + 1) + b);
  }

  protected void handleMouseDown() {
    if (timer == null) {
      timer = new Timer() {
        public void run() {
          click();
        }
      };
    }
    timer.cancel();
    el.blur();

    if (pressClass != null) {
      el.addStyleName(pressClass);
    }
    mousedownTime = new Date();

    waitForMouseOut = true;
    fireEvent(Events.OnMouseDown);
    fireEvent(Events.OnClick);

    // Do not honor delay or interval if acceleration wanted.
    if (accelerate) {
      delay = 400;
    }
    timer.schedule(delay);
  }

  protected void handleMouseOut() {
    if (waitForMouseOut) {
      timer.cancel();
      if (pressClass != null) {
        el.removeStyleName(pressClass);
      }
      waitForMouseOver = true;
    }
  }

  protected void handleMouseReturn() {
    if (waitForMouseOver) {
      waitForMouseOver = false;
      if (pressClass != null) {
        el.addStyleName(pressClass);
      }
      click();
    }
  }

  protected void handleMouseUp() {
    if (waitForMouseOut) {
      timer.cancel();
      waitForMouseOut = false;
      waitForMouseOver = false;
      el.removeStyleName(pressClass);
      fireEvent(Events.OnMouseUp);
    }
  }
}
